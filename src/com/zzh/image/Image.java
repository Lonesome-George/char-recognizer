package com.zzh.image;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import com.zzh.model.Feature;

public class Image {

	private static int normHeight = 10; // 归一化高度阈值
	private static int normWidth = 10;  // 归一化宽度阈值

	// 将彩色图转化为灰度图
	private static Mat grayity(Mat rgbaImg) {
		Mat grayImg = new Mat();
		Imgproc.cvtColor(rgbaImg, grayImg, Imgproc.COLOR_RGB2GRAY);
		String filename = "test/gray.png";
		System.out.println(String.format("Writing %s", filename));
		Highgui.imwrite(filename, grayImg);
		return grayImg;
	}

	// 将灰度图二值化
	private static Mat threshold(Mat grayImg) {
		Mat biImg = new Mat();
		Imgproc.threshold(grayImg, biImg, 50, 255, Imgproc.THRESH_BINARY_INV);
		String filename = "test/bi.png";
		System.out.println(String.format("Writing %s", filename));
		Highgui.imwrite(filename, biImg);
		return biImg;
	}

	// 查找所有的区块
	public static ArrayList<RectMat> findBlocks(Mat biImg) {
		ArrayList<MatOfPoint> contours = new ArrayList<>();
		Mat hMat = new Mat();
		Imgproc.findContours(biImg.clone(), contours, hMat,
				Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		// 所有区块
		ArrayList<RectMat> mats = new ArrayList<>();
		for (int i = 0; i < contours.size(); i++) {
			Rect rect = Imgproc.boundingRect(contours.get(i));
			if (rect.width < normWidth || rect.height < normHeight) {
				System.err.println(String.format("width: %d, height: %d.",
						rect.width, rect.height));
				continue;
			}
			// 保存每部分子图
			Mat subMat = biImg.submat(new Range(rect.y, rect.y + rect.height),
					new Range(rect.x, rect.x + rect.width));
			mats.add(new RectMat(rect, subMat));
		}
		return mats;
	}

	// 查找所有的子图
	public static ArrayList<RectMat> findSubMats(String path) {
		Mat rgbaImg = Highgui.imread(path);
		Mat grayImg = grayity(rgbaImg);
		Mat biImg = threshold(grayImg);
		return findBlocks(biImg);
	}
	
	// 查找所有的子图
		public static ArrayList<RectMat> findSubMats(Mat img) {
			Mat grayImg = grayity(img);
			Mat biImg = threshold(grayImg);
			return findBlocks(biImg);
		}

	// 将图片归一化
	public static Mat normalize(Mat src) {
		Mat dst = new Mat();
		Imgproc.resize(src, dst, new Size(normWidth, normHeight));
		return dst;
	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		ArrayList<RectMat> mats = Image.findSubMats("test/1.jpg");
		System.out.println(mats.size());
		for (int i = 0; i < mats.size(); i++) {
			String filename = "test/2/img_" + i + ".png";
			Highgui.imwrite(filename, mats.get(i).getMat());
		}
	}

}
