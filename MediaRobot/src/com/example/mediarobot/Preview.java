package com.example.mediarobot;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Preview extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder mHolder;
	private Camera mCamera;
	private FaceDetectionListener mFaceDetectionListener;

	public Preview(Context context, FaceDetectionListener faceDetectionListener) {
		super(context);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mFaceDetectionListener = faceDetectionListener;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.setFaceDetectionListener(mFaceDetectionListener);
			mCamera.startFaceDetection();
			Parameters parameters = mCamera.getParameters();
			parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
			Log.i("face detection", String.valueOf(parameters.getMaxNumDetectedFaces()));
			mCamera.setParameters(parameters);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.setFaceDetectionListener(null);
		mCamera.stopFaceDetection();
        mCamera.stopPreview();
		mCamera.release();
        mCamera = null;
	}
}