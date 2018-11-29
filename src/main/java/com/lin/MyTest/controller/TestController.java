package com.lin.MyTest.controller;

import com.lin.MyTest.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class TestController {

    @Autowired
    private TestService TestService;


    @RequestMapping(value = "/test/test", method = POST)
    public Long test() {

        return null;
    }

}