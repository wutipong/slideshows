package com.playground_soft.slideshowsex;

import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.playground_soft.slideshowsex.filesystem.FileEntry;
import com.playground_soft.slideshowsex.filesystem.LocalFileEntry;

public class FileBrowserActivity extends SherlockFragmentActivity {

	FileBrowserFragment.DisplayMode displayMode = FileBrowserFragment.DisplayMode.List;
	FileEntry entry = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jcifs.Config.registerSmbURLHandler();
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
        entry = (FileEntry)getIntent().getSerializableExtra("file");
        if(entry == null) {
			entry = new LocalFileEntry(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
		}
        setContentView(R.layout.activity_file_browser);
        setCurrentFile(entry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getSupportMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()){
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
    public void setCurrentFile(FileEntry entry){
    	this.entry = entry;
    	
    	FileBrowserFragment fragment = new FileBrowserFragment();
    	Bundle bundle = new Bundle();
    	bundle.putSerializable("file", entry);
    	bundle.putSerializable("displayMode", displayMode);
    	
    	fragment.setArguments(bundle);
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    	transaction.replace(R.id.framelayout1, fragment, "filebrowser");
    	transaction.addToBackStack(null);
    	transaction.commit();
    }
    
    public void setDisplayMode(FileBrowserFragment.DisplayMode showAs){
    	this.displayMode = showAs;
    	
    	FileBrowserFragment fragment = new FileBrowserFragment();
    	Bundle bundle = new Bundle();
    	bundle.putSerializable("file", entry);
    	bundle.putSerializable("displayMode", showAs);
    	
    	fragment.setArguments(bundle);
    	
    	FragmentManager fm = getSupportFragmentManager();
    	FragmentTransaction transaction = fm.beginTransaction();
    	transaction.replace(R.id.framelayout1, fragment, "filebrowser");
    	transaction.commit();
    }
}
