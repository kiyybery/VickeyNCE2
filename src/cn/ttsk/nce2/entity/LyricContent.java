package cn.ttsk.nce2.entity;


public class LyricContent {

	
	public int id;
	private String sid;
	private int idx;
	private String Lyric;
    private int LyricTime;

    
    public int getId() {
		return id;
	}
    
    public void setId(int id) {
		this.id = id;
	}
    
    
    public String getSid() {
		return sid;
	}
    
    public void setSid(String sid) {
		this.sid = sid;
	}
    
    public int getIdx() {
		return idx;
	}
    
    public void setIdx(int idx) {
		this.idx = idx;
	}
    
	public String getLyric() {
		return Lyric;
	}

	public void setLyric(String lyric) {
		Lyric = lyric;
	}

	public int getLyricTime() {
		return LyricTime;
	}

	public void setLyricTime(int lyricTime) {
		LyricTime = lyricTime;
	}
	
}
