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

import com.zzh.image.Image;
import com.zzh.image.RectMat;
import com.zzh.utils.MatSort;
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
		KNearest knn = new KNearest(6);
		knn.loadSamples();
		knn.train();

		ArrayList<RectMat> mats = Image.findSubMats("test/card/img_card_number2.png");
		// 对区块位置排序
		Collections.sort(mats, new MatSort());
		for (int i = 0; i < mats.size(); i++) {
			String filename = "test/2/img_" + i + ".png";
			Highgui.imwrite(filename, mats.get(i).getMat());
			Mat normMat = Image.normalize(mats.get(i).getMat());
			System.out.print((int)knn.predict(Feature.featureMat(normMat)));
		}
	}
}
