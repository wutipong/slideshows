package com.playground_soft.slideshowsex;

import com.playground_soft.slideshowsex.filesystem.FileEntry;
import com.playground_soft.slideshowsex.filesystem.FileEntry.FileEntryFilter;

public class ImageSmbFileFilter implements FileEntryFilter{
	@Override
	public boolean accept(FileEntry arg0) {
		String filename = arg0.getName().toLowerCase();
		if (filename.endsWith(".jpg")
				|| filename.endsWith(".jpeg")
				|| filename.endsWith("png")
				|| filename.endsWith(".gif")
				|| filename.endsWith(".bmp")) {
			return true;
		}
		return false;
	}
};