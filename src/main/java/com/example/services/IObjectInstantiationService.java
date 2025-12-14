package com.example.services;

import com.example.exceptions.BeanInstantiationException;
import com.example.exceptions.PreDestroyException;
import com.example.exceptions.ServiceInstantiationException;
import com.example.models.ServiceBeanDetails;
import com.example.models.ServiceDetails;

public interface IObjectInstantiationService
{

	void createInstance(ServiceDetails<?> serviceDetails, Object... constructorParams) throws
			ServiceInstantiationException;
	void createBeanInstance(ServiceBeanDetails<?> beanDetails) throws BeanInstantiationException;
	void destroyInstance(ServiceDetails<?> details)  throws PreDestroyException;
}
