package cn.ttsk.nce2.entity;

import java.util.List;
/*
 * 课程对应的实体类
 */
public class Course {
	public String id;//课程id
	public String title;//课程title
	public List<Section> sections;//单元信息
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	public List<Section> getSections() {
		return sections;
	}
}
