package com.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.example.annotations.Startup;
import com.example.config.DIFConfig;
import com.example.enums.DirectoryType;
import com.example.models.Directory;
import com.example.models.ServiceDetails;
import com.example.services.ClassLocator;
import com.example.services.ClassLocatorDirectory;
import com.example.services.ClassLocatorJar;
import com.example.services.DependencyContainer;
import com.example.services.DirectoryResolverImpl;
import com.example.services.IDependencyContainer;
import com.example.services.ObjectInstantiationService;
import com.example.services.ServicesInstantiationService;
import com.example.services.ServicesScanningService;
import com.example.services.ServicesScanningServiceImpl;

public class DIF
{
	public static final DependencyContainer dependencyContainer;
	static {
		dependencyContainer = new DependencyContainer();
	}

	public static void run(Class<?> clazz) {

		run(clazz, new DIFConfig());
	}

	public static void run(Class<?> startUpClass, DIFConfig config) {

		ServicesScanningService servicesScanningService = new ServicesScanningServiceImpl(
				config.customAnnotationConfiguration());

		ObjectInstantiationService objectInstantiationService = new ObjectInstantiationService();

		ServicesInstantiationService servicesInstantiationService = new ServicesInstantiationService(
				config.instantiationConfiguration(), objectInstantiationService);

		final Directory directory = new DirectoryResolverImpl().resolveDirectory(startUpClass);
		ClassLocator classLocator = null;
		if (directory.getType() == DirectoryType.JAR_PACKAGE) {
			classLocator = new ClassLocatorJar();
		} else {
			classLocator = new ClassLocatorDirectory();
		}
		final Set<Class<?>> classes;
		try {
			classes = classLocator.locateClasses(directory.getDirectory());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		final Set<ServiceDetails<?>> mappedClasses = servicesScanningService.mapServices(classes);

		List<ServiceDetails<?>> instantiatedServices = servicesInstantiationService
				.instantiateServicesAndBean(mappedClasses);

		dependencyContainer.init(instantiatedServices, objectInstantiationService);
		runStartUpMethod(startUpClass);
	}

	private static void runStartUpMethod(Class<?> startUpClass) {
		ServiceDetails<?> serviceDetails = dependencyContainer.getServiceDetails(startUpClass);
		for (Method declaredMethod : serviceDetails.getServiceType().getDeclaredMethods()) {
			if (declaredMethod.getParameterCount() != 0
					|| (declaredMethod.getReturnType() != void.class
							&& declaredMethod.getReturnType() != Void.class)
					|| !declaredMethod.isAnnotationPresent(Startup.class)) {
				continue;
			} else {
				declaredMethod.setAccessible(true);
				try {
					declaredMethod.invoke(serviceDetails.getInstance());
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}

	}
}