package cn.ttsk.nce2.entity;

import java.io.Serializable;

public class UserInfo implements Serializable{
	public String id;
	public String uname;
	public String avatar;
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
	public void setUname(String uname) {
		this.uname = uname;
	}
	
	public String getUname() {
		return uname;
	}
		
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getAvatar() {
		return avatar;
	}
}
