package com.zzh.utils;

import java.util.Comparator;

import com.zzh.image.RectMat;

public class MatSort implements Comparator {

	public int compare(Object o1, Object o2) {
		RectMat pMat1 = (RectMat)o1;
		RectMat pMat2 = (RectMat)o2;
		if (pMat1.getRect().x > pMat2.getRect().x)
			return 1;
		else
			return -1;
	}
}
