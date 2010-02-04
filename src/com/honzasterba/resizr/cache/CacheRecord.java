package com.honzasterba.resizr.cache;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class CacheRecord {
	
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.SEQUENCE)
	private String id;
	
	@Persistent
	private String url;

	@Persistent
	private int fragmentIndex;
	
	@Persistent
	private String params;

	@Persistent
	private Date created;

	@Persistent
	private Date lastAccessed;

	@Persistent
	private Blob data;

	public CacheRecord(String aUrl, String aParams, byte[] aData, int aFragmentIndex) {
		url = aUrl;
		if (aParams != null) {
			params = aParams;
		} else {
			params = "";
		}
		data = new Blob(aData);
		fragmentIndex = aFragmentIndex;
		created = lastAccessed = new Date();
	}
	
	public String getId() {
		return id;
	}
	
	public int getFragmentIndex() {
		return fragmentIndex;
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

	public byte[] getData() {
		return data.getBytes();
	}

}
