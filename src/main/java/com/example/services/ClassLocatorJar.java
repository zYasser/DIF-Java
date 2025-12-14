package com.example.services;

import java.util.HashSet;
import java.util.Set;

import java.util.jar.JarFile;

import com.example.constants.Constant;
import com.example.exceptions.ClassLocationException;

import java.util.jar.JarEntry;
import java.io.IOException;
import java.util.Enumeration;

public class ClassLocatorJar implements ClassLocator {

    private final Set<Class<?>> classes;

    public ClassLocatorJar() {
        this.classes = new HashSet<>();
    }

    @Override
    public Set<Class<?>> locateClasses(String startUpDirectory) throws ClassLocationException {
        this.classes.clear();

        try {
            JarFile jarFile = new JarFile(startUpDirectory);

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(".class")) {
                    continue;
                }

                final String className = entry.getName().replace(Constant.JAV_FILE_BINARY_EXTENSION, "")
                        .replaceAll("/", ".").replaceAll("\\\\", ".");
                this.classes.add(Class.forName(className));

            }

        } catch (IOException | ClassNotFoundException e) {
            throw new ClassLocationException(e.getMessage(), e);
        }
        return this.classes;
    }
}
