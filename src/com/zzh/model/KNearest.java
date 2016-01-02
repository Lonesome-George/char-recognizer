package com.zzh.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvKNearest;
import org.opencv.ml.CvSVM;
import org.opencv.ml.CvSVMParams;
import org.opencv.ml.CvStatModel;

import com.zzh.img.Image;
import com.zzh.img.PosMat;
import com.zzh.utils.Sort;
import com.zzh.utils.Utils;

public class KNearest {

	private int k;
	private CvStatModel model;
	private String resource;

	private Mat trainDataMat;
	private Mat labelsMat;

	public KNearest(int k) {
		this.k = k;
		model = new CvKNearest();
		resource = "resources/charSamples";
	}

	public void loadSamples() {
		ArrayList<double[]> trainData = new ArrayList<>();
		ArrayList<Float> labels = new ArrayList<>();

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
//				ArrayList<Mat> subMats = Image.findSubMats(subFile
//						.getAbsolutePath());
//				if (subMats.size() == 0) {
//					System.err.println(subFile.getName());
//					System.err.println("训练样本中的所有子图都被过滤掉了");
//					continue;
//				}
//				if (subMats.size() > 1) {
//					System.err.println(subFile.getName());
//					System.err.println("训练样本中一张图包含多个数字");
//					String filename;
//					for (int i = 0; i < subMats.size(); i++) {
//						filename = "img_" + i + ".png";
//						System.out.println(String.format("Writing %s", filename));
//						Highgui.imwrite(filename, subMats.get(i));
//					}
//					System.exit(0);
//				}
				// 要求样本已经经过二值化切割处理，直接归一化并读取
				Mat normMat = Image.normalize(Highgui.imread(subFile.getPath()));
				double[] feature = Feature.feature(normMat);
				trainData.add(feature);
				labels.add(label);
			}
		}

		// Set up training data
		labelsMat = new Mat(labels.size(), 1, CvType.CV_32FC1);
		// System.out.println("m = " + labelsMat.dump());
		labelsMat.put(0, 0, Utils.ToFloatArray(labels));
		// System.out.println("m = " + labelsMat.dump());

		int rows = trainData.size();
		int cols = trainData.get(0).length;
		trainDataMat = new Mat(rows, cols, CvType.CV_32FC1);
		for (int i = 0; i < rows; i++) {
			trainDataMat.put(i, 0, trainData.get(i));
		}
		// System.out.println("m = " + trainDataMat.dump());
	}

	public void train(/* Mat samples, Mat responses */) {
		((CvKNearest) model).train(trainDataMat, labelsMat);
	}

	public float predict(Mat sample) {
		Mat results = new Mat();
		Mat neighborResponses = new Mat();
		Mat dists = new Mat();
		return ((CvKNearest) model).find_nearest(sample, k, results,
				neighborResponses, dists);
	}

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		KNearest knn = new KNearest(3);
		knn.loadSamples();
		knn.train();

		ArrayList<PosMat> mats = Image.findSubMats("test/card/img_card_number.png");
		// 排序
		Collections.sort(mats, new Sort());
		for (int i = 0; i < mats.size(); i++) {
			Mat normMat = Image.normalize(mats.get(i).getMat());
			System.out.print((int)knn.predict(Feature.featureMat(normMat)));
		}
	}
}
