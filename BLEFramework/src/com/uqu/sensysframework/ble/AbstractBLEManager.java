package com.uqu.sensysframework.ble;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;

import com.uqu.sensysframework.commons.ApplicationContext;
import com.uqu.sensysframework.commons.Commons;

public abstract class AbstractBLEManager extends BroadcastReceiver {
	protected Context context;
	private AbstractBLEManager _this;
	
	protected Handler resultHandler;
	
	protected long SCAN_PERIOD = 5000;
	protected long REPEAT_DELAY = 5000;
	
	protected boolean bBackgroundScan = false;
	protected boolean bScanning = false;
	protected boolean bRepeatScan = false;
	
	private BluetoothAdapter mBluetoothAdapter;
	
	private Handler mHandler;
	private Runnable rScanner;
	
	private static PowerManager.WakeLock wl;
	private static WifiManager.WifiLock wfl;
	
	private AlarmManager am;
	private Handler stopAlarmHandler;
	private Runnable stopAlarmThread = new Runnable() {
		
		public void run() {
			Commons.iLog("AbstractBleManager -> stopAlarmThread");
			//stop scanning here
			mHandler.post(rScanner);
			
			//release wake locks
			if(AbstractBLEManager.wl!=null && AbstractBLEManager.wl.isHeld())
				AbstractBLEManager.wl.release();
			if(AbstractBLEManager.wfl!=null && AbstractBLEManager.wfl.isHeld())
				AbstractBLEManager.wfl.release();
			
			//Globals.Log(new Globals(), "stopAlarmThread -> Alarm wake lock:"+Alarm.wl.isHeld());
			//Globals.Log(new Globals(), "stopAlarmThread -> Alarm wifi wake lock:"+Alarm.wfl.isHeld());
		};
	};
	
	public AbstractBLEManager() {
		
	}
	
	public AbstractBLEManager(Context context) {
		_this = this;
		this.context = context;
		this.mHandler = new Handler();
		this.resultHandler = new BLEScanHandler();
		
		am = (AlarmManager)ApplicationContext.getContext().getSystemService(Context.ALARM_SERVICE);
		stopAlarmHandler = new Handler();
	}
	
	public abstract boolean isBleSupported();
	
	public boolean isBluetoothEnabled() {
		return getBTAdapter().isEnabled();
	}
	
	public boolean enableBluetooth() {
		int retry = 1;
		int timeout = 30;
		while (!getBTAdapter().isEnabled()){
			if (--timeout == 0){
				break;
			}
			if (--retry == 0){
				retry = 10;
				getBTAdapter().enable();
			}
			try 
			{
				Thread.sleep(1000);				
			}catch (Exception e){}
		}
		
		return isBluetoothEnabled();
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public Context getContext() {
		return context;
	}
	
	public void setBTAdapter(BluetoothAdapter mBTAdapter) {
		this.mBluetoothAdapter = mBTAdapter;
	}
	
	public BluetoothAdapter getBTAdapter() {
		return mBluetoothAdapter;
	}
	
	public void setScanResultHandler(Handler handler) {
		resultHandler = handler;
	}
	
	public Handler getScanResultHandler() {
		return resultHandler;
	}
	
	public void setScanPeriod(long scanperiod) {
		this.SCAN_PERIOD = scanperiod;
	}
	
	public long getScanPeriod() {
		return this.SCAN_PERIOD;
	}
	
	public void setRepeatScanDelay(long repeatdelay) {
		this.REPEAT_DELAY = repeatdelay;
	}
	
	public long getRepeatScanDelay() {
		return this.REPEAT_DELAY;
	}
	
	public void setRepeatScan(boolean bool) {
		this.bRepeatScan = bool;
	}
	
	public void doBackgroundScanning(boolean bool) {
		this.bBackgroundScan = bool;
	}
	
	public boolean isBackgroundScanning() {
		return this.bBackgroundScan;
	}
	
	public boolean isScanning() {
		return bScanning;
	}
	
	public void startScanning() {
		mHandler.removeCallbacks(rScanner);
        Commons.iLog("AbstractBleManager -> startScanning");
		// Stops scanning after a pre-defined scan period.
    	scheduleStopScan();
        bScanning = true;
        
        Bundle mBundle = new Bundle();
        Message msg = Message.obtain(resultHandler, BLEScanHandler.BLE_SCAN_START_MESSAGE);
        msg.setData(mBundle);
        msg.sendToTarget();
	}
	
	public void stopScanning() {
		Commons.iLog("AbstractBleManager -> stopScanning");
		cancelScan();
		
        Bundle mBundle = new Bundle();
        Message msg = Message.obtain(resultHandler, BLEScanHandler.BLE_SCAN_STOP_MESSAGE);
        msg.setData(mBundle);
        msg.sendToTarget();
        
        Commons.iLog("AbstractBleManager -> "+this.bRepeatScan);
        
        if(this.bRepeatScan && this.getRepeatScanDelay() > 0) {
        	Commons.iLog("AbstractBleManager -> calling scheduleStartScan");
	        scheduleStartScan();
		}
	}
	
	public void cancelScan() {
		mHandler.removeCallbacks(rScanner);
        bScanning = false;
	}
	
	private void scheduleStartScan() {
		Commons.iLog("AbstractBleManager -> scheduleStartScan");
		rScanner = new Runnable() {
            @Override
            public void run() {
            	Commons.iLog("AbstractBleManager -> calling startScanning");
            	startScanning();
            }
        };
        
        if(isBackgroundScanning())
        	setAlarm();
        else
        	mHandler.postDelayed(rScanner, this.getRepeatScanDelay());
	}
	
	private void scheduleStopScan() {
		Commons.iLog("AbstractBleManager -> scheduleStopScan");
		rScanner = new Runnable() {
            @Override
            public void run() {
            	Commons.iLog("AbstractBleManager -> calling stopScanning");
            	stopScanning();
            }
        };
        
        if(isBackgroundScanning()) {
        	stopAlarmHandler.postDelayed(stopAlarmThread, this.getScanPeriod()); //to realease wakelock
        }
        else
        	mHandler.postDelayed(rScanner, this.getScanPeriod());
        
	}
	
	public void setAlarm()
	{
		Commons.iLog("AbstractBleManager -> setAlarm"); 
		Context c = ApplicationContext.getContext();
		 //Globals.Log(context, "context: "+context);
		 Intent i = new Intent(c, AbstractBLEManager.class);
	     PendingIntent pi = PendingIntent.getBroadcast(c, 0, i, 0);
	     //am.set(AlarmManager.RTC_WAKEUP, 1000 * 60 * 1, pi);
	     //am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), this.REPEAT_DELAY, pi); // Millisec * Second * Minute
	     am.set(AlarmManager.RTC_WAKEUP, this.REPEAT_DELAY, pi); // Millisec * Second * Minute
	 }
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Commons.iLog("AbstractBleManager -> Alarm onReceive");
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wfl = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "");
        wfl.acquire();
        
        //start scanning here
        mHandler.post(rScanner);
	}
}
