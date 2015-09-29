package cn.ttsk.nce2.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttsk.library.NetWorkUtil;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.library.StringUtil;
import cn.ttsk.library.UIHelper;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.UserInfo;

import com.google.gson.Gson;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends Activity implements
		View.OnClickListener {

	public static final int COMMON_DIALOG_IDENTITY = 1022;
	public static final int DATE_PICKER_ID = 1025;
	public static final int COMMON_DIALOG_IDENTITY_POP = 1023;

	public static final int FOOTER_COURSELIST = 1;
	public static final int FOOTER_ACTIVITYLIST = 2;
	public static final int FOOTER_SETTINGS = 3;
	public static final int FOOTER_MESSAGE = 4;

	public static final int HEADER_RIGHT_IMG_SHARE = 1;

	Gson gson = new Gson();

	protected String phone_message_baseActivity;
	protected String auth;
	private boolean showProgressBar = false;
	private Thread thread = null;
	private AsyncTask asyncTask = null;
	private boolean progressDialogCancelable = true;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private TextView tv_title_title;
	private LinearLayout ll_title_back;
	private RelativeLayout rl_title;
	private LinearLayout ll_title_right;
	private ImageView iv_title_right;

	@Override
	public void setContentView(View view) {// 适配xml布局
		RelayoutViewTool.relayoutViewWithScale(view, NCE2.screenWidthScale);
		super.setContentView(view);
	}

	@Override
	public void setContentView(int layoutResID) {
		View view = View.inflate(this, layoutResID, null);
		this.setContentView(view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TestinAgent.onResume(this);// 此行必须放在super.onResume后
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TestinAgent.onStop(this);// 此行必须放在super.onStop后
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	public abstract void initHeader();// 初始化头部

	public abstract void initWidget();// 初始化控件

	public void initFooter(int current) {

	}

	public abstract void setWidgetState();// 设置控件状态（注册监听or设置设配器）

	public String getDeviceId() {// 获取设备id
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public String getAuth() {// 获取auth
		return PreferencesUtil.get(NCE2.KEY_AUTH, "");
	}

	public void showToast(String message, int time) {// 提示框
		Toast.makeText(getApplicationContext(), message, time).show();
	}

	protected void showToast(int str, int time) {// 提示框
		String info = getResources().getString(str);
		Toast.makeText(getApplicationContext(), info, time).show();
	}

	protected boolean checkNetOK() {// 检测网络是否异常
		if (!NetWorkUtil.networkCanUse(getApplicationContext())) {
			Toast.makeText(BaseActivity.this, "请确保您处于联网状态!", Toast.LENGTH_LONG)
					.show();
			return false;
		}
		return true;
	}

	/**
	 * 所有子类对应的Dialog使用当前方法，方便统一控制
	 * 
	 * @param title
	 *            dialog Title
	 * @param message
	 *            dialog Message
	 * @param pTitle
	 *            positive Title
	 * @param nTitle
	 *            Negative Title
	 * @param pListener
	 *            positive OnClickListener
	 * @param nListener
	 *            Negative OnClickListener
	 */
	protected void showDialog(String title, String message, String pTitle,
			String nTitle, DialogInterface.OnClickListener pListener,
			DialogInterface.OnClickListener nListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getTopActivity());
		// 需要title居中，使用自定义控件
		TextView titleView = new TextView(getApplicationContext());
		titleView.setText(message);
		titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX);
		titleView.setGravity(Gravity.CENTER);
		builder.setCustomTitle(titleView);
		builder.setCancelable(false);
		if (pListener != null) {
			pTitle = StringUtil.isEmpty(pTitle) ? "确定" : pTitle;
			builder.setPositiveButton(pTitle, pListener);
		}
		if (nListener != null) {
			nTitle = StringUtil.isEmpty(nTitle) ? "取消" : nTitle;
			builder.setNegativeButton(nTitle, nListener);
		}
		builder.create().show();

	}

	public void initHeaderWidget() {
		tv_title_title = (TextView) findViewById(R.id.tv_section_title_title);
		ll_title_back = (LinearLayout) findViewById(R.id.ll_section_title_back);
		rl_title = (RelativeLayout) findViewById(R.id.rl_section_title);
		ll_title_right = (LinearLayout) findViewById(R.id.ll_section_title_right);
		iv_title_right = (ImageView) findViewById(R.id.iv_section_title_right);
	}

	public void setTitle(String title) {// 初始化页面标题
		tv_title_title.setText(title);
	}

	public void setTitle(int title) {// 初始化页面标题
		setTitle(getText(title).toString());
	}

	public Activity getTopActivity() {
		Activity top = this;
		while (top.getParent() != null) {
			top = top.getParent();
		}
		return top;
	}

	public void setTitleVisibleState(boolean state) {
		if (state) {
			rl_title.setVisibility(View.VISIBLE);
		} else {
			rl_title.setVisibility(View.GONE);
		}
	}

	public void addTitleListener(OnClickListener listener) {
		tv_title_title.setOnClickListener(listener);
	}

	public void setBtnLeftVisibleState(boolean state) {// 左侧按钮的可见性
		if (state) {
			ll_title_back.setVisibility(View.VISIBLE);
		} else {
			ll_title_back.setVisibility(View.GONE);
		}
	}

	public void addBtnLeftListener(OnClickListener listener) {// 添加左侧按钮事件
		ll_title_back.setVisibility(View.VISIBLE);
		ll_title_back.setOnClickListener(listener);
	}

	protected boolean backKeyListener() {
		return false;
	}

	// -------------进度条--------------//
	// 进度条 起
	public synchronized ProgressDialog createProgressDialog() {
		ProgressDialog progressDialog = new ProgressDialog(
				getApplicationContext());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		return progressDialog;
	}

	public synchronized void startProgressBar(int message, Thread thread,
			boolean cancelable) {
		startProgressBar(getResources().getString(message), thread, cancelable);
	}

	public synchronized void startProgressBar(String message, Thread thread,
			boolean cancelable) {
		this.phone_message_baseActivity = message;
		this.progressDialogCancelable = cancelable;
		this.thread = thread;
		showProgressBar = true;
		showDialog(COMMON_DIALOG_IDENTITY);
	}

	public synchronized void startProgressBar(int message, AsyncTask asyncTask,
			boolean cancelable) {
		startProgressBar(getResources().getString(message), asyncTask,
				cancelable);
	}

	public synchronized void startProgressBar(String message,
			AsyncTask asyncTask, boolean cancelable) {
		this.phone_message_baseActivity = message;
		this.asyncTask = asyncTask;
		this.progressDialogCancelable = cancelable;
		showProgressBar = true;
		showDialog(COMMON_DIALOG_IDENTITY);
	}

	// 进度条 关
	public synchronized boolean closeProgressBar() {
		try {
			removeDialog(COMMON_DIALOG_IDENTITY);
		} catch (Exception e) {
			// Dialog is not present
		}
		if (showProgressBar) {
			showProgressBar = false;
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case COMMON_DIALOG_IDENTITY:
			ProgressDialog progressDialog = new ProgressDialog(this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

			if (phone_message_baseActivity != null) {
				progressDialog.setMessage(phone_message_baseActivity);
			} else {
				// progressDialog
				// .setMessage(getString(R.string.common_waiting_please));
			}

			progressDialog.setCancelable(false);
			progressDialog
					.setOnKeyListener(new DialogInterface.OnKeyListener() {
						@Override
						public boolean onKey(DialogInterface dialogInterface,
								int i, KeyEvent keyEvent) {
							if (i == KeyEvent.KEYCODE_BACK
									&& progressDialogCancelable) {
								closeProgressBar();

								if (thread != null && thread.isAlive()) {
									thread.stop();
								}

								if (asyncTask != null) {
									asyncTask.cancel(true);
								}
								progressDialogCancelable = true;
								return true;
							}
							return false;
						}
					});
			return progressDialog;
		default:
			return super.onCreateDialog(id);
		}
	}

	public void closeInput(EditText et, Boolean hasFocus) {
		if (hasFocus) {
			et.setInputType(InputType.TYPE_NULL);
		} else {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
		}
	}

	protected boolean checkLogin() {
		auth = getAuth();
		if (StringUtil.isEmpty(auth)) {
			showToast(R.string.need_login, Toast.LENGTH_LONG);
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent);
			return false;
		}
		return true;
	}

	protected void forceLogin() {
		auth = getAuth();
		if (StringUtil.isEmpty(auth)) {
			showToast(R.string.need_login, Toast.LENGTH_SHORT);
		} else {
			showToast(R.string.login_fail, Toast.LENGTH_SHORT);
		}
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
		this.finish();
	}

	protected void setUserInfo(UserInfo userInfo) {
		PreferencesUtil.put(NCE2.CACHEKEY_USERINFO, userInfo.id);
		UserInfo tmpInfo = NCE2.db.findById(userInfo.id, UserInfo.class);
		if (tmpInfo == null) {
			NCE2.db.save(userInfo);
		} else {
			NCE2.db.update(userInfo);
		}
	}

	protected UserInfo getUserInfo() {
		String id = PreferencesUtil.get(NCE2.CACHEKEY_USERINFO, "");
		if (StringUtil.isEmpty(id)) {
			return null;
		}
		return NCE2.db.findById(id, UserInfo.class);
	}

	/**
	 * 检测侧边栏【缓存】 用户是否登录
	 * 
	 * @return
	 */
	protected boolean checkDownloadLogin() {
		auth = getAuth();
		if (StringUtil.isEmpty(auth)) {
			dialog = UIHelper.buildConfirm(this, "是否登录", "是", "否",
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

			return false;
		}
		return true;
	}

	protected void clearUserInfo() {
		String id = PreferencesUtil.get(NCE2.CACHEKEY_USERINFO, "");
		if (StringUtil.isEmpty(id)) {
			return;
		}
		PreferencesUtil.put(NCE2.CACHEKEY_USERINFO, "");
		NCE2.db.deleteById(UserInfo.class, id);
	}

	public void setBtnRight(int type) {// 右侧按钮的可见性
		switch (type) {
		case HEADER_RIGHT_IMG_SHARE:

			break;

		default:
			ll_title_right.setVisibility(View.INVISIBLE);
			break;
		}
	}

	public void addBtnRightListener(OnClickListener listener) {// 添加右侧按钮事件
		ll_title_right.setVisibility(View.VISIBLE);
		ll_title_right.setOnClickListener(listener);
	}
}
