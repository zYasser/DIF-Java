package com.example.tests;


import com.example.annotations.PostConstructor;

@CustomAnon
public class CustomServiceT1 {





	@PostConstructor()
	private void post(){
		System.out.println("hello from CustomService");
	}


	public void printHello(){
		System.out.println("Trigger Function from CustomService T1");
	}
    
}
