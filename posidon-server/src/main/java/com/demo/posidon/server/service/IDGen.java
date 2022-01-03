package com.demo.posidon.server.service;

import com.demo.posidon.server.models.Result;

public interface IDGen {
    void init();

    Result getId(String serviceName);
}
