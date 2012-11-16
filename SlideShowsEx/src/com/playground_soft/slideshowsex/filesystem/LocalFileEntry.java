package com.playground_soft.slideshowsex.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

public class LocalFileEntry extends FileEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6377827921537440250L;
	private File file;
	private LocalFileEntry parent;
	private String path;
	private String name;
	private final boolean isDirectory;

	public LocalFileEntry(File file) {
		this.file = file;
		isDirectory = file.isDirectory();
	}

	@Override
	public boolean isDirectory() {
		return isDirectory;
	}

	@Override
	public String getName() {
		name = file.getName();
		return name;
	}

	@Override
	public String getPath() {
		if (path == null) {
			path = file.getAbsolutePath();
		}
		return path;
	}

	@Override
	public FileEntry doGetParent() {
		if (parent == null) {
			parent = new LocalFileEntry(file.getParentFile());
		}
		return parent;
	}

	@Override
	public FileEntry[] doGetChildren(FileEntryFilter filter) {
		LinkedList<LocalFileEntry> children = new LinkedList<LocalFileEntry>();
		File[] files = file.listFiles();
		if( files == null)
			return new LocalFileEntry[0];
		for (int i = 0; i < files.length; i++) {
			LocalFileEntry child = new LocalFileEntry(files[i]);
			if(filter.accept(child))
				children.add(child);
		}
		return children.toArray(new LocalFileEntry[children.size()]);
	}

	@Override
	public InputStream doGetInputStream() throws IOException {
		return new FileInputStream(file);
	}

	@Override
	public boolean hasParent() {
		return file.getParentFile() != null;
	}

}
