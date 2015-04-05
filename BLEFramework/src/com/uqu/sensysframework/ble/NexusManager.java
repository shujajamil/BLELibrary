package com.uqu.sensysframework.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.uqu.sensysframework.commons.Commons;

@TargetApi(18)
public class NexusManager extends AbstractBLEManager {
	
	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
		
		@Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            /*
			runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //mLeDeviceListAdapter.addDevice(device);
                    //mLeDeviceListAdapter.notifyDataSetChanged();
                    Commons.iLog("Device found "+device.getAddress()+" "+scanRecord.toString());
                }
            });
            /**/
			
			final BLEDevice bd = new BLEDevice();
			bd.setName(device.getName());
			bd.setAddress(device.getAddress());
			
			Bundle mBundle = new Bundle();
            Message msg = Message.obtain(getScanResultHandler(), BLEScanHandler.BLE_DEVICE_FOUND_MESSAGE);
            mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, bd);
            mBundle.putInt(BLEScanHandler.EXTRA_RSSI, rssi);
            msg.setData(mBundle);
            msg.sendToTarget();
			
        }
    };
    
	public NexusManager(Context context) {
		super(context);
		setContext(context);
		
		final BluetoothManager bluetoothManager =
                (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        setBTAdapter(bluetoothManager.getAdapter());
	}
	
	public NexusManager(Context context, Handler scanresulthandler) {
		this(context);
		
		if(scanresulthandler != null)
			setScanResultHandler(scanresulthandler);
	}
	
	public NexusManager(Context context, Handler scanresulthandler, long scanperiod) {
		this(context, scanresulthandler);
		
		if(scanperiod > 0)
			setScanPeriod(scanperiod);
	}
	
	@Override
	public boolean isBleSupported() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			boolean hasBle = getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
			final BluetoothManager manager = (BluetoothManager) getContext().getSystemService(Context.BLUETOOTH_SERVICE);
			
			if(hasBle && manager != null)
				return true;
		}
		
		return false;
	}
	
	@Override
	public void startScanning() {
		super.startScanning();
        getBTAdapter().startLeScan(mLeScanCallback);
        Commons.iLog("NexusManager scanning started");
	}
	
	@Override
	public void stopScanning() {
		super.stopScanning();
		getBTAdapter().stopLeScan(mLeScanCallback);
        Commons.iLog("NexusManager scanning stopped: "+BLEScanHandler.scanresult.size());
	}
	
}
