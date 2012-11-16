package com.playground_soft.slideshowsex.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.util.LinkedList;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbAuthException;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class CifsFileEntry extends FileEntry {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1039726488263454102L;
	private SmbFile file;
	private boolean isDirectory;
	private CifsFileEntry parent;

	public CifsFileEntry(SmbFile file) throws SmbException {
		this.file = file;

		isDirectory = file.isDirectory();
	}

	@Override
	public boolean isDirectory() {
		return isDirectory;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	@Override
	public String getPath() {
		return file.getPath();
	}

	@Override
	public FileEntry doGetParent() throws IOException {
		if (parent == null) {
			SmbFile parentFile = new SmbFile(file.getParent(),
					(NtlmPasswordAuthentication) file.getPrincipal());
			parent = new CifsFileEntry(parentFile);
		}
		return parent;
	}

	@Override
	public FileEntry[] doGetChildren(FileEntryFilter filter) throws IOException {
		SmbFile[] childrenFile = file.listFiles();

		LinkedList<CifsFileEntry> children = new LinkedList<CifsFileEntry>();
		for (int i = 0; i < childrenFile.length; i++) {
			CifsFileEntry child = new CifsFileEntry(childrenFile[i]);
			if (filter.accept(child))
				children.add(child);
		}
		return children.toArray(new FileEntry[children.size()]);
	}

	@Override
	public InputStream doGetInputStream() throws IOException {
		return file.getInputStream();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		//out.write(file.getPath());
		out.writeObject(file.getPath());
		out.writeObject(file.getPrincipal());
		out.flush();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		String path = (String)in.readObject();
		NtlmPasswordAuthentication auth = (NtlmPasswordAuthentication)in.readObject();
		
		file = new SmbFile(path, auth);
		isDirectory = file.isDirectory();
	}

	private void readObjectNoData() throws ObjectStreamException {
		file = null;
		isDirectory = false;
	}

	@Override
	public boolean hasParent() {
		return file.getParent() != null;
	}

}
