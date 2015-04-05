package com.uqu.sensysframework.datamanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.uqu.sensysframework.commons.Commons;

public class TransferManager {
	
	private Context context;
	private TransferProtocol protocol;
	private TransferSchedule schedule;
	private TransferChannel channel;
	private TransferScheme scheme;
	
	private String server, username, password;
	
	private ArrayList<String> filelist;
	
	private boolean bUploading = false, bStopUploading = false, compressData = true, deleteAfterUpload = true, archiveData = true;
	
	private OnTransferTask mAsyncTask;
	
	private Handler mTransferHandler;
	private Runnable rTransfer;
	
	public static enum TransferProtocol {
		HTTP, FTP, NONE
	};
	
	public static enum TransferSchedule {
		ONCE(0), HOURLY(1000 * 60 * 60), TWICE_PER_DAY(HOURLY.getMillis() * 12), DAILY(HOURLY.getMillis() * 24);
		
		private final long millis;
	    TransferSchedule(long millis) { this.millis = millis; }
	    public long getMillis() { return millis; }
	};
	
	public static enum TransferChannel {
		WIFI, THREE_G, ANY
	};
	
	public static enum TransferScheme {
		SINGLE_FILE_TRANSFER, BULK_TRANSFER
	};
	
	public TransferManager(Context context) {
		this.setContext(context);
		this.setMode(TransferProtocol.NONE);
		this.setSchedule(TransferSchedule.HOURLY);
		this.setChannel(TransferChannel.WIFI);
		this.setScheme(TransferScheme.BULK_TRANSFER);
		this.setUploading(false);
		this.bStopUploading = false;
		this.mTransferHandler = new Handler();
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public TransferProtocol getMode() {
		return protocol;
	}

	public void setMode(TransferProtocol mode) {
		this.protocol = mode;
	}

	public TransferSchedule getSchedule() {
		return schedule;
	}

	public void setSchedule(TransferSchedule schedule) {
		this.schedule = schedule;
	}

	public TransferChannel getChannel() {
		return channel;
	}

	public void setChannel(TransferChannel channel) {
		this.channel = channel;
	}

	public TransferScheme getScheme() {
		return scheme;
	}

	public void setScheme(TransferScheme scheme) {
		this.scheme = scheme;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	protected String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public ArrayList<String> getFilelist() {
		return filelist;
	}

	public void setFilelist(ArrayList<String> filelist) {
		this.filelist = filelist;
	}
	
	public boolean isUploading() {
		return bUploading;
	}

	public void setUploading(boolean bUploading) {
		this.bUploading = bUploading;
	}
	
	public void enableTransfer(TransferProtocol mode, String server, String username, String password) {
		this.setMode(mode);
		this.setServer(server);
		this.setUsername(username);
		this.setPassword(password);
	}
	
	public void disableTransfer() {
		setMode(TransferProtocol.NONE);
		setServer("");
	}
	
	public boolean doTransfer() {
		if(getServer().equals("") || getMode().equals(TransferProtocol.NONE))
			return false;
		
		return true;
	}
	
	public void setDataParams(boolean compressData, boolean deleteAfterUpload, boolean archiveData) {
		setCompressData(compressData);
		setDeleteAfterUpload(deleteAfterUpload);
		setArchiveData(archiveData);
	}
	
	public void scheduleTransfer(TransferSchedule schedule, TransferChannel channel, TransferScheme scheme) {
		this.setSchedule(schedule);
		this.setChannel(channel);
		this.setScheme(scheme);
		this.bStopUploading = false;
		this.startTransfer();
	}
	
	public void startTransfer() {
		if(this.doTransfer() && !this.isUploading() && !this.bStopUploading) {
			this.prepareData();
			
			this.setUploading(true);
			
			Commons.iLog("TransferManager: In startTransfer");
			mAsyncTask = new OnTransferTask(getContext());
			mAsyncTask.execute();
			
			rTransfer = new Runnable() {
	            @Override
	            public void run() {
	            	startTransfer();
	            }
	        };
	        
	        if(getSchedule().getMillis() > 0) //not UPLOAD_ONCE
	        	mTransferHandler.postDelayed(rTransfer, getSchedule().getMillis());
		}
	}
	
	public void stopTransfer() {
		Commons.iLog("TransferManager: In stopTransfer");
		mTransferHandler.removeCallbacks(rTransfer);
		this.bStopUploading = true;
	}
	
	private void prepareData() {
		if(Commons.FileSystem.getFilesCount(Commons.FileSystem.getDataDir()) > 0) {
			boolean bCompressed = false;
			if(isCompressData()) {
				try {
					bCompressed = Commons.FileSystem.compressDirectory(Commons.FileSystem.getDataDir(), Commons.FileSystem.getUploadDir());
					if(bCompressed) {
						Commons.FileSystem.cleanDir(Commons.FileSystem.getDataDir());
					}
				}
				catch(IOException e) {
					bCompressed = false;
				}
			}
			else {
				Commons.FileSystem.moveContents(Commons.FileSystem.getDataDir(), Commons.FileSystem.getUploadDir());
			}
		}
	}

	public boolean isCompressData() {
		return compressData;
	}

	public void setCompressData(boolean compressData) {
		this.compressData = compressData;
	}

	public boolean isDeleteAfterUpload() {
		return deleteAfterUpload;
	}

	public void setDeleteAfterUpload(boolean deleteAfterUpload) {
		this.deleteAfterUpload = deleteAfterUpload;
	}

	public boolean isArchiveData() {
		return archiveData;
	}

	public void setArchiveData(boolean archiveData) {
		this.archiveData = archiveData;
	}

	private class OnTransferTask extends AsyncTask<Boolean, Boolean, Boolean> {
		
		private Context _c;
		
		public OnTransferTask(Context c) {
			_c = c;
		}
		
		@Override
		protected Boolean doInBackground(Boolean... arg0) {
			
			Boolean status = false;
			Commons.iLog("TransferManager: AsyncTask -> doInBackground");
			synchronized (Commons.Locker.getLock(Commons.Locker.FILE_UPLOAD_LOCK)) {
				
				if(Commons.FileSystem.getFilesCount(Commons.FileSystem.getUploadDir()) > 0) {
					setFilelist(Commons.FileSystem.getFiles(Commons.FileSystem.getUploadDir()));
					status = doHTTPTransfer();
					
					//mBroadcastIntent.putExtra(Globals.KEY_UPLOAD_STATUS, Globals.KEY_UPLOAD_FINISH);
					//mBroadcastIntent.putExtra(Globals.KEY_UPLOAD_FINISH, status);
					//sendBroadcast(mBroadcastIntent);
					
				}
				else {
					Commons.iLog("TransferManager: No files to upload.");
				}
				setUploading(false);
			}
			
			return status;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			//_c.stopService(_rcIntent);
		}
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			Commons.iLog("Cancelled");
		}
		
		private Boolean doHTTPTransfer() {
			
			Boolean bTransfer = false;
			String message = "";
			Commons.iLog("TransferManager: doHTTPTransfer Start");
			
			String urlString = getServer()+"";
			
			//mBroadcastIntent.putExtra(Globals.KEY_UPLOAD_STATUS, Globals.KEY_UPLOAD_RUNNING);
			//mBroadcastIntent.putExtra(Globals.KEY_UPLOAD_RUNNING, new Boolean(true));
			//sendBroadcast(mBroadcastIntent);
			
			HttpClient httpclient = new DefaultHttpClient();
	        HttpPost httppost = new HttpPost(urlString);
	        Commons.iLog("doHTTPTransfer -> post to "+urlString);
			try {
		        StringBuilder authentication = new StringBuilder().append(getUsername()).append(":").append(getPassword());
	            String result = com.uqu.sensysframework.commons.Base64.encodeBytes(authentication.toString().getBytes());
	            httppost.setHeader("Authorization", "Basic " + result);
	
		        MultipartEntity reqEntity = new MultipartEntity();
		        
		        ArrayList<String> filelist = getFilelist();
		        
		        for(String mFilename : filelist) {
		        	Commons.iLog("doHTTPTransfer adding file -> "+mFilename);
		        	reqEntity.addPart("UploadedFiles[]", new FileBody(new File(mFilename)));
		        }
		        httppost.setEntity(reqEntity);
			}
			catch(Exception e) {
				Commons.eLog("Upload error: "+e.getMessage());
				message = "Upload error: "+e.getMessage();
			}
			
	        try {
	        	Commons.iLog("doHTTPTransfer executing post");
	            HttpResponse response = httpclient.execute(httppost);
	            
	            BufferedReader reader = new BufferedReader(
	                    new InputStreamReader(
	                            response.getEntity().getContent(), "UTF-8"));
	            String sResponse;
	            StringBuilder mUploadResponse = new StringBuilder();
	            Commons.iLog("doHTTPTransfer reading response");
	            while ((sResponse = reader.readLine()) != null) {
	                mUploadResponse = mUploadResponse.append(sResponse);
	            }
	            
	            Commons.iLog(mUploadResponse.toString());
	            message = "Data files uploaded";
	            bTransfer = true;

	        } catch (ClientProtocolException e) {
	        	Commons.eLog("TransferManager error");
	            Commons.eLog(
	                    "The server ClientProtocolException response message : "
	                            + e.getMessage());
	            message = "The server ClientProtocolException response message : "
                        + e.getMessage();
	            e.printStackTrace();
	        } catch (IOException e) {
	            Commons.eLog("The server  IOException response message : "
	                    + e.getMessage());
	            message = "The server  IOException response message : "
	                    + e.getMessage();
	            e.printStackTrace();
	        } 
	        catch (Exception e) {
	        	message = "Unknown Error " + e.getMessage();
	        }
	        
	        Commons.iLog("doHTTPTransfer End "+message);
	        //mBroadcastIntent.putExtra(Globals.KEY_UPLOAD_MESSAGE, message);
	        
	        if(bTransfer && isArchiveData()) {
				Commons.FileSystem.moveContents(Commons.FileSystem.getUploadDir(), Commons.FileSystem.getArchiveDir());
			}
			else if(bTransfer) { //clean everything because no archiving required, and bulk upload is used
				Commons.FileSystem.cleanDir(Commons.FileSystem.getUploadDir());
			}
	        
	        return bTransfer;
		}
	}
}
