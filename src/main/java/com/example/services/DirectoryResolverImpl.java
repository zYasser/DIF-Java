package com.example.services;

import java.io.File;

import com.example.enums.DirectoryType;
import com.example.models.Directory;

public class DirectoryResolverImpl implements DirectoryResolver {

    private static final String JAR_FILE_EXTENSION = ".jar";
    @Override
    public Directory resolveDirectory(Class<?> startUpClass) {
        final String directory = getDirectory(startUpClass);
        final DirectoryType directoryType = getDirectoryType(directory);
        return new Directory(directory, directoryType);
    }

    private String getDirectory(Class<?> startUpClass) {
        return startUpClass.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    private DirectoryType getDirectoryType(String directory) {
        final File file = new File(directory);
        if (!file.isDirectory() && directory.endsWith(JAR_FILE_EXTENSION)) {
            return DirectoryType.JAR_PACKAGE;
        } 
        return DirectoryType.DIRECTORY;

}
}
