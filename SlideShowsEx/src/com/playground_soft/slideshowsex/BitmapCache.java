package com.playground_soft.slideshowsex;

import android.support.v4.util.LruCache;

public class BitmapCache extends LruCache<Integer, BitmapData> {
	
	public BitmapCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(Integer key, BitmapData value) {
		return value.bitmap.getByteCount();
	}
	
	@Override
	protected void entryRemoved (boolean evicted, 
			Integer key, BitmapData oldValue, BitmapData newValue) {
		super.entryRemoved(evicted, key, oldValue, newValue);
		
		oldValue.bitmap.recycle();
	}
	
}