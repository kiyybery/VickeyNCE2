package cn.ttsk.nce2.ui.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttsk.library.DateUtil;
import cn.ttsk.library.NetUtil;
import cn.ttsk.library.UIHelper;
import cn.ttsk.library.UnZipper;
import cn.ttsk.library.WordTestEngine;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.WordItem;
import cn.ttsk.nce2.entity.WordTest;

import com.baidu.cyberplayer.utils.T;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class WordsTestActivity extends BaseActivity {

	public static final int TYPE_CHALLENGE = 0;
	public static final int TYPE_TEST = 1;
	private int down = 1;// 1表示没有点击过，2表示点击过
	private int goneOrvisible;// 0表示隐藏状态，1表示显示状态

	public static final String SID = "sid";
	public static final String TYPE = "type";

	private ImageView iv_content_voice;
	private String id;
	private Context context;
	private Button bv_activity_words_blank_Button_foot;
	private Button bv_activity_words_blank_Button_commit_foot;
	private TextView tv_activity_words_blank_analysis_mainnamechineseexample__id;
	private TextView tv_activity_words_blank_analysis_mainnameEnglishexample__id;
	private TextView tv_activity_words_blank_analysis_mainnamechinese__id;
	private ImageView iv_activity_words_blank_analysis_mainnameplay__id;
	private TextView tv_activity_words_blank_analysis_mainname__id;
	private RelativeLayout rl_activity_words_blank_analysis_all_layout__id;
	private EditText et_activity_words_blank_editText_id;
	private ImageView iv_activity_words_blank_imageview_error_checked4;
	private ImageView iv_activity_words_blank_imageview_checked4;
	private ImageView iv_activity_words_blank_imageview_error_checked3;
	private ImageView iv_activity_words_blank_imageview_checked3;
	private ImageView iv_activity_words_blank_imageview_error_checked2;
	private ImageView iv_activity_words_blank_imageview_checked2;
	private ImageView iv_activity_words_blank_imageview_error_checked1;
	private ImageView iv_activity_words_blank_imageview_checked1;
	private LinearLayout ll_activity_words_blank_imageview_layout;
	private ImageView iv_activity_words_blank_chioce_item_checked4;
	private TextView tv_activity_words_blank_chioce_item_checked4;
	private ImageView iv_activity_words_blank_chioce_before_item_checked4;
	private ImageView iv_activity_words_blank_chioce_item_checked3;
	private TextView tv_activity_words_blank_chioce_item_checked3;
	private ImageView iv_activity_words_blank_chioce_before_item_checked3;
	private ImageView iv_activity_words_blank_chioce_item_checked2;
	private TextView tv_activity_words_blank_chioce_item_checked2;
	private ImageView iv_activity_words_blank_chioce_item_before_checked2;
	private ImageView iv_activity_words_blank_chioce_item_checked1;
	private TextView tv_activity_words_blank_chioce_item_checked1;
	private ImageView iv_activity_words_blank_chioce_item_before_checked1;
	private LinearLayout ll_activity_words_blank_all_layout_chioce_item;
	private ImageView iv_activity_words_blank_lookPicture;
	private LinearLayout ll_activity_words_blank_lookPicture;
	private TextView tv_activity_words_blank_fillintheblankstranslate;
	private TextView tv_activity_words_blank_fillintheblanks;
	private LinearLayout ll_activity_words_blank_fillintheblanks;
	private TextView tv_activity_words_blank_namephonetic;
	private ImageView iv_activity_words_blank_nameplay;
	private TextView tv_activity_words_blank_nameplay;// 获取到的单词
	private LinearLayout ll_activity_words_blank_nameplay;
	private TextView tv_activity_words_blank_testing_progress;
	private ProgressBar pb_activity_words_blank_testing;
	private LinearLayout rl_activity_words_blank_testing_progress;
	private RelativeLayout rl_activity_words_blank_chioce_item_before_checked1;
	private RelativeLayout rl_activity_words_blank_chioce_item_before_checked2;
	private RelativeLayout rl_activity_words_blank_chioce_item_before_checked3;
	private RelativeLayout rl_activity_words_blank_chioce_item_before_checked4;
	private RelativeLayout rl_activity_words_blank_imageview_checked1;
	private RelativeLayout rl_activity_words_blank_imageview_checked2;
	private RelativeLayout rl_activity_words_blank_imageview_checked3;
	private RelativeLayout rl_activity_words_blank_imageview_checked4;
	private RelativeLayout rl_activity_words_blank_editText_id;
	private ImageView iv_activity_words_blank_editText;
	// 标识符，当选项为文字的时候
	private int textview = 0; // 0 代表可以点击，1代表不能点击
	// 标识符，当选择错误时，用户可以点击正确选项，并且可以进入下一题。
	// 用户可以点击正确选项的前提是，我先获取到正确答案的选项。
	private int tv_index_true;// 文字选择题答错时赋值正确答案的变量

	// 标识符，当选项为图片的时候
	private int imageview = 0;// 0 代表可以点击，1代表不能点击

	private int right_index; // 选择题的答案
	private String right_answer;// 填空题的答案。
	private String pron; // 发音音频。

	private MediaPlayer mediaPlayer;// 播放音频。
	private int playcishu = 0; // 播放次数
	private String woiditemid; // 解析的时候的id。
	private int type; // 页面上需要展示的类型。
	private List<String> choices; // 解析内容的集合。
	private String content; // 题面，文字或图片地址
	private int typeshow; // 用来确定跳转到下一个页面的时候该展示的内容，1表示练习模式。2表示挑战模式。
	private int Challenge = 1; // 用来计算挑战的时候，做的题数，最多为CHALLENGE_COUNT道题目。
	private WordTest test;// 获取数据的类。
	private int time;
	private int right = 0;// 正确题的数量。
	private Timer timer;
	private int nextintent = 0;
	private WordTestEngine testEngine;
	private Dialog dialog;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				tv_activity_words_blank_testing_progress.setText(DateUtil
						.getMsStr(time * 1000));
				break;

			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_words_blank);
		// 用于计算时间的计时器
		if (!checkLogin()) {
			return;
		}
		Intent intent1 = getIntent();
		id = intent1.getStringExtra(SID);
		typeshow = intent1.getIntExtra(TYPE, TYPE_CHALLENGE);

		initHeader();
		initWidget();
		setWidgetState();

		context = WordsTestActivity.this;
		if (id != null) {
			testEngine = new WordTestEngine(id);

			if (testEngine.needInit()) {
				initData();
			} else {
				getNext();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bv_activity_words_blank_Button_commit_foot: // 按钮是提交的时候
			String str = et_activity_words_blank_editText_id.getText()
					.toString();
			if (str.equals(right_answer)) {
				iv_activity_words_blank_editText
						.setImageResource(R.drawable.adapter_words_correct);
				iv_activity_words_blank_editText.setVisibility(View.VISIBLE);

				if (typeshow == TYPE_TEST) {
					testEngine.setResult(woiditemid, true);
				}
				if (nextintent == 0) {
					nextintent = 1;
					DownThridIntent();
				}
				right++;
			} else {
				iv_activity_words_blank_editText
						.setImageResource(R.drawable.adapter_words_wrong);
				iv_activity_words_blank_editText.setVisibility(View.VISIBLE);
				rl_activity_words_blank_analysis_all_layout__id
						.setVisibility(View.VISIBLE);
				bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);
			}
			bv_activity_words_blank_Button_commit_foot.setVisibility(View.GONE);
			closeInputMethod();

			break;
		// TODO 答错的情况下点击下一题
		case R.id.bv_activity_words_blank_Button_foot: // 按钮是下一题的时候
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {
					down = 1;
					textview = 0;
					imageview = 0;
					if (mediaPlayer != null) {
						if (mediaPlayer.isPlaying()) {
							mediaPlayer.stop();
							playcishu = 0;

						}
					}
					if (nextintent == 0) {
						nextintent = 1;
						getNext();
					}
					inintui();

					et_activity_words_blank_editText_id.setText("");
					if (typeshow == TYPE_CHALLENGE) {

						pb_activity_words_blank_testing.setProgress(Challenge);
						pb_activity_words_blank_testing
								.setMax(NCE2.CHALLENGE_COUNT);
						if (Challenge >= NCE2.CHALLENGE_COUNT) {
							Challenge = NCE2.CHALLENGE_COUNT;
						} else {
							Challenge++;
						}

					}
				}
			}else{

				down = 1;
				textview = 0;
				imageview = 0;
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
						playcishu = 0;

					}
				}
				if (nextintent == 0) {
					nextintent = 1;
					getNext();
				}
				inintui();

				et_activity_words_blank_editText_id.setText("");
				if (typeshow == TYPE_CHALLENGE) {

					pb_activity_words_blank_testing.setProgress(Challenge);
					pb_activity_words_blank_testing
							.setMax(NCE2.CHALLENGE_COUNT);
					if (Challenge >= NCE2.CHALLENGE_COUNT) {
						Challenge = NCE2.CHALLENGE_COUNT;
					} else {
						Challenge++;
					}

				}
			
			}
			

			break;
		case R.id.rl_activity_words_blank_imageview_checked1:// 选项是图片的时候
			
			//如果该选项为最后一题，判断当前网络是否正常，
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {
					if (imageview == 0) {
						imageview = 1;
						DownImageviewfirstWay();
					}
					if (right_index == tv_index_true) {
						imageview = 0;
					} else {
						imageview = 1;
					}
				}
			}else{
				if (imageview == 0) {
					imageview = 1;
					DownImageviewfirstWay();
				}
				if (right_index == tv_index_true) {
					imageview = 0;
				} else {
					imageview = 1;
				}
			}
			

			break;
		case R.id.rl_activity_words_blank_imageview_checked2:// 选项是图片的时候
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {
					if (imageview == 0) {
						imageview = 1;
						DownImageviewsencodWay();
					}
					if (right_index == tv_index_true) {
						imageview = 0;
					} else {
						imageview = 1;
					}
				}
			}else{
				if (imageview == 0) {
					imageview = 1;
					DownImageviewsencodWay();
				}
				if (right_index == tv_index_true) {
					imageview = 0;
				} else {
					imageview = 1;
				}
			}
			
		

			break;
		case R.id.rl_activity_words_blank_imageview_checked3:// 选项是图片的时候
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {
					if (imageview == 0) {
						imageview = 1;
						DownImageviewthirdWay();
					}
					if (right_index == tv_index_true) {
						imageview = 0;
					} else {
						imageview = 1;
					}
				}
			}else{
				if (imageview == 0) {
					imageview = 1;
					DownImageviewthirdWay();
				}
				if (right_index == tv_index_true) {
					imageview = 0;
				} else {
					imageview = 1;
				}	
			}
			

			break;
		case R.id.rl_activity_words_blank_imageview_checked4:// 选项是图片的时候
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {
					if (imageview == 0) {
						imageview = 1;
						DownImageviewfourWay();
					}
					if (right_index == tv_index_true) {
						imageview = 0;
					} else {
						imageview = 1;
					}
				}
			}else{
				if (imageview == 0) {
					imageview = 1;
					DownImageviewfourWay();
				}
				if (right_index == tv_index_true) {
					imageview = 0;
				} else {
					imageview = 1;
				}
			}
		

			break;
		case R.id.iv_activity_words_blank_nameplay:// 最上面的声音播放的时候
			if (playcishu == 0) {
				playcishu = 1;
				PlayMusic();
			}
			break;
		case R.id.rl_activity_words_blank_chioce_item_before_checked1:// 选项是文字的时候
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {
					if (textview == 0) {
						textview = 1;
						DownfirstWay();
					}

					if (right_index == tv_index_true) {
						textview = 0;
					} else {
						textview = 1;
					}

				}
			}else{
				if (textview == 0) {
					textview = 1;
					DownfirstWay();
				}

				if (right_index == tv_index_true) {
					textview = 0;
				} else {
					textview = 1;
				}
			}
		

			break;
		case R.id.rl_activity_words_blank_chioce_item_before_checked2:// 选项是文字的时候
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {

					if (textview == 0) {
						textview = 1;
						DownsencodWay();
					}
					if (right_index == tv_index_true) {
						textview = 0;
					} else {
						textview = 1;
					}
				}

			}else{

				if (textview == 0) {
					textview = 1;
					DownsencodWay();
				}
				if (right_index == tv_index_true) {
					textview = 0;
				} else {
					textview = 1;
				}
			}
			
			break;
		case R.id.rl_activity_words_blank_chioce_item_before_checked3:// 选项是文字的时候
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {
					if (textview == 0) {
						textview = 1;
						DownthirdWay();
					}

					if (right_index == tv_index_true) {
						textview = 0;
					} else {
						textview = 1;
					}
				}

			}else{
				if (textview == 0) {
					textview = 1;
					DownthirdWay();
				}

				if (right_index == tv_index_true) {
					textview = 0;
				} else {
					textview = 1;
				}	
			}
		
			break;
		case R.id.rl_activity_words_blank_chioce_item_before_checked4:// 选项是文字的时候
			if(Challenge == NCE2.CHALLENGE_COUNT){
				if (!NetUtil.isNetConnected(this)) {
					showToast(getResources().getString(R.string.commit_results), 1);
				} else {
					if (textview == 0) {
						textview = 1;
						DownfourWay();
					}
					if (right_index == tv_index_true) {
						textview = 0;
					} else {
						textview = 1;
					}

				}

			}else{
				if (textview == 0) {
					textview = 1;
					DownfourWay();
				}
				if (right_index == tv_index_true) {
					textview = 0;
				} else {
					textview = 1;
				}	
			}
			
			break;
		case R.id.iv_activity_words_blank_analysis_mainnameplay__id:// 最下面的声音播放的时候
			if (playcishu == 0) {
				playcishu = 1;
				PlayMusic();
			}
			break;
		case R.id.et_activity_words_blank_editText_id:// 设置监听器调用系统软键盘
			openInputMethod(et_activity_words_blank_editText_id);
			break;

		default:
			break;
		}
	}

	@Override
	public void initHeader() {

		initHeaderWidget();
		setTitleVisibleState(true);
		setTitle("背单词");
		setBtnLeftVisibleState(true);
		addBtnLeftListener(new OnClickListener() {
			public void onClick(View v) {
				dialog = UIHelper.buildConfirm(WordsTestActivity.this,
						"请确认是否退出背单词", "确定", "取消", new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								finish();
							}
						}, new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
							}

						});

			}
		});

	}

	@Override
	public void initWidget() {

		pb_activity_words_blank_testing = (ProgressBar) findViewById(R.id.pb_activity_words_blank_testing);
		tv_activity_words_blank_testing_progress = (TextView) findViewById(R.id.tv_activity_words_blank_testing_progress);
		ll_activity_words_blank_nameplay = (LinearLayout) findViewById(R.id.ll_activity_words_blank_nameplay);
		tv_activity_words_blank_nameplay = (TextView) findViewById(R.id.tv_activity_words_blank_nameplay);
		iv_activity_words_blank_nameplay = (ImageView) findViewById(R.id.iv_activity_words_blank_nameplay);
		tv_activity_words_blank_namephonetic = (TextView) findViewById(R.id.tv_activity_words_blank_namephonetic);
		ll_activity_words_blank_fillintheblanks = (LinearLayout) findViewById(R.id.ll_activity_words_blank_fillintheblanks);
		tv_activity_words_blank_fillintheblanks = (TextView) findViewById(R.id.tv_activity_words_blank_fillintheblanks);
		tv_activity_words_blank_fillintheblankstranslate = (TextView) findViewById(R.id.tv_activity_words_blank_fillintheblankstranslate);
		ll_activity_words_blank_lookPicture = (LinearLayout) findViewById(R.id.ll_activity_words_blank_lookPicture);
		iv_activity_words_blank_lookPicture = (ImageView) findViewById(R.id.iv_activity_words_blank_lookPicture);
		ll_activity_words_blank_all_layout_chioce_item = (LinearLayout) findViewById(R.id.ll_activity_words_blank_all_layout_chioce_item);
		iv_activity_words_blank_chioce_item_before_checked1 = (ImageView) findViewById(R.id.iv_activity_words_blank_chioce_item_before_checked1);
		tv_activity_words_blank_chioce_item_checked1 = (TextView) findViewById(R.id.tv_activity_words_blank_chioce_item_checked1);
		iv_activity_words_blank_chioce_item_checked1 = (ImageView) findViewById(R.id.iv_activity_words_blank_chioce_item_checked1);
		iv_activity_words_blank_chioce_item_before_checked2 = (ImageView) findViewById(R.id.iv_activity_words_blank_chioce_item_before_checked2);
		tv_activity_words_blank_chioce_item_checked2 = (TextView) findViewById(R.id.tv_activity_words_blank_chioce_item_checked2);
		iv_activity_words_blank_chioce_item_checked2 = (ImageView) findViewById(R.id.iv_activity_words_blank_chioce_item_checked2);
		iv_activity_words_blank_chioce_before_item_checked3 = (ImageView) findViewById(R.id.iv_activity_words_blank_chioce_before_item_checked3);
		tv_activity_words_blank_chioce_item_checked3 = (TextView) findViewById(R.id.tv_activity_words_blank_chioce_item_checked3);
		iv_activity_words_blank_chioce_item_checked3 = (ImageView) findViewById(R.id.iv_activity_words_blank_chioce_item_checked3);
		iv_activity_words_blank_chioce_before_item_checked4 = (ImageView) findViewById(R.id.iv_activity_words_blank_chioce_before_item_checked4);
		tv_activity_words_blank_chioce_item_checked4 = (TextView) findViewById(R.id.tv_activity_words_blank_chioce_item_checked4);
		iv_activity_words_blank_chioce_item_checked4 = (ImageView) findViewById(R.id.iv_activity_words_blank_chioce_item_checked4);
		ll_activity_words_blank_imageview_layout = (LinearLayout) findViewById(R.id.ll_activity_words_blank_imageview_layout);
		iv_activity_words_blank_imageview_checked1 = (ImageView) findViewById(R.id.iv_activity_words_blank_imageview_checked1);
		iv_activity_words_blank_imageview_error_checked1 = (ImageView) findViewById(R.id.iv_activity_words_blank_imageview_error_checked1);
		iv_activity_words_blank_imageview_checked2 = (ImageView) findViewById(R.id.iv_activity_words_blank_imageview_checked2);
		iv_activity_words_blank_imageview_error_checked2 = (ImageView) findViewById(R.id.iv_activity_words_blank_imageview_error_checked2);
		iv_activity_words_blank_imageview_error_checked4 = (ImageView) findViewById(R.id.iv_activity_words_blank_imageview_error_checked4);
		iv_activity_words_blank_imageview_checked4 = (ImageView) findViewById(R.id.iv_activity_words_blank_imageview_checked4);
		iv_activity_words_blank_imageview_error_checked3 = (ImageView) findViewById(R.id.iv_activity_words_blank_imageview_error_checked3);
		iv_activity_words_blank_imageview_checked3 = (ImageView) findViewById(R.id.iv_activity_words_blank_imageview_checked3);
		et_activity_words_blank_editText_id = (EditText) findViewById(R.id.et_activity_words_blank_editText_id);
		rl_activity_words_blank_analysis_all_layout__id = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_analysis_all_layout__id);
		tv_activity_words_blank_analysis_mainname__id = (TextView) findViewById(R.id.tv_activity_words_blank_analysis_mainname__id);
		iv_activity_words_blank_analysis_mainnameplay__id = (ImageView) findViewById(R.id.iv_activity_words_blank_analysis_mainnameplay__id);
		tv_activity_words_blank_analysis_mainnamechinese__id = (TextView) findViewById(R.id.tv_activity_words_blank_analysis_mainnamechinese__id);
		tv_activity_words_blank_analysis_mainnameEnglishexample__id = (TextView) findViewById(R.id.tv_activity_words_blank_analysis_mainnameEnglishexample__id);
		tv_activity_words_blank_analysis_mainnamechineseexample__id = (TextView) findViewById(R.id.tv_activity_words_blank_analysis_mainnamechineseexample__id);
		bv_activity_words_blank_Button_foot = (Button) findViewById(R.id.bv_activity_words_blank_Button_foot);
		bv_activity_words_blank_Button_commit_foot = (Button) findViewById(R.id.bv_activity_words_blank_Button_commit_foot);
		rl_activity_words_blank_testing_progress = (LinearLayout) findViewById(R.id.rl_activity_words_blank_testing_progress);
		rl_activity_words_blank_chioce_item_before_checked1 = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_chioce_item_before_checked1);
		rl_activity_words_blank_chioce_item_before_checked2 = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_chioce_item_before_checked2);
		rl_activity_words_blank_chioce_item_before_checked3 = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_chioce_item_before_checked3);
		rl_activity_words_blank_chioce_item_before_checked4 = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_chioce_item_before_checked4);
		rl_activity_words_blank_imageview_checked1 = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_imageview_checked1);
		rl_activity_words_blank_imageview_checked2 = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_imageview_checked2);
		rl_activity_words_blank_imageview_checked3 = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_imageview_checked3);
		rl_activity_words_blank_imageview_checked4 = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_imageview_checked4);
		iv_activity_words_blank_editText = (ImageView) findViewById(R.id.iv_activity_words_blank_editText);
		rl_activity_words_blank_editText_id = (RelativeLayout) findViewById(R.id.rl_activity_words_blank_editText_id);

	}

	@Override
	public void setWidgetState() {
		bv_activity_words_blank_Button_commit_foot.setOnClickListener(this);
		bv_activity_words_blank_Button_foot.setOnClickListener(this);
		// 选项为文字的时候设置的监听器
		rl_activity_words_blank_chioce_item_before_checked1
				.setOnClickListener(this);
		rl_activity_words_blank_chioce_item_before_checked2
				.setOnClickListener(this);
		rl_activity_words_blank_chioce_item_before_checked3
				.setOnClickListener(this);
		rl_activity_words_blank_chioce_item_before_checked4
				.setOnClickListener(this);
		// 最上面播放声音的
		iv_activity_words_blank_nameplay.setOnClickListener(this);
		// 最下面播放声音的
		iv_activity_words_blank_analysis_mainnameplay__id
				.setOnClickListener(this);

		// 选项为图片的时候设置的监听器
		rl_activity_words_blank_imageview_checked1.setOnClickListener(this);
		rl_activity_words_blank_imageview_checked2.setOnClickListener(this);
		rl_activity_words_blank_imageview_checked3.setOnClickListener(this);
		rl_activity_words_blank_imageview_checked4.setOnClickListener(this);

		// 编辑文本设置监听器调用系统键盘
		et_activity_words_blank_editText_id.setOnClickListener(this);
	}

	public void getNext() {
		nextintent = 0;
		if (typeshow == TYPE_TEST) {
			if (testEngine.hasNext()) {
				test = testEngine.getNext();
			} else {
				Intent intent = new Intent(context, WordsResultActivity.class);
				intent.putExtra(WordsResultActivity.WR_ID, id);
				startActivity(intent);
			}
		} else {

			if (Challenge < NCE2.CHALLENGE_COUNT) {
				test = testEngine.getChallenge();
			} else {

				timer.cancel();
				Ion.with(context, NCE2.SERVER_URL + "nce2word/chsubmit")
						.setMultipartParameter("auth", getAuth())
						.setMultipartParameter("device_id", getDeviceId())
						.setMultipartParameter("sid", id)
						.setMultipartParameter("duration", time + "")
						.setMultipartParameter("right", right + "")
						.asJsonObject()
						.setCallback(new FutureCallback<JsonObject>() {

							@Override
							public void onCompleted(Exception e,
									JsonObject result) {
								if (e != null) {
									e.printStackTrace();
									showToast(R.string.tips_ion_exception,
											Toast.LENGTH_SHORT);
									return;
								}
								int code = result.get("code").getAsInt();
								switch (code) {
								case 200:

									Intent intent = new Intent(context,
											WordsChartsActivity.class);
									intent.putExtra(WordsChartsActivity.RIGHT,
											right);
									intent.putExtra(WordsChartsActivity.TIME,
											time);
									intent.putExtra(WordsChartsActivity.SID, id);
									intent.putExtra(WordsChartsActivity.RANK,
											result.get("msg").getAsJsonObject()
													.get("rank").getAsInt());
									startActivity(intent);
									break;
								case 401:
									forceLogin();
									break;
								default:
									showToast(result.get("msg").getAsString(),
											Toast.LENGTH_SHORT);
									break;
								}
								finish();
							}

						});
				return;
			}

		}

		type = test.type;
		content = test.content; // 题面，文字或图片地址
		right_index = test.right_index;// 选择题答案
		right_answer = test.right_answer; // 填空题答案
		choices = test.choices; // 选择题选项
		// TODO 解析
		WordItem analysis = test.analysis;
		String word = analysis.word;
		String chn = analysis.chn;
		String symbol = analysis.symbol;
		woiditemid = analysis.id;
		pron = analysis.pron;// 发音音频
		String exp = analysis.exp;// 英文例句
		String exp_chn = analysis.exp_chn;// 中文例句

		if (timer == null) {
			timer = new Timer();
			timer.schedule(task, 0, 1000);
		}

		// 给解析赋值的代码
		tv_activity_words_blank_analysis_mainname__id.setText(word);
		tv_activity_words_blank_analysis_mainnamechinese__id.setText(chn);
		tv_activity_words_blank_analysis_mainnameEnglishexample__id
				.setText(exp);
		tv_activity_words_blank_analysis_mainnamechineseexample__id
				.setText(exp_chn);
		tv_activity_words_blank_namephonetic.setText(symbol);
		switch (type) {
		case WordTest.TYPE_ENG_CHN:

			// 对不需要的布局进行隐藏的代码
			rl_activity_words_blank_testing_progress.setVisibility(View.GONE);
			ll_activity_words_blank_nameplay.setVisibility(View.GONE);
			tv_activity_words_blank_namephonetic.setVisibility(View.GONE);
			ll_activity_words_blank_fillintheblanks.setVisibility(View.GONE);
			ll_activity_words_blank_lookPicture.setVisibility(View.GONE);
			ll_activity_words_blank_all_layout_chioce_item
					.setVisibility(View.GONE);
			ll_activity_words_blank_imageview_layout.setVisibility(View.GONE);
			rl_activity_words_blank_editText_id.setVisibility(View.GONE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.GONE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			bv_activity_words_blank_Button_commit_foot.setVisibility(View.GONE);

			ll_activity_words_blank_nameplay.setVisibility(View.VISIBLE);
			ll_activity_words_blank_all_layout_chioce_item
					.setVisibility(View.VISIBLE);
			tv_activity_words_blank_namephonetic.setVisibility(View.VISIBLE);
			// 将内容填入到里面
			tv_activity_words_blank_nameplay.setText(content);
			tv_activity_words_blank_chioce_item_checked1
					.setText(choices.get(0));
			tv_activity_words_blank_chioce_item_checked2
					.setText(choices.get(1));
			tv_activity_words_blank_chioce_item_checked3
					.setText(choices.get(2));
			tv_activity_words_blank_chioce_item_checked4
					.setText(choices.get(3));

			break;
		case WordTest.TYPE_CHN_ENG:

			rl_activity_words_blank_testing_progress.setVisibility(View.GONE);
			ll_activity_words_blank_nameplay.setVisibility(View.GONE);
			tv_activity_words_blank_namephonetic.setVisibility(View.GONE);
			ll_activity_words_blank_fillintheblanks.setVisibility(View.GONE);
			ll_activity_words_blank_lookPicture.setVisibility(View.GONE);
			ll_activity_words_blank_all_layout_chioce_item
					.setVisibility(View.GONE);
			ll_activity_words_blank_imageview_layout.setVisibility(View.GONE);
			rl_activity_words_blank_editText_id.setVisibility(View.GONE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.GONE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			bv_activity_words_blank_Button_commit_foot.setVisibility(View.GONE);

			// ll_activity_words_blank_fillintheblanks.setVisibility(View.VISIBLE);
			ll_activity_words_blank_nameplay.setVisibility(View.VISIBLE);
			ll_activity_words_blank_all_layout_chioce_item
					.setVisibility(View.VISIBLE);
			tv_activity_words_blank_namephonetic.setVisibility(View.VISIBLE);

			// 将内容填入到里面
			tv_activity_words_blank_nameplay.setText(content);
			tv_activity_words_blank_chioce_item_checked1
					.setText(choices.get(0));
			tv_activity_words_blank_chioce_item_checked2
					.setText(choices.get(1));
			tv_activity_words_blank_chioce_item_checked3
					.setText(choices.get(2));
			tv_activity_words_blank_chioce_item_checked4
					.setText(choices.get(3));

			break;
		case WordTest.TYPE_CHN_WRITE:
			rl_activity_words_blank_testing_progress.setVisibility(View.GONE);
			ll_activity_words_blank_nameplay.setVisibility(View.GONE);
			tv_activity_words_blank_namephonetic.setVisibility(View.GONE);
			ll_activity_words_blank_fillintheblanks.setVisibility(View.GONE);
			ll_activity_words_blank_lookPicture.setVisibility(View.GONE);
			ll_activity_words_blank_all_layout_chioce_item
					.setVisibility(View.GONE);
			ll_activity_words_blank_imageview_layout.setVisibility(View.GONE);
			rl_activity_words_blank_editText_id.setVisibility(View.GONE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.GONE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			bv_activity_words_blank_Button_commit_foot.setVisibility(View.GONE);
			iv_activity_words_blank_editText.setVisibility(View.GONE);

			ll_activity_words_blank_nameplay.setVisibility(View.VISIBLE);
			tv_activity_words_blank_namephonetic.setVisibility(View.VISIBLE);
			rl_activity_words_blank_editText_id.setVisibility(View.VISIBLE);
			bv_activity_words_blank_Button_commit_foot
					.setVisibility(View.VISIBLE);
			// 将内容填入到里面
			tv_activity_words_blank_nameplay.setText(content);

			break;
		case WordTest.TYPE_IMG_ENG:
			rl_activity_words_blank_testing_progress.setVisibility(View.GONE);
			ll_activity_words_blank_nameplay.setVisibility(View.GONE);
			tv_activity_words_blank_namephonetic.setVisibility(View.GONE);
			ll_activity_words_blank_fillintheblanks.setVisibility(View.GONE);
			ll_activity_words_blank_lookPicture.setVisibility(View.GONE);
			ll_activity_words_blank_all_layout_chioce_item
					.setVisibility(View.GONE);
			ll_activity_words_blank_imageview_layout.setVisibility(View.GONE);
			rl_activity_words_blank_editText_id.setVisibility(View.GONE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.GONE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			bv_activity_words_blank_Button_commit_foot.setVisibility(View.GONE);

			ll_activity_words_blank_lookPicture.setVisibility(View.VISIBLE);
			ll_activity_words_blank_all_layout_chioce_item
					.setVisibility(View.VISIBLE);

			// 根据图片填空
			Ion.with(context).load(new File(testEngine.getUnzipDir(), content))
					.withBitmap().placeholder(R.drawable.defaultimg)
					.error(R.drawable.defaultimg)
					.intoImageView(iv_activity_words_blank_lookPicture);
			tv_activity_words_blank_chioce_item_checked1
					.setText(choices.get(0));
			tv_activity_words_blank_chioce_item_checked2
					.setText(choices.get(1));
			tv_activity_words_blank_chioce_item_checked3
					.setText(choices.get(2));
			tv_activity_words_blank_chioce_item_checked4
					.setText(choices.get(3));

			break;
		case WordTest.TYPE_ENG_IMG:
			rl_activity_words_blank_testing_progress.setVisibility(View.GONE);
			ll_activity_words_blank_nameplay.setVisibility(View.GONE);
			tv_activity_words_blank_namephonetic.setVisibility(View.GONE);
			ll_activity_words_blank_fillintheblanks.setVisibility(View.GONE);
			ll_activity_words_blank_lookPicture.setVisibility(View.GONE);
			ll_activity_words_blank_all_layout_chioce_item
					.setVisibility(View.GONE);
			ll_activity_words_blank_imageview_layout.setVisibility(View.GONE);
			rl_activity_words_blank_editText_id.setVisibility(View.GONE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.GONE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			bv_activity_words_blank_Button_commit_foot.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked1
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked2
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked3
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked4
					.setVisibility(View.GONE);

			ll_activity_words_blank_nameplay.setVisibility(View.VISIBLE);
			tv_activity_words_blank_namephonetic.setVisibility(View.VISIBLE);
			ll_activity_words_blank_imageview_layout
					.setVisibility(View.VISIBLE);

			// 根据图片填空
			tv_activity_words_blank_nameplay.setText(content);
			Ion.with(context)
					.load(new File(testEngine.getUnzipDir(), choices.get(0)))
					.withBitmap().placeholder(R.drawable.defaultimg)
					.error(R.drawable.defaultimg)
					.intoImageView(iv_activity_words_blank_imageview_checked1);
			Ion.with(context)
					.load(new File(testEngine.getUnzipDir(), choices.get(1)))
					.withBitmap().placeholder(R.drawable.defaultimg)
					.error(R.drawable.defaultimg)
					.intoImageView(iv_activity_words_blank_imageview_checked2);
			Ion.with(context)
					.load(new File(testEngine.getUnzipDir(), choices.get(2)))
					.withBitmap().placeholder(R.drawable.defaultimg)
					.error(R.drawable.defaultimg)
					.intoImageView(iv_activity_words_blank_imageview_checked3);
			Ion.with(context)
					.load(new File(testEngine.getUnzipDir(), choices.get(3)))
					.withBitmap().placeholder(R.drawable.defaultimg)
					.error(R.drawable.defaultimg)
					.intoImageView(iv_activity_words_blank_imageview_checked4);

			break;

		default:
			break;
		}

		PlayMusic();
		if (typeshow == TYPE_CHALLENGE) {
			rl_activity_words_blank_testing_progress
					.setVisibility(View.VISIBLE);
		}
	}

	// 点击第一个是文字的选项的时候执行的方法
	public void DownfirstWay() {
		iv_activity_words_blank_chioce_item_before_checked1
				.setImageResource(R.drawable.item_yes);

		if (right_index == 0) {
			// TODO 回答对的时候

			iv_activity_words_blank_chioce_item_checked1
					.setVisibility(View.VISIBLE);
			iv_activity_words_blank_chioce_item_checked1
					.setImageResource(R.drawable.adapter_words_correct);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			if (typeshow == TYPE_TEST) {// TYPE_TEST=1
				testEngine.setResult(woiditemid, true);
			}
			if (nextintent == 0) {
				nextintent = 1;
				DownThridIntent();
			}
			// if (bv_activity_words_blank_Button_foot.getVisibility() ==
			// View.GONE) {
			// Log.e("===============", "点击第一个是文字的选项的时候执行的方法");
			// right++;
			// }
			if (down == 1) {
				right++;
			}
		} else {
			// TODO 回答错误时
			down = 2;
			textview = 0;
			iv_activity_words_blank_chioce_item_checked1
					.setVisibility(View.VISIBLE);
			iv_activity_words_blank_chioce_item_checked1
					.setImageResource(R.drawable.adapter_words_wrong);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.VISIBLE);
			if (typeshow == 1) {
				testEngine.setResult(woiditemid, false);
			}
			bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);

			tv_index_true = right_index;
			// 选项2错时，在1 3 4 初始化2
			iv_activity_words_blank_chioce_item_before_checked2
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked2
					.setVisibility(View.GONE);

			iv_activity_words_blank_chioce_before_item_checked3
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked3
					.setVisibility(View.GONE);

			iv_activity_words_blank_chioce_before_item_checked4
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked4
					.setVisibility(View.GONE);
		}
	}

	// 点击第二个是文字的选项的时候执行的方法
	public void DownsencodWay() {
		iv_activity_words_blank_chioce_item_before_checked2
				.setImageResource(R.drawable.item_yes);

		if (right_index == 1) {

			iv_activity_words_blank_chioce_item_checked2
					.setVisibility(View.VISIBLE);
			iv_activity_words_blank_chioce_item_checked2
					.setImageResource(R.drawable.adapter_words_correct);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, true);
			}

			if (nextintent == 0) {
				nextintent = 1;
				DownThridIntent();
			}
			if (down == 1) {
				right++;
			}
		} else {
			down = 2;
			textview = 0;
			iv_activity_words_blank_chioce_item_checked2
					.setVisibility(View.VISIBLE);
			iv_activity_words_blank_chioce_item_checked2
					.setImageResource(R.drawable.adapter_words_wrong);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.VISIBLE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, false);
			}
			bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);

			tv_index_true = right_index;

			// 如果选项1错，选2错时,初始化1选项
			iv_activity_words_blank_chioce_item_before_checked1
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked1
					.setVisibility(View.GONE);

			iv_activity_words_blank_chioce_before_item_checked3
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked3
					.setVisibility(View.GONE);

			iv_activity_words_blank_chioce_before_item_checked4
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked4
					.setVisibility(View.GONE);
		}
	}

	// 点击第三个是文字的选项的时候执行的方法
	public void DownthirdWay() {
		iv_activity_words_blank_chioce_before_item_checked3
				.setImageResource(R.drawable.item_yes);
		if (right_index == 2) {

			iv_activity_words_blank_chioce_item_checked3
					.setVisibility(View.VISIBLE);
			iv_activity_words_blank_chioce_item_checked3
					.setImageResource(R.drawable.adapter_words_correct);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, true);
			}
			if (nextintent == 0) {
				nextintent = 1;
				DownThridIntent();
			}
			if (down == 1) {
				right++;
			}
		} else {
			down = 2;
			textview = 0;
			iv_activity_words_blank_chioce_item_checked3
					.setVisibility(View.VISIBLE);
			iv_activity_words_blank_chioce_item_checked3
					.setImageResource(R.drawable.adapter_words_wrong);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.VISIBLE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, false);
			}
			bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);

			tv_index_true = right_index;

			// 如果选项1错，选2错时,初始化1选项
			iv_activity_words_blank_chioce_item_before_checked1
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked1
					.setVisibility(View.GONE);

			// 选项2错时，在1 3 4 初始化2
			iv_activity_words_blank_chioce_item_before_checked2
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked2
					.setVisibility(View.GONE);

			iv_activity_words_blank_chioce_before_item_checked4
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked4
					.setVisibility(View.GONE);

		}
	}

	// 点击第四个是文字的选项的时候执行的方法
	public void DownfourWay() {
		iv_activity_words_blank_chioce_before_item_checked4
				.setImageResource(R.drawable.item_yes);
		if (right_index == 3) {

			iv_activity_words_blank_chioce_item_checked4
					.setVisibility(View.VISIBLE);
			iv_activity_words_blank_chioce_item_checked4
					.setImageResource(R.drawable.adapter_words_correct);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, true);
			}
			if (nextintent == 0) {
				nextintent = 1;
				DownThridIntent();
			}
			if (down == 1) {
				right++;
			}
		} else {
			down = 2;
			textview = 0;
			iv_activity_words_blank_chioce_item_checked4
					.setVisibility(View.VISIBLE);
			iv_activity_words_blank_chioce_item_checked4
					.setImageResource(R.drawable.adapter_words_wrong);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.VISIBLE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, false);
			}
			bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);

			tv_index_true = right_index;
			// 如果选项1错，选2错时,初始化1选项
			iv_activity_words_blank_chioce_item_before_checked1
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked1
					.setVisibility(View.GONE);

			// 选项2错时，在1 3 4 初始化2
			iv_activity_words_blank_chioce_item_before_checked2
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked2
					.setVisibility(View.GONE);

			iv_activity_words_blank_chioce_before_item_checked3
					.setImageResource(R.drawable.item_no);
			iv_activity_words_blank_chioce_item_checked3
					.setVisibility(View.GONE);
		}
	}

	// 初始化控件的方法
	public void inintui() {
		iv_activity_words_blank_chioce_item_checked4.setVisibility(View.GONE);
		iv_activity_words_blank_chioce_item_checked3.setVisibility(View.GONE);
		iv_activity_words_blank_chioce_item_checked2.setVisibility(View.GONE);
		iv_activity_words_blank_chioce_item_checked1.setVisibility(View.GONE);
		iv_activity_words_blank_chioce_before_item_checked4
				.setImageResource(R.drawable.item_no);
		iv_activity_words_blank_chioce_before_item_checked3
				.setImageResource(R.drawable.item_no);
		iv_activity_words_blank_chioce_item_before_checked2
				.setImageResource(R.drawable.item_no);
		iv_activity_words_blank_chioce_item_before_checked1
				.setImageResource(R.drawable.item_no);
	}

	// 点击第一张图片的时候执行的方法
	public void DownImageviewfirstWay() {
		if (right_index == 0) {
			iv_activity_words_blank_imageview_error_checked1
					.setImageResource(R.drawable.adapter_words_correct);
			iv_activity_words_blank_imageview_error_checked1
					.setVisibility(View.VISIBLE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, true);
			}
			if (nextintent == 0) {
				nextintent = 1;
				DownThridIntent();
			}
			if (down == 1) {
				right++;
			}
		} else {
			down = 2;
			imageview = 0;
			iv_activity_words_blank_imageview_error_checked1
					.setImageResource(R.drawable.adapter_words_wrong);
			iv_activity_words_blank_imageview_error_checked1
					.setVisibility(View.VISIBLE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.VISIBLE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, false);
			}
			bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);
			tv_index_true = right_index;
			iv_activity_words_blank_imageview_error_checked2
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked3
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked4
					.setVisibility(View.GONE);
		}

	}

	// 点击第二张图片的时候执行的方法
	public void DownImageviewsencodWay() {
		if (right_index == 1) {
			iv_activity_words_blank_imageview_error_checked2
					.setImageResource(R.drawable.adapter_words_correct);
			iv_activity_words_blank_imageview_error_checked2
					.setVisibility(View.VISIBLE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, true);
			}
			if (nextintent == 0) {
				nextintent = 1;
				DownThridIntent();
			}
			if (down == 1) {
				right++;
			}
		} else {
			down = 2;
			imageview = 0;
			iv_activity_words_blank_imageview_error_checked2
					.setImageResource(R.drawable.adapter_words_wrong);
			iv_activity_words_blank_imageview_error_checked2
					.setVisibility(View.VISIBLE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.VISIBLE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, false);
			}
			bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);
			tv_index_true = right_index;

			iv_activity_words_blank_imageview_error_checked1
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked3
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked4
					.setVisibility(View.GONE);
		}

	}

	// 点击第三张图片的时候执行的方法
	public void DownImageviewthirdWay() {
		if (right_index == 2) {
			iv_activity_words_blank_imageview_error_checked3
					.setImageResource(R.drawable.adapter_words_correct);
			iv_activity_words_blank_imageview_error_checked3
					.setVisibility(View.VISIBLE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, true);
			}
			if (nextintent == 0) {
				nextintent = 1;
				DownThridIntent();
			}
			if (down == 1) {
				right++;
			}
		} else {
			down = 2;
			imageview = 0;
			iv_activity_words_blank_imageview_error_checked3
					.setImageResource(R.drawable.adapter_words_wrong);
			iv_activity_words_blank_imageview_error_checked3
					.setVisibility(View.VISIBLE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.VISIBLE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, false);
			}
			bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);
			tv_index_true = right_index;

			iv_activity_words_blank_imageview_error_checked1
					.setVisibility(View.GONE);

			iv_activity_words_blank_imageview_error_checked2
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked4
					.setVisibility(View.GONE);

		}

	}

	// 点击第四张图片的时候执行的方法
	public void DownImageviewfourWay() {
		if (right_index == 3) {
			iv_activity_words_blank_imageview_error_checked4
					.setImageResource(R.drawable.adapter_words_correct);
			iv_activity_words_blank_imageview_error_checked4
					.setVisibility(View.VISIBLE);
			bv_activity_words_blank_Button_foot.setVisibility(View.GONE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, true);
			}
			if (nextintent == 0) {
				nextintent = 1;
				DownThridIntent();
			}
			if (down == 1) {
				right++;
			}
		} else {
			down = 2;
			imageview = 0;
			iv_activity_words_blank_imageview_error_checked4
					.setImageResource(R.drawable.adapter_words_wrong);
			iv_activity_words_blank_imageview_error_checked4
					.setVisibility(View.VISIBLE);
			rl_activity_words_blank_analysis_all_layout__id
					.setVisibility(View.VISIBLE);
			if (typeshow == TYPE_TEST) {
				testEngine.setResult(woiditemid, false);
			}
			bv_activity_words_blank_Button_foot.setVisibility(View.VISIBLE);
			tv_index_true = right_index;

			iv_activity_words_blank_imageview_error_checked1
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked2
					.setVisibility(View.GONE);
			iv_activity_words_blank_imageview_error_checked3
					.setVisibility(View.GONE);
		}

	}

	// 播放音频的代码
	public void PlayMusic() {
		// 创建的mediaPlayer对象

		String url = new File(testEngine.getUnzipDir(), pron).getAbsolutePath();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		// 当前音乐是否播放mediaPlayer.isPlaying();
		try {
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepare(); // might take long! (for buffering,
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					playcishu = 0;

				}
			});
			mediaPlayer.start();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 停止播放声音的方法
	public void StopMusic() {
		if (mediaPlayer != null) {

			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				mediaPlayer.reset();
				mediaPlayer.release();
			} else {
				mediaPlayer.reset();
				mediaPlayer.release();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
		}
		if (testEngine != null) {
			testEngine = null;
		}
	}

	/**
	 * 
	 * @MethodName: closeInputMethod
	 * @Description:关闭系统软键盘
	 * @throws
	 */
	public void closeInputMethod() {
		try {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(
							getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e) {
		} finally {
		}
	}

	/**
	 * 
	 * @MethodName: openInputMethod
	 * @Description:打开系统软键盘
	 * @throws
	 */
	public void openInputMethod(final EditText editText) {

		editText.requestFocus();

		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(editText, 20);
	}

	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			time++;
			Message message = new Message();
			message.what = 1;
			message.obj = time;
			handler.sendMessage(message);

		}
	};

	// 点击正确答案以后3秒跳转的方法
	public void DownThridIntent() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				down = 1;
				textview = 0;
				imageview = 0;
				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {
						mediaPlayer.stop();
						playcishu = 0;

					}

				}
				getNext();
				inintui();

				et_activity_words_blank_editText_id.setText("");
				if (typeshow == TYPE_CHALLENGE) {

					pb_activity_words_blank_testing.setProgress(Challenge);
					pb_activity_words_blank_testing
							.setMax(NCE2.CHALLENGE_COUNT);

					if (Challenge >= NCE2.CHALLENGE_COUNT) {
						Challenge = NCE2.CHALLENGE_COUNT;
					} else {
						Challenge++;
					}
				}

			}
		}, 500);
	}

	public void initData() {
		final File filename = testEngine.getZipPath();
		File fDir = testEngine.getUnzipDir();
		if (!fDir.exists() && !fDir.mkdirs()) {
			showToast("缓存目录不能写", Toast.LENGTH_LONG);
			return;
		}
		startProgressBar("下载单词数据中...", new Thread(), false);
		Ion.with(getApplicationContext()).load(testEngine.getZipUrl())
				.write(filename).setCallback(new FutureCallback<File>() {

					@Override
					public void onCompleted(Exception e, File file) {
						if (e != null) {
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
						} else {
							unzipFile();
						}
					}
				});
	}

	private void unzipFile() {
		new unzipTask().execute(1, 1, 1);
	}

	private class unzipTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			// 解压
			File dir = testEngine.getUnzipDir();
			UnZipper.Unzip(testEngine.getZipPath().getAbsolutePath(),
					dir.getAbsolutePath());
			File zipFile = testEngine.getZipPath();
			zipFile.delete();
			// 解析
			testEngine.decodeDataFile();
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			closeProgressBar();
			getNext();
		}
	}

	@Override
	public void onBackPressed() {
		dialog = UIHelper.buildConfirm(WordsTestActivity.this, "请确认是否退出背单词",
				"确定", "取消", new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						finish();
					}
				}, new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}

				});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		super.onNewIntent(intent);
		testEngine = new WordTestEngine(id);
		if (testEngine.needInit()) {
			initData();
		} else {
			getNext();
		}
	}
}
