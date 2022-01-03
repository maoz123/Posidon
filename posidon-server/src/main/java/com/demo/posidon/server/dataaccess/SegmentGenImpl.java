package com.demo.posidon.server.dataaccess;

import com.alibaba.druid.pool.DruidDataSource;
import com.demo.posidon.server.common.PropertiesFactory;
import com.demo.posidon.server.models.PosidonAlloc;
import com.demo.posidon.server.utils.Consts;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SegmentGenImpl implements IGen {
    SqlSessionFactory sqlSessionFactory;

    public SegmentGenImpl() {
        Properties properties = PropertiesFactory.getProperties();
        boolean enabled = Boolean.parseBoolean(properties.getProperty(Consts.POSIDON_SEGMENT_ENABLED));
        ReentrantReadWriteLock.WriteLock wLock = this.lock.writeLock();
        wLock.lock();
        if(initialized){
            return;
        }
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource.setUrl(properties.getProperty(Consts.POSIDON_JDBC_URL));
            dataSource.setUsername(properties.getProperty(Consts.POSIDON_JDBC_User));
            dataSource.setPassword(properties.getProperty(Consts.POSIDON_JDBC_PASSWORD));
            dataSource.init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!enabled) {
            System.out.println("Segment is not enabled!");
        }
        Configuration configuration = new Configuration();
        configuration.addMapper(IDaoMapper.class);
        TransactionFactory factory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", factory, dataSource);
        configuration.setEnvironment(environment);
        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        this.initialized = true;
        wLock.unlock();

    }

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private boolean initialized = false;

    @Override
    public PosidonAlloc updateMaxIdAndGetLeafAlloc(String serviceName) {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE, TransactionIsolationLevel.REPEATABLE_READ);
        try{
            session.selectOne("IDaoMapper.updateMaxIdAndGetLeafAlloc", serviceName);
            PosidonAlloc alloc = session.selectOne("IDaoMapper.getAllocBySeriveName", serviceName);
            session.commit();
            return alloc;
        }
        finally {
            session.close();
        }
    }

    @Override
    public void updateMaxIdByCustomStepAndGetLeafAlloc(PosidonAlloc leafAlloc) {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE, TransactionIsolationLevel.REPEATABLE_READ);
        try
        {
            session.selectOne("IDaoMapper.updateMaxIdByCustomStepAndGetLeafAlloc", leafAlloc);
            session.commit();
        }finally {
            session.close();
        }
    }

    @Override
    public PosidonAlloc getAllocBySeriveName(String serviceName){
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE, TransactionIsolationLevel.REPEATABLE_READ);
        try
        {
            PosidonAlloc alloc = session.selectOne("IDaoMapper.getAllocBySeriveName", serviceName);
            session.commit();
            return alloc;
        }finally {
            session.close();
        }
    }

    @Override
    public List<String> getAllSupportedServices()
    {
        SqlSession session = sqlSessionFactory.openSession(ExecutorType.REUSE, TransactionIsolationLevel.REPEATABLE_READ);
        try
        {
            List<String> services = session.selectList("IDaoMapper.getAllSupportedServices");
            session.commit();
            return services;
        }finally {
            session.close();
        }
    }
}