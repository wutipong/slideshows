package com.playground_soft.slideshows;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Comparator;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

import com.playground_soft.slideshows.R;

import android.animation.LayoutTransition;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.playground_soft.widget.FrameLayout;
import com.playground_soft.widget.FrameLayout.OnSizeChangedListener;

import android.widget.ImageView;

public class ImageViewerFragment extends Fragment implements
		OnSizeChangedListener {

	private FrameLayout frame;
	private LruCache<Integer, Bitmap> cache;

	private int frameWidth = 0;
	private int frameHeight = 0;

	private SmbFile[] files = null;
	private LoadImageTask loadTask;
	
	private int position = -1;
	private String password;
	private String userid;
	private String domain;
	private String networkPath;
	private ProgressDialog progressDialog;
	private ImageView currentImageView = null;
	private PowerManager.WakeLock wakelock;
	
	public ImageViewerFragment() {
		cache = new BitmapCache(16 * 1024 * 1024);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_image_viewer, container,
				false);

		frame = (FrameLayout) v.findViewById(R.id.frame_layout);
		frame.setOnSizeChangedListener(this);
		frame.setLayoutTransition(new LayoutTransition());

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		networkPath = getActivity().getIntent().getStringExtra("networkPath");
		domain = getActivity().getIntent().getStringExtra("domain");
		userid = getActivity().getIntent().getStringExtra("userid");
		password = getActivity().getIntent().getStringExtra("password");
	}

	private class ListFileTask extends AsyncTask<String, Void, SmbFile[]> {

		private SmbFile file;
		

		@Override
		protected SmbFile[] doInBackground(String... params) {
			try {
				String path = params[0];
				String domain = params[1];
				String userid = params[2];
				String password = params[3];
				
				NtlmPasswordAuthentication auth = 
						new NtlmPasswordAuthentication(domain, userid, password);
				file = new SmbFile(path, auth);

				SmbFile parent = new SmbFile(file.getParent(), auth);

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

				SmbFile[] imgFiles = parent.listFiles(filterImg);
				Comparator<SmbFile> comparator = new Comparator<SmbFile>() {

					@Override
					public int compare(SmbFile lhs, SmbFile rhs) {
						return lhs.getName().compareToIgnoreCase(rhs.getName());
					}

				};

				Arrays.sort(imgFiles, comparator);

				return imgFiles;

			} catch (MalformedURLException e) {
				Log.e(getTag(), "cifs", e);
			} catch (IOException e) {
				Log.e(getTag(), "cifs", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(SmbFile[] result) {
			super.onPostExecute(result);

			files = result;
			position = Arrays.binarySearch(files, file,
					new Comparator<SmbFile>() {

						@Override
						public int compare(SmbFile lhs, SmbFile rhs) {
							
							return lhs.getPath().compareTo(rhs.getPath());
						}

					});
			loadTask = new LoadImageTask();
			loadTask.execute();
		}

	}

	private class LoadImageTask extends AsyncTask<Void, Void, Bitmap> {
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				Thread.sleep(5000);
				SmbFile file = files[position];
				Bitmap bitmap = BitmapFactory.decodeStream(file
						.getInputStream());
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();

				double ratio = (double) width / (double) height;
				if (ratio > 1) {
					width = frameWidth;
					height = (int) (frameWidth / ratio);
				} else {
					height = frameHeight;
					width = (int) (frameHeight * ratio);
				}

				Bitmap output = Bitmap.createScaledBitmap(bitmap, width,
						height, true);
				bitmap.recycle();
				return output;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			cache.put(position, result);
			ImageView iv = new ImageView(getActivity());
			iv.setImageBitmap(result);
			iv.setBackgroundColor(Color.BLACK);
			
			if(currentImageView != null)
				frame.removeView(currentImageView);
			frame.addView(iv);
			currentImageView = iv;
			progressDialog.dismiss();
			position++;

			loadTask = new LoadImageTask();
			loadTask.execute();
		}
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		frameWidth = frame.getWidth();
		frameHeight = frame.getHeight();
		progressDialog = ProgressDialog.show(getActivity(), "loading", "Please wait...");
		
		if(files == null) {
			new ListFileTask().execute(networkPath, domain, userid, password);
		} else {
			loadTask = new LoadImageTask();
			loadTask.execute();
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		cache.evictAll();
		
		loadTask.cancel(true);
	}
}
