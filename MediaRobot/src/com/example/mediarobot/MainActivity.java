package com.example.mediarobot;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class MainActivity extends Activity implements FaceDetectionListener, OnClickListener, OnMenuItemClickListener {

	public static final String TAG = MainActivity.class.getSimpleName();

	public static final int VENDOR_ARDUINO2 = 0xFFF1;
	public static final int ARDUINO_TEST = 0xFF48;

	private ViewGroup mRoot;
	private Preview mPreview;
	private FaceCaptureView mFaceCaptureView;
	private View mMode;
	private View mSetting;

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
		mRoot.addView(mPreview, 0, params);
		mRoot.addView(mFaceCaptureView, 1, params);
		mMode = findViewById(R.id.main_mode);
		mSetting = findViewById(R.id.main_setting);
		mMode.setOnClickListener(this);
		mSetting.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		FaceTracker.onFaceDetection(faces, camera);
		mFaceCaptureView.captureFaceCapture(FaceTracker.getFaceWrappers());
		FaceWrapper selection = FaceSelectionPolicy.select(FaceTracker.getFaceWrappers());
		if (selection != null) {
			send(selection);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mMode) {
			onClickMode();
		} else if (v == mSetting) {
			onClickSetting();
		}
	}

	private void onClickMode() {
		PopupMenu menu = new PopupMenu(this, mMode);
		menu.inflate(R.menu.mode);
		menu.setOnMenuItemClickListener(this);
		menu.show();
	}

	private void onClickSetting() {
	}

	private void send(FaceWrapper faceWrapper) {
		if (DeviceListActivity.sDriver != null) {
			try {
				String x = String.format("%03d", Math.abs(faceWrapper.getX()));
				String y = String.format("%03d", Math.abs(faceWrapper.getY()));
				String z = String.format("%04d", Math.abs(faceWrapper.getZ()));
				StringBuffer sb = new StringBuffer();
				sb.append(x).append(y).append(z).append("0000").append(faceWrapper.getX() > 0 ? "1" : "0").append(faceWrapper.getY() > 0 ? "1" : "0");
				char[] buffer = sb.toString().toCharArray();
				byte[] bytes = new byte[buffer.length];
				for (int i = 0; i < bytes.length; i++) {
					bytes[i] = (byte) buffer[i];
				}
				writeData(bytes);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void writeData(final byte[] data) {
		try {
			DeviceListActivity.sDriver.write(data, 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		FaceSelectionPolicy.changeMode(item.getItemId());
		return true;
	}
}