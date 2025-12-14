package com.example.services;

import java.util.Set;

import com.example.exceptions.ClassLocationException;

public interface ClassLocator {
    Set<Class<?>> locateClasses(String startUpDirectory) throws ClassLocationException, ClassNotFoundException;
}