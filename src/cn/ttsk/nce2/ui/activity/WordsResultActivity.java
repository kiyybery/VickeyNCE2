package cn.ttsk.nce2.ui.activity;


import cn.ttsk.nce2.R;
import cn.ttsk.library.WordTestEngine;
import cn.ttsk.nce2.entity.WordTestResult;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class WordsResultActivity extends BaseActivity {

	public static final String WR_ID = "WordsResult-id";
	private Button btn_redo;
	private Button btn_challenge;
	private TextView tv_wordsresult_number;
	private String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_words_result);
		initHeader();
		initWidget();
		setWidgetState();
		id = getIntent().getStringExtra(WR_ID);
		getNext();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_words_result_redo:// 再次练习。
			goTest();
			break;
		case R.id.btn_words_result_challenge:// 重新挑战。
			goChallenge();
		default:
			break;
		}

	}

	@Override
	public void initHeader() {
	}

	@Override
	public void initWidget() {
		btn_redo = (Button) findViewById(R.id.btn_words_result_redo);
		btn_challenge = (Button) findViewById(R.id.btn_words_result_challenge);
		tv_wordsresult_number = (TextView) findViewById(R.id.tv_wordsresult_number);
		btn_challenge.setOnClickListener(this);
		btn_redo.setOnClickListener(this);

	}

	@Override
	public void setWidgetState() {
		btn_redo.setOnClickListener(this);
		btn_challenge.setOnClickListener(this);
	}

	private void getNext() {
		WordTestEngine testEngine = new WordTestEngine(id);
		if (testEngine.needInit()) {
			showToast("还未完成练习", Toast.LENGTH_SHORT);
		} else {
			WordTestResult result = testEngine.getResult();
			int right_words = result.right_words; 
			tv_wordsresult_number.setText("已经掌握了"+ right_words+"个单词");
		}
		
	}

	private void goChallenge() {
		Intent intent = new Intent();
		intent.setClass(WordsResultActivity.this, WordsTestActivity.class);
		intent.putExtra(WordsTestActivity.TYPE, WordsTestActivity.TYPE_CHALLENGE);
		intent.putExtra(WordsTestActivity.SID, id);
		startActivity(intent);
		finish();
		return;
	}

	private void goTest() {

		Intent intent = new Intent();
		intent.setClass(WordsResultActivity.this, WordsTestActivity.class);
		intent.putExtra(WordsTestActivity.TYPE, WordsTestActivity.TYPE_TEST);
		intent.putExtra(WordsTestActivity.SID, id);
		startActivity(intent);
		finish();
		return;
	}
}
