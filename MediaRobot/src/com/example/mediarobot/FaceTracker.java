package com.example.mediarobot;

import java.util.ArrayList;

import android.hardware.Camera;
import android.hardware.Camera.Face;

public class FaceTracker {
	
	public static ArrayList<FaceWrapper> FACE_WRAPPER_LIST = new ArrayList<FaceWrapper>();

	public static void onFaceDetection(Face[] faces, Camera camera) {
		if (FACE_WRAPPER_LIST.size() == 0) {
			FaceWrapper faceWrapper = new FaceWrapper();
			faceWrapper.setFace(faces[0]);
			FACE_WRAPPER_LIST.add(faceWrapper);
		} else {
			FACE_WRAPPER_LIST.get(0).setFace(faces[0]);
		}
	}
	
	public static FaceWrapper[] getFaceWrappers() {
		FaceWrapper[] faceWrappers = new FaceWrapper[FACE_WRAPPER_LIST.size()];
		FACE_WRAPPER_LIST.toArray(faceWrappers);
		return faceWrappers;
	}
}