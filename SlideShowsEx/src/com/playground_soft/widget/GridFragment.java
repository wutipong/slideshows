package com.playground_soft.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockFragment;
import com.playground_soft.slideshowsex.R;

public class GridFragment extends SherlockFragment implements OnItemClickListener {

	private GridView gridView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_grid_filebrowser, container, false);
		
		gridView = (GridView)v.findViewById(R.id.gridView);
		gridView.setNumColumns(5);
		gridView.setOnItemClickListener(this);
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

	public void setChildPreferredWidth(int width){
		gridView.setChildPreferredWidth(width);
	}
}
