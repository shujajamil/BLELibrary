package com.uqu.sensysframework.ble;

import android.os.Parcel;
import android.os.Parcelable;

//This class is a Wrapper for BluetoothDevice
public class BLEDevice implements Parcelable{
	private String name;
	private String address;
	
	BLEDevice() {
		
	}
	
	BLEDevice(Parcel in) {
		this.name = in.readString();
		this.address = in.readString();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getName() {
		return name;
	}
	
	public String getAddress() {
		return address;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(name);
		dest.writeString(address);
	}
	
	static final Parcelable.Creator<BLEDevice> CREATOR
		    = new Parcelable.Creator<BLEDevice>() {
		
		public BLEDevice createFromParcel(Parcel in) {
		    return new BLEDevice(in);
		}
		
		public BLEDevice[] newArray(int size) {
		    return new BLEDevice[size];
		}
		};
}
