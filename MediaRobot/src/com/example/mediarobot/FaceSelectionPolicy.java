package com.example.mediarobot;

import android.util.FloatMath;

public class FaceSelectionPolicy {

	public static final int CENTER = 0;
	public static final int MOST_CLOSE = 1;
	public static final int MEAN = 2;

	public static int POLICY = CENTER;

	private static FaceWrapper LAST_SELECTION;
	private static long LAST_SELECTION_TIME = 0;

	public static FaceWrapper select(FaceWrapper faces[]) {
		FaceWrapper result = null;
		if (POLICY == CENTER) {
			result = selectCenter(faces);
		} else if (POLICY == MOST_CLOSE) {
			result = selectMostClose(faces);
		} else if (POLICY == MEAN) {
			result = selectMean(faces);
		}
		result = reduceError(result);
		return result;
	}
	
	private static FaceWrapper selectCenter(FaceWrapper faces[]) {
		FaceWrapper result = null;
		float distance = Float.MAX_VALUE;
		for (FaceWrapper face : faces) {
			float iDistance = FloatMath.sqrt(FloatMath.pow(face.getX(), 2) + FloatMath.pow(face.getY(), 2));
			if (iDistance < distance) {
				distance = iDistance;
				result = face;
			}
		}
		return result;
	}
	
	private static FaceWrapper selectMostClose(FaceWrapper faces[]) {
		FaceWrapper result = null;
		int z = Integer.MAX_VALUE;
		for (FaceWrapper face : faces) {
			int iz = face.getZ();
			if (iz < z) {
				z = iz;
				result = face;
			}
		}
		return result;
	}
	
	private static FaceWrapper selectMean(FaceWrapper faces[]) {
		FaceWrapper result = null;
		if (faces.length == 0) {
			result = new FaceWrapper();
			result.setX(0);
			result.setY(0);
			result.setZ(0);
		} else {
			int x = 0;
			int y = 0;
			int z = 0;
			for (FaceWrapper face : faces) {
				x += face.getX();
				y += face.getY();
				z += face.getZ();
			}
			x /= faces.length;
			y /= faces.length;
			z /= faces.length;
			result = new FaceWrapper();
			result.setX(x);
			result.setY(y);
			result.setZ(z);
		}
		return result;
	}
	
	private static FaceWrapper reduceError(FaceWrapper result) {
		long selectionTime = System.currentTimeMillis();
		if (LAST_SELECTION != null && result != null) {
			float distance = FloatMath.sqrt(FloatMath.pow(LAST_SELECTION.getX() - result.getX(), 2) + FloatMath.pow(LAST_SELECTION.getY() - result.getY(), 2) + FloatMath.pow(LAST_SELECTION.getZ() - result.getZ(), 2));
			if (distance > 30) {
				if (selectionTime - LAST_SELECTION_TIME > 2000) {
					LAST_SELECTION_TIME = selectionTime;
					LAST_SELECTION = result;
				} else {
					result = LAST_SELECTION;
					result.setX(0);
					result.setY(0);
				}
			} else {
				LAST_SELECTION_TIME = selectionTime;
				LAST_SELECTION = result;
			}
		} else {
			LAST_SELECTION_TIME = selectionTime;
			LAST_SELECTION = result;
		}
		return result;
	}

	public static void changeMode(int id) {
		switch (id) {
		case R.id.one:
			FaceSelectionPolicy.POLICY = FaceSelectionPolicy.CENTER;
			break;
		case R.id.two:
			FaceSelectionPolicy.POLICY = FaceSelectionPolicy.MOST_CLOSE;
			break;
		case R.id.three:
			FaceSelectionPolicy.POLICY = FaceSelectionPolicy.MEAN;
			break;
		}
	}
}