package com.example.models;

import java.lang.reflect.Method;

public class ServiceBeanDetails<T> extends ServiceDetails<T>
{

	private final Method originalMethod;

	private final ServiceDetails<?> rootService;


	public ServiceBeanDetails(Method originalMethod, ServiceDetails<?> rootService , Class<T> beanType) {
		this.setServiceType(beanType);
		this.setBeanMethod(new Method[0]);
		this.originalMethod = originalMethod;
		this.rootService = rootService;
	}

	public Method getOriginalMethod() {
		return originalMethod;
	}

	public ServiceDetails<?> getRootService() {
		return rootService;
	}
}
