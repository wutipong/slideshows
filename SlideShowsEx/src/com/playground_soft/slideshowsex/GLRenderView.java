package com.playground_soft.slideshowsex;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GLRenderView extends GLSurfaceView  {
	
	public GLRenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
	}

}
