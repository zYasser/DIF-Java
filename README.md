# DIF - Dependency Injection Framework

A lightweight, annotation-driven dependency injection framework for Java applications built with Java 17.

## Overview

DIF provides a simple yet powerful way to manage dependencies and lifecycle in Java projects through annotations. It automatically discovers services, resolves dependencies, and manages object lifecycles.

## Key Features

- **Annotation-Driven**: Uses `@Service`, `@AutoWired`, `@Bean`, `@PostConstructor`, `@PreDestroy`, and `@Startup` annotations
- **Automatic Discovery**: Scans JAR files or directories to find annotated services
- **Dependency Injection**: Constructor-based injection with automatic resolution
- **Lifecycle Management**: Supports initialization and cleanup hooks
- **Configuration System**: Flexible configuration through fluent API
- **Startup Execution**: Automatic execution of startup methods

## Quick Usage

```java
@Service
public class UserService {
    private final DatabaseService database;

    @AutoWired
    public UserService(DatabaseService database) {
        this.database = database;
    }

    @PostConstructor
    private void init() {
        System.out.println("UserService initialized");
    }
}

@Service
public class Application {
    @Startup
    private void start() {
        System.out.println("Application started!");
    }
}

// Run the application
Main.run(Application.class);
```

## Architecture

The framework consists of core services for scanning, instantiation, dependency resolution, and lifecycle management. It supports both JAR-based and directory-based class loading with comprehensive error handling.

## Requirements

- Java 17+
- Maven 3.6+
