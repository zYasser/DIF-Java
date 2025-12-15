package com.example.tests;

import com.example.annotations.AutoWired;
import com.example.annotations.PostConstructor;
import com.example.annotations.PreDestory;
import com.example.annotations.Service;

@Service
public class TestService2 {
    

    private TestService1 testService1;

    @AutoWired
    public TestService2(TestService1 testService1) {
        this.testService1 = testService1;
    }

    @PostConstructor()
    private void onInit(){
        System.out.println("--------------------------------TestService2 initialized--------------------------------");
    }

    @PreDestory()
    private void onDestroy(){
        System.out.println("TestService2 destroyed");
    }
}
