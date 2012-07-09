package com.playground_soft.widget;

import android.content.Context;
import android.util.AttributeSet;

public class FrameLayout extends android.widget.FrameLayout {
    private OnSizeChangedListener onSizeChangedListener = null;

    public FrameLayout(Context context) {
        super(context);
    }

    public FrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public FrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        if(onSizeChangedListener != null) {
            onSizeChangedListener.onSizeChanged(w, h, oldw, oldh);
        }
    }
    
    public interface OnSizeChangedListener {
        public void onSizeChanged (int w, int h, int oldw, int oldh);
    }
    
    public void setOnSizeChangedListener(FrameLayout.OnSizeChangedListener listener) {
        this.onSizeChangedListener = listener;
    }
}
