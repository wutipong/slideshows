package com.playground_soft.slideshowsex.renderer;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
public abstract class TransitionRenderer implements GLSurfaceView.Renderer{

	private Bitmap fromBitmap, toBitmap;
	private int width;
	private int height;
	
	public static final int DURATION = 1000;

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		this.width = width;
		this.height = height;
	}
	
	public Bitmap getFromBitmap() {
		return fromBitmap;
	}

	public void setFromBitmap(Bitmap fromBitmap) {
		this.fromBitmap = fromBitmap;
	}

	public Bitmap getToBitmap() {
		return toBitmap;
	}

	public void setToBitmap(Bitmap toBitmap) {
		this.toBitmap = toBitmap;
	}

	
	protected final int getWidth(){
		return width;
	}
	
	protected final int getHeight() {
		return height;
	}
	
	public abstract boolean isEnded();
	public abstract void start();
}
