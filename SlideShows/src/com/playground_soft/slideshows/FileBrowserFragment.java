package com.playground_soft.slideshows;

import java.util.Arrays;
import java.util.Comparator;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.playground_soft.widget.GridFragment;

public class FileBrowserFragment extends GridFragment {
	private ProgressDialog dialog;
	private String networkPath;
	private String domain;
	private String userid;
	private String password;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		dialog = ProgressDialog
				.show(getActivity(), "loading...", "Please Wait");
		ListFileTask task = new ListFileTask(getActivity());

		networkPath = getActivity().getIntent().getStringExtra("networkPath");
		domain = getActivity().getIntent().getStringExtra("domain");
		userid = getActivity().getIntent().getStringExtra("userid");
		password = getActivity().getIntent().getStringExtra("password");
		
		task.execute(networkPath, domain, userid, password);
	}

	@Override
	public void onListItemClick(GridView l, View v, int position, long id) {

		SmbFile file = (SmbFile) l.getItemAtPosition(position);
		try {
			if (file.isDirectory()) {
				Intent intent = new Intent(this.getActivity(),
						FileBrowserActivity.class);

				intent.putExtra("networkPath", file.getPath());
				intent.putExtra("userid", userid);
				intent.putExtra("password", password);
				intent.putExtra("domain", domain);
				
				startActivity(intent);
			} else {
				Intent intent = new Intent(this.getActivity(),
						ImageViewActivity.class);

				intent.putExtra("networkPath", file.getPath());
				intent.putExtra("userid", userid);
				intent.putExtra("password", password);
				intent.putExtra("domain", domain);
				
				startActivity(intent);
			}
		} catch (SmbException e) {
			Log.e(getTag(), "cifs", e);
		}

	}

	private class ListFileTask extends AsyncTask<String, Integer, SmbFile[]> {

		private Exception err;
		private Context context;
		
		protected ListFileTask(Context context) {
			this.context = context;
		}
		@Override
		protected SmbFile[] doInBackground(String... params) {
			try {
				String path = params[0];
				String domain = params[1];
				String userid = params[2];
				String password = params[3];
				
				SmbFile file = new SmbFile(path, new NtlmPasswordAuthentication(domain, userid, password));

				file.connect();

				SmbFileFilter filterFolder = new SmbFileFilter() {

					@Override
					public boolean accept(SmbFile arg0) throws SmbException {
						if (arg0.isDirectory())
							return true;
						else
							return false;
					}
				};
				SmbFile[] folders = file.listFiles(filterFolder);

				SmbFileFilter filterImg = new SmbFileFilter() {

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

				SmbFile[] imgFiles = file.listFiles(filterImg);
				Comparator<SmbFile> comparator = new Comparator<SmbFile>() {

					@Override
					public int compare(SmbFile lhs, SmbFile rhs) {
						return lhs.getName().compareToIgnoreCase(rhs.getName());
					}

				};

				Arrays.sort(folders, comparator);
				Arrays.sort(imgFiles, comparator);

				SmbFile[] output = new SmbFile[folders.length + imgFiles.length];
				for (int i = 0; i < output.length; i++) {
					if (i < folders.length)
						output[i] = folders[i];
					else
						output[i] = imgFiles[i - folders.length];
				}

				return output;

			} catch (Exception e) {
				err = e;
				return null;
			}
			
		}

		@Override
		protected void onPostExecute(SmbFile[] result) {
			super.onPostExecute(result);

			dialog.dismiss();
			if( err!=null ){
				String error ;
				if(err instanceof SmbException) {
					error = err.toString();
				} else {
					error = err.getMessage();
				}
					
				new AlertDialog.Builder(context)
						.setMessage(error)
						.setCancelable(false)
						.setPositiveButton("OK", new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(context, MainActivity.class);
								startActivity(intent);
							}
						})
						.create()
						.show();
				
			} 
			else {
				setListAdapter(new SmbBitmapFileAdapter(R.layout.widget_file_griditem,
						getActivity(), result));
			}
		}

	}
}
