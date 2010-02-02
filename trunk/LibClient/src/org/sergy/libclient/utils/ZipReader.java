package org.sergy.libclient.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ZipReader {
	private static final File file = new File("/sdcard/libattachedfiles.zip");
	
	public static Bitmap getBitmap(String name) throws IOException {
		Bitmap result = null;
		ZipFile zip = null;
		try {
			zip = new ZipFile(file, ZipFile.OPEN_READ);
		
			ZipEntry entry = zip.getEntry(name);
			if (entry != null && !entry.isDirectory()) {
				InputStream is = null;
				try {
					is = zip.getInputStream(entry);
					result = BitmapFactory.decodeStream(is);
				} finally {
					if (is != null) {
						is.close();
					}
				}
				
			}
		} finally {
			if (zip != null) {
				zip.close();
			}
		}
		return result;
	}

}
