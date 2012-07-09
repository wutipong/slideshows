package com.playground_soft.widget;

import com.playground_soft.slideshows.R;
import com.playground_soft.slideshows.R.id;
import com.playground_soft.slideshows.R.layout;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;

public class GridFragment extends Fragment implements OnItemClickListener {

	private GridView gridView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_grid_filebrowser, container, false);
		
		gridView = (GridView)v.findViewById(R.id.gridView);
		gridView.setOnItemClickListener(this);
		Configuration config = getResources().getConfiguration();
		gridView.setNumColumns(config.screenWidthDp / 150);
		
		//gridView.addOnLayoutChangeListener(this);
		return v;
	}
	
	public void setListAdapter(ListAdapter adapter) {
		gridView.setAdapter(adapter);
	}

	public void onListItemClick(GridView grid, View view, int position, long id){
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		onListItemClick((GridView) arg0, arg1, arg2, arg3);
	}

}
