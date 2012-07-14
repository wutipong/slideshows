package com.playground_soft.slideshows;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Comparator;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;

import com.playground_soft.widget.FrameLayout;
import com.playground_soft.widget.FrameLayout.OnSizeChangedListener;

public class ImageViewerFragment extends Fragment implements
		OnSizeChangedListener, OnTouchListener {

	private FrameLayout frame;
	ImageSwitcher imageSwitcher;
	int frameWidth = 0;
	int frameHeight = 0;

	SmbFile[] files = null;
	LoadImageTask loadTaskCurrent = null;

	int position = 0;
	private String password;
	private String userid;
	private String domain;
	private String networkPath;
	ProgressDialog progressDialog;
	Bitmap bitmapCurrent = null;
	Bitmap bitmapNext = null;
	Bitmap bitmapPrevious = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_image_viewer, container,
				false);

		frame = (FrameLayout) v.findViewById(R.id.frame_layout);
		frame.setOnSizeChangedListener(this);

		imageSwitcher = (ImageSwitcher) v.findViewById(R.id.image_switcher);

		v.findViewById(R.id.imageView1).setOnTouchListener(this);
		v.findViewById(R.id.imageView2).setOnTouchListener(this);

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

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		frameWidth = frame.getWidth();
		frameHeight = frame.getHeight();
		progressDialog = ProgressDialog.show(getActivity(), "loading",
				"Please wait...");

		if (files == null) {
			try {
				NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(
						domain, userid, password);
				final SmbFile file = new SmbFile(networkPath, auth);
				ListFileTask task = new ListFileTask(
						new ListFileTask.TaskDone() {

							@Override
							public void onTaskDone(SmbFile[] result) {

								files = result;
								position = Arrays.binarySearch(files, file,
										new Comparator<SmbFile>() {

											@Override
											public int compare(SmbFile lhs,
													SmbFile rhs) {

												return lhs.getPath().compareTo(
														rhs.getPath());
											}

										});
								loadTaskCurrent = new LoadImageTask(frameWidth,
										frameHeight,
										LoadImageTask.ScaleBaseOn.LongerSide,
										new LoadImageTask.TaskDone() {
											@Override
											void onTaskDone(Bitmap bitmap) {
												BitmapDrawable drawable = new BitmapDrawable(
														getResources(), bitmap);
												imageSwitcher
														.setImageDrawable(drawable);
												progressDialog.dismiss();
											}
										});
								loadTaskCurrent.execute(files[position]);
							}
						});
				task.execute(new SmbFile(file.getParent(), auth));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			loadTaskCurrent = new LoadImageTask(w, h, LoadImageTask.ScaleBaseOn.LongerSide,
					new LoadImageTask.TaskDone() {
						@Override
						void onTaskDone(Bitmap bitmap) {
							BitmapDrawable drawable = new BitmapDrawable(
									getResources(), bitmap);
							imageSwitcher.setImageDrawable(drawable);
							progressDialog.dismiss();
						}
					});
			loadTaskCurrent.execute(files[position]);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		loadTaskCurrent.cancel(true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		PointerCoords outPointerCoords = new PointerCoords();
		event.getPointerCoords(0, outPointerCoords);

		return false;
	}

	void getIndices(int current, int size, Integer previous, Integer next) {

		previous = current == 0 ? size - 1 : current - 1;
		next = current == size - 1 ? 0 : current + 1;
	}
}
