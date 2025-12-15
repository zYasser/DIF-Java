package com.example.services;

import java.lang.annotation.Annotation;

import com.example.annotations.Service;
import com.example.exceptions.AlreadyInitializedException;
import com.example.models.ServiceBeanDetails;
import com.example.models.ServiceDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class DependencyContainer implements IDependencyContainer

{
    private static final String ALREADY_INITIALIZED = "Dependency container already initialized";

    private boolean isInitialized = false;

    private List<ServiceDetails<?>> servicesAndBeans;
    private ObjectInstantiationService objectInstantiationService;

    public DependencyContainer() {
        this.isInitialized = false;
        this.servicesAndBeans = new ArrayList<>();
        this.objectInstantiationService = new ObjectInstantiationService();
    }

    @Override
    public List<ServiceDetails<?>> getServiceByAnnotation(Class<? extends Annotation> annotationType) {
        return this.servicesAndBeans.stream().filter(e -> e.getAnnotation().annotationType() == annotationType)
                .collect(Collectors.toList());

    }

    @Override
    public void init(List<ServiceDetails<?>> services, ObjectInstantiationService objectInstantiationService)
            throws AlreadyInitializedException {
        if (isInitialized) {
            throw new AlreadyInitializedException(ALREADY_INITIALIZED);
        }
        this.servicesAndBeans.addAll(services);
        this.objectInstantiationService = objectInstantiationService;
        isInitialized = true;

    }

    @Override
    public <T> void reload(ServiceDetails<T> serviceDetails, boolean reloadDependantServices) {
        this.objectInstantiationService.destroyInstance(serviceDetails);
        this.handleReload(serviceDetails);
        if (reloadDependantServices) {
            for (ServiceDetails<?> dependencies : serviceDetails.getDependencies()) {
                this.reload(dependencies, reloadDependantServices);
            }
        }
    }

    @Override
    public <T> T reload(T service) {
        return this.reload(service, false);
    }

    private void handleReload(ServiceDetails<?> serviceDetails) {
        if (serviceDetails instanceof ServiceBeanDetails<?>) {
            this.objectInstantiationService.createBeanInstance((ServiceBeanDetails<?>) serviceDetails);
        }else{
            this.objectInstantiationService.createInstance(serviceDetails , this.collectDependencies(serviceDetails));
        }
    }

    private Object[] collectDependencies(ServiceDetails<?> serviceDetails){
        int n = serviceDetails.getConstructor().getParameterCount();
        Class<?>[] paramsTypes=serviceDetails.getConstructor().getParameterTypes();
        Object[] dependenciesInstance = new Object[n];

        for(int i=0; i<n; i++){
            dependenciesInstance[i]= this.getService(paramsTypes[i]);
        }

        return dependenciesInstance;
    }

    @Override
    public <T> T reload(T service, boolean reloadDependantServices) {
        ServiceDetails<?> serviceDetails = getServiceDetails(service.getClass());
        if (serviceDetails != null) {
            this.reload(serviceDetails, reloadDependantServices);
            return (T) serviceDetails.getInstance();
        }
        return null;
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        ServiceDetails<T> service = getServiceDetails(serviceType);
        if (service != null) {
            return (T) service.getInstance();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ServiceDetails<T> getServiceDetails(Class<T> serviceType) {
        return (ServiceDetails<T>) this.servicesAndBeans.stream()
                .filter(e -> serviceType.isAssignableFrom(e.getServiceType()))
                .findFirst().orElse(null);
    }

    @Override
    public <T> void registerService(Class<T> serviceType, ServiceDetails<T> serviceDetails) {

    }

    @Override
    public List<ServiceDetails<?>> getServicesDetails() {
        return Collections.unmodifiableList(this.servicesAndBeans);
    }

    @Override
    public List<Object> getServices() {
        return servicesAndBeans.stream().map(sd -> sd.getInstance()).collect(Collectors.toList());

    }
}
