package com.zzh.utils;

import java.util.Comparator;

import com.zzh.img.PosMat;

public class Sort implements Comparator {

	public int compare(Object o1, Object o2) {
		PosMat pMat1 = (PosMat)o1;
		PosMat pMat2 = (PosMat)o2;
		if (pMat1.getRect().x > pMat2.getRect().x)
			return 1;
		else
			return -1;
	}

}
