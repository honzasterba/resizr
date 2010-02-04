package com.honzasterba.resizr.cache;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;

public class Cachr {

	public static final int LIMIT = 777 * 1024;

	@SuppressWarnings("unchecked")
	public static Image get(String url, String params) {
		PersistenceManager pm = Persistence.get().getPersistenceManager();
		try {
			String query = "select from " + CacheRecord.class.getName()
					+ " where url == '" + url + "' && params == '" + params
					+ "'";
			List<CacheRecord> list = (List<CacheRecord>) pm.newQuery(query)
					.execute();
			return createImage(list);
		} finally {
			pm.close();
		}
	}

	private static Image createImage(List<CacheRecord> list) {
		if (list.size() == 0) {
			return null;
		} else if (list.size() == 1) {
			return ImagesServiceFactory.makeImage(list.get(0).getData());
		} else {
			return ImagesServiceFactory.makeImage(concatList(list));
		}
	}

	private static byte[] concatList(List<CacheRecord> list) {
		int length = 0;
		for (CacheRecord r : list) {
			length += r.getData().length;
		}
		byte[] res = new byte[length];
		int written = 0;
		for (int i = 0; i < list.size(); i++) {
			for (CacheRecord r : list) {
				if (r.getFragmentIndex() == i) {
					System.arraycopy(r.getData(), 0, res, written,
							r.getData().length);
					written += r.getData().length;
					break;
				}
			}
		}
		return res;
	}

	public static void put(String url, String params, byte[] data) {
		PersistenceManager pm = Persistence.get().getPersistenceManager();
		try {
			if (data.length < LIMIT) {
				CacheRecord r = new CacheRecord(url, params, data, 0);
				pm.makePersistent(r);
			} else {
				saveFragmented(pm, url, params, data);
			}
		} finally {
			pm.close();
		}
	}

	private static void saveFragmented(PersistenceManager pm, String url,
			String params, byte[] data) {
		int last = 0;
		int index = 0;
		while (last < data.length) {
			int segmentLength = Math.min(LIMIT, data.length - last);
			byte[] segment = new byte[segmentLength];
			System.arraycopy(data, last, segment, 0, segmentLength);
			CacheRecord r = new CacheRecord(url, params, segment, index);
			pm.makePersistent(r);
			pm.flush();
			last += segmentLength;
			index++;
		}
	}
}
