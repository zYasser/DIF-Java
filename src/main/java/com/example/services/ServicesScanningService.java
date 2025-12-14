package com.example.services;

import com.example.models.ServiceDetails;

import java.util.Set;

public interface ServicesScanningService
{
	Set<ServiceDetails<?>> mapServices(Set<Class<?>> locatedClasses);
}
