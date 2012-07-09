package com.playground_soft.slideshows;

import java.io.IOException;

import com.playground_soft.slideshows.R;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
		ImageView ic = (ImageView) v.findViewById(R.id.iconView);
		
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
						LoadImageTask task = new LoadImageTask(ic, position,
								subFile);
						task.execute();
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

	private class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {

		private ImageView iv;
		private int position;
		private SmbFile file;

		protected LoadImageTask(ImageView iv, int position, SmbFile file) {
			this.iv = iv;
			this.position = position;
			this.file = file;
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				//options.inSampleSize = 4;
				 BitmapFactory.decodeStream(file
						.getInputStream(), null, options);
				
				int width = options.outWidth;
				int height = options.outHeight;
				
				int shorterSide = (width<height)? width : height;
				
				//double ratio = ((double) width) / ((double) height);
				int thumbSize = 150;
				int ratio = shorterSide / thumbSize;
				
				int newRatio = 1;
				for (int i = 0; ratio > newRatio; i++) {
					newRatio = (int)Math.pow(2, i);
				}
					
				options.inSampleSize = newRatio;
				options.inJustDecodeBounds = false;

				Bitmap bitmap = BitmapFactory.decodeStream(file
						.getInputStream(), null, options);
				
				return bitmap;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			cache.put(position, result);
			iv.setImageBitmap(result);
		}
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
