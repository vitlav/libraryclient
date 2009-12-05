package org.sergy.libclient.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class BookDownloader extends Thread {
	public final static String KEY_SIZE = "size";
	public final static String KEY_STATE = "state";
	public final static String KEY_CURRENT = "current";
	public final static String KEY_MESSAGE = "message";
	
	//Donload process states
	public final static int CONNECTING = 0;
	public final static int DOWLOADING = 1;
	public final static int FINISHED = 2;
	public final static int ERROR = 3;
	public final static int ERROR_BAD_FILE_RETURNED = 4;
	public final static int ERROR_RESPONSE_CODE = 5;
	
	
	
	private final static int BUF_SIZE = 1024 * 10; //kb
	private final static String LIB_URL = "http://lib.rus.ec/b/";
	private final static String DIR = "Books";
	private final static String EXTENSION = "zip";
	private final static int SLEEP_ON_ERROR = 2000; //Sleep before exit in error.
	
	private String url;
	private Handler handler;
	
	public BookDownloader(long id, String format, Handler handler) throws IOException {
		url = LIB_URL + String.valueOf(id) + "/" + format;
		this.handler=handler;
	}
	
	@Override
	public void run() {
		sendMessage(CONNECTING, "", 0, -1);
		
		BufferedInputStream in = null;
		BufferedOutputStream bout = null;
		FileOutputStream fos = null;
		HttpURLConnection connection = null;
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection.setFollowRedirects(true);
			connection = (HttpURLConnection)httpUrl.openConnection();
			connection.setInstanceFollowRedirects(true);
			connection.connect();
			int resopnseCode = connection.getResponseCode();
			
			if (resopnseCode == HttpURLConnection.HTTP_OK) {
				String fname  = getFileName(connection.getURL().toString());
				String ext = getFileExtension(fname);
				int size = connection.getContentLength();
				File root = Environment.getExternalStorageDirectory();
				in = new BufferedInputStream(connection.getInputStream());
				
				if (EXTENSION.equalsIgnoreCase(ext)) {
					File dir = new File(root, DIR);
					dir.mkdirs();
					File file = new File(dir, fname);
					fos = new FileOutputStream(file);

					bout = new BufferedOutputStream(fos, BUF_SIZE);
					byte data[] = new byte[BUF_SIZE];
					int total = 0;
					int current = 0;
					
					sendMessage(DOWLOADING, fname, size, total);
					
					while((current = in.read(data,0,BUF_SIZE)) >=0) {
						bout.write(data, 0, current);
						
						total += current;
						sendMessage(DOWLOADING, fname, size, total);
					}
					
				} else {
					sendMessage(ERROR_BAD_FILE_RETURNED, fname, 0, -1);
					sleep(SLEEP_ON_ERROR);
				}
			} else {
				sendMessage(ERROR_RESPONSE_CODE, connection.getResponseMessage(), 0, -1);
				sleep(SLEEP_ON_ERROR);
			}
					
		} catch (MalformedURLException e) {
			sendExceptionMessage(e);
		} catch (IOException e) {
			sendExceptionMessage(e);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (bout != null) {
				try {
					bout.close();
					fos.close();
				} catch (IOException e) {
					sendExceptionMessage(e);
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					sendExceptionMessage(e);
				}
			}
			
			sendMessage(FINISHED, "", 0, -1);
			if (connection != null) {
				connection.disconnect();
			}
			
			
		}
		
	}
	
	private void sendExceptionMessage(Exception e) {
		sendMessage(ERROR, e.getClass() + ":" + e.getMessage(), 0, -1);
		try {
			sleep(SLEEP_ON_ERROR);
		} catch (InterruptedException e1) {
		}
	}
	
	private void sendMessage(int state, String message, int size, int current) {
		Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt(KEY_STATE, state);
        b.putString(KEY_MESSAGE, message);
        b.putInt(KEY_SIZE, size);
        b.putInt(KEY_CURRENT, current);
        msg.setData(b);
        handler.sendMessage(msg);
	}
	
	/**
	 * Returns part of path after last '/'
	 * @param path
	 * @return
	 */
	private String getFileName(String path) {
		return path != null ? path.substring(path.lastIndexOf('/') + 1) : null;
	}
	
	/**
	 * Returns file extension
	 * @param fname
	 * @return
	 */
	private String getFileExtension(String fname) {
		return fname != null ? fname.substring(fname.lastIndexOf(".") + 1) : null;
	}
}
