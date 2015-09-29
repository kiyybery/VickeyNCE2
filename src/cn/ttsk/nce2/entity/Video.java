package cn.ttsk.nce2.entity;

public class Video {
	public String id;
	public String sid;
	public String title;
	public String url;
	public String img;
	public int length;
	public long size;
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setSid(String sid) {
		this.sid = sid;
	}
	
	public String getSid() {
		return sid;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setImg(String img) {
		this.img = img;
	}
	
	public String getImg() {
		return img;
	}
	
	public void setLength(int length) {
		this.length = length;
	}
	
	public int getLength() {
		return length;
	}
	
	public void setSize(long size) {
		this.size = size;
	}
	
	public long getSize() {
		return size;
	}
	
}
