package com.honzasterba.resizr.cache;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class Persistence {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private Persistence() {
	}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}

}
