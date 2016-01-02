package com.zzh.img;

import java.security.Principal;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Image {

	private static int normHeight = 10;// 这里要定一个合理的阈值
	private static int normWidth = 10;

	// 将彩色图转化为灰度图
	private static Mat grayity(Mat rgbaImg) {
		Mat grayImg = new Mat();
		Imgproc.cvtColor(rgbaImg, grayImg, Imgproc.COLOR_RGB2GRAY);
		// String filename = "gray.png";
		// System.out.println(String.format("Writing %s", filename));
		// Highgui.imwrite(filename, grayImg);
		return grayImg;
	}

	// 将灰度图二值化
	private static Mat threshold(Mat grayImg) {
		Mat biImg = new Mat();
		Imgproc.threshold(grayImg, biImg, 48, 255, Imgproc.THRESH_BINARY_INV);
		// filename = "bi.png";
		// System.out.println(String.format("Writing %s", filename));
		// Highgui.imwrite(filename, biImg);
		return biImg;
	}

	// 查找所有的区块
	public static ArrayList<PosMat> findBlocks(Mat biImg) {
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat hMat = new Mat();
		Imgproc.findContours(biImg.clone(), contours, hMat,
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		// 所有区块
		ArrayList<PosMat> mats = new ArrayList<>();
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.width < normWidth || rect.height < normHeight) {
				System.err.println(String.format("width: %d, height: %d.", rect.width, rect.height));
				continue;
			}
			// 保存每部分子图
			Mat subMat = biImg.submat(new Range(rect.y, rect.y + rect.height),
					new Range(rect.x, rect.x + rect.width));
			mats.add(new PosMat(rect, subMat));
		}
		return mats;
	}

	// 查找所有的子图
	public static ArrayList<PosMat> findSubMats(String path) {
		Mat rgbaImg = Highgui.imread(path);
		Mat grayImg = grayity(rgbaImg);
		Mat biImg = threshold(grayImg);
		return findBlocks(biImg);
	}

	// 将图片大小归一化
	public static Mat normalize(Mat src) {// 这里可能图片本身的大小还小于normWidth、normHeight
		Mat dst = new Mat();
		Imgproc.resize(src, dst, new Size(normWidth, normHeight));
		return dst;
	}

	public static void main(String[] args) {

	}

}
