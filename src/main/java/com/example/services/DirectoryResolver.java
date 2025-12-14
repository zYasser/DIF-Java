package com.example.services;

import com.example.models.Directory;

public interface DirectoryResolver {
    Directory resolveDirectory(Class<?> startUpClass);
}
