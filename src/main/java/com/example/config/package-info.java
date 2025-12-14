/**
 * DIF Configuration Package.
 *
 * <p>This package provides a comprehensive configuration system for the
 * Dependency Injection Framework (DIF). It implements a hierarchical,
 * fluent builder pattern that allows developers to customize various
 * aspects of the framework's behavior.</p>
 *
 * <h2>Package Overview</h2>
 *
 * <p>The configuration system is built around several key classes:</p>
 * <ul>
 *   <li>{@link com.example.config.DIFConfig} - Main configuration builder</li>
 *   <li>{@link com.example.config.BaseSubConfiguration} - Base class for sub-configurations</li>
 *   <li>{@link com.example.config.configurations.CustomAnnotationConfiguration} - Custom annotation management</li>
 *   <li>{@link com.example.config.configurations.InstantiationConfiguration} - Instantiation behavior control</li>
 * </ul>
 *
 * <h2>Key Features</h2>
 * <ul>
 *   <li><b>Fluent API</b>: Method chaining for readable configuration</li>
 *   <li><b>Hierarchical Design</b>: Sub-configurations compose within main config</li>
 *   <li><b>Type Safety</b>: Compile-time validation of configuration options</li>
 *   <li><b>Extensibility</b>: Easy to add new configuration categories</li>
 *   <li><b>Default Values</b>: Sensible defaults for all settings</li>
 * </ul>
 *
 * <h2>Basic Usage</h2>
 * <pre>{@code
 * DIFConfig config = new DIFConfig()
 *     .customAnnotationConfiguration()
 *         .addCustomServiceAnnotation(MyService.class)
 *         .and()
 *     .instantiationConfiguration()
 *         .setMaximumAllowedIterations(50)
 *         .and()
 *     .build();
 * }</pre>
 *
 * <h2>Integration Points</h2>
 * <p>The configuration is used by:</p>
 * <ul>
 *   <li>{@link com.example.Main} - Application entry point</li>
 *   <li>{@link com.example.services.ServicesScanningServiceImpl} - Service discovery</li>
 *   <li>{@link com.example.services.ServicesInstantiationService} - Dependency resolution</li>
 * </ul>
 *
 * <h2>Documentation</h2>
 * <ul>
 *   <li>{@code README.md} - Comprehensive package documentation</li>
 *   <li>{@code USAGE_EXAMPLES.md} - Practical usage examples</li>
 *   <li>Class-level Javadoc - Detailed API documentation</li>
 * </ul>
 *
 * @author DIF Framework
 * @version 1.0
 * @since 1.0
 */
package com.example.config;


