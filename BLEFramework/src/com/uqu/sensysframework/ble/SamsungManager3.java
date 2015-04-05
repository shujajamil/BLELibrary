package com.uqu.sensysframework.ble;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import com.samsung.android.bts3.BluetoothAdapter;
import com.samsung.android.bts3.BluetoothDevice;
import com.uqu.sensysframework.commons.Commons;

@TargetApi(16)
public class SamsungManager3 extends AbstractBLEManager {
	
	private BluetoothAdapter mBluetoothAdapter = null;
	/**
     * The BroadcastReceiver that listens for discovered devices and changes the
     * title when discovery is finished.
     */
	
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //if (deviceList.size() == 0) 
                {
                    //mEmptyList.setText(R.string.no_ble_devices);
                }
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (!mBluetoothAdapter.isEnabled())
                    ;//finish();
            }
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            	BluetoothDevice localBluetoothDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            	if (localBluetoothDevice.getDeviceType() == BluetoothDevice.DEVICE_TYPE_LE) {
            		short s = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, (short) 0);
            		//DeviceListActivity.this.addDevice(localBluetoothDevice, (int) s);
            		
            		final BLEDevice bd = new BLEDevice();
        			bd.setName(localBluetoothDevice.getName());
        			bd.setAddress(localBluetoothDevice.getAddress());
            		
            		Bundle mBundle = new Bundle();
                    Message msg = Message.obtain(getScanResultHandler(), BLEScanHandler.BLE_DEVICE_FOUND_MESSAGE);
                    mBundle.putParcelable(BluetoothDevice.EXTRA_DEVICE, bd);
                    mBundle.putInt(BLEScanHandler.EXTRA_RSSI, s);
                    msg.setData(mBundle);
                    msg.sendToTarget();
            	}
            }
        }
    };
	
	public SamsungManager3(Context context) {
		super(context);
		setContext(context);
		
		setBTAdapter(BluetoothAdapter.getDefaultAdapter());
	}
	
	@Override
	public boolean isBleSupported() {
		if(Build.MANUFACTURER.equalsIgnoreCase("samsung") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			if(this.mBluetoothAdapter != null)
				return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isBluetoothEnabled() {
		return this.mBluetoothAdapter.isEnabled();
	}
	
	public boolean enableBluetooth() {
		int retry = 1;
		int timeout = 30;
		while (!mBluetoothAdapter.isEnabled()){
			if (--timeout == 0){
				break;
			}
			if (--retry == 0){
				retry = 10;
				mBluetoothAdapter.enable();
			}
			try 
			{
				Thread.sleep(1000);				
			}catch (Exception e){}
		}
		
		return isBluetoothEnabled();
	}
	
	public void setBTAdapter(BluetoothAdapter mBTAdapter) {
		this.mBluetoothAdapter = mBTAdapter;
	}
	
	@Override
	public void startScanning() {
		super.startScanning();
		
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
		getContext().registerReceiver(mReceiver, filter);
        
		//getBTAdapter().startLeDiscovery();
		mBluetoothAdapter.startLeDiscovery();
		
        Commons.iLog("SamsungManager3 scanning started");
	}

	@Override
	public void stopScanning() {
		super.stopScanning();
		mBluetoothAdapter.cancelDiscovery();
		getContext().unregisterReceiver(mReceiver);
		
		Commons.iLog("SamsungManager3 scanning stopped: "+BLEScanHandler.scanresult.size());
	}
}
