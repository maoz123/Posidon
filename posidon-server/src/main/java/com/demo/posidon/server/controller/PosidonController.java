package com.demo.posidon.server.controller;

import com.demo.posidon.server.models.Result;
import com.demo.posidon.server.service.IDGen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PosidonController {
    @Autowired
    private IDGen segmentService;

    @Autowired
    @Qualifier(value = "bufferedSegmentService")
    private IDGen bufferedSegmentService;

    @RequestMapping(value = "/id", method = RequestMethod.GET)
    public Result getId(String serviceName){
        return segmentService.getId(serviceName);
    }

    @RequestMapping(value = "/buffered/id", method = RequestMethod.GET)
    public Result getIdInBuffer(String serviceName){
        return bufferedSegmentService.getId(serviceName);
    }
}