package cn.ttsk.nce2.entity;


public class WordItem {
	public String id;//唯一id
	public String word;//英文
	public String chn;//中文
	public String symbol;//音标
	public String pron;//发音音频
	public String img;//图片，不一定有
	public String wt_id;//对应练习
	public String exp;//英文例句
	public String exp_chn;//中文例句
	public int right_count;//正确数
	public int wrong_count;//错误数
	public int last_time;//最后一次做题时间
	
	public WordItem() {}
	public WordItem(String id, String word, String chn, String symbol, String pron, String img, String wt_id, String exp, String exp_chn) {
		this.id = id;
		this.word = word;
		this.chn = chn;
		this.symbol = symbol;
		this.pron = pron;
		this.img = img;
		this.wt_id = wt_id;
		this.exp = exp;
		this.exp_chn = exp_chn;
		this.right_count = 0;
		this.wrong_count = 0;
		this.last_time = 0;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public String getWord() {
		return word;
	}
	
	public void setChn(String chn) {
		this.chn = chn;
	}
	
	public String getChn() {
		return chn;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setPron(String pron) {
		this.pron = pron;
	}
	
	public String getPron() {
		return pron;
	}
	
	public void setImg(String img) {
		this.img = img;
	}
	
	public String getImg() {
		return img;
	}
	
	public void setWt_id(String wt_id) {
		this.wt_id = wt_id;
	}
	
	public String getWt_id() {
		return wt_id;
	}
	
	public void setExp(String exp) {
		this.exp = exp;
	}
	
	public String getExp() {
		return exp;
	}
	
	public void setExp_chn(String exp_chn) {
		this.exp_chn = exp_chn;
	}
	
	public String getExp_chn() {
		return exp_chn;
	}
	
	public void setRight_count(int right_count) {
		this.right_count = right_count;
	}
	
	public int getRight_count() {
		return right_count;
	}
	
	public void setWrong_count(int wrong_count) {
		this.wrong_count = wrong_count;
	}
	
	public int getWrong_count() {
		return wrong_count;
	}
	
	public void setLast_time(int last_time) {
		this.last_time = last_time;
	}
	
	public int getLast_time() {
		return last_time;
	}
	
	
}
