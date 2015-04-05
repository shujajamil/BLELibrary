package com.uqu.sensysframework.ble;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

public class BLEService extends Service {
	private AbstractBLEManager bManager = null; 
	Context context;
	
	@Override
	public void onCreate() {
		this.context = getApplicationContext();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			bManager = new NexusManager(context);
		}
		else {
			bManager = new SamsungManager4(context);
		}
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		bManager.startScanning();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		bManager.stopScanning();
		super.onDestroy();
	}
}
