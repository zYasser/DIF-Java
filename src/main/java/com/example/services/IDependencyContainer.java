package com.example.services;

import java.lang.annotation.Annotation;
import java.util.List;

import com.example.exceptions.AlreadyInitializedException;
import com.example.models.ServiceDetails;

public interface IDependencyContainer {

    void init(List<ServiceDetails<?>> services, ObjectInstantiationService objectInstantiationService)
            throws AlreadyInitializedException;

    <T> void reload(ServiceDetails<T> serviceDetails , boolean reloadDependantServices); 

    <T> T reload(T service);
    <T> T reload(T service , boolean reloadDependantServices);
    <T> T getService(Class<T> serviceType);
    <T> ServiceDetails<T> getServiceDetails(Class<T> serviceType);
    <T> void registerService(Class<T> serviceType, ServiceDetails<T> serviceDetails);
    List<ServiceDetails<?>> getServicesDetails();
    List<Object> getServices();
    List<ServiceDetails<?>> getServiceByAnnotation(Class<? extends Annotation> annotationType);
    
    
}
