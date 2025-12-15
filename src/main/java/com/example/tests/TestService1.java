package com.example.tests;

import com.example.annotations.PostConstructor;
import com.example.annotations.PreDestory;
import com.example.annotations.Service;

@Service
public class TestService1 {

	@PostConstructor
	private void onInit() {
		System.out.println("--------------------------------TestService1 initialized--------------------------------");
	}

	@PreDestory()
	private void onDestroy() {
		System.out.println("TestService1 destroyed");
	}
}
