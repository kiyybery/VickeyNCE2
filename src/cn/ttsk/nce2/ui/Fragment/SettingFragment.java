package cn.ttsk.nce2.ui.Fragment;

import java.io.File;
import java.text.DecimalFormat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttsk.library.FileUtil;
import cn.ttsk.library.MLog;
import cn.ttsk.library.NetWorkUtil;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.library.RoundedImageView;
import cn.ttsk.library.StringUtil;
import cn.ttsk.library.UIHelper;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.UserInfo;
import cn.ttsk.nce2.ui.activity.AboutActivity;
import cn.ttsk.nce2.ui.activity.CourseActivity;
import cn.ttsk.nce2.ui.activity.LoginActivity;
import cn.ttsk.nce2.ui.activity.WebActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class SettingFragment extends BaseFragment implements OnClickListener {

	private TextView tv_uname;
	private ImageView iv_avatar;
	private RelativeLayout rl_aboutus;
	private RelativeLayout rl_update;
	private TextView tv_phone_memory;
	private TextView tv_card_memory;
	private TextView tv_phone_usage;
	private TextView tv_card_usage;
	private TextView tv_cur_update;
	private ImageView iv_nowifi_play;
	private ImageView iv_nowifi_dl;
	private TextView tv_nowifi_play;
	private TextView tv_nowifi_dl;
	private Button btn_logout;
	private Button btn_settings_logout_immediately;
	private Dialog dialog;
	private UserInfo userInfo;
	private boolean isChecking = false;
	private boolean setting_dl_to_sdcard = false;
	private boolean setting_nowifi_play = false;
	private boolean setting_nowifi_dl = false;
	protected String auth;
	private RoundedImageView iv_fragment_setting_head;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setting, container,
				false);
		RelayoutViewTool.relayoutViewWithScale(view, NCE2.screenWidthScale);
		userInfo = getUserInfo();
		initWidget(view);
		setWidgetState();
		SettingTitle();
		return view;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		// case R.id.btn_settings_avatar:
		// goAvatar();
		// break;
		case R.id.rl_settings_aboutus:
			// TODO 统计【设置】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"about_id");
			goAboutUs();
			break;
		case R.id.rl_settings_update:
			// TODO 统计【检测版本】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"checkversion_id");
			checkUpdate();
			break;
		case R.id.iv_settings_nowifi_play:
			switchNoWifiPlay();
			break;
		case R.id.iv_settings_nowifi_dl:
			switchNoWifiDl();
			break;
		case R.id.btn_settings_logout:

			toggleLogin();
			break;
		default:
			break;
		}

	}
	
	
	
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onResume(getActivity());
	}

	protected void toggleLogin() {

		if (userInfo != null) {
			tv_uname.setText("");
			PreferencesUtil.put(NCE2.KEY_AUTH, "");
			clearUserInfo();
			Intent intent = new Intent(getActivity(), CourseActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

		} else {
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			startActivity(intent);
		}

		if (btn_logout.getText().equals("退出登录")) {
			// TODO 统计【退出登录】点击量
						MobclickAgent.onEvent(getActivity().getApplicationContext(),
								"exit_id");
			iv_fragment_setting_head
					.setImageResource(R.drawable.adapter_words_charts_avatar);
			showToast("退出成功", Toast.LENGTH_SHORT);
		}
	}

	public void initHeader() {
		// initHeaderWidget();
		// setTitle(R.string.title_settings);
		// setBtnLeftVisibleState(false);
	}

	public void initWidget(View view) {
		tv_uname = (TextView) view.findViewById(R.id.tv_settings_uname);
		rl_aboutus = (RelativeLayout) view
				.findViewById(R.id.rl_settings_aboutus);
		iv_nowifi_play = (ImageView) view
				.findViewById(R.id.iv_settings_nowifi_play);
		iv_nowifi_dl = (ImageView) view
				.findViewById(R.id.iv_settings_nowifi_dl);
		tv_nowifi_play = (TextView) view
				.findViewById(R.id.tv_settings_nowifi_play);
		tv_nowifi_dl = (TextView) view.findViewById(R.id.tv_settings_nowifi_dl);
		rl_update = (RelativeLayout) view.findViewById(R.id.rl_settings_update);
		btn_logout = (Button) view.findViewById(R.id.btn_settings_logout);
		iv_fragment_setting_head = (RoundedImageView) view
				.findViewById(R.id.iv_fragment_setting_head);
	}

	public void setWidgetState() {
		// btn_avatar.setOnClickListener(this);

		rl_aboutus.setOnClickListener(this);
		iv_nowifi_play.setOnClickListener(this);
		iv_nowifi_dl.setOnClickListener(this);
		rl_update.setOnClickListener(this);
		btn_logout.setOnClickListener(this);
	}

	// private void goAvatar() {
	// Intent intent = new Intent(getApplicationContext(),
	// AvatarActivity.class);
	// startActivityForResult(intent, VickeyNCE.REQUEST_CHANGEAVATAR);
	// }
	//
	// private void goFeedback() {
	// Intent intent = new Intent(getApplicationContext(),
	// FeedbackActivity.class);
	// startActivity(intent);
	// }
	//
	private void goAboutUs() {
		Intent intent = new Intent(getActivity(), AboutActivity.class);
		startActivity(intent);
//		Intent intent = new Intent(getActivity(), WebActivity.class);
//		intent.putExtra(WebActivity.TITLE, getString(R.string.title_about));
//		intent.putExtra(WebActivity.URL, getString(R.string.url_about));
//		startActivity(intent);
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// switch (requestCode) {
	// case NCE2.REQUEST_CHANGEAVATAR:
	// if (resultCode == RESULT_OK) {
	// Toast.makeText(getActivity(), "头像修改完毕", 1).show();
	// } else {
	// Toast.makeText(getActivity(), "取消修改头像", 1).show();
	// }
	// break;
	// default:
	// break;
	// }
	// super.onActivityResult(requestCode, resultCode, data);
	// }

	private void getCache() {
		setting_dl_to_sdcard = PreferencesUtil.get(NCE2.SETTING_DL_TO_SDCARD,
				true);
		setting_nowifi_play = PreferencesUtil.get(NCE2.SETTING_NOWIFI_PLAY,
				false);
		setting_nowifi_dl = PreferencesUtil.get(NCE2.SETTING_NOWIFI_DL, false);
		userInfo = getUserInfo();
		draw();
	}

	private void getData() {
		if (!NetWorkUtil.networkCanUse(getActivity())) {
			return;
		}
		Ion.with(getActivity(), NCE2.SERVER_URL + "user/info")
				.setBodyParameter("device_id", getDeviceId())
				.setBodyParameter("auth", getAuth()).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {

							Toast.makeText(getActivity(), "服务器返回异常，请重试", 1)
									.show();
							return;
						}
						String code = result.get("code").getAsString();
						if ("200".equals(code)) {
							Gson gson = new Gson();
							userInfo = gson.fromJson(result.get("msg"),
									UserInfo.class);
							if (userInfo != null) {
								setUserInfo(userInfo);
								draw();
							}
						} else if ("401".equals(code)) {
							forceLogin();
						} else {
							Toast.makeText(getActivity(),
									result.get("msg").getAsString(), 1).show();
						}
					}
				});
	}

	private void draw() {
		if (userInfo != null) {
			tv_uname.setText(userInfo.uname);
			if (!StringUtil.isEmpty(userInfo.avatar)) {
				Ion.with(iv_avatar).load(userInfo.avatar);
			}
		}
		if (setting_dl_to_sdcard) {
			tv_phone_memory.setTextColor(getResources().getColor(
					R.color.settings_tv_disabled));
			tv_card_memory.setTextColor(getResources().getColor(
					R.color.settings_tv_enabled));
		} else {
			tv_phone_memory.setTextColor(getResources().getColor(
					R.color.settings_tv_enabled));
			tv_card_memory.setTextColor(getResources().getColor(
					R.color.settings_tv_disabled));
		}
		if (setting_nowifi_play) {
			tv_nowifi_play.setTextColor(getResources().getColor(
					R.color.settings_tv_enabled));
			iv_nowifi_play.setBackgroundResource(R.drawable.option_open);
		} else {
			tv_nowifi_play.setTextColor(getResources().getColor(
					R.color.settings_tv_disabled));
			iv_nowifi_play.setBackgroundResource(R.drawable.option_close);
		}
		if (setting_nowifi_dl) {
			tv_nowifi_dl.setTextColor(getResources().getColor(
					R.color.settings_tv_enabled));
			iv_nowifi_dl.setBackgroundResource(R.drawable.option_open);
		} else {
			tv_nowifi_dl.setTextColor(getResources().getColor(
					R.color.settings_tv_disabled));
			iv_nowifi_dl.setBackgroundResource(R.drawable.option_close);
		}
	}

	private void checkUpdate() {
		if (!NetWorkUtil.networkCanUse(getActivity())) {
			showToast("此操作需要联网", Toast.LENGTH_SHORT);
			return;
		}
		if (isChecking)
			return;
		isChecking = true;
		// startProgressBar(R.string.tips_update_checking, new Thread(), true);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				// closeProgressBar();
				isChecking = false;
				switch (updateStatus) {
				case 0: // has update
					UmengUpdateAgent
							.showUpdateDialog(getActivity(), updateInfo);
					PreferencesUtil.put(NCE2.KEY_NEW_VERSION, true);
					break;
				case 1: // has no update
					dialog = UIHelper
							.buildAlert(
									getActivity(),
									"检查更新",
									getString(R.string.tips_update_already_newest_version),
									"确定", new OnClickListener() {

										@Override
										public void onClick(View v) {
											dialog.dismiss();
										}
									});
					PreferencesUtil.put(NCE2.KEY_NEW_VERSION, false);
					break;
				case 2: // none wifi
					showToast("请您在WIFI环境下使用此功能", Toast.LENGTH_SHORT);
					break;
				case 3: // time out
					showToast("网络连接超时，请确认您的手机能够正常访问网络后再重试", Toast.LENGTH_SHORT);
					break;
				}
			}
		});
		UmengUpdateAgent.update(getActivity());
	}

	private void getVersion() {
		String verName = "";
		try {
			verName = getActivity().getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 0).versionName;
			tv_cur_update.setText("（当前版本：v" + verName + "）");
		} catch (NameNotFoundException e) {
			MLog.e("version_check", e.getMessage());
		}
	}

	private void switchNoWifiPlay() {
		setting_nowifi_play = !setting_nowifi_play;
		PreferencesUtil.put(NCE2.SETTING_NOWIFI_PLAY, setting_nowifi_play);
		if (setting_nowifi_play) {
			tv_nowifi_play.setTextColor(getResources().getColor(
					R.color.settings_tv_enabled));
			iv_nowifi_play.setBackgroundResource(R.drawable.option_open);
		} else {
			tv_nowifi_play.setTextColor(getResources().getColor(
					R.color.settings_tv_disabled));
			iv_nowifi_play.setBackgroundResource(R.drawable.option_close);
		}
	}

	private void switchNoWifiDl() {
		setting_nowifi_dl = !setting_nowifi_dl;
		PreferencesUtil.put(NCE2.SETTING_NOWIFI_DL, setting_nowifi_dl);
		if (setting_nowifi_dl) {
			tv_nowifi_dl.setTextColor(getResources().getColor(
					R.color.settings_tv_enabled));
			iv_nowifi_dl.setBackgroundResource(R.drawable.option_open);
		} else {
			tv_nowifi_dl.setTextColor(getResources().getColor(
					R.color.settings_tv_disabled));
			iv_nowifi_dl.setBackgroundResource(R.drawable.option_close);
		}
	}

	private void switchDlToSDCard(boolean status) {
		setting_dl_to_sdcard = status;
		PreferencesUtil.put(NCE2.SETTING_DL_TO_SDCARD, setting_dl_to_sdcard);
		if (setting_dl_to_sdcard) {
			tv_phone_memory.setTextColor(getResources().getColor(
					R.color.settings_tv_disabled));
			tv_card_memory.setTextColor(getResources().getColor(
					R.color.settings_tv_enabled));
		} else {
			tv_phone_memory.setTextColor(getResources().getColor(
					R.color.settings_tv_enabled));
			tv_card_memory.setTextColor(getResources().getColor(
					R.color.settings_tv_disabled));
		}
	}

	private void getCapacity() {

		DecimalFormat df = new DecimalFormat("0.00");
		File data = Environment.getDataDirectory();
		StatFs statFs = new StatFs(data.getPath());
		long availableBlocks = statFs.getAvailableBlocks();// 可用存储块的数量
		long blockCount = statFs.getBlockCount();// 总存储块的数量

		int size = statFs.getBlockSize();// 每块存储块的大小

		double totalSize = blockCount * size / 1024.0 / 1024 / 1024;// 总存储量

		double availableSize = availableBlocks * size / 1024.0 / 1024 / 1024;// 可用容量

		File sdData = Environment.getExternalStorageDirectory();
		StatFs sdStatFs = new StatFs(sdData.getPath());

		long sdAvailableBlocks = sdStatFs.getAvailableBlocks();// 可用存储块的数量
		long sdBlockcount = sdStatFs.getBlockCount();// 总存储块的数量
		int sdSize = sdStatFs.getBlockSize();// 每块存储块的大小
		double sdTotalSize = sdBlockcount * sdSize / 1024.0 / 1024 / 1024;
		double sdAvailableSize = sdAvailableBlocks * sdSize / 1024.0 / 1024
				/ 1024;

		final String SDCardRoot = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator;

		double per = 0;

		per = FileUtil.getSize(NCE2.fBaseDir) / 1024.0 / 1024;

		String phoneCapacity = "总量" + df.format(totalSize) + "GB/已用"
				+ df.format(availableSize) + "GB";
		String sdcardCapacity = "总量" + df.format(sdTotalSize) + "GB/已用"
				+ df.format(sdAvailableSize) + "GB";
		tv_phone_usage.setText(phoneCapacity);
		tv_card_usage.setText(sdcardCapacity);

	}

	private void clearCache() {
		dialog = UIHelper.buildConfirm(getActivity(), "是否清除缓存", "确认", "取消",
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						FileUtil.delete(NCE2.fBaseDir, false);
						PreferencesUtil.put(
								NCE2.COURSELISTDAILOGADAPTER_CLEAREN, 1);
						// 用来标记清理过缓冲的
						PreferencesUtil.put(NCE2.STETING_ACTIVITY_CLEAN_BUFFER,
								true);
						// if (bufferidlist != null && bufferidlist.size() > 0)
						// {
						// for (int i = 0; i < bufferidlist.size(); i++) {
						// PreferencesUtil.putPreferences(
						// VickeyNCE.COURSE_BUFFER_ID
						// + bufferidlist.get(i), 0);
						// }
						// // 清空list集合里面的数据
						// bufferidlist.clear();
						// Db.put(VickeyNCE.BUFFER_ID, bufferidlist);
						// // Db.del(VickeyNCE.BUFFER_ID);
						// bufferidlist = (List<String>) Db
						// .get(VickeyNCE.BUFFER_ID);
						//
						// }
						dialog.dismiss();
						Toast.makeText(getActivity(), "缓存已清除", 1).show();
					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	}

	@Override
	public void onResume() {
		userInfo = getUserInfo();
		SettingTitle();
		super.onResume();
		MobclickAgent.onResume(getActivity());
	}

	public void SettingTitle() {
		if (userInfo != null) {
			if (!StringUtil.isEmpty(userInfo.uname)) {
				tv_uname.setText(userInfo.uname);
			}
			if (!StringUtil.isEmpty(userInfo.avatar)) {
				Ion.with(getActivity()).load(userInfo.avatar).asBitmap()
						.setCallback(new FutureCallback<Bitmap>() {

							@Override
							public void onCompleted(Exception e, Bitmap bitmap) {
								iv_fragment_setting_head.setImageBitmap(bitmap);
							}
						});
			}
			btn_logout.setText("退出登录");
		} else {
			btn_logout.setText("立即登录");
		}
	}
}
