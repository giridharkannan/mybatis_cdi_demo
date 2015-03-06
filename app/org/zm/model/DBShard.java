package org.zm.model;

public class DBShard {
	
	private long id;
	private String driver;
	private String url;
	private String usr;
	private String passwd;
	//Used to get free DB when creating new User
	//At present weight represents number of Users in this shard
	private int weight;
	
	private DBShard() {}
	
	public long getId() {
		return id;
	}
	
	public String getDriver() {
		return driver;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getUsr() {
		return usr;
	}
	
	public String getPasswd() {
		return passwd;
	}
	
	public int getWeight() {
		return weight;
	}
		
}
