package com.playground_soft.widget;

import android.content.Context;
import android.util.AttributeSet;


public class GridView extends android.widget.GridView {

	private int childPreferredWidth = 1;
	
	public GridView(Context context) {
		super(context);
	}
	public GridView(Context context, AttributeSet attSet) {
		super(context, attSet);
	}
	

	public GridView(Context context, AttributeSet attSet, int defStyle) {
		super(context, attSet, defStyle);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int width = r-l;
		setNumColumns((int)(width/childPreferredWidth));
		super.onLayout(changed, l, t, r, b);
	}
	
	public void setChildPreferredWidth(int width){
		childPreferredWidth = width;
	}
	

}
