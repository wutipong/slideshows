package com.playground_soft.slideshowsexp;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GLRenderView extends GLSurfaceView implements
		GLSurfaceView.Renderer {
	
	public GLRenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
		setRenderer(this);
	}



	Sprite sprite = null;
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

		if(sprite != null)
			sprite.draw();
		
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		if(sprite != null)
			sprite.setSize(width, height);
	}



	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		sprite = Sprite.fromResource(getResources(), R.raw.image1);
	}

}
