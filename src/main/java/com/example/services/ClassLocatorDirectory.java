package com.example.services;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.example.constants.Constant;
import com.example.exceptions.ClassLocationException;

public class ClassLocatorDirectory implements ClassLocator {
    private final Set<Class<?>> set;

    public ClassLocatorDirectory() {
        this.set = new HashSet<>();
    }

    @Override
    public Set<Class<?>> locateClasses(String startUpDirectory) throws ClassLocationException {
        this.set.clear();
        File file = new File(startUpDirectory);
        if (!file.isDirectory()) {
            throw new ClassLocationException("The startUpDirectory is not a directory");
        }

        for (File subFile : file.listFiles()) {
            try {
                this.scanDir(subFile, "");
            } catch (ClassLocationException | ClassNotFoundException e) {
                throw new ClassLocationException(e.getMessage(), e);

            }
        }

        return this.set;
    }

    private void scanDir(File file, String packageName) throws ClassLocationException, ClassNotFoundException {

        if (file.isDirectory()) {
            packageName += file.getName() + ".";
            for (File subFile : file.listFiles()) {
                this.scanDir(subFile, packageName);
            }
        } else {
            if (!file.getName().endsWith(Constant.JAV_FILE_BINARY_EXTENSION)) {
                return;
            }
            final String className = packageName + file.getName().replace(Constant.JAV_FILE_BINARY_EXTENSION, "");
            this.set.add(Class.forName(className));
        }
    }
}
