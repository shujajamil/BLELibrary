package com.uqu.sensysframework.datamanager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.uqu.sensysframework.commons.Commons;

public class CSVSaver {

	@SuppressWarnings("unused")
	private File root;
	private File csvfile;
	private FileWriter writer;
	
	private ArrayList<String> mMeta;
	private ArrayList<String> mHeader;
	private ArrayList<String> mDataLines;
	private Boolean bAppend = true;
		
	public CSVSaver(String filename) {
		/*
		root = new File(Globals.getRootDir());//Environment.getExternalStorageDirectory();
		csvfile = new File(Globals.getRootDir(), filename+".csv");
		
		mHeader = new ArrayList<String>();
		mDataLines = new ArrayList<String>();
		/**/
		this(filename, true);
	}
	
	public CSVSaver(String filename, Boolean append) {
		
		Commons.FileSystem.createRootDir();
		
		root = new File(Commons.FileSystem.getDataDir());//Environment.getExternalStorageDirectory();
		csvfile = new File(Commons.FileSystem.getDataDir(), filename+".csv");
		
		mHeader = new ArrayList<String>();
		mDataLines = new ArrayList<String>();
		bAppend = append;
	}
	
	public void reset() {
		mMeta.clear();
		mHeader.clear();
		mDataLines.clear();
	}
	
	public boolean fileExists() {
		return csvfile.exists();
	}
	
	public void setMeta(ArrayList<String> meta) {
		mMeta = meta;
	}
	
	public void setHeader(ArrayList<String> header) {
		mHeader = header;
	}
	
	public ArrayList<String> getHeader() {
		return mHeader;
	}
	
	public void addLine(String line, Boolean lineFeed) {
		if(!line.isEmpty() && lineFeed) line += '\n';
		mDataLines.add(line);
	}
	
	public void addData(ArrayList<String> data) {
		mDataLines.add(listToLine(data, true));
	}
	
	public void addData(List<String> data) {
		ArrayList<String> list = new ArrayList<String>(data);
		mDataLines.add(listToLine(list, true));
	}
	
	public void addObjectData(ArrayList<Object> data) {
		mDataLines.add(Commons.Utils.join(data.toArray(), Commons.CSVSeparator)+'\n');
	}
	
	public String getDataLine() {
		if(mDataLines.size() > 0)
			return mDataLines.get(mDataLines.size()-1);
		else 
			return null;
	}
	
	public String getDataLine(int index) {
		if(mDataLines.size() > index)
			return mDataLines.get(index);
		else 
			return null;
	}
	
	public int countDataLines() {
		return mDataLines.size();
	}
	
	public String listToLine(ArrayList<String> list, Boolean lineFeed) {
		String line = "";
		line = list.toString().replace("[", "");
		line = line.replace("]", "");
		
		if(!line.isEmpty() && lineFeed) line += '\n';
		return line;
	}
	
	private void writeMeta() throws IOException {
		if(mMeta!=null)
		for(String line : mMeta)
			writer.append(line+'\n');
	}
	
	private void writeHeader() throws IOException {
		if(mHeader!=null) {
			String line = listToLine(mHeader, true);
			writer.append(line);
		}
	}
	
	private void writeData() throws IOException {
		for(String line : mDataLines) 
			writer.append(line);
		
		mDataLines = new ArrayList<String>();
	}
	
	public void writeLine() throws IOException {
		String line = listToLine(mHeader, true);
		writer.append(line);
	}
	
	public void write() throws IOException {
		//Globals.Log(this, "File exists: "+csvfile.exists()+" - "+bAppend+" - "+csvfile.getAbsolutePath());
		synchronized (Commons.Locker.getLock(Commons.Locker.FILE_SYSTEM_LOCK)) {
			
			if(csvfile.exists() && !bAppend) {
				Commons.iLog("Existing file deleted because append not allowed");
				csvfile.delete();
				bAppend = true;
			}
			
			if(!csvfile.exists()) {
				Commons.iLog("CSV file not exists, writing headers in new file");
				writer = new FileWriter(csvfile, true); //creates the file if not present
				writeMeta();
				writeHeader();
			}
			else {
				writer = new FileWriter(csvfile, true);
			}
			
			writeData();
			
			commit();
		}
	}
	
	private void commit() throws IOException {
		writer.flush();
		writer.close();
	}
	
}
