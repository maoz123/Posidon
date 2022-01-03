package com.demo.posidon.server.service;

import com.demo.posidon.server.dataaccess.SegmentGenImpl;
import com.demo.posidon.server.exception.NoKeyException;
import com.demo.posidon.server.models.BufferSegments;
import com.demo.posidon.server.models.PosidonAlloc;
import com.demo.posidon.server.models.Result;
import com.demo.posidon.server.models.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class BufferedSegmentService implements IDGen {
    private static final Logger LOGGER = LoggerFactory.getLogger(BufferedSegmentService.class);

    public BufferedSegmentService() {
        init();
    }
    private ConcurrentHashMap<String, BufferSegments> buffers = new ConcurrentHashMap<>();
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private ExecutorService schedulerExecutor = Executors.newScheduledThreadPool(1);

    private SegmentGenImpl _segmentDataAccess;

    @Override
    public void init(){
        _segmentDataAccess = new SegmentGenImpl();
        List<String> services = _segmentDataAccess.getAllSupportedServices();
        for (String service : services){
            InitCacheFromDB(service);
        }
    }

    public Result getId(String serviceName) {
        if (buffers.containsKey(serviceName)) {
            LOGGER.info("start getting id from cache");
            Result result =  getIdFromBuffer(serviceName);
            LOGGER.info("id result from cache : {}", result.getId());
            return result;
        }
        else
        {
            throw new NoKeyException("no key exists");
        }
    }

    private Result getIdFromBuffer(String serviceName){
        while (true) {
            BufferSegments buffer = buffers.get(serviceName);
            buffer.getLock().readLock().lock();
            Segment segment = buffer.getCurrentSegment();
            try {
                if ((segment.getMaxValue().intValue() - segment.getCurrentValue().intValue()) > 0.1 * segment.getStep()
                        && !buffer.isNextSegmentReady() && buffer.getIsUpdating().compareAndSet(false, true)) {
                    //update next segment with background job.
                    executor.submit(() -> {
                        boolean updateOk = false;
                        try {
                            PosidonAlloc alloc = _segmentDataAccess.updateMaxIdAndGetLeafAlloc(serviceName);
                            Segment next = buffer.getNextSegment();
                            next.setMaxValue(new AtomicInteger(alloc.getMaxValue()));
                            next.setCurrentValue(new AtomicInteger(alloc.getMaxValue() - alloc.getStep()));
                            next.setStep(alloc.getStep());
                            updateOk = true;
                            LOGGER.info("update segments succeed.");
                        } catch (Exception e) {
                            LOGGER.warn("update and get segment value exception. {}", e.getMessage());
                        } finally {
                            if (updateOk) {
                                buffer.getLock().writeLock().lock();
                                buffer.setNextSegmentReady(true);
                                buffer.getIsUpdating().compareAndSet(true, false);
                                buffer.getLock().writeLock().unlock();
                            } else
                                buffer.getIsUpdating().compareAndSet(true, false);
                        }
                    });
                }
                int current = segment.getCurrentValue().getAndIncrement();
                if (current < segment.getMaxValue().intValue()) {
                    return new Result(current, serviceName, OffsetDateTime.now(ZoneId.of("UTC")).toString());
                }
            } finally {
                buffer.getLock().readLock().unlock();
            }
            // Thread not update next segment.
            if (segment.getCurrentValue().intValue() < segment.getMaxValue().intValue()) {
                return new Result(segment.getCurrentValue().getAndIncrement(), serviceName,
                        OffsetDateTime.now(ZoneId.of("UTC")).toString());
            } else {
                waitAndSleep(buffer);
                //this lock was in the outer lock, as it was used to prevent the switchPosition method.
                ReentrantReadWriteLock.WriteLock bfwLock = buffer.getLock().writeLock();
                bfwLock.lock();
                try {
                    Segment seg = buffer.getCurrentSegment();
                    if (seg.getCurrentValue().getAndIncrement() < seg.getMaxValue().intValue()) {
                        return new Result(seg.getCurrentValue().intValue(), serviceName, OffsetDateTime.now(ZoneId.of("UTC")).toString());
                    }
                    if (buffer.isNextSegmentReady()) {
                        buffer.switchPosition();
                        buffer.setNextSegmentReady(false);
                    }
                } finally {
                    bfwLock.unlock();
                }
            }
        }
    }

    private PosidonAlloc updateCacheFromDB(String serviceName){
        return _segmentDataAccess.updateMaxIdAndGetLeafAlloc(serviceName);
    }

    private void waitAndSleep(BufferSegments buffer){
        int round = 1;
        while(buffer.getIsUpdating().get()){
            round += 1;
            if(round > 10000)
            {
                try{
                    TimeUnit.MILLISECONDS.sleep(10);
                    break;
                }catch(InterruptedException e)
                {
                    LOGGER.warn("Thread interrupted exception Message : {} ThreadId {}", e.getMessage(), Thread.currentThread().getId());
                    break;
                }
            }
        }
    }

    private void InitCacheFromDB(String serviceName){
        PosidonAlloc alloc = _segmentDataAccess.updateMaxIdAndGetLeafAlloc(serviceName);
        BufferSegments buffer = new BufferSegments();
        Segment currentSegment = buffer.getCurrentSegment();
        currentSegment.setCurrentValue(new AtomicInteger(alloc.getMaxValue()-alloc.getStep()));
        currentSegment.setStep(alloc.getStep());
        currentSegment.setMaxValue(new AtomicInteger(alloc.getMaxValue()));
        buffer.setReady(true);
        if(!buffers.containsKey(serviceName)){
            buffers.put(serviceName, buffer);
        }
    }
}