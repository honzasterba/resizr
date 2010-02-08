package com.honzasterba.resizr.cache;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.honzasterba.bigblobae.BigBlob;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class CacheRecord {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String url;

	@Persistent
	private String params;

	@Persistent
	private Date created;

	@Persistent
	private Date lastAccessed;

	@Persistent
	private BigBlob dataBlob;

	public CacheRecord(String aUrl, String aParams, byte[] aData) {
		url = aUrl;
		if (aParams == null) {
			params = "";
		} else {
			params = aParams;
		}
		dataBlob = new BigBlob(aData);
		created = lastAccessed = new Date();
	}
	
	public String getUrl() {
		return url;
	}

	public String getParams() {
		return params;
	}

	public Date getCreated() {
		return created;
	}

	public Date getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(Date lastAccessed) {
		this.lastAccessed = lastAccessed;
	}
	
	public BigBlob getDataBlob() {
		return dataBlob;
	}

	public byte[] getData() {
		return getDataBlob().getData();
	}

}
