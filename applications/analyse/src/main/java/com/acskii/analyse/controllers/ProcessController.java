package com.acskii.analyse.controllers;

import com.acskii.analyse.services.AnalyticProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( path = "/process" )
public class ProcessController {
    private final AnalyticProcessor processor;

    public ProcessController(AnalyticProcessor processor) {
        this.processor = processor;
    }

    @GetMapping
    @ResponseStatus( value = HttpStatus.ACCEPTED )
    public void process() {
        processor.processDaily();
    }
}
