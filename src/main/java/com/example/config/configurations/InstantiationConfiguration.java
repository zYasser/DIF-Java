package com.example.config.configurations;

import com.example.config.BaseSubConfiguration;
import com.example.config.DIFConfig;
import com.example.constants.Constant;

/**
 * Configuration for service instantiation behavior in the DIF framework.
 *
 * <p>This class controls how the dependency injection framework handles
 * the instantiation process, particularly focusing on preventing infinite
 * loops and circular dependencies through iteration limits.</p>
 *
 * <h2>Key Concerns</h2>
 * <ul>
 *   <li>Maximum iteration limits to prevent infinite dependency resolution loops</li>
 *   <li>Circular dependency detection through iteration counting</li>
 *   <li>Performance control for complex dependency graphs</li>
 * </ul>
 *
 * <h2>Default Values</h2>
 * <ul>
 *   <li>Maximum iterations: {@value Constant#MAXIMUM_NUMBER_OF_INSTANCES} (defined in Constant class)</li>
 * </ul>
 *
 * <h2>Circular Dependency Prevention</h2>
 * <p>The framework uses a queue-based algorithm for dependency resolution.
 * Services with unresolved dependencies are moved to the back of the queue.
 * If the maximum iteration limit is exceeded, it indicates either:</p>
 * <ul>
 *   <li>A circular dependency that cannot be resolved</li>
 *   <li>An excessively complex dependency graph</li>
 *   <li>Incorrect dependency declarations</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Basic Configuration</h3>
 * <pre>{@code
 * config.instantiationConfiguration()
 *       .setMaximumAllowedIterations(20);
 * }</pre>
 *
 * <h3>Complete Configuration Chain</h3>
 * <pre>{@code
 * DIFConfig config = new DIFConfig()
 *     .instantiationConfiguration()
 *         .setMaximumAllowedIterations(50)
 *         .and()  // Navigate back to DIFConfig
 *     .build();
 * }</pre>
 *
 * <h3>High-Complexity Applications</h3>
 * <pre>{@code
 * // For applications with complex dependency graphs
 * config.instantiationConfiguration()
 *       .setMaximumAllowedIterations(100);
 * }</pre>
 *
 * <h2>Performance Considerations</h2>
 * <ul>
 *   <li><b>Low values (5-15)</b>: Fast failure detection, suitable for simple applications</li>
 *   <li><b>Medium values (15-50)</b>: Balanced for typical enterprise applications</li>
 *   <li><b>High values (50+)</b>: For complex applications with deep dependency trees</li>
 * </ul>
 *
 * <h2>Error Handling</h2>
 * <p>When the maximum iterations are exceeded, the framework throws a
 * {@code ServiceInstantiationException} with details about the failure.
 * This allows applications to handle circular dependency issues gracefully.</p>
 *
 * @author DIF Framework
 * @version 1.0
 * @see DIFConfig
 * @see BaseSubConfiguration
 * @see Constant
 * @see com.example.services.ServicesInstantiationService
 */
public class InstantiationConfiguration extends BaseSubConfiguration {

    /** Maximum number of iterations allowed during dependency resolution */
    private int maximumAllowedIterations;

    /**
     * Constructs a new InstantiationConfiguration with default values.
     *
     * <p>Initializes the maximum allowed iterations to the default value
     * defined in {@link Constant#MAXIMUM_NUMBER_OF_INSTANCES}.</p>
     *
     * @param parentConfig The parent DIFConfig instance
     */
    public InstantiationConfiguration(DIFConfig parentConfig) {
        super(parentConfig);
        this.maximumAllowedIterations = Constant.MAXIMUM_NUMBER_OF_INSTANCES;
    }

    /**
     * Sets the maximum number of iterations allowed during dependency resolution.
     *
     * <p>This limit prevents infinite loops that can occur due to circular
     * dependencies or complex dependency graphs. Services that cannot be
     * instantiated within this limit will cause instantiation to fail.</p>
     *
     * @param maximumnNumberOfIterations The maximum number of iterations (must be positive)
     * @return This configuration instance for method chaining
     * @throws IllegalArgumentException if iterations is not positive
     */
    public InstantiationConfiguration setMaximumAllowedIterations(int maximumnNumberOfIterations) {
        if (maximumnNumberOfIterations <= 0) {
            throw new IllegalArgumentException("Maximum iterations must be positive");
        }
        this.maximumAllowedIterations = maximumnNumberOfIterations;
        return this;
    }

    /**
     * Returns the configured maximum number of iterations.
     *
     * @return The maximum allowed iterations for dependency resolution
     */
    public int getMaximumAllowedIterations() {
        return this.maximumAllowedIterations;
    }
}
