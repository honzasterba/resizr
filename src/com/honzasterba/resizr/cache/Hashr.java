package com.honzasterba.resizr.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashr {
	public static String hash(String key) {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not available.", e);
		}
		byte[] md5hash = new byte[32];
		md.update(key.getBytes(), 0, key.length());
		md5hash = md.digest();
		return convertToHex(md5hash);

	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

}