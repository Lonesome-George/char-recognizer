package com.zzh.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import com.zzh.image.Image;
import com.zzh.image.RectMat;
import com.zzh.utils.MatSort;

public class KNN {

	private int k;
	private String resource;

	private ArrayList<double[]> trainData;
	private ArrayList<Float> labels;
	private ArrayList<Double> moduleData; // 向量的模

	public KNN(int k) {
		this.k = k;
//		resource = "resources/charSamples";
		resource = "resources/samples";
		trainData = new ArrayList<>();
		labels = new ArrayList<>();
		moduleData = new ArrayList<>();
	}

	public void loadSamples() {
		File resourceFile = new File(resource);
		File[] files = resourceFile.listFiles();
		for (File file : files) {
			// System.out.println(file.getName());
			if (file.getName().compareTo("0") < 0
					|| file.getName().compareTo("9") > 0) {
				continue;
			}
			float label = Float.valueOf(file.getName());
			File[] subFiles = new File(file.getAbsolutePath()).listFiles();
			for (File subFile : subFiles) {
				// 要求样本已经经过二值化切割处理，直接归一化并读取
				Mat normMat = Image
						.normalize(Highgui.imread(subFile.getPath()));
				double[] feature = Feature.feature(normMat);
				trainData.add(feature);
				labels.add(label);
			}
		}
	}

	private double module(double[] vector) {
		double module = 0;
		for (int i = 0; i < vector.length; i++) {
			module += vector[i] * vector[i];
		}
		return Math.sqrt(module);
	}

	// public void train() {
	// for (int i = 0; i < trainData.size(); i++) {
	// double module = module(trainData.get(i));
	// moduleData.add(module);
	// }
	// }

	public double innerProduct(double[] a, double[] b) {
		double product = 0.0;
		for (int i = 0; i < a.length; i++) {
			product += a[i] * b[i];
		}
		return product;
	}

	public double cos(double[] a, double[] b) {
		return Math.abs(innerProduct(a, b) / (module(a) * module(b)));
	}

	public float predict(double[] sample) {
		TreeMap<Integer, Double> predMap = new TreeMap<>();
		for (int i = 0; i < trainData.size(); i++) {
			predMap.put(i, cos(trainData.get(i), sample));
		}
		List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(
				predMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Entry<Integer, Double> o1,
					Entry<Integer, Double> o2) {
				return -o1.getValue().compareTo(o2.getValue());
			}
		});
		// 统计最近的k个邻居的类标
		TreeMap<Float, Integer> neighborMap = new TreeMap<>();
		int count = 0;
		for (Entry<Integer, Double> mapping : list) {
			if (++count > k)
				break;
			int key = mapping.getKey();
			neighborMap.put(labels.get(key), key);
		}
		// 查看哪个类标最多
		int max_count = -1;
		float pred_label = -1;
		for (Map.Entry<Float, Integer> mapping : neighborMap.entrySet()) {
			float label = mapping.getKey();
			int pred_count = mapping.getValue();
			if (pred_count > max_count || max_count == -1) {
				max_count = pred_count;
				pred_label = label;
			}
		}
		return pred_label;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		KNN knn = new KNN(5);
		knn.loadSamples();
		ArrayList<RectMat> mats = Image
				.findSubMats("test/card/img_card_number2.png");
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
