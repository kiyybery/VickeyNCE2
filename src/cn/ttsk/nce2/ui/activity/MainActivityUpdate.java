package cn.ttsk.nce2.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import cn.ttsk.library.NetWorkUtil;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;

import com.testin.agent.TestinAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

public class MainActivityUpdate extends BaseActivity {
	private boolean updateChecked = false;
	private boolean needUpdate = false;
	private boolean timerOK = false;
	private Timer timer;

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case NCE2.MSG_MAIN_UPDATE_OK:
				if (!needUpdate && timerOK) {
					into();
				}
				break;
			case NCE2.MSG_MAIN_TIMER_OK:
				if (updateChecked && !needUpdate) {
					into();
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TestinAgent.init(this, "7b4bcef17998ef3d2bf6b41bcabf8a85");// 此行必须放在super.onCreate后
		setContentView(R.layout.activity_main);
		initHeader();
		initWidget();
		setWidgetState();
		// 检测版本更新
		checkVersion();
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				timerOK = true;
				Message message = new Message();
				message.what = NCE2.MSG_MAIN_TIMER_OK;
				handler.sendMessage(message);
			}
		}, 3000);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		}
	}

	@Override
	public void initHeader() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setWidgetState() {
		// TODO Auto-generated method stub

	}

	private void into() {
		if (PreferencesUtil.get(NCE2.USERFIRST, true)) {
			Intent mainIntent = new Intent(MainActivityUpdate.this,
					FirstInstall.class);
			MainActivityUpdate.this.startActivity(mainIntent);
			MainActivityUpdate.this.finish();
			PreferencesUtil.put(NCE2.USERFIRST, false);
		} else {

			Intent mainIntent = new Intent(MainActivityUpdate.this,
					CourseActivity.class);
			startActivity(mainIntent);
			finish();
		}
	}

	protected void checkVersion() {
		if (!NetWorkUtil.networkCanUse(getApplicationContext())) {
			updateChecked = true;
			return;
		}
		UmengUpdateAgent.setUpdateOnlyWifi(true);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					needUpdate = true;
					PreferencesUtil.put(NCE2.KEY_NEW_VERSION, true);
					break;
				case 1: // has no update
						// 没有更新
					PreferencesUtil.put(NCE2.KEY_NEW_VERSION, false);
					break;
				case 2: // none wifi
					break;
				case 3: // time out
					// 超时
					break;
				}
				closeProgressBar();
				updateChecked = true;
				Message message = new Message();
				message.what = NCE2.MSG_MAIN_UPDATE_OK;
				handler.sendMessage(message);
			}
		});
		UmengDialogButtonListener listener = new UmengDialogButtonListener() {
			@Override
			public void onClick(int status) {
				switch (status) {
				case UpdateStatus.Ignore:
				case UpdateStatus.NotNow:
					into();
					break;
				}
			}
		};
		UmengUpdateAgent.setDialogListener(listener);
		startProgressBar(R.string.tips_update_checking, new Thread(), true);
		UmengUpdateAgent.update(this);
	}
}
