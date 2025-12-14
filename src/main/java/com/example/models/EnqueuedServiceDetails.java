package com.example.models;

/**
 * Wrapper class that tracks the instantiation state of a service during dependency resolution.
 *
 * <p>This class is used in the queue-based dependency injection algorithm to manage services
 * that are waiting for their required dependencies to be resolved before they can be instantiated.
 * It maintains the relationship between a service's required dependency types and their actual
 * instances as they become available through the instantiation process.</p>
 *
 * <p>The class serves as a state tracker where:</p>
 * <ul>
 *   <li>{@code dependencies} contains the required parameter types from the service's constructor</li>
 *   <li>{@code dependenciesInstances} holds the actual resolved instances (initially all null)</li>
 *   <li>A service is considered "resolved" when all dependency instances are available</li>
 * </ul>
 *
 * <p>This wrapper enables the {@link com.example.services.ServicesInstantiationService} to:</p>
 * <ul>
 *   <li>Track which services are ready for instantiation</li>
 *   <li>Manage the queue of services waiting for dependencies</li>
 *   <li>Prevent circular dependency issues through controlled iteration</li>
 * </ul>
 *
 * @author DIF Framework
 * @version 1.0
 * @see com.example.services.ServicesInstantiationService
 * @see ServiceDetails
 */
public class EnqueuedServiceDetails {
    /** The service details containing metadata about the service to be instantiated */
    private final ServiceDetails<?> serviceDetails;

    /** Array of dependency types required by the service's constructor parameters */
    private final Class<?>[] dependencies;

    /** Array holding the actual resolved dependency instances, parallel to dependencies array */
    private final Object[] dependenciesInstances;

    /**
     * Creates a new EnqueuedServiceDetails wrapper for tracking service instantiation state.
     *
     * <p>Extracts the constructor parameter types as dependencies and initializes an array
     * to hold the resolved instances. Initially, all dependency instances are null,
     * indicating that no dependencies have been resolved yet.</p>
     *
     * @param serviceDetail The service details to wrap and track
     * @throws NullPointerException if serviceDetail is null
     */
    public EnqueuedServiceDetails(ServiceDetails<?> serviceDetail) {
        this.serviceDetails = serviceDetail;
        this.dependencies = serviceDetail.getConstructor().getParameterTypes();
        this.dependenciesInstances = new Object[this.dependencies.length];
    }


    /**
     * Adds a resolved dependency instance to the appropriate position in the dependencies array.
     *
     * <p>This method finds the first dependency type that is assignable from the instance's class
     * and stores the instance in the corresponding position. This allows the service to track
     * which dependencies have been resolved.</p>
     *
     * @param instance The resolved dependency instance to add
     * @throws IllegalArgumentException if no matching dependency type is found for the instance
     * @throws NullPointerException if instance is null
     */
    public void addDependencyInstance(Object instance) {
        Class<?> instanceClass = instance.getClass();
        for(int i = 0; i < this.dependencies.length; i++){
            if(this.dependencies[i].isAssignableFrom(instanceClass)){
                this.dependenciesInstances[i] = instance;
                return;
            }
        }
        throw new IllegalArgumentException("Dependency not found");

    }
    /**
     * Returns the wrapped service details.
     *
     * @return The service details being tracked for instantiation
     */
    public ServiceDetails<?> getServiceDetails() {
        return serviceDetails;
    }

    /**
     * Returns the array of dependency types required by the service.
     *
     * @return Array of Class objects representing the required dependency types
     */
    public Class<?>[] getDependencies() {
        return dependencies;
    }

    /**
     * Determines if all required dependencies have been resolved for this service.
     *
     * <p>A service is considered resolved when all positions in the dependenciesInstances
     * array contain non-null values, indicating that all required dependencies are available
     * and the service can be instantiated.</p>
     *
     * @return true if all dependencies are resolved, false if any dependency is still missing
     */
    public boolean isResolved() {
        for (int i = 0; i < this.dependencies.length; i++) {
            if (this.dependenciesInstances[i] == null) {
                return false;
            }
        }
        return true;
    }



    /**
     * Returns the resolved dependency instance at the specified index.
     *
     * @param index The index of the dependency instance to retrieve
     * @return The dependency instance at the specified index, or null if not yet resolved
     * @throws ArrayIndexOutOfBoundsException if index is out of bounds
     */
    public Object getDependencyInstance(int index) {
        return this.dependenciesInstances[index];
    }

    /**
     * Returns the complete array of resolved dependency instances.
     *
     * <p>The returned array is parallel to the dependencies array, where each position
     * contains either a resolved instance or null if the dependency is not yet available.</p>
     *
     * @return Array of dependency instances (may contain null values for unresolved dependencies)
     */
    public Object[] getDependenciesInstances() {
        return this.dependenciesInstances;
    }

    /**
     * Checks if a given dependency type is required by this service.
     *
     * <p>This method is used to determine if a newly instantiated service can satisfy
     * any of this service's dependencies, enabling the queue-based resolution algorithm.</p>
     *
     * @param dependencyType The class type to check against required dependencies
     * @return true if the type is assignable to any required dependency, false otherwise
     * @throws NullPointerException if dependencyType is null
     */
    public boolean isDependencyRequired(Class<?> dependencyType) {
        for (Class<?> dependency : this.dependencies) {
            if (dependency.isAssignableFrom(dependencyType)) {
                return true;
            }
        }
        return false;

    }
}
