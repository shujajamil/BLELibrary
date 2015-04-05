package com.uqu.sensysframework.ble;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.uqu.sensysframework.commons.Commons;
import com.uqu.sensysframework.datamanager.DataManager;
import com.uqu.sensysframework.datamanager.TransferManager;


public class BLEManager {
	
	private Context context;
	private AbstractBLEManager bleManager = null; 
	private DataManager dataManager = null;
	private TransferManager transferManager = null;
	
	public BLEManager(Context context) {
		this.setContext(context);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			bleManager = new NexusManager(context);
		}
		else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			bleManager = new SamsungManager4(context);
		}
		else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			bleManager = new SamsungManager3(context);
		}
		
		setDataManager(new DataManager(context));
		setTransferManager(new TransferManager(context));
		
		((BLEScanHandler)bleManager.getScanResultHandler()).setDataManager(dataManager);
	}
	
	public boolean isBleSupported() {
		return bleManager.isBleSupported();
	}
	
	public boolean isBluetoothEnabled() {
		return bleManager.isBluetoothEnabled();
	}
	
	public boolean enableBluetooth() {
		return bleManager.enableBluetooth();
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public DataManager getDataManager() {
		return dataManager;
	}
	
	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}
	
	public TransferManager getTransferManager() {
		return transferManager;
	}

	public void setTransferManager(TransferManager transferManager) {
		this.transferManager = transferManager;
	}
	
	public void setScanResultHandler(Handler handler) {
		bleManager.setScanResultHandler(handler);
		((BLEScanHandler)bleManager.getScanResultHandler()).setDataManager(getDataManager());
	}
	
	public void setScanPeriod(long scanperiod) {
		bleManager.setScanPeriod(scanperiod);
	}
	
	public void setRepeatScanDelay(long repeatdelay) {
		bleManager.setRepeatScanDelay(repeatdelay);
	}
	
	public void doBackgroundScanning(boolean bool) {
		bleManager.doBackgroundScanning(bool);
	}
	
	public void startRepeatScanning() throws Exception {
		this.startRepeatScanning(bleManager.REPEAT_DELAY);
	}
	
	public void startRepeatScanning(long repeatdelay) throws Exception {
		this.setRepeatScanDelay(repeatdelay);
		bleManager.setRepeatScan(true);
		this.startScanning();
	}
	
	public void startScanning() throws Exception {
		if(bleManager.isScanning())
			Commons.iLog("BLE Scanner already running");
		else {
			bleManager.startScanning();
			/*
			Bundle mBundle = new Bundle();
            Message msg = Message.obtain(bleManager.getScanResultHandler(), BLEScanHandler.BLE_SCAN_START_MESSAGE);
            mBundle.putString("DATA_FORMAT", getDataManager().getLoggingFormat());
            msg.setData(mBundle);
            msg.sendToTarget();
            /**/
		}
    }
	
	public void stopRepeatScanning() {
		Commons.iLog("BLEManager -> stopRepeatScanning");
		bleManager.setRepeatScan(false);
		
		stopScanning();
	}
	
	public void stopScanning() {
		if(bleManager.isScanning()) {
			bleManager.stopScanning();
			/*
			Bundle mBundle = new Bundle();
            Message msg = Message.obtain(bleManager.getScanResultHandler(), BLEScanHandler.BLE_SCAN_STOP_MESSAGE);
            msg.setData(mBundle);
            msg.sendToTarget();
            /**/
		}
		else {
			Commons.iLog("Scanner already stopped!");
			bleManager.cancelScan();
		}
	}
	
	public boolean isScanning() {
		return bleManager.isScanning();
	}
}
