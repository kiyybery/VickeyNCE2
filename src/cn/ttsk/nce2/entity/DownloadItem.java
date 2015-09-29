package cn.ttsk.nce2.entity;

import java.io.File;

import net.tsz.afinal.annotation.sqlite.Id;

import cn.ttsk.nce2.NCE2;

public class DownloadItem {

	@Id(column="video_id")
	public String video_id;
	public static final int STATUS_FINISHED = 20;
	public static final int STATUS_PAUSED = 10;
	public static final int STATUS_DOWNLOADING = 0;
	public static final int STATUS_FAILED = 15;
	public String section_id;
	public int status;
	public String title;
	public String url;
	public String img;
	public int length;
	public long size;
	public String tmpFile;
	public String file;
	public long downloaded;
	public long created;
	
	public DownloadItem() {}
		
	public static DownloadItem fromVideo(String section_id, Video video, int status) {
		DownloadItem item = new DownloadItem();
		item.section_id = section_id;
		item.video_id = video.id;
		item.status = status;
		item.title = video.title;
		item.url = video.url;
		item.img = video.img;
		item.length = video.length;
		item.size = video.size;
		item.tmpFile = NCE2.getVideoTmpFile(video.id).getAbsolutePath();
		item.file = NCE2.getVideoFile(video.id).getAbsolutePath();
		item.created = System.currentTimeMillis();
		return item;
	}
	
	public File getVideoCover() {
		Section section = NCE2.db.findById(section_id, Section.class);
		if (section != null) {
			return NCE2.getSectionCover(section);
		} else {
			return null;
		}
	}
	
	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}
	
	public String getVideo_id() {
		return video_id;
	}
	
	public void setSection_id(String section_id) {
		this.section_id = section_id;
	}
	
	public String getSection_id() {
		return section_id;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return status;
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
	
	public void setTmpFile(String tmpFile) {
		this.tmpFile = tmpFile;
	}
	
	public String getTmpFile() {
		return tmpFile;
	}
	
	public void setFile(String file) {
		this.file = file;
	}
	
	public String getFile() {
		return file;
	}
	
	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}
	
	public long getDownloaded() {
		return downloaded;
	}
	
	public void setCreated(long created) {
		this.created = created;
	}
	
	public long getCreated() {
		return created;
	}
}
