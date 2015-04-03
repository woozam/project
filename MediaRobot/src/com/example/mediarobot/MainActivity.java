package com.example.mediarobot;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements FaceDetectionListener {
	
	private ViewGroup mRoot;
	private Preview mPreview;
	private FaceCaptureView mFaceCaptureView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mRoot = (ViewGroup) findViewById(R.id.main_root);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mPreview = new Preview(this, this);
		mFaceCaptureView = new FaceCaptureView(this);
		mRoot.addView(mPreview, params);
		mRoot.addView(mFaceCaptureView, params);
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		if (faces.length > 0) {
			mFaceCaptureView.captureFaceCapture(faces, camera);
		}
	}
}