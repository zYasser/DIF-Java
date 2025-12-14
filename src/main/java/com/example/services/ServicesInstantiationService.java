package com.example.services;

import com.example.exceptions.ServiceInstantiationException;
import com.example.models.EnqueuedServiceDetails;
import com.example.models.ServiceBeanDetails;
import com.example.models.ServiceDetails;
import com.example.config.configurations.InstantiationConfiguration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedList;
import java.util.List;

/**
 * Service responsible for instantiating services and resolving their dependencies in a dependency injection framework.
 *
 * <p>This class implements a queue-based algorithm to handle the complex process of service instantiation,
 * ensuring that services are created only when all their required dependencies are available. It prevents
 * circular dependency issues by implementing a maximum iteration limit and uses reflection to create
 * service instances through the {@link ObjectInstantiationService}.</p>
 *
 * <p>The instantiation process follows these steps:</p>
 * <ol>
 *   <li>Initialize the service with all discovered services</li>
 *   <li>Validate that all required dependencies exist in the available services</li>
 *   <li>Use a queue-based approach to instantiate services when their dependencies are resolved</li>
 *   <li>Create bean instances for methods annotated with {@code @Bean}</li>
 *   <li>Return the list of successfully instantiated services</li>
 * </ol>
 *
 * <p>This service collaborates with:</p>
 * <ul>
 *   <li>{@link ObjectInstantiationService} - For actual instance creation via reflection</li>
 *   <li>{@link InstantiationConfiguration} - For configuration settings like maximum iterations</li>
 *   <li>{@link EnqueuedServiceDetails} - For tracking instantiation state of individual services</li>
 * </ul>
 *
 * @author DIF Framework
 * @version 1.0
 * @see IServicesInstantiationService
 * @see ObjectInstantiationService
 * @see InstantiationConfiguration
 */
public class ServicesInstantiationService implements IServicesInstantiationService {

    /** Error message constant for when maximum instantiation iterations are exceeded */
    private final String MAX_NUMBER_OF_ITERATIONS_EXCEEDED = "Max number of iterations exceeded for instantiation";

    /** Configuration object containing instantiation settings like maximum allowed iterations */
    private final InstantiationConfiguration instantiationConfiguration;

    /** Service responsible for creating actual object instances via reflection */
    private final ObjectInstantiationService objectInstantiationService;

    /** Queue of services waiting to be instantiated, ordered by dependency resolution state */
    private final LinkedList<EnqueuedServiceDetails> enqueuedServices;

    /** List of all available service and bean classes in the current context */
    private final List<Class<?>> allAvailableClasses;

    /** List of services that have been successfully instantiated */
    private final List<ServiceDetails<?>> instantiatedServices;

    /**
     * Constructs a new ServicesInstantiationService with the required dependencies.
     *
     * @param instantiationConfiguration Configuration object containing instantiation settings
     * @param objectInstantiationService Service for creating object instances via reflection
     * @throws IllegalArgumentException if either parameter is null
     */
    public ServicesInstantiationService(InstantiationConfiguration instantiationConfiguration,
            ObjectInstantiationService objectInstantiationService) {
        this.instantiationConfiguration = instantiationConfiguration;
        this.objectInstantiationService = objectInstantiationService;
        this.enqueuedServices = new LinkedList<>();
        this.allAvailableClasses = new ArrayList<>();
        this.instantiatedServices = new ArrayList<>();
    }

    /**
     * Instantiates all services and their associated beans in dependency order.
     *
     * <p>This method implements a queue-based dependency resolution algorithm:</p>
     * <ol>
     *   <li>Initializes the service with all mapped services</li>
     *   <li>Validates that all required dependencies are available</li>
     *   <li>Processes services in a queue, instantiating only when all dependencies are resolved</li>
     *   <li>Services with unresolved dependencies are moved to the back of the queue</li>
     *   <li>Prevents infinite loops with a maximum iteration limit</li>
     *   <li>Creates bean instances for methods annotated with {@code @Bean}</li>
     * </ol>
     *
     * @param mappedServices Set of service details discovered by the scanning service
     * @return List of successfully instantiated services and beans
     * @throws ServiceInstantiationException if dependencies cannot be resolved or instantiation fails
     * @throws ServiceInstantiationException if maximum iteration limit is exceeded (possible circular dependency)
     */
    @Override
    public List<ServiceDetails<?>> instantiateServicesAndBean(Set<ServiceDetails<?>> mappedServices)
            throws ServiceInstantiationException {
        this.init(mappedServices);
        this.checkForMissingServices(mappedServices);

        int counter = 0;
        while (!this.enqueuedServices.isEmpty()) {
            if (counter > this.instantiationConfiguration.getMaximumAllowedIterations()) {
                throw new ServiceInstantiationException(MAX_NUMBER_OF_ITERATIONS_EXCEEDED);
            }
            EnqueuedServiceDetails enqueuedService = this.enqueuedServices.poll();
            if (enqueuedService.isResolved()) {
                ServiceDetails<?> serviceDetails = enqueuedService.getServiceDetails();
                Object[] dependenciesInstances = enqueuedService.getDependenciesInstances();
                this.objectInstantiationService.createInstance(serviceDetails, dependenciesInstances);
                this.registerInstantiatedService(serviceDetails);
                this.registerBean(serviceDetails);
            } else {
                this.enqueuedServices.addLast(enqueuedService);
                counter++;
            }

        }
        return this.instantiatedServices;

    }

    /**
     * Updates dependency relationships for all instantiated services that depend on the newly created service.
     *
     * <p>When a new service is instantiated, this method checks if any existing instantiated services
     * require the new service as a dependency and updates their dependency lists accordingly.</p>
     *
     * @param newService The service that was just instantiated
     */
    private void updateDependantServices(ServiceDetails<?> newService) {
        for (Class<?> parameterType : newService.getConstructor().getParameterTypes()) {
            for (ServiceDetails<?> instantiatedServices : this.instantiatedServices) {
                if (parameterType.isAssignableFrom(instantiatedServices.getServiceType())) {
                    instantiatedServices.addDependency(newService);
                }
            }
        }
    }

    /**
     * Checks if a given class type is available among all discovered services and beans.
     *
     * <p>This method is used during dependency validation to ensure that all required
     * dependency types have corresponding service implementations available.</p>
     *
     * @param cls The class type to check for availability
     * @return true if the type is assignable from any available service/bean class, false otherwise
     */
    private boolean isAssignableTypePresent(Class<?> cls) {
        for (Class<?> serviceType : this.allAvailableClasses) {
            if (cls.isAssignableFrom(serviceType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates that all required dependencies for the mapped services are available.
     *
     * <p>This method performs a pre-instantiation check to ensure no service has unsatisfied
     * dependencies. It examines each service's constructor parameters and verifies that
     * corresponding service implementations exist in the available services list.</p>
     *
     * @param mappedServices Set of services to validate
     * @throws ServiceInstantiationException if any service has unsatisfied dependencies
     */
    private void checkForMissingServices(Set<ServiceDetails<?>> mappedServices)
            throws ServiceInstantiationException {

        for (ServiceDetails<?> service : mappedServices) {
            for (Class<?> paramType : service.getConstructor().getParameterTypes()) {
                if (!this.isAssignableTypePresent(paramType)) {
                    throw new ServiceInstantiationException(
                            "Unsatisfied dependency: " + paramType.getName() +
                                    " required by " + service.getServiceType().getName());
                }
            }
        }
    }

    /**
     * Registers and instantiates all bean methods for a given service.
     *
     * <p>Bean methods are methods annotated with {@code @Bean} that produce additional
     * objects managed by the dependency injection container. This method creates
     * {@link ServiceBeanDetails} objects for each bean method and instantiates them.</p>
     *
     * @param serviceDetails The service containing bean methods to register
     */
    private void registerBean(ServiceDetails<?> serviceDetails) {
        for (Method beanMethod : serviceDetails.getBeanMethod()) {
            ServiceBeanDetails<?> beanDetails = new ServiceBeanDetails<>(beanMethod, serviceDetails,
                    beanMethod.getReturnType());
            this.objectInstantiationService.createBeanInstance(beanDetails);
            this.registerInstantiatedService(beanDetails);
        }
    }

    /**
     * Registers a newly instantiated service and updates dependency resolution state.
     *
     * <p>This method performs several important tasks:</p>
     * <ul>
     *   <li>Adds the service to the list of instantiated services</li>
     *   <li>Updates dependency relationships (unless it's a bean)</li>
     *   <li>Notifies all queued services that a new dependency instance is available</li>
     * </ul>
     *
     * @param serviceDetails The service that was just instantiated
     */
    private void registerInstantiatedService(ServiceDetails<?> serviceDetails) {
        if (!(serviceDetails instanceof ServiceBeanDetails<?>)) {
            this.updateDependantServices(serviceDetails);
        }
        this.instantiatedServices.add(serviceDetails);
        for (EnqueuedServiceDetails enqueuedServices : this.enqueuedServices) {
            if (enqueuedServices.isDependencyRequired(serviceDetails.getServiceType())) {
                enqueuedServices.addDependencyInstance(serviceDetails.getInstance());
            }
        }

    }

    /**
     * Initializes the service with the set of mapped services for instantiation.
     *
     * <p>This method prepares the internal state for the instantiation process by:</p>
     * <ul>
     *   <li>Clearing previous state (available classes and queued services)</li>
     *   <li>Creating {@link EnqueuedServiceDetails} wrappers for all services</li>
     *   <li>Building the list of all available classes (services + their bean return types)</li>
     * </ul>
     *
     * @param mappedServices Set of services discovered by the scanning service
     */
    private void init(Set<ServiceDetails<?>> mappedServices) {
        this.allAvailableClasses.clear();
        this.enqueuedServices.clear();
        for (ServiceDetails<?> service : mappedServices) {
            this.enqueuedServices.add(new EnqueuedServiceDetails(service));
            this.allAvailableClasses.add(service.getServiceType());
            this.allAvailableClasses.addAll(
                    Arrays.stream(service.getBeanMethod())
                            .map(Method::getReturnType)
                            .collect(Collectors.toList()));

        }
    }

}
