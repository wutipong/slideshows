package com.playground_soft.slideshows;

import java.io.IOException;
import java.security.InvalidParameterException;

import jcifs.smb.SmbFile;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

class LoadImageTask extends AsyncTask<SmbFile, Void, Bitmap> {
	
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
	protected Bitmap doInBackground(SmbFile... params) {
		try {
			
			if(params.length != 1)
				throw new InvalidParameterException();
			
			SmbFile file = null;
			SmbFile inputFile = params[0];
			if(inputFile.isDirectory())
			{
				SmbFile[] files = inputFile.listFiles(new ImageSmbFileFilter());
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
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			
			Bitmap bitmap = BitmapFactory.decodeStream(file
					.getInputStream(), null, options);
			int imageWidth = options.outWidth;
			int imageHeight = options.outHeight;
			
			
			int ratio = 0;
			if( baseOn == ScaleBaseOn.LongerSide){
				if(imageWidth > imageHeight) {
					ratio = imageWidth/widthTarget;
				} else {
					ratio = imageHeight/heightTarget;
				}
			} else if (baseOn == ScaleBaseOn.ShorterSide){
				if(imageWidth < imageHeight) {
					ratio = imageWidth/widthTarget;
				} else {
					ratio = imageHeight/heightTarget;
				}
			}
			
			options.inSampleSize = 1;
			for(int i = 0; options.inSampleSize < ratio ;i++ ) {
				options.inSampleSize = (int)Math.pow(2, i);
			}
			options.inSampleSize/=2;
			if(options.inSampleSize == 0)
				options.inSampleSize = 1;
			
			Log.d("inSampleSize", "" + options.inSampleSize);
			
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(file
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
		taskDone.onTaskDone(result);
	}
	
	public static abstract class TaskDone {
		abstract void onTaskDone(Bitmap bitmap);
	}
}