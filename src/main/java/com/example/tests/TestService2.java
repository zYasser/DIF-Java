package com.example.tests;

import com.example.annotations.AutoWired;
import com.example.annotations.PostConstructor;
import com.example.annotations.Service;


@Service
public class TestService2 {

    private final TestService testService;
    private  TestBeanService testBeanService;

    @AutoWired
    public TestService2(TestService testService , TestBeanService testBeanService) {
        this.testService = testService;
        this.testBeanService = testBeanService;
    }

    @PostConstructor
    private void postConstructor() {
        System.out.println("TestService2 postConstructor");
        this.testBeanService.test();
    }



}
