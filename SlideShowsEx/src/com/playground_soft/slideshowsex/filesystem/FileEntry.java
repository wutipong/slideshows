package com.playground_soft.slideshowsex.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public abstract class FileEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1446213082106532127L;
	public abstract boolean isDirectory();
	
	public abstract String getName();
	public abstract String getPath();
	public abstract boolean hasParent();
	
	public final FileEntry getParent() throws IOException	{
		return doGetParent();
	}
	
	public final FileEntry[] getChildren(FileEntryFilter filter) throws IOException {
		if(!isDirectory())
			throw new IOException("File entry does not have children.");
		
		return doGetChildren(filter);
	}
	
	public final FileEntry[] getChildren() throws IOException {
		return getChildren(acceptAllFilter);
	}
	
	public final InputStream createInputStream() throws IOException {
		if(isDirectory())
			throw new IOException("Directory can not be read directly.");
		
		return doGetInputStream();
	}
	public abstract FileEntry doGetParent() throws IOException;
	
	public abstract FileEntry[] doGetChildren(FileEntryFilter filter) throws IOException;
	
	public abstract InputStream doGetInputStream() throws IOException;
	
	public interface FileEntryFilter{
		boolean accept(FileEntry entry);
	}
	private static final FileEntryFilter acceptAllFilter = new FileEntryFilter() {
		
		@Override
		public boolean accept(FileEntry entry) {
			return true;
		}
	};
}
