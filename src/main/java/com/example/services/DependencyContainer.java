package com.example.services;

import com.example.exceptions.AlreadyInitializedException;
import com.example.models.ServiceDetails;

import java.util.List;

public class DependencyContainer implements IDependencyContainer
{

	@Override
	public void init(List<ServiceDetails<?>> services, ObjectInstantiationService objectInstantiationService)
			throws AlreadyInitializedException {


	}

	@Override
	public <T> void reload(ServiceDetails<T> serviceDetails, boolean reloadDependantServices) {

	}

	@Override
	public <T> T reload(T service) {
		return null;
	}

	@Override
	public <T> T reload(T service, boolean reloadDependantServices) {
		return null;
	}

	@Override
	public <T> T get(Class<T> serviceType) {
		return null;
	}

	@Override
	public <T> ServiceDetails<T> getServiceDetails(Class<T> serviceType) {
		return null;
	}

	@Override
	public <T> void registerService(Class<T> serviceType, ServiceDetails<T> serviceDetails) {

	}

	@Override
	public List<ServiceDetails<?>> getServicesDetails() {
		return List.of();
	}

	@Override
	public List<Object> getServices() {
		return List.of();
	}
}
