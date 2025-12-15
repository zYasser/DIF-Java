package com.example;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.example.annotations.Service;
import com.example.annotations.Startup;
import com.example.config.DIFConfig;
import com.example.enums.DirectoryType;
import com.example.models.Directory;
import com.example.models.ServiceDetails;
import com.example.services.*;
import com.example.tests.TestService1;
import com.example.tests.TestService2;

@Service
public class Main {
	public static final IDependencyContainer dependencyContainer;
	static {
		dependencyContainer = new DependencyContainer();
	}

	public static void main(String[] args) {
		run(Main.class);
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

		for (ServiceDetails<?> instantiatedService : instantiatedServices) {
			System.out.println("instantiatedService = " + instantiatedService.getServiceType().getTypeName());
		}
		dependencyContainer.init(instantiatedServices, objectInstantiationService);
		runStartUpMethod(startUpClass);
	}

	@Startup
	private void StartUpFunction() {
		System.out.println("Should be last function");
		dependencyContainer.reload(dependencyContainer.getService(TestService1.class) , true);

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