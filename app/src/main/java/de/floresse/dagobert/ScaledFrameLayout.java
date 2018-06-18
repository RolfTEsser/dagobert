package de.floresse.dagobert;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

public class ScaledFrameLayout extends FrameLayout {

	private float scaleFactor = 1.0f;
	
	public ScaledFrameLayout(Context context) {
		super(context);
	}

	public ScaledFrameLayout(Context context, AttributeSet attrset) {
		super(context, attrset);
	}


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        
        setMeasuredDimension(
                resolveSizeAndState((int)(getMeasuredWidth() * scaleFactor), widthSpec, 0),
                resolveSizeAndState((int)(getMeasuredHeight() * scaleFactor), heightSpec, 0));

    }
    
    public void setScaleFactor(float scaleFactor) {
    	this.scaleFactor = scaleFactor;
    }
    
}
