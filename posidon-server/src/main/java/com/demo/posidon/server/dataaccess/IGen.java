package com.demo.posidon.server.dataaccess;

import com.demo.posidon.server.models.PosidonAlloc;

import java.util.List;

public interface IGen {
    PosidonAlloc updateMaxIdAndGetLeafAlloc(String tag);
    void updateMaxIdByCustomStepAndGetLeafAlloc(PosidonAlloc leafAlloc);
    PosidonAlloc getAllocBySeriveName(String serviceName);

    List<String> getAllSupportedServices();
}
