package cn.ttsk.nce2.ui.Fragment;

import com.testin.agent.TestinAgent;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.StringUtil;
import cn.ttsk.library.UIHelper;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.UserInfo;
import cn.ttsk.nce2.ui.activity.CourseActivity;
import cn.ttsk.nce2.ui.activity.LoginActivity;

public class BaseFragment extends Fragment {

	public static final int COMMON_DIALOG_IDENTITY = 1;
	private Thread thread = null;
	private AsyncTask asyncTask = null;
	private ProgressDialog progressDialog;
	public Boolean hasProgressDialog = false;
	private Dialog dialog;
	protected String auth;

	public Context getApplicationContext() {
		return NCE2.context;
	}

	public void showToast(String message, int time) {// 提示框
		Toast.makeText(getApplicationContext(), message, time).show();
	}

	protected void showToast(int str, int time) {// 提示框
		String info = getResources().getString(str);
		Toast.makeText(getApplicationContext(), info, time).show();
	}

	public String getDeviceId() {
		String imei = "";
		Context ctx = getApplicationContext();
		if (ctx != null) {
			
			TelephonyManager telephonyManager = (TelephonyManager) ctx
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager != null)
				imei = telephonyManager.getDeviceId();

			if (TextUtils.isEmpty(imei))
				imei = "0";
		}
		return imei;

	}

	public String getAuth() {// 获取auth
		return PreferencesUtil.get(NCE2.KEY_AUTH, "");
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

	/**
	 * 检测侧边栏【缓存】 用户是否登录
	 * 
	 * @return
	 */
	protected boolean checkDownloadLogin() {
		auth = getAuth();
		if (StringUtil.isEmpty(auth)) {
			dialog = UIHelper.buildConfirm(getActivity(),
					getString(R.string.tab_download_title),
					getString(R.string.tab_download_login),
					getString(R.string.tab_download_cancel),
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(getApplicationContext(),
									LoginActivity.class);
							intent.putExtra(LoginActivity.TARGET,
									LoginActivity.TARGET_DOWNLOAD);
							intent.putExtra(CourseActivity.FRAGMENT,
									CourseActivity.FRAGMENT_DOWNLOAD);
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

	protected void forceLogin() {
		auth = getAuth();
		if (StringUtil.isEmpty(auth)) {
			showToast(R.string.need_login, Toast.LENGTH_SHORT);
		} else {
			showToast(R.string.login_fail, Toast.LENGTH_SHORT);
		}
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
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

	protected void clearUserInfo() {
		String id = PreferencesUtil.get(NCE2.CACHEKEY_USERINFO, "");
		if (StringUtil.isEmpty(id)) {
			return;
		}
		PreferencesUtil.put(NCE2.CACHEKEY_USERINFO, "");
		NCE2.db.deleteById(UserInfo.class, id);
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
		if (progressDialog != null && progressDialog.isShowing()) {
			return;
		}
		this.thread = thread;
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancelable);
		if (cancelable) {
			progressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					closeProgressBar();
				}
			});
		}
		progressDialog.show();

	}

	public synchronized void startProgressBar(int message, AsyncTask asyncTask,
			boolean cancelable) {
		startProgressBar(getResources().getString(message), asyncTask,
				cancelable);
	}

	public synchronized void startProgressBar(String message,
			AsyncTask asyncTask, boolean cancelable) {
		this.asyncTask = asyncTask;
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(cancelable);
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				closeProgressBar();
			}
		});
		progressDialog.show();
	}

	// 进度条 关
	public synchronized void closeProgressBar() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}
		if (asyncTask != null) {
			asyncTask.cancel(true);
		}
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TestinAgent.onResume(getActivity());// 此行必须放在super.onResume后
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TestinAgent.onStop(getActivity());// 此行必须放在super.onStop后
	}
}
