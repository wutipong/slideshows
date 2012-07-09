package com.playground_soft.slideshows;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ConnectionSetupFragment extends DialogFragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(
				R.layout.fragment_connection_setup, 
				container, false);
		((Button)v.findViewById(R.id.button_ok)).setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.button_ok) {
			String networkPath = ((EditText)getView().findViewById(R.id.edit_network_path)).
					getText().toString();
			
			String domain = ((EditText)getView().findViewById(R.id.edit_domain)).
					getText().toString();
			String userid = ((EditText)getView().findViewById(R.id.edit_userid)).
					getText().toString();
			String password = ((EditText)getView().findViewById(R.id.edit_password)).
					getText().toString();
			
			if(!networkPath.startsWith("smb://"))
				networkPath = "smb://" + networkPath;
			
			if(!networkPath.endsWith("/"))
				networkPath = networkPath + "/";
			
			Intent intent = new Intent(getActivity(), FileBrowserActivity.class);
			intent.putExtra("networkPath", networkPath);
			intent.putExtra("userid", userid);
			intent.putExtra("password", password);
			intent.putExtra("domain", domain);
			
			startActivity(intent);
		}
		
	}
}
