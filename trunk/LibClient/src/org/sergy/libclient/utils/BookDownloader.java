package org.sergy.libclient.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Entity;

import android.os.Environment;

public class BookDownloader {
	private final static String LIB_URL = "http://lib.rus.ec/b/";
	private final static String DIR = "Books";
	private final static String EXTENSION = "zip";
	
	public void downloadBook(long id, String format) throws IOException {
		String url = LIB_URL + String.valueOf(id) + "/" + format;
		download(url);
	}
	
	private void download(String url) throws IOException {
		BufferedInputStream in = null;
		BufferedOutputStream bout = null;
		HttpURLConnection connection = null;
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection.setFollowRedirects(true);
			connection = (HttpURLConnection)httpUrl.openConnection();
			connection.setInstanceFollowRedirects(true);
			//connection.setRequestMethod("GET");
			connection.connect();
			int resopnseCode = connection.getResponseCode();
			/*HttpGet httpGet = new HttpGet(url);
			DefaultHttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();*/
			String fname  = getFileName(connection.getURL().toString());
			String ext = getFileExtension(fname);
			int size = connection.getContentLength();
			File root = Environment.getExternalStorageDirectory();
			in = new BufferedInputStream(connection.getInputStream());
			
			if (root.canWrite() && EXTENSION.equalsIgnoreCase(ext)) {
				File dir = new File(root, DIR);
				dir.mkdirs();
				File file = new File(dir, fname);
				FileOutputStream fos = new FileOutputStream(file);
				bout = new BufferedOutputStream(fos,1024);
				
				byte data[] = new byte[1024];
				while(in.read(data,0,1024)>=0) {
					bout.write(data);
				}
				
			}
					
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (bout != null) {
				bout.close();
			}
			if (in != null) {
				in.close();
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
		
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
