package cn.ttsk.nce2.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.ttsk.nce2.R;
import cn.ttsk.handler.LrcRead;
import cn.ttsk.library.DateUtil;
import cn.ttsk.library.Db;
import cn.ttsk.library.MLog;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.entity.LyricContent;
import cn.ttsk.view.LyricViewOrialmyself;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class TextOralFinishActivity extends BaseActivity implements
		OnClickListener {

	public final static String TOTA_TITLE = "tota_title";
	public final static String TOTA_ID_FILE = "tota_id_file";
	public static final String TOFA_ID = "tofa_id";

	private MediaPlayer mediaPlayer;// 播放音频。
	public static final String TR_ID = "tr_id";
	public static final String AMR_FILE = "amr-file";
	public static final String TEST_RESULT = "test-result";
	private String ta_id;
	private File amr_file;

	private String share_url;
	private String tofa_title;
	private ImageView iv_text_oral_finish_play;
	private ImageView iv_text_oral_finish_stop;

	private File tota_id_file;
	private LyricViewOrialmyself btn_text_oral_finish_LyricShow;
	private int CurrentTime = 0;
	private int CountTime = 0;
	private int index = 0;
	private List<LyricContent> LyricList = new ArrayList<LyricContent>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_oral_finish);
		Intent intent = getIntent();
		ta_id = intent.getStringExtra(TOFA_ID);
		tota_id_file = (File) intent.getSerializableExtra(TOTA_ID_FILE);

		initHeader();
		initWidget();
		setWidgetState();
		getData();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.iv_text_oral_finish_play:
			PlayMusic();
			iv_text_oral_finish_play.setVisibility(View.GONE);
			iv_text_oral_finish_stop.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_text_oral_finish_stop:
			StopMusic();
			iv_text_oral_finish_play.setVisibility(View.VISIBLE);
			iv_text_oral_finish_stop.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	@Override
	public void initHeader() {
		// initHeaderWidget();
		// setTitle(tofa_title);
	}

	@Override
	public void initWidget() {
		mediaPlayer = new MediaPlayer();
		iv_text_oral_finish_play = (ImageView) findViewById(R.id.iv_text_oral_finish_play);
		iv_text_oral_finish_stop = (ImageView) findViewById(R.id.iv_text_oral_finish_stop);
		btn_text_oral_finish_LyricShow = (LyricViewOrialmyself) findViewById(R.id.btn_text_oral_finish_LyricShow);
	}

	@Override
	public void setWidgetState() {

		iv_text_oral_finish_play.setOnClickListener(this);
		iv_text_oral_finish_stop.setOnClickListener(this);
	}

	// 点击重录的时候跳转回以前的页面
	public void IntentRerecord() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), TextOralTestActivity.class);
		intent.putExtra(TextOralTestActivity.TO_ID, ta_id);
		intent.putExtra(TextOralTestActivity.TO_TITLE, tofa_title);
		startActivity(intent);
		finish();
	}

	// 点击下一步的时候调用的方法
	public void IntentNext() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(),
				TextOralTestResultActivity.class);
		intent.putExtra(TextOralTestResultActivity.TOTR_ID_FILE, amr_file + "");
		intent.putExtra(TextOralTestResultActivity.TOTR_ID, ta_id);
		intent.putExtra(TextOralTestResultActivity.TOTR_SHARE_URL, share_url);
		intent.putExtra(TextOralTestResultActivity.TOTR_TITLE, tofa_title);
		startActivity(intent);
		finish();
	}

	Handler mHandler = new Handler();

	Runnable mRunnable = new Runnable() {
		public void run() {

			btn_text_oral_finish_LyricShow.SetIndex(Index());

			btn_text_oral_finish_LyricShow.invalidate();

			mHandler.postDelayed(mRunnable, 100);
		}
	};

	public int Index() {
		if (mediaPlayer.isPlaying()) {
			CurrentTime = mediaPlayer.getCurrentPosition();
			CountTime = mediaPlayer.getDuration();
		}

		if (CurrentTime < CountTime) {

			for (int i = 0; i < LyricList.size(); i++) {
				if (i < LyricList.size() - 1) {

					if (CurrentTime < (LyricList.get(i).getLyricTime())
							&& i == 0) {

						index = i;
					}

					if (CurrentTime > (LyricList.get(i).getLyricTime())
							&& CurrentTime < (LyricList.get(i + 1)
									.getLyricTime())) {

						index = i + 1;
					}

				}

				if (i == LyricList.size() - 1
						&& CurrentTime > LyricList.get(i).getLyricTime()) {

					index = i + 1;
				}

			}
		}

		return index;
	}

	// 播放音频的代码
	public void PlayMusic() {
		// 当前音乐是否播放mediaPlayer.isPlaying();
		// mHandler.post(mRunnable);
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(tota_id_file + "");
			mediaPlayer.prepare(); // might take long! (for buffering,
			mediaPlayer.start();
			mediaPlayer.setLooping(false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {

				iv_text_oral_finish_play.setVisibility(View.VISIBLE);
				iv_text_oral_finish_stop.setVisibility(View.GONE);

			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		StopMusic();
	}

	// 获取mp3和歌词的方法
	private void getData() {
		LyricList = NCE2.db.findAllByWhere(LyricContent.class, "sid=\"" + ta_id + "\"", "idx ASC");

		// 设置歌词资源
		btn_text_oral_finish_LyricShow.setSentenceEntities(LyricList);
		draw();
	}


	private void draw() {
		mHandler.post(mRunnable);
	}


	// 点击停止按钮的时候执行的方法
	public void StopMusic() {
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
		}
	}

}
