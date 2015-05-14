/* Copyright 2011 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: http://code.google.com/p/usb-serial-for-android/
 */

package com.example.mediarobot;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

/**
 * Monitors a single {@link UsbSerialDriver} instance, showing all data
 * received.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
public class SerialConsoleActivity extends Activity {

	private final String TAG = SerialConsoleActivity.class.getSimpleName();

	/**
	 * Driver instance, passed in statically via
	 * {@link #show(Context, UsbSerialDriver)}.
	 *
	 * <p/>
	 * This is a devious hack; it'd be cleaner to re-create the driver using
	 * arguments passed in with the {@link #startActivity(Intent)} intent. We
	 * can get away with it because both activities will run in the same
	 * process, and this is a simple demo.
	 */
	private UsbSerialDriver mDriver = null;

	private TextView mTitleTextView;
	private TextView mDumpTextView;
	private ScrollView mScrollView;
	private Timer mWriteTimer;

	private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();

	private SerialInputOutputManager mSerialIoManager;

	private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

		@Override
		public void onRunError(Exception e) {
			Log.d(TAG, "Runner stopped.");
		}

		@Override
		public void onNewData(final byte[] data) {
			SerialConsoleActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					SerialConsoleActivity.this.updateReceivedData(data);
				}
			});
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDriver = DeviceListActivity.sDriver;
		
		if (mDriver == null) {
			finish();
			return;
		}
		
		setContentView(R.layout.serial_console);
		mTitleTextView = (TextView) findViewById(R.id.demoTitle);
		mDumpTextView = (TextView) findViewById(R.id.consoleText);
		mScrollView = (ScrollView) findViewById(R.id.demoScroller);

		if (mDriver == null) {
			mTitleTextView.setText("No serial device.");
		} else {
			try {
				mDriver.open();
				mDriver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
			} catch (IOException e) {
				Log.e(TAG, "Error setting up device: " + e.getMessage(), e);
				mTitleTextView.setText("Error opening device: " + e.getMessage());
				try {
					mDriver.close();
				} catch (IOException e2) {
					// Ignore.
				}
				mDriver = null;
				return;
			}
			mTitleTextView.setText("Serial device: " + mDriver.getClass().getSimpleName());
		}
		onDeviceStateChange();
	}

	private void scheduleWriteTask() {
		mWriteTimer = new Timer();
		mWriteTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				writeData(new byte[] { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6' });
			}
		}, 1000, 3000);
	}

	private void stopWriteTask() {
		if (mWriteTimer != null) {
			try {
				mWriteTimer.cancel();
				mWriteTimer = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			stopIoManager();
			if (mDriver != null) {
				try {
					mDriver.close();
				} catch (IOException e) {
				}
				mDriver = null;
			}
		}
	}

	private void stopIoManager() {
		if (mSerialIoManager != null) {
			Log.i(TAG, "Stopping io manager ..");
			mSerialIoManager.stop();
			mSerialIoManager = null;
			stopWriteTask();
		}
	}

	private void startIoManager() {
		if (mDriver != null) {
			Log.i(TAG, "Starting io manager ..");
			mSerialIoManager = new SerialInputOutputManager(mDriver, mListener);
			mExecutor.submit(mSerialIoManager);
			scheduleWriteTask();
		}
	}

	private void onDeviceStateChange() {
		stopIoManager();
		startIoManager();
	}

	private void updateReceivedData(byte[] data) {
		final String message = "Read " + data.length + " bytes: \n" + new String(data) + "\n\n";
		printLog(message);
	}

	private void writeData(final byte[] data) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					final String message = "Write " + data.length + " bytes: \n" + new String(data) + "\n\n";
					printLog(message);
					mDriver.write(data, 1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void printLog(String message) {
		mDumpTextView.append(message);
		scrollToBottom();
	}

	private void scrollToBottom() {
		mScrollView.post(new Runnable() {
			@Override
			public void run() {
				mScrollView.smoothScrollTo(0, mDumpTextView.getBottom());
			}
		});
	}
}