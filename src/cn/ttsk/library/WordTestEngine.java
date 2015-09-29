package cn.ttsk.library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.entity.WordItem;
import cn.ttsk.nce2.entity.WordTest;
import cn.ttsk.nce2.entity.WordTestResult;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WordTestEngine {
	public static final int MAX_RIGHT = 1;
	private String section_id;
	private List<WordItem> allWords = new ArrayList<WordItem>();
	private List<WordItem> testingWords = new ArrayList<WordItem>();

	public WordTestEngine(String section_id)  {
		this.section_id = section_id;
		allWords = NCE2.db.findAllByWhere(WordItem.class, "wt_id=\"" + section_id + "\"");
		if (allWords != null && allWords.size() > 0) {
			testingWords.addAll(allWords);
		}
	}
	
	public Boolean needInit() {
		return allWords == null || allWords.size() == 0;
	}

	public boolean hasNext() {
		return testingWords.size() > 0;
	}

	public WordTest getNext() {
		// 随机抽取一个单词
		WordTest wordTest = new WordTest();
		Random random = new Random();
		int id = random.nextInt(testingWords.size());
		wordTest.analysis = testingWords.get(id);
		List<WordItem> temp_words = new ArrayList<WordItem>();
		temp_words.addAll(allWords);
		int size = temp_words.size();
		for (int i = size - 1; i >= 0; i--) {
			if (wordTest.analysis.id.equals(temp_words.get(i).id)) {
				temp_words.remove(i);
				break;
			}
		}
		return randTest(wordTest, temp_words);
	}

	public WordTest getChallenge() {
		WordTest wordTest = new WordTest();
		Random random = new Random();
		int id = random.nextInt(testingWords.size());
		wordTest.analysis = testingWords.get(id);
		testingWords.remove(id);
		List<WordItem> temp_words = new ArrayList<WordItem>();
		temp_words.addAll(allWords);
		int size = temp_words.size();
		for (int i = size - 1; i >= 0; i--) {
			if (wordTest.analysis.id.equals(temp_words.get(i).id)) {
				temp_words.remove(i);
				break;
			}
		}
		return randTest(wordTest, temp_words);
	}

	private WordTest randTest(WordTest wordTest, List<WordItem> otherItems) {
		Random random = new Random();
		int id;
		// 有图片则在4种试题类型中随机，无图片则在2种试题类型中随机
		if (StringUtil.isEmpty(wordTest.analysis.img)) {
			int[] types = { WordTest.TYPE_CHN_ENG,
					WordTest.TYPE_ENG_CHN };
			int index = (int) (random.nextInt(types.length));
			wordTest.type = types[index];
		} else {
			int[] types = { WordTest.TYPE_CHN_ENG, 
					WordTest.TYPE_ENG_CHN, WordTest.TYPE_ENG_IMG,
					WordTest.TYPE_IMG_ENG };
			int index = (int) (Math.random() * types.length);
			wordTest.type = types[index];
		}
		// 根据类型生成题目
		switch (wordTest.type) {
		case WordTest.TYPE_ENG_IMG:// 根据英文选图片
			wordTest.content = wordTest.analysis.word;
			wordTest.choices.add(wordTest.analysis.img);
			for (int i = otherItems.size() - 1; i >= 0; i--) {
				if (StringUtil.isEmpty(otherItems.get(i).img)) {
					otherItems.remove(i);
				}
			}
			for (int i = 0; i < 3; i++) {
				random = new Random();
				id = (int) (random.nextInt(otherItems.size()));
				wordTest.choices.add(otherItems.get(id).img);
				otherItems.remove(id);
			}
			wordTest.choices = randChoices(wordTest.choices);
			for (int i = 0; i < wordTest.choices.size(); i++) {
				if (wordTest.choices.get(i).equals(wordTest.analysis.img)) {
					wordTest.right_index = i;
					break;
				}
			}
			break;
		case WordTest.TYPE_ENG_CHN:// 根据英文选中文
			wordTest.content = wordTest.analysis.word;
			wordTest.choices.add(wordTest.analysis.chn);
			for (int i = 0; i < 3; i++) {
				random = new Random();
				id = (int) (random.nextInt(otherItems.size()));
				wordTest.choices.add(otherItems.get(id).chn);
				otherItems.remove(id);
			}
			wordTest.choices = randChoices(wordTest.choices);
			for (int i = wordTest.choices.size() - 1; i >= 0; i--) {
				if (wordTest.choices.get(i).equals(wordTest.analysis.chn)) {
					wordTest.right_index = i;
					break;
				}
			}
			break;
		case WordTest.TYPE_CHN_ENG:// 根据中文选英文
			wordTest.content = wordTest.analysis.chn;
			wordTest.choices.add(wordTest.analysis.word);
			for (int i = 0; i < 3; i++) {
				random = new Random();
				id = (int) (random.nextInt(otherItems.size()));
				wordTest.choices.add(otherItems.get(id).word);
				otherItems.remove(id);
			}
			wordTest.choices = randChoices(wordTest.choices);
			for (int i = 0; i < wordTest.choices.size(); i++) {
				if (wordTest.choices.get(i).equals(wordTest.analysis.word)) {
					wordTest.right_index = i;
					break;
				}
			}
			break;
		case WordTest.TYPE_IMG_ENG:// 根据图片选英文
			wordTest.content = wordTest.analysis.img;
			wordTest.choices.add(wordTest.analysis.word);
			for (int i = 0; i < 3; i++) {
				random = new Random();
				id = (int) (random.nextInt(otherItems.size()));
				wordTest.choices.add(otherItems.get(id).word);
				otherItems.remove(id);
			}
			wordTest.choices = randChoices(wordTest.choices);
			for (int i = 0; i < wordTest.choices.size(); i++) {
				if (wordTest.choices.get(i).equals(wordTest.analysis.word)) {
					wordTest.right_index = i;
					break;
				}
			}
			break;
		default:
			break;
		}
		return wordTest;
	}

	public WordTestResult getResult() {
		WordTestResult result = new WordTestResult();
		int size = allWords.size();
		for (int i = 0; i < size; i++) {
			WordItem item = allWords.get(i);
			if (item.right_count >= MAX_RIGHT) {
				result.right_words++;
			}
		}
		return result;
	}

	public void setResult(String item_id, boolean result) {
		int size = allWords.size();
		for (int i = 0; i < size; i++) {
			WordItem item = allWords.get(i);
			if (item.id.equals(item_id)) {
				if (result) {
					item.right_count++;
				} else {
					item.wrong_count++;
				}
				break;
			}
			NCE2.db.update(item);
		}
		size = testingWords.size();
		for (int i = 0; i < size; i++) {
			WordItem item = testingWords.get(i);
			if (item.id.equals(item_id)) {
				if (item.right_count == MAX_RIGHT) {
					testingWords.remove(item);
				}
				break;
			}
		}
	}

	private List<String> randChoices(List<String> arr) {
		List<String> arr1 = new ArrayList<String>();
		Random random;
		int size = arr.size();
		for (int i = 0; i < size; i++) {
			random = new Random();
			int index = random.nextInt(arr.size());
			arr1.add(arr.get(index));
			arr.remove(index);
		}
		return arr1;
	}

	public boolean decodeDataFile() {
		File dataFile = getDataFilePath();
		String jsonString = new String();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				jsonString += tempString;

			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					return false;
				}
			}
		}
		Map<String, WordItem> jsonMap = new Gson().fromJson(jsonString,
				new TypeToken<Map<String, WordItem>>() {
				}.getType());
		
		List<WordItem> cacheItems = new ArrayList<WordItem>();
		Iterator<String> it = jsonMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next().toString();
			WordItem item = jsonMap.get(key);
			item.wt_id = this.section_id;
			cacheItems.add(item);
			WordItem old = NCE2.db.findById(item.id, WordItem.class);
			if (old == null) {
				NCE2.db.save(item);
			} else {
				NCE2.db.update(item);
			}
		}
		allWords.addAll(cacheItems);
		testingWords.addAll(cacheItems);
		return true;
	}

	public String getZipUrl() {
		return "http://img.tiantianshangke.com/nce2/words_"+section_id+".zip";
	}

	public File getUnzipDir() {
		return getDir();
	}

	public File getZipPath() {
		return new File(getDir(), "words_"+section_id+".zip");
	}

	public File getDataFilePath() {
		return new File(getUnzipDir(), "data.txt");
	}

	public static File getDir() {

		return NCE2.getWordTestDir();
	}

	public void clearResult() {
		for (WordItem wordItem : allWords) {
			wordItem.right_count = 0;
			wordItem.wrong_count = 0;
			NCE2.db.update(wordItem);
		}
		testingWords.clear();
		testingWords.addAll(allWords);
	}
}
