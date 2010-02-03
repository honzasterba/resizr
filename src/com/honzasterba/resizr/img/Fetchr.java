package com.honzasterba.resizr.img;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;

public class Fetchr {

	public static Image fetchImage(String urlString) throws Exception {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream is = conn.getInputStream();
		byte[] data = readStream(is);
		return ImagesServiceFactory.makeImage(data);
	}

	private static byte[] readStream(InputStream is) throws Exception {
		byte[] data = new byte[is.available()];
		int read = 0;
		int totalRead = 0;
		while ((read = is.read(data, totalRead, data.length - totalRead)) != -1) {
			totalRead += read;
			if (totalRead == data.length) {
				byte[] d = new byte[data.length + 1024];
				System.arraycopy(data, 0, d, 0, data.length);
				data = d;
			}
		}
		// return just the right length
		if (totalRead != data.length) {
			byte[] d = new byte[totalRead];
			System.arraycopy(data, 0, d, 0, totalRead);
			data = d;
		}
		return data;
	}

}
