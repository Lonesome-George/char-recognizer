package com.zzh.img;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;

public class PosMat {

	private Rect rect;
	private Mat mat;
	
	public PosMat(Rect rect, Mat mat) {
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
