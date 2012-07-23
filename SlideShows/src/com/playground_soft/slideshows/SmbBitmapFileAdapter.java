package com.playground_soft.slideshows;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
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
	private int layout;

	SmbBitmapFileAdapter(int layout, Context context, SmbFile[] files) {
		this.layout = layout;
		this.context = context;
		this.files = files;

		cache = new BitmapCache(1024 * 512);
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
			loadIcon(position, ic, pos, subFile);

			if (subFile.isDirectory())
				v.setBackgroundResource(R.drawable.bg_folder);
			else
				v.setBackgroundResource(R.drawable.bg_image);
			
			if (tv2 != null) {
				if (subFile.isDirectory()) {
					tv2.setText("");
				} else {
					int size = subFile.getContentLength();
					String strSize;
					if (size < 1024) {
						strSize = size + "B";
					} else if (size < 1024 * 1024) {
						strSize = size / 1024 + "KiB";
					} else if (size < 1024 * 1024 * 1024) {
						strSize = size / (1024 * 1024) + "MiB";
					} else {
						strSize = size / (1024 * 1024 * 1024) + "GiB";
					}
					tv2.setText("File size: " + strSize);

				}

			}
		} catch (SmbException e) {
			Log.e("cifs", "", e);
			e.printStackTrace();
		}
		return v;
	}

	private void loadIcon(int position, final ImageView ic, final int pos,
			SmbFile subFile) {
		Bitmap bitmap = cache.get(position);
		if (bitmap != null) {
			ic.setImageBitmap(bitmap);
		} else {
			ic.setImageDrawable(new ColorDrawable(
					(int) (Math.random() * Integer.MAX_VALUE) | 0xff000000));
			LoadImageTask task = new LoadImageTask(150, 150,
					LoadImageTask.ScaleBaseOn.ShorterSide,
					new LoadImageTask.TaskDone() {

						@Override
						void onTaskDone(Bitmap bitmap) {
							if (bitmap == null) {
								return;
							}

							cache.put(pos, bitmap);
							ic.setImageBitmap(bitmap);

						}
					});

			task.execute(subFile);
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

	public void trimMemory(int level) {
		cache.evictAll();
	}
}
