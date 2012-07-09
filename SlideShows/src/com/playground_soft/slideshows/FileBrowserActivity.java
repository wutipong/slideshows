package com.playground_soft.slideshows;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class FileBrowserActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.activity_file_browser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
