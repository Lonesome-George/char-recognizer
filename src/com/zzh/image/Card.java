package com.zzh.image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;

import com.zzh.model.Feature;
import com.zzh.model.KNN;
import com.zzh.utils.MatSort;

public class Card {

	public static ArrayList<RectMat> findCardNumber(String path) {
//		Mat mat = Highgui.imread(path);
//		int x = 0;
//		int y = (int) (mat.height() * ((double) 30/54));
//		int width = mat.cols();
//		int height = (int) (mat.height() * ((double) 7/54));
//		return mat.submat(new Rect(x, y, width, height));
		
		ArrayList<RectMat> mats = Image.findSubMats(path);
		if (mats.size() < 19) return mats;
			
		Collections.sort(mats, new Comparator<RectMat>() {
			public int compare(RectMat o1, RectMat o2) {
				double area1 = o1.getRect().area();
				double area2 = o2.getRect().area();
				if (area1 < area2)
					return 1;
				else
					return -1;
			}
		});
		for (int i = 0; i < mats.size(); i++) {
			String filename = "test/1/img_" + i + ".png";
			Highgui.imwrite(filename, mats.get(i).getMat());
		}
		double tempArea = 0.0;
		// 计算排序后第五到第十个块的平均面积作为面积阈值
		for (int i = 5; i < 15; i++) {
			tempArea += mats.get(i).getRect().area();
		}
		final double thresArea = tempArea / 10;
		// 根据每个块与面积阈值的差的绝对值排序，越小越靠前
		Collections.sort(mats, new Comparator<RectMat>() {
			public int compare(RectMat o1, RectMat o2) {
				double diff1 = Math.abs(o1.getRect().area() - thresArea);
				double diff2 = Math.abs(o2.getRect().area() - thresArea);
				if (diff1 > diff2)
					return 1;
				else
					return -1;
			}
		});
		// 取出前19个块
		ArrayList<RectMat> retMats = new ArrayList<>();
		for (int i = 0; i < 19; i++) {
			retMats.add(mats.get(i));
		}
		
//		// 求均值，目的是过滤掉“银联”那一块。
//		System.out.println(mats.size());
//		double area = 0.0;
//		double avgArea = 0.0;
//		for (int i = 0; i < mats.size(); i++) {
//			Rect rect = mats.get(i).getRect();
//			area += rect.width * rect.height;
//		}
//		avgArea = area / mats.size();
//		System.out.println("average area: " + avgArea);
		
		for (int i = 0; i < retMats.size(); i++) {
			String filename = "test/2/img_" + i + ".png";
			Highgui.imwrite(filename, retMats.get(i).getMat());
		}
		return retMats;
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//		Mat mat = Card.findCardNumber("test/1.jpg");
//		String filename = "test/card/img_card_number.png";
////		Mat mat = Card.findCardNumber("test/2.jpg");
////		String filename = "test/card/img_card_number2.png";
//		System.out.println(String.format("Writing %s", filename));
//		Highgui.imwrite(filename, mat);
//		
		KNN knn = new KNN(1);
		knn.loadSamples();
////		ArrayList<PosMat> mats = Image
////				.findSubMats("test/card/img_card_number2.png");
//		ArrayList<RectMat> mats = Image.findSubMats(mat);
		ArrayList<RectMat> mats = Card.findCardNumber("test/1.jpg");
		// 对区块位置排序
		Collections.sort(mats, new MatSort());
		for (int i = 0; i < mats.size(); i++) {
			String filename = "test/2/img_" + i + ".png";
			Highgui.imwrite(filename, mats.get(i).getMat());
			Mat normMat = Image.normalize(mats.get(i).getMat());
			System.out.print((int) knn.predict(Feature.feature(normMat)));
		}
	}
}
