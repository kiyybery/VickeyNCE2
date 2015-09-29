package cn.ttsk;

import com.iflytek.cloud.SpeechUtility;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

public class MumianApplication extends Application {

	public static final String KEY_LOGIN_AUTH = "Member-Login-Auth";// 用户登录授权码
	public static String content;
	public static Context context;
	public static SharedPreferences preferences;
	public static DisplayMetrics displayMetrics;
	// 原始UI界面设计图的宽度(px)，用于后期对控件做缩放
	public static final float UI_Design_Width = 720;
	public static final float UI_Design_Height = 1136;
	// 屏幕宽度缩放比（相对于原设计图）
	public static float screenWidthScale = 1f;
	public static float screenHeightScale = 1f;

	@Override
	public void onCreate() {
		super.onCreate();
		SpeechUtility.createUtility(getApplicationContext(), "appid=53f02092");
		context = getApplicationContext();
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		displayMetrics = getResources().getDisplayMetrics();
		// 初始化屏幕宽度缩放比例
		screenWidthScale = displayMetrics.widthPixels / UI_Design_Width;
		screenHeightScale = displayMetrics.heightPixels / UI_Design_Height;
	}
}