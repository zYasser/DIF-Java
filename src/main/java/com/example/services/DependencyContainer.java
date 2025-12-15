package com.example.services;

import java.lang.annotation.Annotation;

import com.example.exceptions.AlreadyInitializedException;
import com.example.models.ServiceBeanDetails;
import com.example.models.ServiceDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DependencyContainer manages the lifecycle and resolution of services and beans
 * in the dependency injection framework. It acts as a registry for all instantiated
 * services and provides methods to retrieve, reload, and manage service dependencies.
 *
 * This class implements the IDependencyContainer interface and provides the core
 * functionality for service resolution and dependency management.
 */
public class DependencyContainer implements IDependencyContainer

{
    /** Error message for when the container is already initialized */
    private static final String ALREADY_INITIALIZED = "Dependency container already initialized";

    /** Flag indicating whether this container has been initialized */
    private boolean isInitialized = false;

    /** List of all registered services and beans managed by this container */
    private List<ServiceDetails<?>> servicesAndBeans;

    /** Service responsible for instantiating objects and managing their lifecycle */
    private ObjectInstantiationService objectInstantiationService;

    /**
     * Constructs a new DependencyContainer with default initialization state.
     * Creates an empty service registry and initializes the object instantiation service.
     */
    public DependencyContainer() {
        this.isInitialized = false;
        this.servicesAndBeans = new ArrayList<>();
        this.objectInstantiationService = new ObjectInstantiationService();
    }

    /**
     * Retrieves all services that are annotated with the specified annotation type.
     *
     * @param annotationType the annotation class to filter services by
     * @return a list of ServiceDetails objects that have the specified annotation
     */
    @Override
    public List<ServiceDetails<?>> getServiceByAnnotation(Class<? extends Annotation> annotationType) {
        return this.servicesAndBeans.stream().filter(e -> e.getAnnotation().annotationType() == annotationType)
                .collect(Collectors.toList());

    }

    /**
     * Initializes the dependency container with a list of services and an instantiation service.
     * This method can only be called once - subsequent calls will throw an exception.
     *
     * @param services the list of service details to register in the container
     * @param objectInstantiationService the service responsible for object instantiation
     * @throws AlreadyInitializedException if the container has already been initialized
     */
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

    /**
     * Reloads a service by destroying and recreating its instance.
     * Optionally reloads all dependent services recursively.
     *
     * @param serviceDetails the service details to reload
     * @param reloadDependantServices if true, recursively reload all services that depend on this one
     * @param <T> the type of the service
     */
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

    /**
     * Reloads a service instance without reloading its dependencies.
     *
     * @param service the service instance to reload
     * @param <T> the type of the service
     * @return the reloaded service instance
     */
    @Override
    public <T> T reload(T service) {
        return this.reload(service, false);
    }

    /**
     * Handles the actual reloading of a service based on its type.
     * For beans, creates a new bean instance. For regular services, creates a new instance
     * with collected dependencies.
     *
     * @param serviceDetails the service details to reload
     */
    private void handleReload(ServiceDetails<?> serviceDetails) {
        if (serviceDetails instanceof ServiceBeanDetails<?>) {
            this.objectInstantiationService.createBeanInstance((ServiceBeanDetails<?>) serviceDetails);
        }else{
            this.objectInstantiationService.createInstance(serviceDetails , this.collectDependencies(serviceDetails));
        }
    }

    /**
     * Collects all dependency instances required for a service's constructor.
     * Resolves each parameter type to its corresponding service instance.
     *
     * @param serviceDetails the service details whose dependencies need to be collected
     * @return an array of dependency instances in the order expected by the constructor
     */
    private Object[] collectDependencies(ServiceDetails<?> serviceDetails){
        int n = serviceDetails.getConstructor().getParameterCount();
        Class<?>[] paramsTypes=serviceDetails.getConstructor().getParameterTypes();
        Object[] dependenciesInstance = new Object[n];

        for(int i=0; i<n; i++){
            dependenciesInstance[i]= this.getService(paramsTypes[i]);
        }

        return dependenciesInstance;
    }

    /**
     * Reloads a service instance by its class type, with optional dependency reloading.
     *
     * @param service the service instance to reload
     * @param reloadDependantServices if true, recursively reload dependent services
     * @param <T> the type of the service
     * @return the reloaded service instance, or null if the service type is not found
     */
    @Override
    public <T> T reload(T service, boolean reloadDependantServices) {
        ServiceDetails<?> serviceDetails = getServiceDetails(service.getClass());
        if (serviceDetails != null) {
            this.reload(serviceDetails, reloadDependantServices);
            return (T) serviceDetails.getInstance();
        }
        return null;
    }

    /**
     * Retrieves a service instance by its class type.
     *
     * @param serviceType the class type of the service to retrieve
     * @param <T> the type of the service
     * @return the service instance, or null if no service of the specified type is found
     */
    @Override
    public <T> T getService(Class<T> serviceType) {
        ServiceDetails<T> service = getServiceDetails(serviceType);
        if (service != null) {
            return (T) service.getInstance();
        }
        return null;
    }

    /**
     * Retrieves service details by service class type.
     * Uses isAssignableFrom to support inheritance and interface implementations.
     *
     * @param serviceType the class type of the service to find
     * @param <T> the type of the service
     * @return the ServiceDetails object, or null if no matching service is found
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> ServiceDetails<T> getServiceDetails(Class<T> serviceType) {
        return (ServiceDetails<T>) this.servicesAndBeans.stream()
                .filter(e -> serviceType.isAssignableFrom(e.getServiceType()))
                .findFirst().orElse(null);
    }

    /**
     * Registers a new service in the dependency container.
     * This method is currently not implemented.
     *
     * @param serviceType the class type of the service
     * @param serviceDetails the service details to register
     * @param <T> the type of the service
     */
    @Override
    public <T> void registerService(Class<T> serviceType, ServiceDetails<T> serviceDetails) {

    }

    /**
     * Returns an unmodifiable view of all registered service details.
     * Changes to the returned list are not reflected in the container.
     *
     * @return an unmodifiable list of all ServiceDetails objects
     */
    @Override
    public List<ServiceDetails<?>> getServicesDetails() {
        return Collections.unmodifiableList(this.servicesAndBeans);
    }

    /**
     * Returns a list of all service instances currently managed by the container.
     *
     * @return a list containing all instantiated service objects
     */
    @Override
    public List<Object> getServices() {
        return servicesAndBeans.stream().map(sd -> sd.getInstance()).collect(Collectors.toList());

    }
}
