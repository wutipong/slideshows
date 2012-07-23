package com.playground_soft.slideshows;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

public class ImageSmbFileFilter implements SmbFileFilter{
	@Override
	public boolean accept(SmbFile arg0) throws SmbException {
		String filename = arg0.getName().toLowerCase();
		if (filename.endsWith(".jpg")
				|| filename.endsWith(".jpeg")
				|| filename.endsWith("png")) {
			return true;
		}
		return false;
	}
};