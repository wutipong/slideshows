package com.playground_soft.slideshowsex;

import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.playground_soft.slideshowsex.filesystem.CifsFileEntry;
import com.playground_soft.slideshowsex.filesystem.FileEntry;
import com.playground_soft.slideshowsex.filesystem.LocalFileEntry;

public class StartFileSystemFragment 
extends Fragment 
implements OnItemClickListener, OnItemLongClickListener {
	
	private ListView lvFileSystem;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_start_file_system, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		lvFileSystem = (ListView)view.findViewById(R.id.listview_file_system_types);
		lvFileSystem.setAdapter(new ArrayAdapter<String>(getActivity(), 
				android.R.layout.simple_list_item_1, 
				new String[]{"Local File", "Windows Share"}));
		lvFileSystem.setOnItemClickListener(this);
	}
 
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(parent == lvFileSystem){
			Intent intent = new Intent(getActivity(), FileBrowserActivity.class);
			FileEntry entry = null;
			switch(position){
			case 0:
				entry = new LocalFileEntry(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
				break;
			case 1:
				try {
					entry = new CifsFileEntry(new SmbFile("smb://"));
				} catch (SmbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			intent.putExtra("file", entry);
			startActivity(intent);
		}
		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}
}
