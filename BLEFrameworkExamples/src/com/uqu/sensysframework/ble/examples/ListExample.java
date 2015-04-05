package com.uqu.sensysframework.ble.examples;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.uqu.sensysframework.ble.BLEManager;
import com.uqu.sensysframework.commons.Commons;
import com.uqu.sensysframework.datamanager.DataManager;
import com.uqu.sensysframework.datamanager.TransferManager;

public class ListExample extends ListActivity {
	private LeDeviceListAdapter mLeDeviceListAdapter;
	BLEManager bleManager;
	
	private static final int REQUEST_ENABLE_BT = 1;
	private long repeatdelay = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setTitle(R.string.title_devices);
		//setContentView(R.layout.activity_sample_list);
		
		mLeDeviceListAdapter = new LeDeviceListAdapter(getLayoutInflater());
        setListAdapter(mLeDeviceListAdapter);
		Commons.iLog("ListExample onCreate");
		boolean compressData=true, deleteAfterUpload=true, archiveData=true;
		
		try {
			bleManager = new BLEManager(getApplicationContext());
			
			if(!bleManager.isBleSupported()) {
				Commons.Toast("BLE not supported");
				finish();
				return;
			}
			else if(!bleManager.isBluetoothEnabled()) {
				if(!bleManager.enableBluetooth()) {
					Commons.Toast("Unable to enable Bluetooth. Please enable manually.");
					finish();
					return;
				}
			}
			
			//bleManager.doBackgroundScanning(false);
			
			bleManager = new BLEManager(getApplicationContext());
			bleManager.setScanPeriod(500);
			bleManager.setScanResultHandler(new ScanResultHandler(getApplicationContext(), this, mLeDeviceListAdapter));
			bleManager.getDataManager().enableLogging(DataManager.FORMAT_CSV);
			bleManager.getDataManager().enableBatchUpdate();
			bleManager.getTransferManager().enableTransfer(TransferManager.TransferProtocol.HTTP, 
															"http://yourserveraddress.com/bledata",
															"username",
															"password");
			bleManager.getTransferManager().setDataParams(compressData, deleteAfterUpload, archiveData);
			bleManager.getTransferManager().scheduleTransfer(TransferManager.TransferSchedule.HOURLY, 
															 TransferManager.TransferChannel.WIFI, 
															 TransferManager.TransferScheme.BULK_TRANSFER);
			bleManager.startScanning();
			
			
			/**/
			bleManager.setRepeatScanDelay(30000);
			bleManager.startRepeatScanning();
			
			/*
			bleManager.getTransferManager().enableTransfer(TransferManager.TransferProtocol.HTTP, 
					"http://tickypack.com/hajjexplorer/",
					"vdbms",
					"sbdbcodeuqu777");
					/**/
			
		}
		catch(Exception e) {
			System.err.println(e.toString());
			//Commons.eLog(e.getMessage());
		}
	}
	
	public void startScanningPressed() {
		try {
			mLeDeviceListAdapter.clear();
        	bleManager.startRepeatScanning();
        	//invalidateOptionsMenu();
        }
		catch(Exception e) {
			Commons.eLog(e.getMessage());
		}
	}
	
	public void stopScanningPressed() {
		Commons.iLog("stopScanningPressed");
		bleManager.stopRepeatScanning(); 
		//invalidateOptionsMenu();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Commons.iLog("onCreatOptionsMenu");
		getMenuInflater().inflate(R.menu.main, menu);
        if (!bleManager.isScanning()) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		/**/
        switch (item.getItemId()) {
            case R.id.menu_scan:
                startScanningPressed();
                break;
            case R.id.menu_stop:
                stopScanningPressed();
                break;
        }
        /**/
        invalidateOptionsMenu();
        return true;
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
	
	@Override
	public void onBackPressed() {
		Commons.iLog("ListExample: onBackPressed");
		destroy();
	}
	
	@Override
	protected void onDestroy() {
		destroy();
		super.onDestroy();
	}
	
	private void destroy() {
		Commons.iLog("ListExample: destroy");
		stopScanningPressed();
		bleManager.getTransferManager().stopTransfer();
		finish();
	}

}
