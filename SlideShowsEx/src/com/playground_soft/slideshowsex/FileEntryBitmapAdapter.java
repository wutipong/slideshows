package com.playground_soft.slideshowsex;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.playground_soft.slideshowsex.filesystem.FileEntry;

public class FileEntryBitmapAdapter implements ListAdapter{

	private final Context context;
	private final FileEntry[] files;
	private final BitmapCache cache;
	private final int layout;
	private final SparseArrayCompat<View> itemViews;

	private final int thumbWidth;
	private final int thumbHeight;
	
	//private ViewMode mode;
	FileEntryBitmapAdapter(FileBrowserFragment.DisplayMode mode, Context context, FileEntry[] files) {
		//this.mode = mode;
		switch(mode) {
		case List :
			this.layout = R.layout.widget_file_listitem;
			thumbWidth = (int) context.getResources().getDimension(R.dimen.detailed_icon_size);
			thumbHeight = (int) context.getResources().getDimension(R.dimen.detailed_icon_size);
			break;
		case Grid:
			this.layout = R.layout.widget_file_griditem;
			thumbWidth = (int) context.getResources().getDimension(R.dimen.grid_icon_size);
			thumbHeight = (int) context.getResources().getDimension(R.dimen.grid_icon_size);
			break;
		default:
			this.layout = 0;
			thumbWidth = 0;
			thumbHeight = 0;
			
			break;
		}
		
		this.context = context;
		this.files = files;

		cache = new BitmapCache(4 * 1024 * 1024);
		itemViews = new SparseArrayCompat<View>();
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
		itemViews.put(position, v);
		
		TextView tv1 = (TextView) v.findViewById(R.id.textView1);
		TextView tv2 = (TextView) v.findViewById(R.id.textView2);
		final ImageView ic = (ImageView) v.findViewById(R.id.iconView);
		final int pos = position;
		ic.setImageBitmap(null);
		FileEntry subFile = files[position];
		String filename = subFile.getName();
		
		tv1.setText(filename);

		if(subFile.isDirectory())
			tv1.setCompoundDrawablesWithIntrinsicBounds(
					context.getResources().getDrawable(R.drawable.icon_folder),
					null, null, null);
		else
			tv1.setCompoundDrawablesWithIntrinsicBounds(
					context.getResources().getDrawable(R.drawable.icon_picture), 
					null, null, null);
		
		try {
			if (tv2 != null) {
				if (subFile.isDirectory()) {
					tv2.setText(String.format("%1$,d files", subFile.getChildren().length));
				} else {
					tv2.setText("");
				}

			}
			if(subFile.isDirectory())
				loadIcon(position, ic, null, pos, subFile);
			else
				loadIcon(position, ic, tv2, pos, subFile);

			
		} catch (Exception e) {
			Log.e("error", "", e);
			e.printStackTrace();
		}
		return v;
	}

	private void loadIcon(int position, final ImageView ic, final TextView tv, final int pos,
			FileEntry file) {
		BitmapData bitmapData = cache.get(position);
		if (bitmapData != null) {
			ic.setImageBitmap(bitmapData.bitmap);
			if(tv != null)
				tv.setText(String.format("%1$s %2$d x %3$d.", bitmapData.mimeType, bitmapData.width, bitmapData.height));
		} else {
			ic.setImageDrawable(new ColorDrawable(
					(int) (Math.random() * Integer.MAX_VALUE) | 0xff000000));
			LoadImageTask task = new LoadImageTask(thumbWidth, thumbHeight,
					LoadImageTask.ScaleBaseOn.ShorterSide,
					new LoadImageTask.TaskDone() {

						@Override
						void onTaskDone(BitmapData data) {
							if (data == null) {
								return;
							}

							cache.put(pos, data);
							ic.setImageBitmap(data.bitmap);
							if(tv != null)
								tv.setText(String.format("%1$s %2$d x %3$d.", data.mimeType, data.width, data.height));
						}
					});

			task.execute(file);
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
