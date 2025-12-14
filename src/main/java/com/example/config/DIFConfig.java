package com.example.config;

import com.example.config.configurations.CustomAnnotationConfiguration;
import com.example.config.configurations.InstantiationConfiguration;

/**
 * Main configuration builder for the DIF (Dependency Injection Framework).
 *
 * <p>This class serves as the central configuration entry point for customizing
 * framework behavior. It implements a fluent builder pattern that allows
 * configuration of various aspects of the dependency injection process.</p>
 *
 * <p>The configuration system is hierarchical, with this class composing
 * specialized sub-configurations for different concerns:</p>
 * <ul>
 *   <li>{@link CustomAnnotationConfiguration} - For custom service and bean annotations</li>
 *   <li>{@link InstantiationConfiguration} - For instantiation behavior settings</li>
 * </ul>
 *
 * <h2>Basic Usage</h2>
 * <pre>{@code
 * // Create default configuration
 * DIFConfig config = new DIFConfig();
 *
 * // Access and configure sub-configurations
 * config.customAnnotationConfiguration()
 *       .addCustomServiceAnnotation(MyService.class);
 *
 * // Build final configuration
 * DIFConfig finalConfig = config.build();
 * }</pre>
 *
 * <h2>Advanced Usage</h2>
 * <pre>{@code
 * DIFConfig config = new DIFConfig()
 *     .customAnnotationConfiguration()
 *         .addCustomServiceAnnotation(MyService.class)
 *         .and()  // Navigate back to parent
 *     .build();
 * }</pre>
 *
 * @author DIF Framework
 * @version 1.0
 * @see CustomAnnotationConfiguration
 * @see InstantiationConfiguration
 */
public class DIFConfig {

    /** Configuration for custom service and bean annotations */
    private final CustomAnnotationConfiguration customAnnotationConfiguration;


    /** Configuration for instantiation behavior */
    public final InstantiationConfiguration instantiationConfiguration;

    /**
     * Constructs a new DIFConfig with default settings.
     *
     * <p>Initializes all sub-configurations with their default values.
     * Currently creates a {@link CustomAnnotationConfiguration} instance.</p>
     */
    public DIFConfig() {
        this.customAnnotationConfiguration = new CustomAnnotationConfiguration(this);
        this.instantiationConfiguration = new InstantiationConfiguration(this);
    }

    /**
     * Returns the custom annotation configuration for modification.
     *
     * <p>Use this method to configure which annotations should be treated
     * as service or bean markers by the framework.</p>
     *
     * @return The custom annotation configuration instance
     */
    public CustomAnnotationConfiguration customAnnotationConfiguration() {
        return this.customAnnotationConfiguration;
    }
    /**
     * Returns the instantiation configuration for modification.
     *
     * <p>Use this method to configure the instantiation behavior of the framework.</p>
     *
     * @return The instantiation configuration instance
     */
    public InstantiationConfiguration instantiationConfiguration() {
        return this.instantiationConfiguration;
    }

    /**
     * Builds and returns the final configuration instance.
     *
     * <p>This method validates the configuration and returns the configured
     * instance. In the current implementation, no validation is performed,
     * but this method provides a clear termination point for the fluent API.</p>
     *
     * @return The fully configured DIFConfig instance
     */
    public DIFConfig build() {
        return this;
    }
}