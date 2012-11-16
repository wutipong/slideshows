package com.playground_soft.slideshowsex;

import java.io.IOException;

import com.actionbarsherlock.app.SherlockActivity;
import com.playground_soft.slideshowsex.filesystem.FileEntry;
import com.playground_soft.slideshowsex.renderer.StillImageRenderer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class ViewerActivity extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView renderView = new GLSurfaceView(this);
        renderView.setEGLContextClientVersion(2);
        
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide(); */
        
        FileEntry file = (FileEntry)
        		getIntent().getSerializableExtra("file");
        
        if(file != null) {
        	try {
				Bitmap bitmap = BitmapFactory.decodeStream(file.createInputStream());
				 renderView.setRenderer(new StillImageRenderer(bitmap));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        
        setContentView(renderView);
    }
}
