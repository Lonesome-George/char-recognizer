package com.zzh.image;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;

public class RectMat {

	private Rect rect;
	private Mat mat;
	
	public RectMat(Rect rect, Mat mat) {
		this.rect = rect;
		this.mat = mat;
	}
	
	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}
	
	public Mat getMat() {
		return mat;
	}
	
	public void setMat(Mat mat) {
		this.mat = mat;
	}
	
	
}
