package com.honzasterba.resizr.cache;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;

public class Cachr {

	public static Logger logger = Logger.getLogger(Cachr.class.getPackage()
			.getName());

	@SuppressWarnings("unchecked")
	public static Image get(String url, String params) {
		PersistenceManager pm = Persistence.get().getPersistenceManager();
		if (params == null) {
			params = "";
		}
		try {
			String query = "select from " + CacheRecord.class.getName()
					+ " where url == '" + url + "' && params == '" + params
					+ "'";
			List<CacheRecord> list = (List<CacheRecord>) pm.newQuery(query)
					.execute();
			if (list.size() == 0) {
				logger.info("MISS: " + url + " " + params);
				return null;
			} else {
				logger.info("HIT " + url + " " + params);
				if (list.size() > 1) {
					for (int i = 1; i < list.size(); i++) {
						logger.info("MULTIHIT: deleting " + (list.size()-1));
						pm.deletePersistent(list.get(i));
					}
				}
				return ImagesServiceFactory.makeImage(list.get(0).getData());
			}
		} finally {
			pm.close();
		}
	}

	public static void put(String url, String params, byte[] data) {
		PersistenceManager pm = Persistence.get().getPersistenceManager();
		try {
			logger.info("PUT: " + url + " " + params);
			CacheRecord r = new CacheRecord(url, params, data);
			pm.makePersistent(r);
		} finally {
			pm.close();
		}
	}

}
