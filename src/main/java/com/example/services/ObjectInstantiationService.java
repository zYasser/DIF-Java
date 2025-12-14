package com.example.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.example.exceptions.BeanInstantiationException;
import com.example.exceptions.PostConstructorException;
import com.example.exceptions.ServiceInstantiationException;
import com.example.exceptions.PreDestroyException;
import com.example.models.ServiceBeanDetails;
import com.example.models.ServiceDetails;

public class ObjectInstantiationService implements IObjectInstantiationService {

	private final String INVALID_PARAMS_COUNT = "Constructor parameters count mismatch for service %s";

	@Override
	public void createInstance(ServiceDetails<?> serviceDetails, Object... constructorParams)
			throws ServiceInstantiationException {
		Constructor<?> constructor = serviceDetails.getConstructor();
		if (constructor.getParameterCount() != constructorParams.length) {
			throw new ServiceInstantiationException(
					String.format(INVALID_PARAMS_COUNT, serviceDetails.getServiceType().getName()));
		}
		try {
			Object instance = constructor.newInstance(constructorParams);
			serviceDetails.setInstance(instance);
			this.invokePostConstruct(serviceDetails);

		} catch (ServiceInstantiationException | IllegalAccessException | InvocationTargetException
				| InstantiationException e) {
			throw new ServiceInstantiationException("Failed to create instance", e);
		}

	}

	private void invokePostConstruct(ServiceDetails<?> serviceDetails) throws PostConstructorException {
		if (serviceDetails.getPostConstructorMethod() == null) {
			return;
		}
		try {
			serviceDetails.getPostConstructorMethod().invoke(serviceDetails.getInstance());
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new PostConstructorException(e.getMessage(), e);

		}
	}

	@Override
	public void createBeanInstance(ServiceBeanDetails<?> beanDetails) throws BeanInstantiationException {
		Method originalMethod = beanDetails.getOriginalMethod();
		Object rootInstance = beanDetails.getRootService().getInstance();
		try {
			Object instance = originalMethod.invoke(rootInstance);
			beanDetails.setInstance(instance);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new BeanInstantiationException(e.getMessage(), e);
		}
	}

	@Override
	public void destroyInstance(ServiceDetails<?> details) throws PreDestroyException {
		if (details.getPreDestoryMethod() == null) {
			try {
				details.getPreDestoryMethod().invoke(details.getInstance());
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new PreDestroyException(e.getMessage(), e);
			}
			
		}
		details.setInstance(null);
	}

}
