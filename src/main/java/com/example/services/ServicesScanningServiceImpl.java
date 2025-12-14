package com.example.services;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.annotations.AutoWired;
import com.example.annotations.Bean;
import com.example.annotations.PostConstructor;
import com.example.annotations.PreDestory;
import com.example.annotations.Service;
import com.example.config.configurations.CustomAnnotationConfiguration;
import com.example.models.ServiceDetails;
import com.example.utils.ServiceDetailsConstructComparator;

public class ServicesScanningServiceImpl implements ServicesScanningService {
	private final CustomAnnotationConfiguration configuration;

	public ServicesScanningServiceImpl(CustomAnnotationConfiguration configuration) {
		this.configuration = configuration;
		this.init();
	}

	private void init() {
		this.configuration.addCustomServiceAnnotation(Service.class);
		this.configuration.addCustomBeanAnnotation(Bean.class);
	}

	@Override
	public Set<ServiceDetails<?>> mapServices(Set<Class<?>> locatedClasses) {
		final Set<ServiceDetails<?>> servicesStorage = new HashSet<>();
		final Set<Class<? extends Annotation>> customServiceAnnotations = this.configuration
				.getCustomServiceAnnotations();
		customServiceAnnotations.add(Service.class);

		for (Class<?> clazz : locatedClasses) {
			for (Annotation annotation : clazz.getAnnotations()) {
				if (customServiceAnnotations.contains(annotation.annotationType())) {
					ServiceDetails<?> serviceDetails = new ServiceDetails(
							clazz,
							annotation,
							this.findConstructor(clazz),
							this.findVoidMethodWithZeroParamsAndAnnotation(clazz, PostConstructor.class),
							this.findVoidMethodWithZeroParamsAndAnnotation(clazz, PreDestory.class),
							this.findBeanMethods(clazz));
					servicesStorage.add(serviceDetails);
					break;
				}
			}
		}
		return servicesStorage.stream().sorted(new ServiceDetailsConstructComparator()).collect(Collectors.toSet());
	}

	private Constructor<?> findConstructor(Class<?> cls) {
		for (Constructor<?> cts : cls.getDeclaredConstructors()) {
			if (cts.isAnnotationPresent(AutoWired.class)) {
				cts.setAccessible(true);
				return cts;
			}
		}
		return cls.getConstructors()[0];
	}

	private Method findVoidMethodWithZeroParamsAndAnnotation(Class<?> cls, Class<? extends Annotation> annotation) {
		for (Method method : cls.getDeclaredMethods()) {
			if (method.isAnnotationPresent(annotation) && method.getParameterCount() == 0
					&& (method.getReturnType() == void.class || method.getReturnType() == Void.class)) {
				method.setAccessible(true);
				return method;
			}
		}
		return null;
	}

	private Method[] findBeanMethods(Class<?> cls) {
		final Set<Class<? extends Annotation>> customBeanAnnotations = this.configuration.getCustomBeanAnnotations();
		final Set<Method> beanMethods = new HashSet<>();
		for (Method method : cls.getDeclaredMethods()) {
			if (method.getParameterCount() == 0
					&& (method.getReturnType() != void.class && method.getReturnType() != Void.class)) {

				for (Class<? extends Annotation> annotation : customBeanAnnotations) {
					if (method.isAnnotationPresent(annotation)) {
						beanMethods.add(method);
						method.setAccessible(true);
						break;
					}
				}
			}
		}
		return beanMethods.toArray(Method[]::new);
	}

}
