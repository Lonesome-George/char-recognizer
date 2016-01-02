package com.zzh.utils;

import java.util.ArrayList;

public class Utils {

	public static float[] ToFloatArray(ArrayList<Float> list) {
		float[] array = new float[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = list.get(i);
		}
		return array;
	}

}
