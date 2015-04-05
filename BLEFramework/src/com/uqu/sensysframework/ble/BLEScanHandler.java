package com.uqu.sensysframework.ble;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.uqu.sensysframework.commons.Commons;
import com.uqu.sensysframework.datamanager.DataManager;

public class BLEScanHandler extends Handler {
	public static final int BLE_SCAN_START_MESSAGE = 1001;
	public static final int BLE_SCAN_STOP_MESSAGE = 1002;
	public static final int BLE_DEVICE_FOUND_MESSAGE = 1003;
	
	public static final String EXTRA_RSSI = "RSSI";
	
	public static Map<String, BLEScanResult> scanresult = new LinkedHashMap<String, BLEScanResult>();
	
	private DataManager dManager;
	
	public void setDataManager(DataManager dManager) {
		this.dManager = dManager;
	}
	
	public DataManager getDataManager() {
		return this.dManager;
	}
	
	@Override
    public void handleMessage(Message msg) {
		//Commons.iLog("In BLEScanHandler");
		Bundle data;
		
        switch (msg.what) {
        case BLEScanHandler.BLE_SCAN_START_MESSAGE:
        	new Thread(new Runnable() {
                @Override
                public void run() {
                	handleScanStarted();
                }
            }).start();
        	break;
        case BLEScanHandler.BLE_SCAN_STOP_MESSAGE:
        	data = msg.getData();
        	final String format = data.getString("DATA_FORMAT");
        	new Thread(new Runnable() {
                @Override
                public void run() {
                	handleScanStopped(format);
                }
            }).start();
        	break;
        // Gatt device found message.
        case BLEScanHandler.BLE_DEVICE_FOUND_MESSAGE:
            data = msg.getData();
            final BLEDevice device = data.getParcelable(BluetoothDevice.EXTRA_DEVICE);
            final int rssi = data.getInt(BLEScanHandler.EXTRA_RSSI);
            new Thread(new Runnable() {
                @Override
                public void run() {
                	handleFoundDevice(device, rssi);
                }
            }).start();
            
            break;
        default:
            super.handleMessage(msg);
        }
    }
	
	public void handleScanStarted() {
		
	}
	
	public void handleScanStopped(String dataFormat) {
		if(getDataManager().doLogging() && getDataManager().doBatchUpdate()) {
			//Object[] devices = scanresult.entrySet().toArray();
			ArrayList<ArrayList<String>> records = new ArrayList<ArrayList<String>>();
			Set<String> fieldset = scanresult.keySet();
			for(String label : fieldset) {
				BLEScanResult sr = (BLEScanResult)scanresult.get(label);
			//for(int i=0; i < devices.length; i++) {
				//BLEScanResult sr = (BLEScanResult)devices[i];
				BLEDevice device = sr.getDevice();
				int rssi = sr.getRSSI();
				
				ArrayList<String> ls = new ArrayList<String>();
				ls.add(device.getName());
				ls.add(device.getAddress());
				ls.add(String.valueOf(rssi));
				ls.add(Commons.DateTime.getDateTimeStandard(sr.getTimestamp()));
				records.add(ls);
			}
			
			getDataManager().storeBatch(records);
		}
	}
	
	public void handleFoundDevice(final BLEDevice device, final int rssi) {
		long now = Commons.DateTime.getLongMillis();
		scanresult.put(device.getAddress(), new BLEScanResult(device, rssi, now));
		if(getDataManager().doLogging() && !getDataManager().doBatchUpdate()) {
			ArrayList<String> ls = new ArrayList<String>();
			ls.add(device.getName());
			ls.add(device.getAddress());
			ls.add(String.valueOf(rssi));
			ls.add(Commons.DateTime.getDateTimeStandard(now));
			getDataManager().store(ls);
		}
	}
}
