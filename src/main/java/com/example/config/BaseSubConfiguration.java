package com.example.config;

/**
 * Abstract base class for all DIF sub-configurations.
 *
 * <p>This class provides common functionality for configuration subclasses
 * that are part of the hierarchical DIF configuration system. It maintains
 * a reference to the parent {@link DIFConfig} and provides fluent navigation
 * back to the parent configuration.</p>
 *
 * <h2>Purpose</h2>
 * <p>Sub-configurations extend this class to inherit:</p>
 * <ul>
 *   <li>Reference to parent configuration</li>
 *   <li>Fluent navigation with {@link #and()} method</li>
 *   <li>Type-safe parent access</li>
 * </ul>
 *
 * <h2>Usage Pattern</h2>
 * <pre>{@code
 * public class MySubConfiguration extends BaseSubConfiguration {
 *     public MySubConfiguration(DIFConfig parentConfig) {
 *         super(parentConfig);
 *     }
 *
 *     public MySubConfiguration configureSomething() {
 *         // configuration logic
 *         return this;
 *     }
 *
 *     // Use inherited and() method to navigate back
 *     public DIFConfig and() {
 *         return super.and();
 *     }
 * }
 * }</pre>
 *
 * <h2>Fluent API</h2>
 * <p>The {@link #and()} method enables fluent navigation back to the parent:</p>
 * <pre>{@code
 * config.customAnnotationConfiguration()
 *       .addCustomServiceAnnotation(MyService.class)
 *       .and()  // Navigate back to DIFConfig
 *       .build();
 * }</pre>
 *
 * @author DIF Framework
 * @version 1.0
 * @see DIFConfig
 */
public abstract class BaseSubConfiguration {

    /** Reference to the parent DIF configuration */
    private final DIFConfig parentConfig;

    /**
     * Constructs a new BaseSubConfiguration with reference to parent.
     *
     * @param parentConfig The parent DIFConfig instance, must not be null
     * @throws IllegalArgumentException if parentConfig is null
     */
    protected BaseSubConfiguration(DIFConfig parentConfig) {
        if (parentConfig == null) {
            throw new IllegalArgumentException("Parent configuration cannot be null");
        }
        this.parentConfig = parentConfig;
    }

    /**
     * Returns the parent DIFConfig for fluent navigation.
     *
     * <p>This method allows chaining back to the parent configuration
     * after configuring a sub-configuration, enabling fluent API usage.</p>
     *
     * @return The parent DIFConfig instance
     */
    public DIFConfig and() {
        return this.parentConfig;
    }
}
