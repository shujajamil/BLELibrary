package com.uqu.sensysframework.datamanager;

import java.util.ArrayList;
import java.util.List;

import com.uqu.sensysframework.ble.BLEScanResult;
import com.uqu.sensysframework.commons.Commons;

public class CompositeField {
	@SuppressWarnings("unused")
	private String mLabel;
	private Object mField;
	private Boolean isFresh;
	private long mTimestamp = 0;
	private FieldType mType;
	@SuppressWarnings("unused")
	private String[] mSensorLabels = {"x", "y", "z"};
	
	public static String FIELD_NULL = "NULL";
	
	public enum FieldType {
		Sensor, Location, WifiScan, NeighboringCellinfo, BleScan
	}
	
	public CompositeField(String label, FieldType type, Object field, Boolean fresh) {
		mLabel = label;
		mType = type;
		mField = field;
		isFresh = fresh;
	}
	
	public Object getField() {
		return mField;
	}
	
	public void setFresh(Boolean bool) {
		isFresh = bool;
	}
	
	public Boolean isFresh() {
		return isFresh;
	}
	
	public void setTimestamp(long timestamp) {
		mTimestamp = timestamp;
	}
	
	public long getTimestamp() {
		return mTimestamp;
	}
	
	public ArrayList<String> getHeaders() {
		//String headers = "";
		ArrayList<String> headers = new ArrayList<String>();
		
		/*
		if(mType == FieldType.Sensor) {
			if(mField!=null) {
				
				for(int i=0; i < Globals.allSensorPreferences.getSupportedValues(mLabel); i++) {
					//if(headers.length() > 0) headers += Globals.CSVSeparator;
					//headers += mLabel+"_"+mSensorLabels[i];
					headers.add(mLabel+"_"+mSensorLabels[i]);
				}
				
			}
			else {
				
				for(int i=0; i < Globals.allSensorPreferences.getSupportedValues(mLabel); i++) {
					//if(headers.length() > 0) headers += Globals.CSVSeparator;
					//headers += mLabel+"_"+mSensorLabels[i];
					Globals.Log(this, mLabel);
					headers.add(mLabel+"_"+mSensorLabels[i]);
				}
				
			}
			//headers += Globals.CSVSeparator+"isfresh";
			headers.add(mLabel+"_accuracy");
			headers.add(mLabel+"_isfresh");
			headers.add(mLabel+"_timestamp");
		} else if(mType==Globals.FieldType.Location) {
			//headers += mLabel + "_latitude";
			headers.add(mLabel + "_latitude");
			//headers += Globals.CSVSeparator+mLabel + "_longitude";
			headers.add(mLabel + "_longitude");
			//headers += Globals.CSVSeparator+mLabel + "_accuracy";
			headers.add(mLabel + "_accuracy");
			//headers += Globals.CSVSeparator+mLabel + "_altitude";
			headers.add(mLabel + "_altitude");
			//headers += Globals.CSVSeparator+mLabel + "_bearing";
			headers.add(mLabel + "_bearing");
			//headers += Globals.CSVSeparator+mLabel+"_isfresh";
			headers.add(mLabel+"_isfresh");
			headers.add(mLabel+"_timestamp");
		} else if(mType==Globals.FieldType.WifiScan) {
			headers.add("WifiAP_count");
			headers.add("WifiAP_data");
			
		} else if(mType==Globals.FieldType.NeighboringCellinfo) {
			headers.add("CellTowers_count");
			headers.add("CellTowers_data");
			
		} else 
		/**/
		if(mType==FieldType.BleScan) {
			headers.add("BLE_count");
			headers.add("BLE_data");
		}
		
		return headers;
	}
	
	public ArrayList<Object> getValues() {
		ArrayList<Object> values = new ArrayList<Object>();
		/*
		if(mType==Globals.FieldType.Sensor) {
			if(mField!=null) {
				//SensorEvent se = (SensorEvent)mField;
				SensorPreference se = (SensorPreference)mField;
				
				//for(int i=0; i < se.values.length; i++) {
				for(int i=0; i < Globals.allSensorPreferences.getSupportedValues(mLabel); i++) {
					values.add(se.values[i]);
					//values.add(se.sensor.getType());
				}
				values.add(se.accuracy);
				values.add(String.valueOf(isFresh()));
				//Globals.Log(this, se.timestamp+" - "+se.timestamp/1000+" - "+se.timestamp/1000000+" - "+Globals.DateTime.getLongMillis());
				//values.add(Globals.DateTime.getTimeStandard(se.timestamp)); //se.timestamp is uptime(time since device booted) in nanosecond
				values.add(Globals.DateTime.getTimeStandard(getTimestamp()));
			}
			else {
				for(int i=0; i < Globals.allSensorPreferences.getSupportedValues(mLabel); i++) {
					values.add(Globals.FIELD_NULL);
				}
				values.add(Globals.FIELD_NULL);
				values.add(String.valueOf(isFresh()));
				values.add(Globals.DateTime.getTimeStandard(getTimestamp()));
			}
		} else if(mType==Globals.FieldType.Location) {
			if(mField!=null) {
				Location l = (Location)mField;
				values.add(l.getLatitude());
				values.add(l.getLongitude());
				values.add(l.getAccuracy());
				values.add(l.getAltitude());
				values.add(l.getBearing());
				values.add(String.valueOf(isFresh()));
				values.add(Globals.DateTime.getTimeStandard(l.getTime()));
			}
			else {
				values.add(Globals.FIELD_NULL);
				values.add(Globals.FIELD_NULL);
				values.add(Globals.FIELD_NULL);
				values.add(Globals.FIELD_NULL);
				values.add(Globals.FIELD_NULL);
				values.add(String.valueOf(isFresh()));
				values.add(Globals.DateTime.getTimeStandard(getTimestamp()));
			}
		} else if(mType==Globals.FieldType.WifiScan) {
			if(mField!=null) {
				List<ScanResult> lsr = (List<ScanResult>)mField;
				values.add(lsr.size());
				
				ArrayList<Object> wifidetail = new ArrayList<Object>();
				//for(int i=0; i < Globals.MAX_WIFI_AP; i++)
				for(int i=0; i < lsr.size(); i++)
				{
					ArrayList<Object> wifirecord = new ArrayList<Object>();
					
					ScanResult sr = lsr.get(i);
					String sep = ";";
					wifirecord.add("SSID"+sep+sr.SSID);
					wifirecord.add("BSSID"+sep+sr.BSSID);
					wifirecord.add("RSSI"+sep+sr.level);
					wifirecord.add("Strength"+sep+WifiManager.calculateSignalLevel(sr.level, 20));
					wifirecord.add("isFresh"+sep+String.valueOf(isFresh()));
					wifirecord.add("Timestamp"+sep+Globals.DateTime.getTimeStandard(getTimestamp()));
					
					wifidetail.add(Globals.join(wifirecord.toArray(), ">> "));
					
				}
				values.add(Globals.join(wifidetail.toArray(), "||"));
			}
			else {
				values.add(0);
				
				values.add(Globals.FIELD_NULL);
			}
		} else if(mType==Globals.FieldType.NeighboringCellinfo) {
			if(mField!=null) {
				List<NeighboringCellInfo> lsr = (List<NeighboringCellInfo>)mField;
				values.add(lsr.size());
				
				ArrayList<Object> ctdetail = new ArrayList<Object>();
				//for(int i=0; i < Globals.MAX_NEIGHBORING_CELLS; i++)
				for(int i=0; i < lsr.size(); i++) {
					
					ArrayList<Object> ctrecord = new ArrayList<Object>();
					
					NeighboringCellInfo sr = lsr.get(i);
					String sep = ";";
					ctrecord.add("Cid"+sep+sr.getCid());
					ctrecord.add("Lac"+sep+sr.getLac());
					ctrecord.add("NetworkType"+sep+TelephonyUtil.getNetworkType(sr.getNetworkType()));
					ctrecord.add("Psc"+sep+sr.getPsc());
					ctrecord.add("RSSI"+sep+sr.getRssi());
					ctrecord.add("isFresh"+sep+String.valueOf(isFresh()));
					ctrecord.add("Timestamp"+sep+Globals.DateTime.getTimeStandard(getTimestamp()));
					
					ctdetail.add(Globals.join(ctrecord.toArray(), ">> "));
					
				}
				values.add(Globals.join(ctdetail.toArray(), "||"));
			}
			else {
				values.add(0);
				
				values.add(Globals.FIELD_NULL);
			}
		} else 
		/**/
		if(mType==FieldType.BleScan) {
			if(mField!=null) {
				@SuppressWarnings("unchecked")
				List<BLEScanResult> lsr = (List<BLEScanResult>)mField;
				values.add(lsr.size());
				
				ArrayList<Object> ctdetail = new ArrayList<Object>();
				//for(int i=0; i < Globals.MAX_NEIGHBORING_CELLS; i++)
				for(int i=0; i < lsr.size(); i++) {
					
					ArrayList<Object> ctrecord = new ArrayList<Object>();
					
					BLEScanResult sr = lsr.get(i);
					String sep = ";";
					ctrecord.add("Name"+sep+sr.getDevice().getName());
					ctrecord.add("Address"+sep+sr.getDevice().getAddress());
					ctrecord.add("RSSI"+sep+sr.getRSSI());
					ctrecord.add("Timestamp"+sep+Commons.DateTime.getTimeStandard(getTimestamp()));
					
					ctdetail.add(Commons.Utils.join(ctrecord.toArray(), ">> "));
					
				}
				values.add(Commons.Utils.join(ctdetail.toArray(), "||"));
			}
			else {
				values.add(0);
				
				values.add(FIELD_NULL);
			}
		}
		
		return values;
	}
}
