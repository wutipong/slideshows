package com.playground_soft.slideshows;

import android.graphics.Bitmap;
import android.util.LruCache;

public class BitmapCache extends LruCache<Integer, Bitmap> {
	public BitmapCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(Integer key, Bitmap value) {
		return value.getByteCount();
	}
	
	protected void entryRemoved (boolean evicted, 
			Integer key, Bitmap oldValue, Bitmap newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);
	}
	
}