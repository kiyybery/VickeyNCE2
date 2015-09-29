package cn.ttsk.nce2.ui.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttsk.library.NetWorkUtil;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.StringUtil;
import cn.ttsk.library.UIHelper;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.DownloadItem;
import cn.ttsk.nce2.entity.Section;
import cn.ttsk.nce2.entity.SectionItem;
import cn.ttsk.nce2.entity.Video;
import cn.ttsk.nce2.ui.adapter.VideoAdapter;
import cn.ttsk.nce2.ui.service.BufferVideoService;

import com.baidu.cyberplayer.core.BVideoView;
import com.baidu.cyberplayer.core.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.core.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.core.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.core.BVideoView.OnPlayingBufferCacheListener;
import com.baidu.cyberplayer.core.BVideoView.OnPreparedListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.umeng.analytics.MobclickAgent;

public class VideoViewPlayingActivity extends BaseActivity implements
		OnPreparedListener, OnCompletionListener, OnErrorListener,
		OnInfoListener, OnPlayingBufferCacheListener, OnClickListener {

	public static final String SID = "sid";
	public static final String VID = "vid";
	public static final String PID = "pid";
	public static final String URL = "url";
	private List<DownloadItem> notsuccesslist = new ArrayList<DownloadItem>();

	// 页面的id
	private String cid;
	private String sid;// 单元id
	private String vid;// 视频id
	private int pid;// 当前视频索引
	private Section section;
	private String url;// 当前播放地址
	private Video video;// 当前播放的视频
	// 上下环境
	private Context inContext;
	private final String TAG = "VideoViewPlayingActivity";
	private ImageView mPlaybtn = null;

	private SeekBar mProgress = null;
	private TextView mDuration = null;
	private TextView mCurrPostion = null;
	private ImageView mscreenOrientation = null;

	// 对播放位置的记录
	private int currPosition;
	private int currte = 0;
	private ImageView btn_controllerplay_down;
	private Dialog dialog;

	private boolean isClicked = false; // 是否手动点击
	/**
	 * 记录播放位置
	 */
	private int mLastPos = 0;

	/**
	 * 播放状态
	 */
	private enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}

	int num = 0;
	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	private BVideoView mVV = null;
	private EventHandler mEventHandler;
	private HandlerThread mHandlerThread;
	private final Object SYNC_Playing = new Object();
	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "VideoViewPlayingActivity";
	private boolean mIsHwDecode = false;
	private final int UI_EVENT_UPDATE_CURRPOSITION = 1;
	private long mTouchTime;
	VideoAdapter videoAdapter;
	RelativeLayout rl_top;
	// 新界面
	LinearLayout itembar;
	RelativeLayout three_id;
	LinearLayout root1;
	// 切换成横屏之后，显示下载&全屏按钮
	ImageView download_btn, screenOrientation_btn;
	ListView video_lv;
	private List<DownloadItem> successlist;
	private List<SectionItem> items = new ArrayList<SectionItem>();

	class EventHandler extends Handler {
		public EventHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			File videoFile;
			switch (msg.what) {

			case NCE2.MSG_VIDEO_PLAY_LIST:
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				Bundle b = msg.getData();
				vid = b.getString(VID);
				videoFile = NCE2.getVideoFile(vid);
				if (videoFile.exists()) {
					url = videoFile.getAbsolutePath();
					mVV.setVideoPath(url);
				} else if (!NetWorkUtil.networkCanUse(inContext)) {
					return;
				} else {
					// wifi网络的时候执行的代码
					if (NetWorkUtil.checkWifiConnection(inContext)) {
						url = video.url;
						mVV.setVideoPath(url);
					} else {
						// 3G网络执行的代码
						if (!PreferencesUtil
								.get(NCE2.SETTING_NOWIFI_PLAY, true)) {
							url = video.url;
							mVV.setVideoPath(url);
						} else {
							dialog = UIHelper.buildConfirm(
									VideoViewPlayingActivity.this,
									"网络是3G需要切换WIFI下载视频，是否切换", "确定", "取消",
									new OnClickListener() {
										@Override
										public void onClick(View v) {
											dialog.dismiss();
											Intent intent = new Intent(
													Settings.ACTION_WIFI_SETTINGS);
											inContext.startActivity(intent);
										}
									}, new OnClickListener() {
										@Override
										public void onClick(View v) {
											dialog.dismiss();
										}
									});
						}

					}
				}
				mVV.start();

				break;
			case NCE2.MSG_VIDEO_PLAY_ONE:

				/**
				 * 如果已经播放了，等待上一次播放结束
				 */
				if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
					synchronized (SYNC_Playing) {
						try {
							SYNC_Playing.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				/**
				 * 设置播放url
				 */
				pid = 0;
				video = section.videos.get(pid);
				vid = video.id;
				videoFile = NCE2.getVideoFile(vid);
				if (videoFile.exists()) {
					url = videoFile.getAbsolutePath();
					mVV.setVideoPath(url);
				} else if (!NetWorkUtil.networkCanUse(inContext)) {
					return;
				} else {
					// wifi网络的时候执行的代码
					if (NetWorkUtil.checkWifiConnection(inContext)) {
						url = video.url;
						mVV.setVideoPath(url);
					} else {
						// 3G网络执行的代码
						dialog = UIHelper.buildConfirm(
								VideoViewPlayingActivity.this,
								"网络是3G需要切换WIFI下载视频，是否切换", "确定", "取消",
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										dialog.dismiss();
										Intent intent = new Intent(
												Settings.ACTION_WIFI_SETTINGS);
										inContext.startActivity(intent);
									}
								}, new OnClickListener() {
									@Override
									public void onClick(View v) {
										dialog.dismiss();
									}
								});
					}
				}

				/**
				 * 续播，如果需要如此
				 */
				if (mLastPos > 0) {

					mVV.seekTo(mLastPos);
					mLastPos = 0;
				}

				// if (savedInstanceState1 != null
				// && savedInstanceState1.getInt("currentposition") != 0) {
				//
				// mVV.seekTo(savedInstanceState1.getInt("currentposition"));
				// }
				/**
				 * 显示或者隐藏缓冲提示
				 */
				mVV.showCacheInfo(true);

				/**
				 * 开始播放
				 */
				mVV.start();
				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				break;
			default:
				break;
			}
		}
	}

	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			/**
			 * 更新进度及时间
			 */
			case UI_EVENT_UPDATE_CURRPOSITION:
				currPosition = mVV.getCurrentPosition();
				int duration = mVV.getDuration();
				updateTextViewWithTimeFormat(mCurrPostion, currPosition);
				updateTextViewWithTimeFormat(mDuration, duration);
				mProgress.setMax(duration);
				mProgress.setProgress(currPosition);
				if (currPosition != 0) {
					// 向服务器提交播放进度
					double percent = (currPosition / 1.0) / (duration / 1.0);

					int p1 = (int) (percent * 10 - 1);
					if (currte != p1 && p1 > 0) {
						// putjindu(p1);
						currte = p1;
					}

				}
				mUIHandler.sendEmptyMessageDelayed(
						UI_EVENT_UPDATE_CURRPOSITION, 200);

				break;
			case NCE2.VIDEO_PLAY_UI_V:

				if (msg.arg1 == 11) {
					mscreenOrientation
							.setImageResource(R.drawable.player_btn_full);
				} else if (msg.arg1 == 22) {
					mscreenOrientation.setImageResource(R.drawable.zoomout);
				}
				break;
			case NCE2.MSG_VIDEO_PLAY_COMPLETE:
				if ((section.videos.size() - pid) > 1) {
					if (!isClicked) {
						pid++;
					}
					videoAdapter.setSelectedPosition(pid);
					videoAdapter.notifyDataSetInvalidated();
					video = section.videos.get(pid);
					vid = video.id;
					sendURL(video.url, vid);
					isClicked = false;
				}
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.activity_new_ui_video);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
				| PowerManager.ON_AFTER_RELEASE, POWER_LOCK);

		sid = getIntent().getStringExtra(SID);
		if (StringUtil.isEmpty(sid)) {
			finish();
			return;
		}

		section = NCE2.db.findById(sid, Section.class);
		if (section == null) {
			finish();
			return;
		}
		getData();

		section.videos = NCE2.db.findAllByWhere(Video.class, "sid=\"" + sid
				+ "\"");
		if (section.videos == null || section.videos.size() == 0) {
			finish();
			return;
		}
		vid = getIntent().getStringExtra(VID);
		if (StringUtil.isEmpty(vid)) {
			pid = 0;
			video = section.videos.get(pid);
			vid = video.id;
		} else {
			for (int i = 0; i < section.videos.size(); i++) {
				Video tmpVideo = section.videos.get(i);
				if (tmpVideo.id.equals(vid)) {
					video = tmpVideo;
					pid = i;
					break;
				}
			}
		}

		mIsHwDecode = getIntent().getBooleanExtra("isHW", false);

		initWidget();
		setWidgetState();

		videoAdapter = new VideoAdapter(this, items);

		video_lv.setAdapter(videoAdapter);
		videoAdapter.setSelectedPosition(pid);
		video_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if (items.size() > position) {
					SectionItem item = items.get(position);
					switch (item.t) {
					case SectionItem.T_VIDEO:
						if (section.videos.size() > position) {
							isClicked = true;
							pid = position;
							videoAdapter.setSelectedPosition(pid);
							videoAdapter.notifyDataSetInvalidated();
							video = section.videos.get(pid);
							vid = video.id;
							sendURL(video.url, vid);
							mPlaybtn.setImageResource(R.drawable.player_btn_pause);
						}
						break;

					case SectionItem.T_WORDS:

						pid = position;
						videoAdapter.setSelectedPosition(pid);
						videoAdapter.notifyDataSetInvalidated();
						mVV.pause();
						vid = "";
						goWord();
						break;
					case SectionItem.T_RECITE:
						pid = position;
						videoAdapter.setSelectedPosition(pid);
						videoAdapter.notifyDataSetInvalidated();
						mVV.pause();
						vid = "";
						goRecite();
						break;
					}
				}
			}
		});
		/**
		 * 开启后台事件处理线程
		 */
		mHandlerThread = new HandlerThread("event handler thread",
				Process.THREAD_PRIORITY_BACKGROUND);
		mHandlerThread.start();
		mEventHandler = new EventHandler(mHandlerThread.getLooper());

		/**
		 * 获取当前视频播放View的高度，并且将高度保存，切屏回来之后把第一次获取到的高度设置给竖屏的视频播放View
		 */
		ViewTreeObserver vto = rl_top.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				int height = rl_top.getMeasuredHeight();
				String str_height = PreferencesUtil.get("H", "");
				if (str_height.equals("")) {
					PreferencesUtil.put("H", height + "");
				} else {
				}

				return true;
			}
		});

		// /**
		// *rl_tab
		// */
		// ViewTreeObserver vtor = three_id.getViewTreeObserver();
		// vtor.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
		// public boolean onPreDraw() {
		// int height = three_id.getMeasuredHeight();
		// String str_height = PreferencesUtil.get("TH", "");
		// Log.e("==============【可隐藏的背景高】", ""+str_height);
		// if (str_height.equals("")) {
		// PreferencesUtil.put("TH", height + "");
		// } else {
		// }
		//
		// return true;
		// }
		// });
    	
	}

	/**
	 * 为控件注册回调处理函数
	 */
	private void registerCallbackForControl() {
		mPlaybtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if (mVV.isPlaying()) {
					mPlaybtn.setImageResource(R.drawable.player_btn_play);
					/**
					 * 暂停播放
					 */
					mVV.pause();
					// TODO 统计【暂停】点击量
					MobclickAgent.onEvent(getApplicationContext(), "pause_id");
				} else {
					mPlaybtn.setImageResource(R.drawable.player_btn_pause);
					/**
					 * 继续播放
					 */
					mVV.resume();
					// TODO 统计【播放】点击量
					MobclickAgent.onEvent(getApplicationContext(), "play_id");
				}

			}
		});

		OnSeekBarChangeListener osbc1 = new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				updateTextViewWithTimeFormat(mCurrPostion, progress);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				/**
				 * SeekBar开始seek时停止更新
				 */
				mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				int iseekPos = seekBar.getProgress();
				/**
				 * SeekBark完成seek时执行seekTo操作并更新界面
				 * 
				 */
				mVV.seekTo(iseekPos);
				mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
			}
		};
		mProgress.setOnSeekBarChangeListener(osbc1);
	}

	private void updateTextViewWithTimeFormat(TextView view, int second) {
		int hh = second / 3600;
		int mm = second % 3600 / 60;
		int ss = second % 60;
		String strTemp = null;
		if (0 != hh) {
			strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
		} else {
			strTemp = String.format("%02d:%02d", mm, ss);
		}
		view.setText(strTemp);
	}

	@Override
	protected void onPause() {
		super.onPause();
		/**
		 * 在停止播放前 你可以先记录当前播放的位置,以便以后可以续播
		 */
		if (mPlayerStatus == PLAYER_STATUS.PLAYER_PREPARED) {
			mLastPos = mVV.getCurrentPosition();
			mVV.stopPlayback();
			mWakeLock.release();
		}
		MobclickAgent.onResume(getApplicationContext());
	}

	// 横竖屏切换
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d("isRun", "onConfigurationChanged");
		// 判断屏幕是否横竖屏 之后相互转换
		LayoutParams params = rl_top.getLayoutParams();
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.e("isRun", "当前屏幕切换成横屏显示");

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			itembar.setVisibility(View.GONE);
			three_id.setVisibility(View.GONE);
			btn_controllerplay_down.setVisibility(View.GONE);
			Message msg = Message.obtain();
			msg.what = NCE2.VIDEO_PLAY_UI_V;
			msg.arg1 = 22;
			mUIHandler.sendMessage(msg);

			params.height = LayoutParams.MATCH_PARENT;
			rl_top.setLayoutParams(params);
			three_id.setBackgroundResource(R.drawable.bg_cover_tiebaguide_outerpress);

			mDuration.setVisibility(View.VISIBLE);
			mCurrPostion.setVisibility(View.VISIBLE);
			mDuration.setTextColor(Color.WHITE);

			screenOrientation_btn.setVisibility(View.VISIBLE);
			mVV.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {

					num++;
					if (num % 2 == 0) {
						three_id.setVisibility(View.GONE);
					} else {
						three_id.setVisibility(View.VISIBLE);
					}

					return false;
				}
			});

		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.e("isRun", "当前屏幕切换成竖屏显示");
		}
	}

	// TODO onKeyDown
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			// 判断屏幕是否横竖屏 之后相互转换
			LayoutParams params = rl_top.getLayoutParams();
			LayoutParams params1 = three_id.getLayoutParams();
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				// 当前为横屏， 在此处添加额外的处理代码
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				itembar.setVisibility(View.VISIBLE);
				three_id.setVisibility(View.VISIBLE);
				Message msg = Message.obtain();
				msg.what = NCE2.VIDEO_PLAY_UI_V;
				msg.arg1 = 11;
				mUIHandler.sendMessage(msg);

				// params1.height = 55;
				// three_id.setLayoutParams(params1);

				params.height = Integer.parseInt(PreferencesUtil.get("H", ""));
				rl_top.setLayoutParams(params);

				// three_id.setBackgroundResource(R.color.black);
				three_id.setBackgroundResource(R.drawable.bg_cover_tiebaguide_outerpress);
				mDuration.setVisibility(View.GONE);
				mCurrPostion.setVisibility(View.GONE);
				screenOrientation_btn.setVisibility(View.GONE);
				btn_controllerplay_down.setVisibility(View.VISIBLE);
				mVV.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {

						num++;
						if (num % 2 == 0) {
							three_id.setVisibility(View.GONE);
						} else {
							three_id.setVisibility(View.VISIBLE);
						}

						return false;
					}
				});
			} else {
				finish();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);

	}

	// TODO onResume
	@Override
	protected void onResume() {
		super.onResume();
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
			mWakeLock.acquire();
		}

		/**
		 * 发起一次播放任务
		 * 
		 */
		// 只有数据加载完成之后才能播放
		if (video != null && !StringUtil.isEmpty(video.url)
				&& !StringUtil.isEmpty(vid)) {
			sendURL(video.url, vid);

		} else {
		}
		MobclickAgent.onResume(getApplicationContext());

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_DOWN)
			mTouchTime = System.currentTimeMillis();
		else if (event.getAction() == MotionEvent.ACTION_UP) {
			long time = System.currentTimeMillis() - mTouchTime;
			if (time < 5000) {
				// updateControlBar(!barShow);
			}
		}
		return true;
	}

	public void updateControlBar(boolean show) {

		if (show) {
			three_id.setVisibility(View.VISIBLE);

		} else {
			three_id.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		mVV.setVideoPath(url);
		mProgress.setProgress(currPosition);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/**
		 * 退出后台事件处理线程
		 */
		mHandlerThread.quit();
	}

	@Override
	public boolean onInfo(int what, int extra) {
		switch (what) {
		/**
		 * 开始缓冲
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_START:
			break;
		/**
		 * 结束缓冲
		 */
		case BVideoView.MEDIA_INFO_BUFFERING_END:
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 当前缓冲的百分比， 可以配合onInfo中的开始缓冲和结束缓冲来显示百分比到界面
	 */
	@Override
	public void onPlayingBufferCache(int percent) {

	}

	/**
	 * 播放出错
	 */
	@Override
	public boolean onError(int what, int extra) {

		synchronized (SYNC_Playing) {

			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
		return false;
	}

	/**
	 * 播放完成
	 */
	@Override
	public void onCompletion() {
		synchronized (SYNC_Playing) {
			SYNC_Playing.notify();
		}
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		mUIHandler.removeMessages(UI_EVENT_UPDATE_CURRPOSITION);
		// TODO 当前视频播放完之后去播放下一个视频
		Message msg = new Message();
		msg.what = NCE2.MSG_VIDEO_PLAY_COMPLETE;
		mUIHandler.sendMessage(msg);
	}

	/**
	 * 准备播放就绪
	 */
	@Override
	public void onPrepared() {
		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
		mUIHandler.sendEmptyMessage(UI_EVENT_UPDATE_CURRPOSITION);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_controllerplay_down:
			DownWifi();
			break;
		default:
			break;
		}
	}

	// 提交播放进度
	public void putjindu(int p) {
		if (getDeviceId() != null) {
			Ion.with(VideoViewPlayingActivity.this,
					NCE2.SERVER_URL + "course/progress")
					.setMultipartParameter("auth", "")
					.setMultipartParameter("device_id", getDeviceId())
					.setMultipartParameter("cid", cid)
					.setMultipartParameter("vid", vid)
					.setMultipartParameter("p", p + "")
					.setMultipartParameter("sid", sid).asString()
					.setCallback(new FutureCallback<String>() {

						@Override
						public void onCompleted(Exception arg0, String result) {
						}
					});

		}
	}

	// 初始化页头
	@Override
	public void initHeader() {

	}

	// 初始化控件
	@Override
	public void initWidget() {

		mPlaybtn = (ImageView) findViewById(R.id.play_btn);
		rl_top = (RelativeLayout) findViewById(R.id.rl_top);
		itembar = (LinearLayout) findViewById(R.id.ll_itembar);
		root1 = (LinearLayout) findViewById(R.id.ll_top_video);
		screenOrientation_btn = (ImageView) findViewById(R.id.screenOrientation_btn);
		video_lv = (ListView) findViewById(R.id.lv_videoviewplayingactivity);
		three_id = (RelativeLayout) findViewById(R.id.rl_tab);
		mProgress = (SeekBar) findViewById(R.id.media_progress);
		mDuration = (TextView) findViewById(R.id.time_total);
		mCurrPostion = (TextView) findViewById(R.id.time_current);
		mscreenOrientation = (ImageView) findViewById(R.id.screenOrientation_id);
		btn_controllerplay_down = (ImageView) findViewById(R.id.btn_controllerplay_down);
		registerCallbackForControl();

		/**
		 * 设置ak及sk的前16位
		 */
		BVideoView.setAKSK(NCE2.VIDEOVIEWPLAYINGACTIVITY_AK,
				NCE2.VIDEOVIEWPLAYINGACTIVITY_SK);

		/**
		 * 获取BVideoView对象
		 */
		mVV = (BVideoView) findViewById(R.id.video_view);
		inContext = VideoViewPlayingActivity.this;
		mVV.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {

				num++;
				if (num % 2 == 0) {
					three_id.setVisibility(View.GONE);
				} else {
					three_id.setVisibility(View.VISIBLE);
				}

				return false;
			}
		});

	}

	// 绑定监听
	@Override
	public void setWidgetState() {

		/**
		 * 注册listener
		 */
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		btn_controllerplay_down.setOnClickListener(this);

		/**
		 * 设置解码模式
		 */
		mVV.setDecodeMode(mIsHwDecode ? BVideoView.DECODE_HW
				: BVideoView.DECODE_SW);
	}

	// 点击离线缓冲的时候执行的方法
	public void ClickOfflineBuffer() {

		if (video != null && !StringUtil.isEmpty(video.url)) {
			successlist = NCE2.db.findAllByWhere(DownloadItem.class,
					"status=\"" + DownloadItem.STATUS_FINISHED + "\"",
					"created");
			if (successlist == null) {
				successlist = new ArrayList<DownloadItem>();
			}
			Boolean exists = false;
			for (DownloadItem tmpItem : successlist) {
				if (tmpItem.video_id.equals(vid)) {
					exists = true;
					break;
				}
			}
			if (exists) {
				showToast("已缓存", Toast.LENGTH_SHORT);
				return;
			}
			notsuccesslist = NCE2.db.findAllByWhere(DownloadItem.class,
					"status!=\"" + DownloadItem.STATUS_FINISHED + "\"",
					"created");
			if (notsuccesslist == null) {
				notsuccesslist = new ArrayList<DownloadItem>();
			}
			exists = false;
			for (DownloadItem tmpItem : notsuccesslist) {
				if (tmpItem.video_id.equals(vid)) {
					exists = true;
					break;
				}
			}
			if (exists) {
				showToast("已经在下载列表", Toast.LENGTH_SHORT);
			} else {
				Intent intent = new Intent(getApplicationContext(),
						BufferVideoService.class);
				intent.putExtra(BufferVideoService.SID, sid);
				intent.putExtra(BufferVideoService.VID, vid);
				startService(intent);
				showToast("开始下载", Toast.LENGTH_SHORT);
			}
			notsuccesslist = null;
			successlist = null;
		}
	}

	public void DownWifi() {
		auth = getAuth();
		if (StringUtil.isEmpty(auth)) {
			dialog = UIHelper.buildConfirm(VideoViewPlayingActivity.this,
					getString(R.string.video_download_title),
					getString(R.string.video_download_login),
					getString(R.string.video_download_cancel),
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(getApplicationContext(),
									LoginActivity.class);
							startActivity(intent);
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		} else if (NetWorkUtil.checkWifiConnection(inContext)) {
			ClickOfflineBuffer();
		} else {
			dialog = UIHelper.buildConfirm(VideoViewPlayingActivity.this,
					"是否切换到WIFI下下载视频", "确定", "取消", new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(
									Settings.ACTION_WIFI_SETTINGS);
							inContext.startActivity(intent);
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		}
	}

	public void sendURL(final String url, final String vid) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = mEventHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putString(VID, vid);
				b.putString(URL, url);
				msg.setData(b);
				msg.what = NCE2.MSG_VIDEO_PLAY_LIST;
				mEventHandler.sendMessage(msg);
				initTag();
			}

		}).start();
	}

	public void initTag() {
		/**
		 * 如果已经播放了，等待上一次播放结束
		 */
		if (mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE) {
			mVV.stopPlayback();
		}

	}

	// TODO 当切换横屏后执行切换竖屏的监听
	/**
	 * 当切换横屏后执行切换竖屏的监听
	 * 
	 * @param v
	 */
	public void OnClicScreenOrientation(View v) {

		// 判断屏幕是否横竖屏 之后相互转换
		LayoutParams params = rl_top.getLayoutParams();
		LayoutParams params1 = three_id.getLayoutParams();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 当前为横屏， 在此处添加额外的处理代码
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			Message msg = Message.obtain();
			msg.what = NCE2.VIDEO_PLAY_UI_V;
			msg.arg1 = 11;
			mUIHandler.sendMessage(msg);

			itembar.setVisibility(View.VISIBLE);
			three_id.setVisibility(View.VISIBLE);
			btn_controllerplay_down.setVisibility(View.VISIBLE);
			mDuration.setVisibility(View.GONE);
			mCurrPostion.setVisibility(View.GONE);
			screenOrientation_btn.setVisibility(View.GONE);
			// three_id.setBackgroundResource(R.color.black);
			three_id.setBackgroundResource(R.drawable.bg_cover_tiebaguide_outerpress);
			params.height = Integer.parseInt(PreferencesUtil.get("H", ""));

			rl_top.setLayoutParams(params);

			// params1.height = 55;
			// three_id.setLayoutParams(params1);

			mVV.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// three_id.setVisibility(View.VISIBLE);
					num++;
					if (num % 2 == 0) {
						three_id.setVisibility(View.GONE);
					} else {
						three_id.setVisibility(View.VISIBLE);
					}
					return false;
				}
			});

		} else {
			// 主动切换为横屏状态
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			itembar.setVisibility(View.GONE);
			three_id.setVisibility(View.GONE);

			three_id.setBackgroundResource(R.drawable.bg_cover_tiebaguide_outerpress);
			btn_controllerplay_down.setVisibility(View.GONE);
			mDuration.setVisibility(View.VISIBLE);
			mCurrPostion.setVisibility(View.VISIBLE);
			mDuration.setTextColor(Color.WHITE);
			screenOrientation_btn.setVisibility(View.VISIBLE);

			Message msg = Message.obtain();
			msg.what = NCE2.VIDEO_PLAY_UI_V;
			msg.arg1 = 22;
			mUIHandler.sendMessage(msg);

			// params1.height = 55;
			// three_id.setLayoutParams(params1);

			params.height = LayoutParams.MATCH_PARENT;
			// params.height = 1080;
			Log.e("横屏高度：", "" + params.height);
			rl_top.setLayoutParams(params);

			mVV.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {

					num++;
					if (num % 2 == 0) {
						three_id.setVisibility(View.GONE);
					} else {
						three_id.setVisibility(View.VISIBLE);
					}

					return false;
				}
			});
		}
	}

	public void getData() {
		List<Video> videos = NCE2.db.findAllByWhere(Video.class, "sid=\""
				+ section.id + "\"");
		if (videos != null) {
			for (Video video : videos) {
				SectionItem videoItem = new SectionItem();
				videoItem.id = video.id;
				videoItem.t = SectionItem.T_VIDEO;
				videoItem.title = video.title;
				items.add(videoItem);
			}
		}
		if (!StringUtil.isEmpty(section.wt_id)) {
			SectionItem wordsItem = new SectionItem();
			wordsItem.t = SectionItem.T_WORDS;
			wordsItem.title = "背单词";
			items.add(wordsItem);
		}
		if (!StringUtil.isEmpty(section.recite_id)) {
			SectionItem reciteItem = new SectionItem();
			reciteItem.t = SectionItem.T_RECITE;
			reciteItem.title = "练口语";
			items.add(reciteItem);
		}
	}

	public void goWord() {
		if (StringUtil.isEmpty(getAuth())) {
			dialog = UIHelper.buildConfirm(this,
					getString(R.string.goword_title),
					getString(R.string.goword_login),
					getString(R.string.goword_cancel), new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(getApplicationContext(),
									LoginActivity.class);
							intent.putExtra(LoginActivity.TARGET,
									LoginActivity.TARGET_WORDS);
							intent.putExtra(WordsTestActivity.SID, section.id);
							intent.putExtra(WordsTestActivity.TYPE,
									WordsTestActivity.TYPE_CHALLENGE);
							startActivity(intent);
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		} else if (NetWorkUtil.networkCanUse(getApplicationContext())) {
			Intent intent = new Intent(getApplicationContext(),
					WordsTestActivity.class);
			intent.putExtra(WordsTestActivity.SID, section.id);
			intent.putExtra(WordsTestActivity.TYPE,
					WordsTestActivity.TYPE_CHALLENGE);
			startActivity(intent);
		} else {
			dialog = UIHelper.buildConfirm(VideoViewPlayingActivity.this,
					"网络中断中，是否开启网络", "确定", "取消", new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(
									Settings.ACTION_WIFI_SETTINGS);
							startActivity(intent);
						}

					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}

					});
		}
	}

	public void goRecite() {
		if (StringUtil.isEmpty(getAuth())) {
			dialog = UIHelper.buildConfirm(this,
					getString(R.string.gorecite_title),
					getString(R.string.gorecite_login),
					getString(R.string.gorecite_cancel), new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(getApplicationContext(),
									LoginActivity.class);
							intent.putExtra(LoginActivity.TARGET,
									LoginActivity.TARGET_RECITE);
							intent.putExtra(TextOralTestActivity.TO_ID,
									section.id);
							intent.putExtra(TextOralTestActivity.TO_TITLE,
									section.title);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
		} else if (NetWorkUtil.networkCanUse(getApplicationContext())) {
			Intent intent = new Intent(getApplicationContext(),
					TextOralTestActivity.class);
			intent.putExtra(TextOralTestActivity.TO_ID, section.id);
			intent.putExtra(TextOralTestActivity.TO_TITLE, section.title);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			dialog = UIHelper.buildConfirm(VideoViewPlayingActivity.this,
					"网络中断中，是否开启网络", "确定", "取消", new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(
									Settings.ACTION_WIFI_SETTINGS);
							startActivity(intent);
						}

					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}

					});
		}
	}

}
