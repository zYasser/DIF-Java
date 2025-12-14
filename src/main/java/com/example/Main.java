package com.example;

import java.util.List;
import java.util.Set;

import com.example.config.DIFConfig;
import com.example.enums.DirectoryType;
import com.example.models.Directory;
import com.example.models.ServiceDetails;
import com.example.services.*;
import com.example.tests.CustomAnon;
import com.example.tests.CustomBeanAnon;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
	public static void main(String[] args) {
		System.out.println("Hello, World!");
		run(Main.class);
	}

	public static void run(Class<?> clazz) {

		run(clazz, new DIFConfig().customAnnotationConfiguration().addCustomServiceAnnotation(CustomAnon.class).addCustomBeanAnnotation(
				CustomBeanAnon.class).and());
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
	}
}