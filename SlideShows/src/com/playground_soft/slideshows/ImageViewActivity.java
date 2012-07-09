package com.playground_soft.slideshows;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ImageViewActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_image_view, menu);
        return true;
    }

    
}
