package com.example.services;

import java.util.List;
import java.util.Set;

import com.example.exceptions.ServiceInstantiationException;
import com.example.models.ServiceDetails;

public interface IServicesInstantiationService {

    
    List<ServiceDetails<?>> instantiateServicesAndBean(Set<ServiceDetails<?>> mappedServices) throws ServiceInstantiationException;
    
}
