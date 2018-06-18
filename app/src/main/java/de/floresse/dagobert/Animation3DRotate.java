package de.floresse.dagobert;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class Animation3DRotate extends Animation {
	float centerX, centerY;
	int duration, degrees;
	boolean rotateBack;
	Camera camera = new Camera();
	
	public Animation3DRotate(float centerX, float centerY, int duration, int degrees, boolean rotateBack) {
		this.centerX = centerX;
		this.centerY = centerY;
		this.duration = duration;
		this.degrees = degrees;
		this.rotateBack = rotateBack;
	}
	
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		setDuration(duration);
		setFillAfter(false);
		setInterpolator(new LinearInterpolator());
	}  // end initialize
	
	@Override
	public void applyTransformation(float interpolatedTime, Transformation t) {
		final Matrix matrix = t.getMatrix();
		camera.save();
		//    kleiner damits noch passt : 
		//camera.translate(0.0f, 0.0f, (300 - 300 * interpolatedTime));
		if(rotateBack) {
			if (interpolatedTime<0.5) {
				camera.rotateY(degrees * 2 * interpolatedTime);
			} else {
				camera.rotateY((degrees * 2) - (degrees * 2 * interpolatedTime));
			}
		} else {
			camera.rotateY(degrees * interpolatedTime);
		}
		camera.getMatrix(matrix);
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
		camera.restore();
		
	}  // end applyTransformation
	
}  // end class 
