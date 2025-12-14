package com.example.models;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServiceDetails<T> {

    private  Class<T> serviceType;
    private Annotation annotation;
    private Constructor<T> constructor;
    private Object instance;
    private Method postConstructorMethod;
    private Method preDestoryMethod;
    private Method[] beanMethod;
    private final List<ServiceDetails<?>> dependencies;

    public Class<T> getServiceType() {
        return serviceType;
    }

    public ServiceDetails(Class<T> serviceType, Annotation annotation, Constructor<T> constructor,
                          Method postConstructorMethod, Method preDestoryMethod, Method[] beanMethod) {
        this();
        this.serviceType = serviceType;
        this.annotation = annotation;
        this.constructor = constructor;
        this.postConstructorMethod = postConstructorMethod;
        this.preDestoryMethod = preDestoryMethod;
        this.beanMethod = beanMethod;
    }

    public void setServiceType(Class<T> serviceType) {
        this.serviceType = serviceType;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Constructor<T> getConstructor() {
        return constructor;
    }
    public void addDependency(ServiceDetails<?> dependency) {
        this.dependencies.add(dependency);
    }

    public void setConstructor(Constructor<T> constructor) {
        this.constructor = constructor;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Method getPostConstructorMethod() {
        return postConstructorMethod;
    }

    public void setPostConstructorMethod(Method postConstructorMethod) {
        this.postConstructorMethod = postConstructorMethod;
    }

    public Method getPreDestoryMethod() {
        return preDestoryMethod;
    }

    public void setPreDestoryMethod(Method preDestoryMethod) {
        this.preDestoryMethod = preDestoryMethod;
    }

    public Method[] getBeanMethod() {
        return beanMethod;
    }

    public void setBeanMethod(Method[] beanMethod) {
        this.beanMethod = beanMethod;
    }

    public List<ServiceDetails<?>> getDependencies() {
        return Collections.unmodifiableList(this.dependencies);
    }
    public void addDependencies(ServiceDetails<?> s){
        this.dependencies.add(s);
    }

    public ServiceDetails(){
        this.dependencies = new ArrayList<>();
    }

    @Override
    public int hashCode() {
        if(this.serviceType==null){
            return super.hashCode();
        }
        return this.serviceType.hashCode();

    }

}
