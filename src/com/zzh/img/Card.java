package com.zzh.img;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Card {

	public static Mat findCardNumber(String path) {
		Mat mat = Highgui.imread(path);
		int x = 0;
		int y = (int) (mat.height() * ((double) 30/54));
		int width = mat.cols();
		int height = (int) (mat.height() * ((double) 7/54));
		return mat.submat(new Rect(x, y, width, height));
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat mat = Card.findCardNumber("test/1.jpg");
		String filename = "test/card/img_card_number.png";
		System.out.println(String.format("Writing %s", filename));
		Highgui.imwrite(filename, mat);
	}
}
