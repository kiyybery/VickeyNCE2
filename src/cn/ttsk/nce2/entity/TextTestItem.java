package cn.ttsk.nce2.entity;

import java.util.List;
public class TextTestItem {
	public static final int TYPE_CHOICE = 0;//选择题
	public static final int TYPE_BLANK = 1;//填空题
	public String id;//唯一id
	public String recite_id;//对应的背课文id
	public int type;//类型
	public int answer_count;//选择题时表示选项数量
	public String subject;//填空题时为题面
	public String text;//选择题时的正确答案的文字
	public String mp3;//音频文件名
	public String right_answer;//选择题时为正确答案的序号，填空题时为正确答案
	public List<TextListenTestChoiceItem> answer;
}
