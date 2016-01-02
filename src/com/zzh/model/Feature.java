package com.zzh.model;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class Feature {

	public static double[] feature(Mat mat) {
		int rows = mat.rows();
		int cols = mat.cols();
		double[] feat = new double[rows * cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double[] data = mat.get(i, j); // 已经变成单通道,所以data只包含一个元素
				feat[i * cols + j] = data[0];
			}
		}
		return feat;
	}

	public static Mat featureMat(Mat mat) {
		double[] feature = feature(mat);
		Mat featureMat = new Mat(1, feature.length, CvType.CV_32FC1);
		featureMat.put(0, 0, feature);
		return featureMat;
	}

	public static void main(String[] args) {

	}

}
