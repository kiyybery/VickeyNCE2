package cn.ttsk.nce2.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttsk.library.Db;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.library.slidingmenu.SlidingMenu;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.UserInfo;
import cn.ttsk.nce2.ui.Fragment.ConversationFrament;
import cn.ttsk.nce2.ui.Fragment.DownloadFragment;
import cn.ttsk.nce2.ui.Fragment.LeftSlidingMenuFragment;
import cn.ttsk.nce2.ui.Fragment.PicViewFragment;
import cn.ttsk.nce2.ui.Fragment.SettingFragment;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.socialize.sso.UMSsoHandler;

public class CourseActivity extends SlidingFragmentActivity implements
		OnClickListener {
	public final static String USERINFO = "userinfo";
	public final static String FRAGMENT = "fragment";
	public final static int FRAGMENT_COURSE = 1;
	public final static int FRAGMENT_DOWNLOAD = 2;
	public final static int FRAGMENT_SETTING = 3;
	public final static int FRAGMENT_FEEDBACK = 4;
	public final static int FRAGMENT_SHARE = 5;
	public  SlidingMenu mSlidingMenu;
	private ImageButton ivTitleBtnLeft;
	private Fragment mContent;
	private FragmentActivity mContent1;
	private String DeviceId;
	public UserInfo userInfo;
	private LeftSlidingMenuFragment mFrag;
	private int width;
	private int fragment = FRAGMENT_COURSE;
	private TextView ivTitleName;
	private TextView tv_main_title_TitleBtnRigh;
	private int excuse = 0;
	private Timer timer;
	private int time = 0;
	private LinearLayout ll_ivTitleBtnLeft;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		DeviceId = getDeviceId();
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		initSlidingMenu();
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.activity_course, null);
		RelayoutViewTool.relayoutViewWithScale(view, NCE2.screenWidthScale);
		setContentView(view);
		
		initView();
		userInfo = (UserInfo) getIntent().getSerializableExtra(USERINFO);
		if (userInfo == null) {
			userInfo = (UserInfo) Db.get(NCE2.CACHEKEY_USERINFO);
		}
	}

	private void initView() {
		ivTitleBtnLeft = (ImageButton) this.findViewById(R.id.ivTitleBtnLeft);
		ivTitleName = (TextView) this.findViewById(R.id.ivTitleName);
		tv_main_title_TitleBtnRigh = (TextView) this
				.findViewById(R.id.tv_main_title_TitleBtnRigh);
		ll_ivTitleBtnLeft = (LinearLayout) this
				.findViewById(R.id.ll_ivTitleBtnLeft);
		ll_ivTitleBtnLeft.setOnClickListener(this);
		ivTitleBtnLeft.setOnClickListener(this);
	}

	public void initSlidingMenu() {
		// 显示住页面的fragment

		setBehindContentView(R.layout.main_left_layout);
		/**
		 * 用来显示左边侧栏菜单的fragment
		 */
		FragmentTransaction mFragementTransaction = getSupportFragmentManager()
				.beginTransaction();
		mFrag = new LeftSlidingMenuFragment();
		mFragementTransaction.replace(R.id.main_left_fragment, mFrag);

		mFragementTransaction.commit();
		mContent = new PicViewFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
		// customize the SlidingMenu
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setMode(SlidingMenu.LEFT);// 设置是左滑还是右滑，还是左右都可以滑，我这里只做了左滑
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);// 设置菜单宽度
		mSlidingMenu.setFadeDegree(0.35f);// 设置淡入淡出的比例
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 设置手势模式
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);// 设置左菜单阴影图片
		mSlidingMenu.setFadeEnabled(true);// 设置滑动时菜单的是否淡入淡出
		mSlidingMenu.setBehindScrollScale(0.333f);// 设置滑动时拖拽效果
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;
		case R.id.ll_ivTitleBtnLeft:
			mSlidingMenu.showMenu(true);
			break;
			
		default:
			break;
		}

	}

	/**
	 * 左侧菜单点击切换首页的内容
	 */
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
		getSlidingMenu().showContent();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		int tmpFragment = intent.getIntExtra(FRAGMENT, 0);
		if (tmpFragment > 0) {
			PreferencesUtil.put(NCE2.CACHEKEY_MAIN_FRAGMENT, tmpFragment);
			fragment = tmpFragment;
			switch (fragment) {
			case FRAGMENT_DOWNLOAD:
				mContent = new DownloadFragment();
				break;
			case FRAGMENT_FEEDBACK:
				mContent = new ConversationFrament();
				break;
			case FRAGMENT_SETTING:
				mContent = new SettingFragment();
				break;
			case FRAGMENT_COURSE:
			default:
				mContent = new PicViewFragment();
				break;
			}
			switchContent(mContent);
		}
		super.onNewIntent(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			switch (PreferencesUtil.get(NCE2.CACHEKEY_MAIN_FRAGMENT,
					FRAGMENT_COURSE)) {
			case FRAGMENT_COURSE:
				if (excuse == 0) {
					Toast.makeText(getApplicationContext(), "再按一次退出",
							Toast.LENGTH_SHORT).show();
					excuse++;
					timer = new Timer();
					TimerTask task = new TimerTask() {
						@Override
						public void run() {
							time++;
						}
					};
					timer.schedule(task, 0, 1000);
				} else {
					if (time <= 10) {
						CourseActivity.this.finish();
					} else {
						excuse = 0;
						if (timer != null) {
							timer.cancel();
						}
						time = 0;
					}
				}
				break;
			default:
				fragment = FRAGMENT_COURSE;
				initSlidingMenu();
				tv_main_title_TitleBtnRigh.setVisibility(View.GONE);
				PreferencesUtil.put(NCE2.CACHEKEY_MAIN_FRAGMENT,
						FRAGMENT_COURSE);
				break;
			}

			
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = NCE2.mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}
}
