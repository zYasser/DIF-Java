package com.example.tests;

import com.example.annotations.*;

@Service
public class TestService
{


	private final CustomServiceT1 customServiceT1;

	public TestService(CustomServiceT1 customServiceT1){
		this.customServiceT1 = customServiceT1;
	}

// TO DO: SUPPORT BEAN WITH PARAMETERS


	@PreDestory
	private void preDestroy(){
		System.out.println("preDestory");
	}	

	@PostConstructor
	private void postConstructor(){
		System.out.println("postConstructor");
		this.customServiceT1.printHello();
	}


	@CustomBeanAnon
	private  TestBeanService testBeanService(){
		return new TestBeanService();
	}


}
