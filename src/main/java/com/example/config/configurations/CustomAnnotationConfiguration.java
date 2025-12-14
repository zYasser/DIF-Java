package com.example.config.configurations;

import com.example.config.BaseSubConfiguration;
import com.example.config.DIFConfig;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.lang.annotation.Annotation;

/**
 * Configuration for custom service and bean annotations in the DIF framework.
 *
 * <p>This class allows developers to register custom annotations that should be
 * treated as service or bean markers by the dependency injection framework.
 * By default, the framework recognizes {@code @Service} and {@code @Bean} annotations,
 * but this configuration enables extension with custom annotations.</p>
 *
 * <h2>Annotation Types</h2>
 * <p>The configuration manages two types of annotations:</p>
 * <ul>
 *   <li><b>Service Annotations</b>: Mark classes as services to be instantiated by the DI container</li>
 *   <li><b>Bean Annotations</b>: Mark methods as bean producers within service classes</li>
 * </ul>
 *
 * <h2>Default Behavior</h2>
 * <p>The framework automatically registers:</p>
 * <ul>
 *   <li>{@code @Service} as a service annotation</li>
 *   <li>{@code @Bean} as a bean annotation</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>Adding Single Annotations</h3>
 * <pre>{@code
 * config.customAnnotationConfiguration()
 *       .addCustomServiceAnnotation(MyService.class)
 *       .addCustomBeanAnnotation(MyBean.class);
 * }</pre>
 *
 * <h3>Adding Multiple Annotations</h3>
 * <pre>{@code
 * config.customAnnotationConfiguration()
 *       .addCustomServiceAnnotations(ServiceA.class, ServiceB.class)
 *       .addCustomBeanAnnotationS(BeanA.class, BeanB.class);
 * }</pre>
 *
 * <h3>Complete Configuration with Navigation</h3>
 * <pre>{@code
 * DIFConfig config = new DIFConfig()
 *     .customAnnotationConfiguration()
 *         .addCustomServiceAnnotation(MyCustomService.class)
 *         .addCustomBeanAnnotation(MyCustomBean.class)
 *         .and()  // Navigate back to DIFConfig
 *     .build();
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 * <p>This class is not thread-safe. Configuration should be performed in a single thread
 * before framework initialization. The underlying {@code HashSet} storage does not
 * provide thread safety guarantees.</p>
 *
 * <h2>Validation</h2>
 * <p>The configuration does not validate annotation classes at registration time.
 * Invalid or non-annotation classes will be ignored during service scanning.
 * Consider validating annotations before registration in production code.</p>
 *
 * @author DIF Framework
 * @version 1.0
 * @see DIFConfig
 * @see BaseSubConfiguration
 * @see com.example.annotations.Service
 * @see com.example.annotations.Bean
 */
public class CustomAnnotationConfiguration extends BaseSubConfiguration {

    /** Set of custom service annotations registered with the framework */
    private final Set<Class<? extends Annotation>> customSerivceAnnotations = new HashSet<>();

    /** Set of custom bean annotations registered with the framework */
    private final Set<Class<? extends Annotation>> customBeanAnnotations = new HashSet<>();

    /**
     * Constructs a new CustomAnnotationConfiguration.
     *
     * @param parentConfig The parent DIFConfig instance
     */
    public CustomAnnotationConfiguration(DIFConfig parentConfig) {
        super(parentConfig);
    }

    /**
     * Adds a single custom service annotation.
     *
     * <p>Classes annotated with this annotation will be treated as services
     * and instantiated by the dependency injection framework.</p>
     *
     * @param customServiceAnnotation The annotation class to register
     * @return This configuration instance for method chaining
     * @throws IllegalArgumentException if annotation is null
     */
    public CustomAnnotationConfiguration addCustomServiceAnnotation(Class<? extends Annotation> customServiceAnnotation) {
        if (customServiceAnnotation == null) {
            throw new IllegalArgumentException("Service annotation cannot be null");
        }
        this.customSerivceAnnotations.add(customServiceAnnotation);
        return this;
    }

    /**
     * Adds multiple custom service annotations.
     *
     * <p>Classes annotated with any of these annotations will be treated as services.</p>
     *
     * @param customServiceAnnotation Varargs array of annotation classes to register
     * @return This configuration instance for method chaining
     * @throws IllegalArgumentException if any annotation is null
     */
    public CustomAnnotationConfiguration addCustomServiceAnnotations(Class<? extends Annotation>... customServiceAnnotation) {
        if (customServiceAnnotation == null) {
            throw new IllegalArgumentException("Service annotations array cannot be null");
        }
        for (Class<? extends Annotation> annotation : customServiceAnnotation) {
            if (annotation == null) {
                throw new IllegalArgumentException("Individual service annotation cannot be null");
            }
        }
        this.customSerivceAnnotations.addAll(Arrays.asList(customServiceAnnotation));
        return this;
    }

    /**
     * Adds a single custom bean annotation.
     *
     * <p>Methods annotated with this annotation will be treated as bean producers
     * and their return values will be registered as injectable dependencies.</p>
     *
     * @param customBeanAnnotation The annotation class to register
     * @return This configuration instance for method chaining
     * @throws IllegalArgumentException if annotation is null
     */
    public CustomAnnotationConfiguration addCustomBeanAnnotation(Class<? extends Annotation> customBeanAnnotation) {
        if (customBeanAnnotation == null) {
            throw new IllegalArgumentException("Bean annotation cannot be null");
        }
        this.customBeanAnnotations.add(customBeanAnnotation);
        return this;
    }

    /**
     * Adds multiple custom bean annotations.
     *
     * <p>Methods annotated with any of these annotations will be treated as bean producers.</p>
     *
     * @param customBeanAnnotation Varargs array of annotation classes to register
     * @return This configuration instance for method chaining
     * @throws IllegalArgumentException if any annotation is null
     */
    public CustomAnnotationConfiguration addCustomBeanAnnotationS(Class<? extends Annotation>... customBeanAnnotation) {
        if (customBeanAnnotation == null) {
            throw new IllegalArgumentException("Bean annotations array cannot be null");
        }
        for (Class<? extends Annotation> annotation : customBeanAnnotation) {
            if (annotation == null) {
                throw new IllegalArgumentException("Individual bean annotation cannot be null");
            }
        }
        this.customBeanAnnotations.addAll(Arrays.asList(customBeanAnnotation));
        return this;
    }

    /**
     * Returns an unmodifiable view of registered service annotations.
     *
     * <p>This includes both default annotations (@Service) and any custom
     * annotations registered via the add methods.</p>
     *
     * @return Set of registered service annotation classes
     */
    public Set<Class<? extends Annotation>> getCustomServiceAnnotations() {
        return this.customSerivceAnnotations;
    }

    /**
     * Returns an unmodifiable view of registered bean annotations.
     *
     * <p>This includes both default annotations (@Bean) and any custom
     * annotations registered via the add methods.</p>
     *
     * @return Set of registered bean annotation classes
     */
    public Set<Class<? extends Annotation>> getCustomBeanAnnotations() {
        return this.customBeanAnnotations;
    }
}
