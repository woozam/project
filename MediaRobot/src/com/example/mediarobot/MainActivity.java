package com.example.mediarobot;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

public class MainActivity extends Activity implements FaceDetectionListener {

	public static final String TAG = MainActivity.class.getSimpleName();

	private ViewGroup mRoot;
	private Preview mPreview;
	private FaceCaptureView mFaceCaptureView;
	private UsbManager mManager;
	private UsbSerialDriver mDriver;
	private LinkedBlockingQueue<byte[]> mWriteQueue;

	private final static String DATA_RECEIVED_INTENT = "primavera.arduino.intent.action.DATA_RECEIVED";
	private final static String SEND_DATA_INTENT = "primavera.arduino.intent.action.SEND_DATA";
	private final static String DATA_EXTRA = "primavera.arduino.intent.extra.DATA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.activity_main);
//		mRoot = (ViewGroup) findViewById(R.id.main_root);
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		mPreview = new Preview(this, this);
//		mFaceCaptureView = new FaceCaptureView(this);
//		mRoot.addView(mPreview, params);
//		mRoot.addView(mFaceCaptureView, params);
		startActivity(new Intent(this, DeviceListActivity.class));
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		if (faces.length > 0) {
			FaceTracker.onFaceDetection(faces, camera);
			mFaceCaptureView.captureFaceCapture(FaceTracker.getFaceWrappers());
		}
	}

	private void openUSBDriver() {
//		mManager = (UsbManager) getSystemService(Context.USB_SERVICE);
//		mDriver = UsbSerialProber.acquire(mManager);
//		if (mDriver == null) {
//			HashMap<String, UsbDevice> deviceList = mManager.getDeviceList();
//			Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//			while (deviceIterator.hasNext()) {
//				mDriver = UsbSerialProber.acquire(mManager, deviceIterator.next());
//			}
//		}
//		Toast.makeText(this, mDriver == null ? "null" : "not null", Toast.LENGTH_SHORT).show();
//		if (mDriver != null) {
//			try {
//				mWriteQueue = new LinkedBlockingQueue<byte[]>();
//				mDriver.open();
//				mDriver.setBaudRate(115200);
//				new Thread(getReadRunnable()).start();
//				new Thread(getWriteRunnable()).start();
//				new Timer().schedule(new TimerTask() {
//					@Override
//					public void run() {
//						mWriteQueue.add(new byte[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5' });
//					}
//				}, 0, 4000);
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					mDriver.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}

		// new Timer().schedule(new TimerTask() {
		// @Override
		// public void run() {
		// byte[] data = new byte[] { '0', '1', '2', '3', '4', '5', '6', '7',
		// '8', '9', '0', '1', '2', '3', '4', '5' };
		// Intent intent = new Intent(SEND_DATA_INTENT);
		// intent.putExtra(DATA_EXTRA, data);
		// sendBroadcast(intent);
		// String str = new String(data);
		// Log.i(TAG, str);
		// }
		// }, 0, 4000);
		//
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(DATA_RECEIVED_INTENT);
		// registerReceiver(new BroadcastReceiver() {
		// @Override
		// public void onReceive(Context context, Intent intent) {
		// final String action = intent.getAction();
		// if (DATA_RECEIVED_INTENT.equals(action)) {
		// final byte[] data = intent.getByteArrayExtra(DATA_EXTRA);
		// final String str = new String(data);
		// Log.i(TAG, str);
		// MainActivity.this.runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
		// }
		// });
		// }
		// }
		// }, filter);
	}

	private Runnable getReadRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				while (true) {
					byte buffer[] = new byte[16];
					try {
						if (mDriver.read(buffer, 1000) > 0) {
							String str = new String(buffer);
							Log.i(TAG, str);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	private Runnable getWriteRunnable() {
		return new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						byte[] data = mWriteQueue.take();
						String str = new String(data);
						Log.i(TAG, str);
						mDriver.write(data, 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}
}