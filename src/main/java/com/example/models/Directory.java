package com.example.models;

import com.example.enums.DirectoryType;

public class Directory {
    private final String directory;
    private final DirectoryType type;

	public Directory(String directory, DirectoryType type) {
		this.directory = directory;
		this.type = type;
	}

	public String getDirectory() {
        return directory;
    }

    public DirectoryType getType() {
        return type;
    }
}
