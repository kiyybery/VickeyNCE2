package cn.ttsk.nce2.entity;

import java.util.ArrayList;


public class TextListenTestChoiceItem {
	public int id;
	public String answer;
	
	public TextListenTestChoiceItem() {}
	
	public static ArrayList<TextListenTestChoiceItem> getDemo() {
		ArrayList<TextListenTestChoiceItem> list = new ArrayList<TextListenTestChoiceItem>();
		TextListenTestChoiceItem item1 = new TextListenTestChoiceItem();
		item1.answer = "Is this your handbag?";
		list.add(item1);
		TextListenTestChoiceItem item2 = new TextListenTestChoiceItem();
		item2.answer = "Thank you very much.";
		list.add(item2);
		TextListenTestChoiceItem item3 = new TextListenTestChoiceItem();
		item3.answer = "Excuse me";
		list.add(item3);
		TextListenTestChoiceItem item4 = new TextListenTestChoiceItem();
		item4.answer = "Is This your hand?";
		list.add(item4);
		return list;
	}
}
