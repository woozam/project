package com.example.mediarobot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;

/**
 * Shows a {@link ListView} of available USB devices.
 *
 * @author mike wakerly (opensource@hoho.com)
 */
@SuppressWarnings("deprecation")
public class DeviceListActivity extends Activity {

	private final String TAG = DeviceListActivity.class.getSimpleName();
	
	public static UsbSerialDriver sDriver = null;

	private UsbManager mUsbManager;
	private ListView mListView;
	private TextView mProgressBarTitle;
	private ProgressBar mProgressBar;

	private static final int MESSAGE_REFRESH = 101;
	private static final long REFRESH_TIMEOUT_MILLIS = 5000;

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_REFRESH:
				refreshDeviceList();
				mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}

	};

	/** Simple container for a UsbDevice and its driver. */
	public static class DeviceEntry {
		public UsbDevice device;
		public UsbSerialDriver driver;

		DeviceEntry(UsbDevice device, UsbSerialDriver driver) {
			this.device = device;
			this.driver = driver;
		}
	}

	private List<DeviceEntry> mEntries = new ArrayList<DeviceEntry>();
	private ArrayAdapter<DeviceEntry> mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);

		mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		mListView = (ListView) findViewById(R.id.deviceList);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mProgressBarTitle = (TextView) findViewById(R.id.progressBarTitle);

		mAdapter = new ArrayAdapter<DeviceEntry>(this, android.R.layout.simple_expandable_list_item_2, mEntries) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				final TwoLineListItem row;
				if (convertView == null) {
					final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_2, null);
				} else {
					row = (TwoLineListItem) convertView;
				}

				final DeviceEntry entry = mEntries.get(position);
				final String title = String.format("Vendor %s Product %s", HexDump.toHexString((short) entry.device.getVendorId()), HexDump.toHexString((short) entry.device.getProductId()));
				row.getText1().setText(title);

				final String subtitle = entry.driver != null ? entry.driver.getClass().getSimpleName() : "No Driver";
				row.getText2().setText(subtitle);

				return row;
			}

		};
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Log.d(TAG, "Pressed item " + position);
				if (position >= mEntries.size()) {
					Log.w(TAG, "Illegal position.");
					return;
				}

				final DeviceEntry entry = mEntries.get(position);
				final UsbSerialDriver driver = entry.driver;
				if (driver == null) {
					Log.d(TAG, "No driver.");
					return;
				}

				showConsoleActivity(driver);
			}
		});
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(MESSAGE_REFRESH);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeMessages(MESSAGE_REFRESH);
	}

	private void refreshDeviceList() {
		showProgressBar();

		new AsyncTask<Void, Void, List<DeviceEntry>>() {
			@Override
			protected List<DeviceEntry> doInBackground(Void... params) {
				Log.d(TAG, "Refreshing device list ...");
				SystemClock.sleep(1000);
				final List<DeviceEntry> result = new ArrayList<DeviceEntry>();
				for (final UsbDevice device : mUsbManager.getDeviceList().values()) {
					if (device != null && !mUsbManager.hasPermission(device)) {
						mUsbManager.requestPermission(device, PendingIntent.getActivity(DeviceListActivity.this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
					}
					final List<UsbSerialDriver> drivers = UsbSerialProber.probeSingleDevice(mUsbManager, device);
					Log.d(TAG, "Found usb device: " + device);
					if (drivers.isEmpty()) {
						Log.d(TAG, "  - No UsbSerialDriver available.");
						result.add(new DeviceEntry(device, null));
					} else {
						for (UsbSerialDriver driver : drivers) {
							Log.d(TAG, "  + " + driver);
							result.add(new DeviceEntry(device, driver));
						}
					}
				}
				return result;
			}

			@Override
			protected void onPostExecute(List<DeviceEntry> result) {
				mEntries.clear();
				mEntries.addAll(result);
				mAdapter.notifyDataSetChanged();
				mProgressBarTitle.setText(String.format("%s device(s) found", Integer.valueOf(mEntries.size())));
				hideProgressBar();
				Log.d(TAG, "Done refreshing, " + mEntries.size() + " entries found.");
			}

		}.execute((Void) null);
	}

	private void showProgressBar() {
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBarTitle.setText("refreshing");
	}

	private void hideProgressBar() {
		mProgressBar.setVisibility(View.INVISIBLE);
	}

	private void showConsoleActivity(UsbSerialDriver driver) {
		if (driver == null) {
		} else {
			try {
				driver.open();
				driver.setParameters(115200, 8, UsbSerialDriver.STOPBITS_1, UsbSerialDriver.PARITY_NONE);
				sDriver = driver;
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
			} catch (IOException e) {
				try {
					driver.close();
				} catch (IOException e2) {
				}
				driver = null;
				return;
			}
		}
	}
}