package org.zm.model;

import org.zm.dao.UserAccountDO;
import org.zm.miki.BeanLookup;

public class UserAccount {
	
	private long id;
	private String uname;
	private String fname;
	private String lname;
	private String passwd;
	private long shard;
	
	private UserAccount() { }
	
	private UserAccount(String uname, String fname, String lname, String passwd) {
		this.uname = uname;
		this.fname = fname;
		this.lname = lname;
		this.passwd = passwd;
	}

	public long getId() {
		return id;
	}

	public String getUname() {
		return uname;
	}

	public String getFname() {
		return fname;
	}

	public String getLname() {
		return lname;
	}

	public String getPasswd() {
		return passwd;
	}
	
	public long getShard() {
		return shard;
	}

	public static UserAccountDO getDO() {
		return BeanLookup.getDOInstance(UserAccountDO.class);
	}
	
	public static UserAccount create(String uname, String fname, String lname, String passwd) {
		return getDO().create(new UserAccount.Builder(uname, fname, lname, passwd));
	}
	
	public static class Builder {
		private final UserAccount acc;
		
		private Builder(String uname, String fname, String lname, String passwd) {
			acc = new UserAccount(uname, fname, lname, passwd);
		}
		
		public UserAccount build(long shard) {
			acc.shard = shard;
			return acc;
		}
	}
	
	public void changePasswd(String oldPwd, String newPwd) {
		getDO().changePasswd(this, oldPwd, newPwd);
		this.passwd = newPwd;
	}
}
