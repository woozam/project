package com.example.mediarobot;

import java.util.ArrayList;

import android.hardware.Camera;
import android.hardware.Camera.Face;

public class FaceTracker {

	public static ArrayList<FaceWrapper> FACE_WRAPPER_LIST = new ArrayList<FaceWrapper>();

	public static void onFaceDetection(Face[] faces, Camera camera) {
		if (faces != null && faces.length > 0) {
			FACE_WRAPPER_LIST.clear();
			for (Face face : faces) {
				FaceWrapper faceWrapper = new FaceWrapper();
				faceWrapper.setFace(face);
				FACE_WRAPPER_LIST.add(faceWrapper);
			}
		} else {
			FACE_WRAPPER_LIST.clear();
		}
	}

	public static FaceWrapper[] getFaceWrappers() {
		FaceWrapper[] faceWrappers = new FaceWrapper[FACE_WRAPPER_LIST.size()];
		FACE_WRAPPER_LIST.toArray(faceWrappers);
		return faceWrappers;
	}
}