package com.uqu.sensysframework.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.uqu.sensysframework.commons.Commons;

public class SamsungManager4 extends AbstractBLEManager {
	
	private ProximityService mService = null;
	private ServiceConnection onService = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((ProximityService.LocalBinder) rawBinder).getService();
            if (mService != null) {
                mService.setDeviceListHandler(getScanResultHandler());
            }
            mService.scan(true);
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };
	
	public SamsungManager4(Context context) {
		super(context);
		setContext(context);
		
		setBTAdapter(BluetoothAdapter.getDefaultAdapter());
	}
	
	@Override
	public boolean isBleSupported() {
		if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			if(getBTAdapter() != null)
				return true;
		}
		
		return false;
	}
	
	@Override
	public void startScanning() {
		super.startScanning();
		// start service, if not already running (but it is)
		Intent bindIntent = new Intent(getContext(), ProximityService.class);
		getContext().startService(bindIntent);
        getContext().bindService(bindIntent, onService, Context.BIND_AUTO_CREATE);
        
        Commons.iLog("SamsungManager4 scanning started");
	}
	
	@Override
	public void stopScanning() {
		super.stopScanning();
		getContext().unbindService(onService);
		if(mService!=null)
			mService.scan(false);
		
		Intent bindIntent = new Intent(getContext(), ProximityService.class);
		getContext().stopService(bindIntent);
		
		Commons.iLog("SamsungManager4 scanning stopped: "+BLEScanHandler.scanresult.size());
	}
}
