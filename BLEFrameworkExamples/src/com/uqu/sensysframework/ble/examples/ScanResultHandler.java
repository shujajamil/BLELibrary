package com.uqu.sensysframework.ble.examples;

import android.app.Activity;
import android.content.Context;

import com.uqu.sensysframework.ble.BLEDevice;
import com.uqu.sensysframework.ble.BLEScanHandler;

public class ScanResultHandler extends BLEScanHandler {
	Context context;
	Activity activity;
	private LeDeviceListAdapter deviceListAdapter;
	
	public ScanResultHandler(Context context, Activity activity, LeDeviceListAdapter deviceListAdapter) {
		super();
		this.context = context;
		this.activity = activity;
		this.deviceListAdapter = deviceListAdapter;
	}
	
	@Override
	public void handleFoundDevice(final BLEDevice device, final int rssi) {
		activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                deviceListAdapter.addDevice(device);
                deviceListAdapter.notifyDataSetChanged();
            }
        });
		super.handleFoundDevice(device, rssi);
	}
	
	@Override
	public void handleScanStarted() {
		((ListExample)activity).startScanningPressed();
		super.handleScanStarted();
		activity.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	        	activity.invalidateOptionsMenu();
	        }
		});
	}
	
	@Override
	public void handleScanStopped(String dataFormat) {
		super.handleScanStopped(dataFormat);
		activity.runOnUiThread(new Runnable() {
	        @Override
	        public void run() {
	        	activity.invalidateOptionsMenu();
	        }
		});
	}
}
