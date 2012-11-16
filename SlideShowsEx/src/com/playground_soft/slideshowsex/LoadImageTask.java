package com.playground_soft.slideshowsex;

import java.io.IOException;
import java.security.InvalidParameterException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.playground_soft.slideshowsex.filesystem.FileEntry;

class LoadImageTask extends AsyncTask<FileEntry, Void, BitmapData> {
	
	private final TaskDone taskDone;
	private final int widthTarget;
	private final int heightTarget;

	public enum ScaleBaseOn {ShorterSide, LongerSide};
	private final ScaleBaseOn baseOn;
	
	public LoadImageTask(int widthTarget, int heightTarget, ScaleBaseOn baseOn, TaskDone taskDone) {
		this.taskDone = taskDone;
		this.widthTarget = widthTarget;
		this.heightTarget = heightTarget;
		this.baseOn = baseOn;
	}

	@Override
	protected BitmapData doInBackground(FileEntry... params) {
		try {
			
			if(params.length != 1)
				throw new InvalidParameterException();
			
			FileEntry file = null;
			FileEntry inputFile = params[0];
			if(inputFile.isDirectory())
			{
				FileEntry[] files = inputFile.getChildren(new ImageSmbFileFilter());
				if(files.length >0) {
					
					for (int i = 0; i<files.length; i++) {
						String inFilename = files[i].getName();
						if(!inFilename.startsWith("cover.") &&
								!inFilename.startsWith("folder."))
							continue;
						file = files[i];
						break;
					}
					
					if(file == null) {
						int index = (int)(Math.random()* files.length); 
						file = files[index];
					}
				}
				else{
					return null;
				}
			} else {
				file = inputFile;
			}
			BitmapData output = new BitmapData();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(file
					.createInputStream(), null, options);
			
			output.width = options.outWidth;
			output.height = options.outHeight;
			output.mimeType = options.outMimeType;
			
			int ratio = 0;
			if( baseOn == ScaleBaseOn.LongerSide){
				if(output.width > output.height) {
					ratio = output.width/widthTarget;
				} else {
					ratio = output.height/heightTarget;
				}
			} else if (baseOn == ScaleBaseOn.ShorterSide){
				if(output.width < output.height) {
					ratio = output.width/widthTarget;
				} else {
					ratio = output.height/heightTarget;
				}
			}
			
			options.inSampleSize = 1;
			for(int i = 0; options.inSampleSize < ratio ;i++ ) {
				options.inSampleSize = (int)Math.pow(2, i);
			}
			options.inSampleSize/=2;
			if(options.inSampleSize == 0)
				options.inSampleSize = 1;
			
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(file
					.createInputStream(), null, options);

			
			output.bitmap = bitmap;
			return output;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	@Override
	protected void onPostExecute(BitmapData result) {
		super.onPostExecute(result);
		taskDone.onTaskDone(result);
	}
	
	public static abstract class TaskDone {
		abstract void onTaskDone(BitmapData result);
	}
}