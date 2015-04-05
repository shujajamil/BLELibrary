package com.uqu.sensysframework.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class Commons {
	public static String TAG = "BLEFramework";
	
	public static final String ROOT_DIR = "bleframework";
	public static final String DATA_DIR = ROOT_DIR+"/data";
	public static final String UPLOAD_DIR = ROOT_DIR+"/upload";
	public static final String ARCHIVE_DIR = ROOT_DIR+"/archive";
	
	public static final String CSVSeparator = ",";
	
	public static DateTime DateTime = new DateTime();
	
	public static ZipUtility ZipUtility = new ZipUtility();
	
	public static void iLog(String message) {
		android.util.Log.i(Commons.TAG, message);
	}
	
	public static void eLog(String message) {
		android.util.Log.e(Commons.TAG, message);
	}
	
	public static void Toast(String message) {
		android.widget.Toast.makeText(ApplicationContext.getContext(), message, Toast.LENGTH_LONG).show();
	}
	
	public static class Locker {
		
		//Locks
		public static final String FILE_SYSTEM_LOCK = "FILE_SYSTEM_LOCK";
		public static final String FILE_UPLOAD_LOCK = "FILE_UPLOAD_LOCK";
		
		private static final HashMap<String, Object> locks = new HashMap<String, Object>();
		
		public static Object getLock(String key) {
			if(!locks.containsKey(key)) {
				locks.put(key, new Object());
			}
			
			return locks.get(key);
		}
	}
	
	public static class FileSystem {
		public static String getRootDir() {
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ROOT_DIR+"/";
		}
		
		public static String getDataDir() {
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+DATA_DIR+"/";
		}
		
		public static String getUploadDir() {
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+UPLOAD_DIR+"/";
		}
		
		public static String getArchiveDir() {
			return Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+ARCHIVE_DIR+"/";
		}
		
		public static void cleanDir(String path) {
			File directory = new File(path);
		   	File[] files = directory.listFiles();
		   	for (File file : files)
		   	{
		   		try {
		   			file.delete();
		   		}
		   		catch(Exception e) {
		   			Commons.eLog("Unable to delete "+file.getAbsolutePath());
		   		}
		   	}
		}
		
		public static boolean createRootDir() {
			boolean bStatus = true;
			File root = new File(getRootDir());//Environment.getExternalStorageDirectory();
			if(!root.exists()) {
				root.mkdirs();
				
				if(!root.isDirectory()) {
					//Toast.makeText(, "Unable to create root directory at "+root.getAbsolutePath()+" Please create it manually before using the collector.", Toast.LENGTH_LONG).show();
					bStatus = false;
				}
				else
					bStatus = true;//Toast.makeText(c, "Successfully created root directory at "+root.getAbsolutePath()+"", Toast.LENGTH_SHORT).show();
			}
			
			if(bStatus) {
				root = new File(getDataDir());
				if(!root.exists()) {
					root.mkdirs();
					
					if(!root.isDirectory()) {
						bStatus = false;
					}
					else
						bStatus = true;
				}	
			}
			
			if(bStatus) {
				root = new File(getUploadDir());
				if(!root.exists()) {
					root.mkdirs();
					
					if(!root.isDirectory()) {
						bStatus = false;
					}
					else
						bStatus = true;
				}	
			}
			
			if(bStatus) {
				root = new File(getArchiveDir());
				if(!root.exists()) {
					root.mkdirs();
					
					if(!root.isDirectory()) {
						bStatus = false;
					}
					else
						bStatus = true;
				}	
			}
			
			return bStatus;
		}
		
		public static ArrayList<String> getFiles(String path) {
			ArrayList<String> filelist = new ArrayList<String>();
		    File directory = new File(path);
		    File[] files = directory.listFiles();
		    for (File file : files)
		    {
		  	   filelist.add(file.getAbsolutePath());
		    }
		    return filelist;
		}
		
		public static int getFilesCount(String path) {
			try {
				return (new File(path)).list().length;
			}
			catch(Exception e) {
				return 0;
			}
		}
		
		public String convertStreamToString(InputStream is) throws Exception {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		      sb.append(line).append("\n");
		    }
		    return sb.toString();
		}

		public String getStringFromFile (String filePath) throws Exception {
		    File fl = new File(filePath);
		    FileInputStream fin = new FileInputStream(fl);
		    String r = convertStreamToString(fin);
		    //Make sure you close all streams.
		    fin.close();
		    return r;
		}
		
		public static boolean moveContents(String from, String to) {
			File directory = new File(from);
		   	File[] files = directory.listFiles();
		   	boolean bMoved = true;
		   	for (File file : files)
		   	{
		   		try {
		   			file.renameTo(new File(to+file.getName()));
		   		}
		   		catch(Exception e) {
		   			Commons.eLog("Unable to move "+file.getAbsolutePath());
		   			bMoved = false;
		   		}
		   	}
		   	
		   	return bMoved;
		}		
		
		public static boolean compressDirectory(String sourceDir, String destDir) throws IOException {
			File source = new File(sourceDir);
			File zipFile = new File(destDir+Commons.DateTime.getNowPlain()+".zip");
			
			if(source.list().length > 0)
				com.uqu.sensysframework.commons.ZipUtility.zipDirectory(source, zipFile);
			
			if(zipFile.exists() && zipFile.length() > 0)
				return true;
			else
				return false;
		}
		
	}
	
	public static class Utils {
		public static String join(Object r[],String d)
		{
		        if (r.length == 0) return "";
		        StringBuilder sb = new StringBuilder();
		        int i;
		        for(i=0;i<r.length-1;i++)
		            sb.append(String.valueOf(r[i])+d);
		        return sb.toString()+r[i];
		}
	}
}
