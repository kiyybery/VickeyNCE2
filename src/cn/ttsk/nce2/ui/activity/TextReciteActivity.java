package cn.ttsk.nce2.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import cn.ttsk.nce2.R;
import cn.ttsk.handler.LrcRead;
import cn.ttsk.library.DateUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.entity.Lrc;
import cn.ttsk.nce2.entity.LyricContent;
import cn.ttsk.nce2.ui.adapter.TextReciteAdapter;
import cn.ttsk.view.LyricView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class TextReciteActivity extends BaseActivity implements OnClickListener {

	public static final String TR_ID = "tr_id";
	private ListView lv_lrc;
	private List<Lrc> lrcs = new ArrayList<Lrc>();
	private File mp3File;
	private File lrcFile;
	private String tr_id;
	private TextReciteAdapter adapter;
	private boolean isDownloading = false;
	private boolean mp3Downloaded = false;
	private boolean lrcDownloaded = false;

	private Button play;
	private Button stop;
	private TextView curenttime;
	private TextView alltime;
	private MediaPlayer mMediaPlayer;

	private LrcRead mLrcRead;

	private LyricView mLyricView;

	private int index = 0;

	private int CurrentTime = 0;

	private int CountTime = 0;
	private SeekBar mseekbar;
	private List<LyricContent> LyricList = new ArrayList<LyricContent>();
	private float TextHigh = 30;
	private int k;
	private int width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = LayoutInflater.from(TextReciteActivity.this).inflate(
				R.layout.activity_text_recite, null);
		setContentView(view);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		int height = metric.heightPixels; // 屏幕高度（像素）
		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		// if (StringUtil.isEmpty(tr_id)) {
		// finish();
		// return;
		// }
		initHeader();
		initWidget();
		setWidgetState();
		getData();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.but_text_recite_play:

			Play();
			play.setVisibility(View.GONE);
			stop.setVisibility(View.VISIBLE);
			break;
		case R.id.but_text_recite_pause:

			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			play.setVisibility(View.VISIBLE);
			stop.setVisibility(View.GONE);
			break;

		default:
			break;
		}

	}

	@Override
	public void initHeader() {
		// initHeaderWidget();
		// setTitle("Lesson1背课文");
		// setBtnLeftVisibleState(true);
		// addBtnLeftListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// finish();
		// }
		// });
	}

	@Override
	public void initWidget() {
		mMediaPlayer = new MediaPlayer();

		mLrcRead = new LrcRead();

		play = (Button) findViewById(R.id.but_text_recite_play);
		stop = (Button) findViewById(R.id.but_text_recite_pause);
		mLyricView = (LyricView) findViewById(R.id.but_text_recite_LyricShow);
		mseekbar = (SeekBar) findViewById(R.id.but_text_recite_SeekBar);
		curenttime = (TextView) findViewById(R.id.tv_text_recite_time);
		play.setVisibility(View.VISIBLE);

	}

	Handler mHandler = new Handler();

	Runnable mRunnable = new Runnable() {
		public void run() {

			mLyricView.SetIndex(Index());

			mLyricView.invalidate();

			mHandler.postDelayed(mRunnable, 100);
		}
	};

	// 播放音乐
	public void Play() {

		try {
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(mp3File + "");
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setLooping(false);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public int Index() {
		if (mMediaPlayer.isPlaying()) {
			CurrentTime = mMediaPlayer.getCurrentPosition();

			CountTime = mMediaPlayer.getDuration();

			mseekbar.setMax(CountTime);
			mseekbar.setProgress(CurrentTime);
			String str = DateUtil.getMsStr(CurrentTime) + "/"
					+ DateUtil.getMsStr(CountTime);
			curenttime.setText(str);
		}
		if (CurrentTime < CountTime) {

			for (int i = 0; i < LyricList.size(); i++) {
				if (i < LyricList.size() - 1) {
					if (CurrentTime < LyricList.get(i).getLyricTime() && i == 0) {
						index = i;
					}

					if (CurrentTime > LyricList.get(i).getLyricTime()
							&& CurrentTime < LyricList.get(i + 1)
									.getLyricTime()) {
						index = i;
					}
				}

				if (i == LyricList.size() - 1
						&& CurrentTime > LyricList.get(i).getLyricTime()) {
					index = i;
				}
			}
		}

		return index;
	}

	@Override
	public void setWidgetState() {
		// TODO Auto-generated method stub

		play.setOnClickListener(this);
		stop.setOnClickListener(this);
		mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				// mMediaPlayer.stop();
				play.setVisibility(View.VISIBLE);
				stop.setVisibility(View.GONE);
			}
		});

		mseekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

				if (arg2 == true) {
					// mMediaPlayer.stop();

					mMediaPlayer.seekTo(arg1);
					// mMediaPlayer.start();
				}

			}
		});
		mLyricView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {

					float downheiht = event.getY();
					if (downheiht > mLyricView.GetIndex() / 2) {
						k = (int) ((downheiht - mLyricView.GetIndex() / 2) / TextHigh);
						index = index + k;
					} else if (downheiht < mLyricView.GetIndex() / 2) {

						k = (int) ((mLyricView.GetIndex() / 2 - downheiht) / TextHigh);
						index = index - k;
					}

					if (index < 0) {
						index = 0;
					} else if (index > LyricList.size() - 1) {
						index = LyricList.size() - 1;
					}
					for (int i = 0; i < LyricList.size(); i++) {
						if (index == i) {
							mMediaPlayer.seekTo(LyricList.get(index)
									.getLyricTime());
						}
					}

					return true;
				}
				return false;
			}
		});
	}

	private void draw() {
		// adapter.resetData(lrcs);
		try {
			mLrcRead.Read(lrcFile);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LyricList = mLrcRead.GetLyricContent();

		// 设置歌词资源
		mLyricView.setSentenceEntities(LyricList);

		mHandler.post(mRunnable);

		// for (int i = 0; i < mLrcRead.GetLyricContent().size(); i++) {
		// System.out.println(mLrcRead.GetLyricContent().get(i).getLyricTime()
		// + "-");
		// System.out.println(mLrcRead.GetLyricContent().get(i).getLyric()
		// + "----");
		// }

	}

	// 获取mp3和歌词的方法
	private void getData() {
		try {
			mp3File = getMp3();
			lrcFile = getlrc();

			if (mp3File.exists() && lrcFile.exists()) {
				draw();
			} else {
				downloadFile();
			}
		} catch (Exception e) {
			showToast(e.getMessage(), Toast.LENGTH_LONG);
			finish();
		}
	}

	private void downloadFile() {
		if (isDownloading)
			return;
		isDownloading = true;
		// startProgressBar(R.string.common_waiting_please, new Thread(), true);
		Ion.with(getApplicationContext(), NCE2.SERVER_URL + "/text/recite")
				.setBodyParameter("device_id", getDeviceId())
				.setBodyParameter("auth", "")
				.setBodyParameter("recite_id", tr_id).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						isDownloading = false;
						if (e != null) {
							// showToast(R.string.tips_ion_exception,
							// Toast.LENGTH_SHORT);
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
							// forceLogin();

							break;
						default:

							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
							break;
						}
					}
				});
	}

	private void downloadMp3(String mp3Path) {
		System.out.println("mp3File======" + mp3File);
		Ion.with(getApplicationContext()).load(mp3Path).write(mp3File)
				.setCallback(new FutureCallback<File>() {

					@Override
					public void onCompleted(Exception arg0, File arg1) {
						mp3Downloaded = true;
						if (lrcDownloaded) {
							closeProgressBar();
							draw();
						}
					}

				});
	}

	private void downloadLrc(String lrcPath) {
		
		Ion.with(getApplicationContext()).load(lrcPath).write(lrcFile)
				.setCallback(new FutureCallback<File>() {

					@Override
					public void onCompleted(Exception arg0, File arg1) {
						lrcDownloaded = true;
						if (mp3Downloaded) {
							closeProgressBar();
							draw();
						}
					}

				});
	}

	private File getMp3() throws Exception {
		File fDir = getDir();
		String fileName = tr_id + ".mp3";
		File fMp3 = new File(fDir, fileName);
		System.out.println("fMp3=====" + fMp3);
		return fMp3;
	}

	private File getlrc() throws Exception {
		File fDir = getDir();
		String fileName = tr_id + ".lrc";
		File fLrc = new File(fDir, fileName);

		return fLrc;
	}

	private File getDir() {
		return NCE2.getReciteDir();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
		}
	}

}
