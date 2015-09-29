package cn.ttsk.nce2.entity;

import java.util.ArrayList;
import java.util.List;

/*
 * 背单词练习实体
 * 
 */
public class WordTest {
	public static final int TYPE_ENG_CHN = 1;//根据英文选中文
	public static final int TYPE_CHN_ENG = 2;//根据中文选英文
	public static final int TYPE_CHN_WRITE = 3;//根据中文写英文
	public static final int TYPE_IMG_ENG = 4;//根据图片选英文
	public static final int TYPE_ENG_IMG = 5;//根据英文选图片
	public int type;//题型
	public String content;//题面，文字或图片地址
	public String right_answer;//填空题答案
	public int right_index;//选择题答案
	public List<String> choices = new ArrayList<String>();//选择题选项
	public WordItem analysis;//解析
}
