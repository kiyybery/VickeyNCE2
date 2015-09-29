package cn.ttsk.nce2.ui.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttsk.handler.LrcRead;
import cn.ttsk.library.LameUtil;
import cn.ttsk.library.MLog;
import cn.ttsk.library.NumberUtil;
import cn.ttsk.library.UIHelper;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.LyricContent;
import cn.ttsk.view.LyricView;
import cn.ttsk.view.LyricViewOrial;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class TextOralTestActivity extends BaseActivity implements
		OnClickListener, OnTouchListener {

	public static final String TO_ID = "to_id";
	public static final String TO_TITLE = "to_title";
	private ProgressBar pb_text_oral_text_testing_bar;
	private ImageView iv_text_oral_text_play;
	private ImageView iv_text_oral_text_stop;
	private TextView tv_text_oral_text_topic;
	private ImageView iv_text_oral_text_voice_left;
	private Button btn_text_oral_text_voice_record;
	private ImageView iv_text_oral_text_voice_right;
	private TextView tv_text_oral_text_probar_percent;
	private AnimationDrawable animation_left;
	private AnimationDrawable animation_right;
	private AnimationDrawable animation_music;
	private MediaPlayer mediaPlayer;// 播放音频。
	private SpeechRecognizer mRecognizer;
	private String recordString = "";
	private int afterplay = 1;
	private File recordFile;
	private List<Integer> recordResult = new ArrayList<Integer>();
	private int totalResult = 0;
	private boolean isStarting = false;
	private boolean isStoping = true;
	private LyricContent lyriccontent;
	private LameUtil lameUtil = new LameUtil();
	// private List<LyricContent> LyricListmyself = new
	// ArrayList<LyricContent>();
	private int grade = 0;
	private Dialog dialog;
	private int timeTotal=0;//录制总时间
	private long timeStart=0;//当前录制开始时间
	private long timeEnd=0;//当前录制结束时间
	private Boolean isPlaying = false;
	private RecognizerListener mRecognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			recordString = "";
		}

		@Override
		public void onEndOfSpeech() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(SpeechError arg0) {

			Message message = new Message();
			message.what = NCE2.MSG_TEXT_ORAL_REC_FAILED;
			message.obj = arg0;
			handler.sendMessage(message);
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, String arg3) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onResult(RecognizerResult result, boolean isLast) {
			com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
			JsonObject jsonResult = (JsonObject) parser.parse(result
					.getResultString());
			JsonArray ws = jsonResult.get("ws").getAsJsonArray();
			for (JsonElement wsElement : ws) {
				JsonArray cw = wsElement.getAsJsonObject().get("cw")
						.getAsJsonArray();
				String w = cw.get(0).getAsJsonObject().get("w").getAsString();
				recordString += w;
			}
			if (isLast) {
				Message message = new Message();
				message.what = NCE2.MSG_TEXT_ORAL_REC_FINISH;
				handler.sendMessage(message);


			}

		}

		@Override
		public void onVolumeChanged(int arg0) {
			// TODO Auto-generated method stub

		}

	};


	private File mp3File;// 音频路径
	private File lrcFile;// 歌词路径
	private String to_id;
	private String title;

	private boolean isDownloading = false;
	private boolean mp3Downloaded = false;
	private boolean lrcDownloaded = false;
	private LyricViewOrial mLyricView;
	private List<LyricContent> LyricList = new ArrayList<LyricContent>();
	private LrcRead mLrcRead;
	private int index = 0;
	
	private int barprocess = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case NCE2.MSG_TEXT_ORAL_REC_FINISH:
				saveRecord();

				if (afterplay == 1) {

					if (LyricList.size() != 0 && index < LyricList.size()) {
						mediaPlayer.seekTo(LyricList.get(index).getLyricTime());

					}
				}

				break;
			case NCE2.MSG_TEXT_ORAL_SAVE_FAILED:
				closeProgressBar();
				showToast("保存音频文件失败", Toast.LENGTH_SHORT);
				break;
			case NCE2.MSG_TEXT_ORAL_SAVE_FINISH:
				closeProgressBar();
				showToast("保存音频文件成功", Toast.LENGTH_SHORT);
				
				if (barprocess == LyricList.size()) {

					Intent intent = new Intent(getApplicationContext(),
							TextOralTestResultActivity.class);
					intent.putExtra(TextOralTestResultActivity.TOTR_ID, to_id);
					intent.putExtra(TextOralTestResultActivity.TOTR_ID_FILE,
							mp3File);
					intent.putExtra(TextOralTestResultActivity.TOTR_SHARE_URL,
							totalResult);
					intent.putExtra(TextOralTestResultActivity.TOTR_TITLE,
							title);
					grade = 0;
					for (int res : recordResult) {
						grade += res;
					}
					grade = grade / recordResult.size();
					intent.putExtra(TextOralTestResultActivity.TOTR_GRADE,
							grade);
					startActivity(intent);
					finish();
				}
				break;
			case NCE2.MSG_TEXT_ORAL_REC_FAILED:
				recordFailed((SpeechError)msg.obj);

				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!checkLogin()) {
			return;
		}
		setContentView(R.layout.activity_text_oral_test);
		to_id = getIntent().getStringExtra(TO_ID);
		initRecognizer();
		initHeader();
		initWidget();
		mLyricView.setContext(getApplicationContext());
		setWidgetState();
		//清空之前保留的个人歌词数据
		NCE2.db.deleteByWhere(LyricContent.class, "sid=\"" + to_id + "\"");

		getData();

	}

	private void initRecognizer() {

		// 初始化识别对象
		mRecognizer = SpeechRecognizer.createRecognizer(this, null);
		mRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
		mRecognizer.setParameter(SpeechConstant.LANGUAGE, "en_us");
		// mRecognizer.startListening(speechListener);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_text_oral_text_play:
			PlayMusic();
			iv_text_oral_text_play.setVisibility(View.GONE);
			iv_text_oral_text_stop.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_text_oral_text_stop:
			iv_text_oral_text_play.setVisibility(View.VISIBLE);
			iv_text_oral_text_stop.setVisibility(View.GONE);
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
				mediaPlayer.release();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void initHeader() {
		initHeaderWidget();
		setTitle("练口语");
		addBtnLeftListener(new OnClickListener() {
			public void onClick(View v) {

				dialog = UIHelper.buildConfirm(TextOralTestActivity.this,
						"请确认是否退出口语练习", "确定", "取消", new OnClickListener() {
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
		mLrcRead = new LrcRead();
		mediaPlayer = new MediaPlayer();
		pb_text_oral_text_testing_bar = (ProgressBar) findViewById(R.id.pb_text_oral_text_testing_bar);
		iv_text_oral_text_play = (ImageView) findViewById(R.id.iv_text_oral_text_play);
		tv_text_oral_text_probar_percent = (TextView) findViewById(R.id.tv_text_oral_text_probar_percent);
		iv_text_oral_text_voice_left = (ImageView) findViewById(R.id.iv_text_oral_text_voice_left);
		btn_text_oral_text_voice_record = (Button) findViewById(R.id.btn_text_oral_text_voice_record);
		iv_text_oral_text_voice_right = (ImageView) findViewById(R.id.iv_text_oral_text_voice_right);
		mLyricView = (LyricViewOrial) findViewById(R.id.btn_text_oral_text_LyricShow);
		iv_text_oral_text_stop = (ImageView) findViewById(R.id.iv_text_oral_text_stop);
		iv_text_oral_text_voice_left
				.setBackgroundResource(R.drawable.animation);
		iv_text_oral_text_voice_right
				.setBackgroundResource(R.drawable.animation_right);
		iv_text_oral_text_stop.setBackgroundResource(R.drawable.animationmusic);
		animation_left = (AnimationDrawable) iv_text_oral_text_voice_left
				.getBackground();
		animation_right = (AnimationDrawable) iv_text_oral_text_voice_right
				.getBackground();
		animation_music = (AnimationDrawable) iv_text_oral_text_stop
				.getBackground();

	}

	@Override
	public void setWidgetState() {

		btn_text_oral_text_voice_record.setOnTouchListener(this);
		iv_text_oral_text_play.setOnClickListener(this);
		iv_text_oral_text_stop.setOnClickListener(this);
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {

				iv_text_oral_text_play.setVisibility(View.VISIBLE);
				iv_text_oral_text_stop.setVisibility(View.GONE);
				animation_music.stop();

			}
		});

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		double p = 0;
		switch (v.getId()) {
		case R.id.btn_text_oral_text_voice_record:
			if (event.getAction() == MotionEvent.ACTION_DOWN) {

				startRecord();
			} else if (event.getAction() == MotionEvent.ACTION_UP) {

				stopRecord();
			}
			break;

		default:
			break;
		}
		return true;
	}

	// 点击录音按钮的时候调用的方法
	public void startRecord() {
		if (isStarting) {
			showToast("录音中，请稍候", Toast.LENGTH_SHORT);
			return;
		}

		timeStart = System.currentTimeMillis();
		isStarting = true;
		iv_text_oral_text_voice_left.setVisibility(View.VISIBLE);
		iv_text_oral_text_voice_right.setVisibility(View.VISIBLE);
		animation_left.start();
		animation_right.start();
		recordFile = new File(NCE2.getOralDir(), "record_raw_" + to_id + "_"
				+ index + ".raw");
		mRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				recordFile.getAbsolutePath());
		mRecognizer.startListening(mRecognizerListener);
		isStoping = false;
	}

	public void stopRecord() {
		if (isStoping) {
			return;
		}
		timeEnd = System.currentTimeMillis();
		isStarting = false;
		isStoping = true;
		iv_text_oral_text_voice_left.setVisibility(View.GONE);
		iv_text_oral_text_voice_right.setVisibility(View.GONE);
		animation_left.stop();
		animation_right.stop();
		mRecognizer.stopListening();

	}

	private void saveRecord() {
		String right = LyricList.get(index).getLyric();
		String[] rightList = right.split(" ");
		String[] recordList = recordString.split(" ");
		int length = rightList.length > recordList.length ? recordList.length
				: rightList.length;
		int rightCount = 0;
		for (int i = 0; i < length; i++) {
			if (rightList[i].equals(recordList[i])) {
				rightCount++;
			}
		}
		recordResult.add(rightCount * 100 / length);
		grade = rightCount * 100 / length;

		//保存歌词
		timeTotal += timeEnd - timeStart;
		lyriccontent = new LyricContent();
		lyriccontent.setIdx(index);
		lyriccontent.setSid(to_id);
		lyriccontent.setLyric(LyricList.get(index).getLyric());
		lyriccontent.setLyricTime(timeTotal);
		
		NCE2.db.save(lyriccontent);
		
		barprocess++;
		pb_text_oral_text_testing_bar.setMax(LyricList.size() - 1);
		pb_text_oral_text_testing_bar.setProgress(barprocess);
		
		if (index < LyricList.size() - 1) {
			index++;
			tv_text_oral_text_probar_percent.setText(NumberUtil.convertFloatToInt((index+1.0f) / LyricList.size() * 100) + "%");
			draw();
		} else {
			combineRecord();
		}
	}

	// 用来合成音频文件的方法
	public void combineRecord() {
		startProgressBar(getResources().getString(R.string.frequency), new Thread(), false);
		afterplay = 2;
		new Thread() {
			public void run() {
				try {
					File combinedWavFile = new File(NCE2.getOralDir(),
							"record_" + to_id + ".raw");
					mp3File = new File(NCE2.getOralDir(), "record_" + to_id
							+ ".mp3");
					combinedWavFile.createNewFile();

					OutputStream outStream;
					try {
						combinedWavFile.createNewFile();
						outStream = new FileOutputStream(combinedWavFile);

						byte[] x = new byte[2048];
						for (int i = 0; i < LyricList.size(); i++) {
							File wavFilename = new File(NCE2.getOralDir(),
									"record_raw_" + to_id + "_" + i + ".raw");
							InputStream inStream = new FileInputStream(
									wavFilename);
							while ((inStream.read(x)) > 0) {
								outStream.write(x);
							}
							inStream.close();
							wavFilename.delete();
						}
						outStream.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					lameUtil.init(1, 16000, 256, 1, 2);

					int result = lameUtil.encode(
							combinedWavFile.getAbsolutePath(),
							mp3File.getAbsolutePath());
					combinedWavFile.delete();
					if (result == 0) {

						Message message = new Message();
						message.what = NCE2.MSG_TEXT_ORAL_SAVE_FINISH;
						handler.sendMessage(message);
					} else {
						Message message = new Message();
						message.what = NCE2.MSG_TEXT_ORAL_SAVE_FAILED;
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Message message = new Message();
					message.what = NCE2.MSG_TEXT_ORAL_SAVE_FAILED;
					handler.sendMessage(message);
				}
			}
		}.start();
	}

	// 播放音频的代码
	public void PlayMusic() {
		// 当前音乐是否播放mediaPlayer.isPlaying();
		try {
			if (LyricList.size() != 0 && index < LyricList.size()) {
				mediaPlayer.seekTo(LyricList.get(index).getLyricTime());
			}
			mediaPlayer.start();
			mediaPlayer.setLooping(false);
			iv_text_oral_text_play.setVisibility(View.GONE);
			iv_text_oral_text_stop.setVisibility(View.VISIBLE);
			animation_music.start();
			isPlaying = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void pauseMusic() {
		isPlaying =false;
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			iv_text_oral_text_play.setVisibility(View.VISIBLE);
			iv_text_oral_text_stop.setVisibility(View.GONE);
		}
	}

	// 用来设置播放音乐的进度
	public void SeekTo() {

	}

	// 获取mp3和歌词的方法
	private void getData() {
		mp3File = getMp3();
		lrcFile = getlrc();

		if (mp3File.exists() && lrcFile.exists()) {

			try {
				mLrcRead.Read(lrcFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			LyricList = mLrcRead.GetLyricContent();
			try {
				mediaPlayer.reset();
				mediaPlayer.setDataSource(mp3File.getAbsolutePath());
				mediaPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			}
			draw();
		} else {
			downloadFile();
		}
	}

	private File getMp3() {
		File fDir = getDir();
		String fileName = to_id + ".mp3";
		File fMp3 = new File(fDir, fileName);

		return fMp3;
	}

	private File getlrc() {
		File fDir = getDir();
		String fileName = to_id + ".lrc";
		File fLrc = new File(fDir, fileName);

		return fLrc;
	}

	private File getDir() {
		return NCE2.getReciteDir();
	}

	private void downloadLrc(String lrcPath) {
		Ion.with(getApplicationContext()).load(lrcPath).write(lrcFile)
				.setCallback(new FutureCallback<File>() {

					@Override
					public void onCompleted(Exception arg0, File arg1) {
						lrcDownloaded = true;
						if (mp3Downloaded) {
							closeProgressBar();
							getData();
						}
					}

				});
	}

	private void downloadMp3(String mp3Path) {
		Ion.with(getApplicationContext()).load(mp3Path).write(mp3File)
				.setCallback(new FutureCallback<File>() {

					@Override
					public void onCompleted(Exception arg0, File arg1) {
						mp3Downloaded = true;
						if (lrcDownloaded) {
							closeProgressBar();
							getData();
						}
					}

				});
	}

	private void downloadFile() {
		if (isDownloading)
			return;
		isDownloading = true;
		startProgressBar(R.string.common_waiting_please, new Thread(), true);
		Ion.with(getApplicationContext(), NCE2.SERVER_URL + "/nce2oral/download")
				.setBodyParameter("device_id", getDeviceId())
				.setBodyParameter("auth", "")
				.setBodyParameter("sid", to_id).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						isDownloading = false;
						if (e != null) {
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
							return;
						}
						int code = result.get("code").getAsInt();
						switch (code) {
						case 200:
							JsonObject msg = result.get("msg")
									.getAsJsonObject();
							String mp3Path = msg.get("mp3").getAsString();
							String lrcPath = msg.get("lrc").getAsString();
							downloadMp3(mp3Path);
							downloadLrc(lrcPath);

							break;
						case 401:
							forceLogin();
							break;
						default:
							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
							break;
						}
					}
				});
	}

	private void draw() {
		// 设置歌词资源
		mLyricView.setSentenceEntities(LyricList);
		mHandler.post(mRunnable);

	}

	Handler mHandler = new Handler();

	Runnable mRunnable = new Runnable() {
		public void run() {
			mLyricView.SetIndex(index);
			if (isPlaying) {
				int current = mediaPlayer.getCurrentPosition();
				if (index < LyricList.size() -1) {
					if (current >= LyricList.get(index + 1).getLyricTime()) {
						pauseMusic();
					}
				} else if (current == mediaPlayer.getDuration()){
					pauseMusic();
				}
			}
			mLyricView.invalidate();
			mHandler.postDelayed(mRunnable, 100);

		}
	};

	@Override
	protected void onDestroy() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
		}
		lameUtil.destroy();
		super.onDestroy();
	}



	private void recordFailed(SpeechError error) {
		error.printStackTrace();
		MLog.e("textoraltest", error.getErrorCode() + "," + error.getErrorDescription());
		isStarting = false;
		isStoping = false;
		showToast(getResources().getString(R.string.record_btn), Toast.LENGTH_SHORT);
	}
	
	@Override
	public void onBackPressed() {
		dialog = UIHelper.buildConfirm(TextOralTestActivity.this,
				"请确认是否退出口语练习", "确定", "取消", new OnClickListener() {
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

}
