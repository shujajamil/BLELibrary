package com.uqu.sensysframework.ble;

public class BLEScanResult {
	BLEDevice device;
	int RSSI;
	long timestamp;
	
	public BLEScanResult(final BLEDevice device, final int RSSI, final long timestamp) {
		this.device = device;
		this.RSSI = RSSI;
		this.timestamp = timestamp;
	}
	
	public BLEDevice getDevice() {
		return device;
	}
	
	public int getRSSI() {
		return RSSI;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
}
