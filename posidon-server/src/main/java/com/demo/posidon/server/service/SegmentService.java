package com.demo.posidon.server.service;

import com.demo.posidon.server.dataaccess.SegmentGenImpl;
import com.demo.posidon.server.exception.NoKeyException;
import com.demo.posidon.server.models.PosidonAlloc;
import com.demo.posidon.server.models.Result;
import com.demo.posidon.server.models.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.OffsetDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class SegmentService implements IDGen {
    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentService.class);

    public SegmentService() {
        init();
    }
    private ConcurrentHashMap<String, Segment> segments = new ConcurrentHashMap<>();

    private SegmentGenImpl _segmentDataAccess;

    @Override
    public void init(){
        _segmentDataAccess = new SegmentGenImpl();
        updateCacheFromDB();
    }

    public Result getId(String serviceName){
        while(true){
            if(segments.containsKey(serviceName)){
                Segment segment = segments.get(serviceName);
                ReentrantReadWriteLock.WriteLock wLock = segment.getLock().writeLock();
                wLock.lock();
                try
                {
                    if(segment.getCurrentValue().intValue() < segment.getMaxValue().intValue()){
                        int currentValue = segment.getCurrentValue().getAndIncrement();
                        LOGGER.info("return new id {}", currentValue);
                        return new Result(currentValue, serviceName, OffsetDateTime.now().toString());
                    }
                    else
                        {
                        LOGGER.info("start get new segments from DB.");
                        PosidonAlloc alloc = _segmentDataAccess.updateMaxIdAndGetLeafAlloc(serviceName);
                        segment.getMaxValue().set(alloc.getMaxValue());
                        segment.setStep(alloc.getStep());
                        segment.getCurrentValue().set(alloc.getMaxValue()-alloc.getStep());
                        Result result = new Result(segment.getCurrentValue().getAndIncrement(), serviceName, OffsetDateTime.now().toString());
                        segment.getIsUpdated().compareAndSet(true, false);
                        LOGGER.info("finishing get new segments from DB.");
                        return result;
                    }
                }
                finally {
                 wLock.unlock();
                }
            }else
            {
                throw new NoKeyException("no key exists");
            }
        }
    }

    private void updateCacheFromDB(){
        String serviceName = "cloudlicense";
        PosidonAlloc alloc = _segmentDataAccess.updateMaxIdAndGetLeafAlloc(serviceName);
        Segment segment = new Segment(null);
        segment.setStep(alloc.getStep());
        segment.getMaxValue().set(alloc.getMaxValue());
        segment.getCurrentValue().set(alloc.getMaxValue()-alloc.getStep());
        segments.put(serviceName, segment);
    }
    private Object object = new Object();
}