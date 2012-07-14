package com.playground_soft.slideshows;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

public class SmbBitmapFileAdapter implements ListAdapter {

	private Context context;
	private SmbFile[] files;
	private LruCache<Integer, Bitmap> cache;
	private Bitmap tempBitmap;
	private Bitmap folderBitmap;
	private int layout;

	SmbBitmapFileAdapter(int layout, Context context, SmbFile[] files) {
		this.layout = layout;
		this.context = context;
		this.files = files;

		cache = new BitmapCache(1024 * 512);
			
		tempBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_picture);
		folderBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_folder);
	}

	@Override
	public int getCount() {
		return files.length;
	}

	@Override
	public Object getItem(int position) {
		return files[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);

		View v = inflater.inflate(layout, parent, false);

		TextView tv1 = (TextView) v.findViewById(R.id.textView1);
		TextView tv2 = (TextView) v.findViewById(R.id.textView2);
		final ImageView ic = (ImageView) v.findViewById(R.id.iconView);
		final int pos = position;
		ic.setImageBitmap(null);
		SmbFile subFile = files[position];
		String filename = subFile.getName();
		tv1.setText(filename);

		try {
			if (subFile.isDirectory()) {
				ic.setImageBitmap(folderBitmap);
			} else {
				if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")
						|| filename.endsWith(".png")) {
					Bitmap bitmap = cache.get(position);
					if (bitmap != null) {
						ic.setImageBitmap(bitmap);
					} else {
						ic.setImageBitmap(tempBitmap);
						LoadImageTask task = new LoadImageTask(150, 150, LoadImageTask.ScaleBaseOn.ShorterSide, 
								new LoadImageTask.TaskDone() {
									
									@Override
									void onTaskDone(Bitmap bitmap) {

										cache.put(pos, bitmap);
										ic.setImageBitmap(bitmap);
										
									}
								}) ;
								
						task.execute(subFile);
					}
				}
			}
			
			if(tv2 != null){
				if(subFile.isDirectory()){
					tv2.setText("");
				} else {
					int size = subFile.getContentLength();
					String strSize;
					if(size <1024) {
						strSize = size+"B";
					} else if (size <1024*1024){
						strSize = size/1024+"KiB";
					} else if (size < 1024*1024*1024) {
						strSize = size/(1024*1024)+"MiB";
					} else { 
						strSize = size/(1024*1024*1024)+"GiB";
					}	
					tv2.setText("File size: "+strSize);
				}
			}
			
		} catch (SmbException e) {
			Log.e("cifs", "", e);
			e.printStackTrace();
		}

		return v;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return getCount() == 0;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

}
