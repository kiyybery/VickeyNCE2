package cn.ttsk.nce2.entity;

import java.util.List;
import java.util.Set;

public class Section {

	public static final int S_LOCKED = 1;// 开启
	public static final int S_OPEN = 0;// 加锁
	public static final int S_FINISHED = 2;// 已学完
	public String title;// 标题
	public String id;
	public String cid;//课程id
	public int status;
	public String wt_id;//背单词
	public String recite_id;//口语练习
	public List<Video> videos;//视频
	public static final int S_DELETE = 1;// 表示在删除
	public static final int S_NOTEDALETE = 2;// 表示不是删除动作
	public Float price;//价格
	public long free_time;//免费开始时间
	public String img;//封面图
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setCid(String cid) {
		this.cid = cid;
	}
	
	public String getCid() {
		return cid;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setWt_id(String wt_id) {
		this.wt_id = wt_id;
	}
	
	public String getWt_id() {
		return wt_id;
	}
	
	public void setRecite_id(String recite_id) {
		this.recite_id = recite_id;
	}
	
	public String getRecite_id() {
		return recite_id;
	}
	
	public void setVideos(List<Video> videos) {
		this.videos = videos;
	}
	
	public List<Video> getVideos() {
		return videos;
	}
	
	public void setPrice(Float price) {
		this.price = price;
	}
	
	public Float getPrice() {
		return price;
	}
	
	public void setFree_time(long free_time) {
		this.free_time = free_time;
	}
	
	public long getFree_time() {
		return free_time;
	}
	
	public void setImg(String img) {
		this.img = img;
	}
	
	public String getImg() {
		return img;
	}
}
