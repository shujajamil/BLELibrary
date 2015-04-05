package com.uqu.sensysframework.datamanager;

import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;

import com.uqu.sensysframework.commons.Commons;

public class DataManager {
	public Context context;
	public static String FORMAT_NONE = "FORMAT_NONE";
	public static String FORMAT_CSV = "FORMAT_CSV";
	
	private String loggingFormat = DataManager.FORMAT_NONE;
	private boolean bLogging = false;
	private boolean bBatchUpdate = true;
	
	private String csvFilename = "";
	
	public DataManager(Context c) {
		this.context = c;
	}
	
	@SuppressWarnings("unused")
	private Context getContext() {
		//return ApplicationContext.getContext();
		return this.context;
	}
	
	public void enableLogging(String format) throws Exception {
		if(this.isValidFormat(format))
			this.loggingFormat = format;
		else
			throw new Exception("DataManager: Data format is not supported.");
	}
	
	public void disableLogging() {
		this.loggingFormat = DataManager.FORMAT_NONE;
		this.stopLogger();
	}
	
	public void enableBatchUpdate() {
		this.bBatchUpdate = true;
	}
	
	public void disableBatchUpdate() {
		this.bBatchUpdate = false;
	}
	
	public boolean doBatchUpdate() {
		return this.bBatchUpdate;
	}
	
	public void startLogger() throws Exception {
		if(!this.bLogging && this.isValidFormat(this.getLoggingFormat())) {
			this.bLogging = true;
			if(this.loggingFormat == DataManager.FORMAT_CSV) {
				//storeCSV();
			}
			else {
				throw new Exception("DataManager: Data format is not supported.");
			}
		}
	}
	
	public void stopLogger() {
		if(this.bLogging) {
			
			this.bLogging = false;
		}
	}
	
	public boolean isLogging() {
		return this.bLogging;
	}
	
	public boolean isValidFormat(String format) {
		if(format == DataManager.FORMAT_NONE || format == DataManager.FORMAT_CSV)
			return true;
		else
			return false;
	}
	
	public boolean doLogging() {
		if(this.isValidFormat(getLoggingFormat()) && !getLoggingFormat().equalsIgnoreCase(DataManager.FORMAT_NONE))
			return true;
		else
			return false;
	}
		
	public String getLoggingFormat() {
		return this.loggingFormat;
	}
	
	public void store(ArrayList<String> data) {
		storeCSV(data);
	}
	
	public void storeBatch(ArrayList<ArrayList<String>> data) {
		storeCSVBatch(data);
	}
	
	public void storeCSVBatch(ArrayList<ArrayList<String>> data) {
		CSVSaver csvsaver = new CSVSaver(getCSVFilename(), true);
		
		for(ArrayList<String> row : data) {
			csvsaver.addData(row);
		}
		
		try { 
			csvsaver.write();
			Commons.iLog("DataManager: Writing batch lines");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void storeCSV(ArrayList<String> data) {
		CSVSaver csvsaver = new CSVSaver(getCSVFilename(), true);
		csvsaver.addData(data);
		
		try { //write line by line to file
			csvsaver.write();
			//Commons.iLog("DataManager: Writing individual lines");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getCSVFilename() {
		if(csvFilename.equals(""))
			this.csvFilename = Commons.DateTime.getNowPlain();
		
		return this.csvFilename;
	}
	
}
