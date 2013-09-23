package com.mlst.motorcontrol;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.android.future.usb.UsbAccessory;
import com.android.future.usb.UsbManager;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private UsbManager mUsbManager;
	private SeekBar speed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		speed = (SeekBar) findViewById(R.id.speed);
		speed.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, final int progress,
					boolean fromUser) {
				if (output != null) {

					new Thread() {
						public void run() {
							try {
								output.write(progress);
							} catch (IOException e) {
								e.printStackTrace();
							}
						};
					}.run();

				}

			}
		});

		mUsbManager = UsbManager.getInstance(this);

	}

	@Override
	protected void onResume() {
		if (output != null) {
			return;
		}

		UsbAccessory[] accessories = mUsbManager.getAccessoryList();
		UsbAccessory accessory = (accessories == null ? null : accessories[0]);
		if (accessory != null) {
			if (mUsbManager.hasPermission(accessory)) {
				openAccessory(accessory);
			}
		} else {
			Log.d(TAG, "mAccessory is null");
		}
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private ParcelFileDescriptor fileDescriptor;
	private FileOutputStream output;

	protected void openAccessory(UsbAccessory accessory) {
		fileDescriptor = mUsbManager.openAccessory(accessory);
		if (fileDescriptor != null) {
			FileDescriptor fd = fileDescriptor.getFileDescriptor();

			output = new FileOutputStream(fd);
			Toast.makeText(getApplicationContext(), "accessory opened",
					Toast.LENGTH_LONG).show();
			Log.d(TAG, "accessory opened");
		} else {
			Toast.makeText(getApplicationContext(), "accessory failed",
					Toast.LENGTH_LONG).show();
			Log.d(TAG, "accessory open fail");
		}

	}

	protected void closeAccessory() {
		try {
			if(output != null){
				output.write(0);
			}
			if (fileDescriptor != null) {
				fileDescriptor.close();
			}
		} catch (IOException e) {
		} finally {
			fileDescriptor = null;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		closeAccessory();
	}
}