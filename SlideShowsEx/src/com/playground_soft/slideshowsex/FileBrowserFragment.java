package com.playground_soft.slideshowsex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.playground_soft.slideshowsex.filesystem.FileEntry;
import com.playground_soft.slideshowsex.filesystem.LocalFileEntry;
import com.playground_soft.widget.GridFragment;
import com.playground_soft.widget.GridView;

public class FileBrowserFragment extends GridFragment {
	private ProgressDialog dialog;
	private FileEntryBitmapAdapter adapter = null;
	private FileEntry currentFile = null;
	
	public enum DisplayMode{
		List, Grid
	}
	private DisplayMode displayMode;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		dialog = ProgressDialog
				.show(getActivity(), "loading...", "Please Wait");

		Bundle bundle = getArguments();
		if(bundle != null){
			currentFile = (FileEntry)bundle.getSerializable("file");
			displayMode = (DisplayMode) bundle.getSerializable("displayMode");
		}
		
		if(currentFile == null) {
			currentFile = new LocalFileEntry(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
		}
		
		if(displayMode == null)
			displayMode = DisplayMode.Grid;
	
		ListFileTask task = new ListFileTask(getActivity());
		
		int preferredWidth;
		if(displayMode == DisplayMode.Grid){
			preferredWidth = (int)getResources().getDimensionPixelOffset(R.dimen.preferred_grid_item_size);
		} else{
			preferredWidth = (int)getResources().getDimensionPixelOffset(R.dimen.preferred_detailed_item_size);
		}
		setChildPreferredWidth(preferredWidth);
		
		setHasOptionsMenu(true);
		super.onActivityCreated(savedInstanceState);
		task.execute(currentFile);
	}

	@Override
	public void onListItemClick(GridView l, View v, int position, long id) {

		FileEntry file = (FileEntry) l.getItemAtPosition(position);
		try {
			if (file.isDirectory()) {
				((FileBrowserActivity)getActivity()).setCurrentFile(file);
			} else {
				Intent intent = new Intent(this.getActivity(),
						ViewerActivity.class);
				intent.putExtra("file", file);
				startActivity(intent);
			}
		} catch (Exception e) {
			Log.e(getTag(), "cifs", e);
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.menu_item_detailed:
			((FileBrowserActivity)getActivity()).setDisplayMode(DisplayMode.List);
			return true;
		case R.id.menu_item_grid:
			((FileBrowserActivity)getActivity()).setDisplayMode(DisplayMode.Grid);
			return true;
		case R.id.menu_item_parent:
			try {
				((FileBrowserActivity)getActivity()).setCurrentFile(currentFile.getParent());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		
		inflater.inflate(R.menu.menu_fragment_file_browser, menu);
		
		menu.findItem(R.id.menu_item_detailed).setVisible(displayMode!=DisplayMode.List);
		menu.findItem(R.id.menu_item_grid).setVisible(displayMode!=DisplayMode.Grid);
		
		menu.findItem(R.id.menu_item_parent).setVisible(currentFile.hasParent());
	}
	private class ListFileTask extends AsyncTask<FileEntry, Integer, FileEntry[]> {

		private Exception exception;
		final private Context context;
		
		protected ListFileTask(Context context) {
			this.context = context;
		}
		@Override
		protected FileEntry[] doInBackground(FileEntry... params) {
			try {
				
				FileEntry file = params[0];

				FileEntry.FileEntryFilter filterFolder = new FileEntry.FileEntryFilter() {

					@Override
					public boolean accept(FileEntry arg0){
						if (arg0.isDirectory())
							return true;
						else
							return false;
					}
				};
				FileEntry[] folders = file.getChildren(filterFolder);

				FileEntry.FileEntryFilter filterImg = new ImageSmbFileFilter();
				
				FileEntry[] imgFiles = file.getChildren(filterImg);
				Comparator<FileEntry> comparator = new Comparator<FileEntry>() {

					@Override
					public int compare(FileEntry lhs, FileEntry rhs) {
						return lhs.getName().compareToIgnoreCase(rhs.getName());
					}

				};

				Arrays.sort(folders, comparator);
				Arrays.sort(imgFiles, comparator);

				ArrayList<FileEntry> output = new ArrayList<FileEntry>();
				
				for(FileEntry entry : folders)
					output.add(entry);
				
				for(FileEntry entry: imgFiles)
					output.add(entry);


				return output.toArray(new FileEntry[0]);

			} catch (Exception e) {
				exception = e;
				return null;
			}
			
		}

		@Override
		protected void onPostExecute(FileEntry[] result) {
			super.onPostExecute(result);

			dialog.dismiss();
			if( exception!=null ){
				String error ;
				error = exception.toString();
					
				new AlertDialog.Builder(context)
						.setMessage(error)
						.setCancelable(false)
						.setPositiveButton("OK", new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(context, FileBrowserActivity.class);
								startActivity(intent);
							}
						})
						.create()
						.show();
				
			} 
			else {
				
				adapter = new FileEntryBitmapAdapter(
						displayMode,
						getActivity(), result);
				setListAdapter(adapter);
			}
		}

	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		adapter.trimMemory(0);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if(adapter != null)
			adapter.trimMemory(0);
	}
}
