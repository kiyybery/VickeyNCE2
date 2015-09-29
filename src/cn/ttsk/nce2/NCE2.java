package cn.ttsk.nce2;

import java.io.File;

import net.tsz.afinal.FinalDb;

import com.testin.agent.TestinAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.text.TextUtils;
import android.widget.Toast;
import cn.ttsk.MumianApplication;
import cn.ttsk.library.Db;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.StringUtil;
import cn.ttsk.nce2.entity.DownloadItem;
import cn.ttsk.nce2.entity.Section;
import cn.ttsk.nce2.entity.Video;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class NCE2 extends MumianApplication {

	// 友盟反馈
	FeedbackAgent agent;

	public static final boolean DEBUG = true;

	public static final int REQUEST_CHANGEAVATAR = 1001;

	public static final String KEY_AUTH = "key-auth";
	public static final String KEY_NEW_VERSION = "key-new-version";
	public static final String KEY_PUSH_ID = "key-push-id";

	public static final String SERVER_URL = "http://api.vickeynce.com/";
//	 public static final String SERVER_URL =
//	 "http://nceapi.matao.sooker.3322.org/";
	public static final String TENCENT_APP_ID = "101030294";
	public static final String TENCENT_APP_SECRET = "316bc0cb029a39e65bfb059c829f7d44";
	public static final String SHARE_WX_APP_ID = "wxc27756ca3dee5f0e";
	public static final String IFLY_APP_ID = "53f02092";
	public static final String SINA_APP_KEY = "1993697517";
	public static final String SINA_REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
	public static final String SINA_SCOPE = "all";
	public static final String ALIPAY_APP_ID = "2088501891311140";
	//微信
	public static final String WX_APP_ID="wx8e5fa2e76a44104b";
	public static final String WX_APP_SECRET="6e1192432428bd01056daceb8c648a2a";
	
	public static final String CACHEKEY_USERINFO = "cachekey-userinfo-1";
	public static final String CACHEKEY_COURSELIST = "cachekey-courselist-3";
	public static final String CACHEKEY_SECTIONIDLIST_PRE = "cachekey-sectionidlist-3";
	public static final String CACHEKEY_SECTION_PRE = "cachekey-section-2-";
	public static final String CACHEKEY_VIDEO_PRE = "cachekey-video-1-";
	public static final String CACHEKEY_WORDDATA_PRE = "cachekey-worddata-1-";
	public static final String CACHEKEY_TEXTDATA_PRE = "cachekey-textdata-1-";
	public static final String CACHEKEY_UPDATE_VERSION = "cachekey-update-version-2";
	public static final String CACHEKEY_MAIN_FRAGMENT = "cachekey-main_fragment";
	public static final String CACHEKEY_WORD_POS = "cachekey-word-pos";
	public static final String CACHEKEY_WORD_GRADE = "cachekey-word-grade";
	
	public static final String CACHEFILE_NCE_IMG_PRE = "nce_img_";
	public static final String CACHEFILE_COURSE_IMG_PRE = "course_img_";
	public static final String CACHEFILE_VIDEO_IMG_PRE = "video_img_";

	public static final String VICKEY_SD_CARD = "movie";
	public static final String SETTING_DL_TO_SDCARD = "vickeynce-setting-dl-to-sdcard";// 下载到SD卡
	public static final String SETTING_NOWIFI_PLAY = "vickeynce-setting-nowifi-play";// 无wifi环境下播放开关
	public static final String SETTING_NOWIFI_DL = "vickeynce-setting-nowifi-dl";// 无wifi环境下下载开关
	public static final String COURSE_BUFFER_ID = "vickeynce-courseliseviewadapter-move";// 储存数据的时候用的list
	public static final String COURSE_BUFFER_ID_INFO = "vickeynce-courseliseviewadapter-move-info";// 储存数据info
	public static final String COURSE_BUFFER_ID_INFO_ALL = "vickeynce-courseliseviewadapter-move-infoall";// 存储的所有的info信息
	public static final String USERFIRST = "userfirst-1.0.0";// 判断是不是第一次启动
	public static final String VIDEOVIEWPLAYINGACTIVITY_AK = "doReGivLiH2ImPf54v5j9ZP1";
	public static final String VIDEOVIEWPLAYINGACTIVITY_SK = "ZX2Gwi91bETbnLCeIuQH4pC08QxQ2Dpk";
	public static final String COURSELISTDAILOGADAPTER_BUGGERSTATE = "db-state";// 0代表是没有缓冲，1代表是没有缓冲完成，2代表是缓冲完成。
	public static final String COURSELISTDAILOGADAPTER_CLEAREN = "cleanbuffer";// 1表示缓冲清除0表示缓冲没有清除
	public static final String NEEDUPDATE_NCELIST = "vickeynce-needupdate-ncelist";
	public static final String COURSE_BUFFER_STOP_DOWN = "vickeynce-stopdowns";// 点击停止下载
	public static final String COURSE_BUFFER_DOWNING_IS = "vickeynce-buffer-down";
	public static final String COURSE_BUFFER_INITUI_PROGRESS = "vickeynce-progress";// 下载的进度条的数字
	public static final String BUFFER_VIDEO_SERVICE = "vickeynce-dowm-only-one"; // 点击离线缓冲的时候仅仅允许同时去下载一个视频文件，默认值设置为true
	public static final String COURSE_BUFFER_BEIDANCI_RANKING = "vickeynce- Ranking"; // 背单词的排行榜。
	public static final String COURSE_BUFFER_GRADE = "vickeynce-grade"; // 成绩。
	public static final String COURSE_BUFFER_TIME = "vickeynce-time"; // 用的时间。
	public static final String KEY_CURRENT_COURSE_ID = "vickeynce-current-course-id";
	public static final String KEY_CURRENT_SECTION_ID = "vickeynce-current-section-id";
	public static final String COURSE_RECEIVER_ACTION = "course_receiver_action";
	public static final String BUFFER_ID = "vickeynce-buffer-id";
	public static final String BUFFER_VIDEOSERVICE_DOWN_STATE = "buffer_videoservice_down_state";// 用来标记下载完毕用来标记清除缓冲的1表示下载完毕，默认0
	public static final String STETING_ACTIVITY_CLEAN_BUFFER = "setting_activity_clean_buffer";// true表示清理完毕，false表示未清理

	
	public static final int MSG_TESTING_BLANK_ANSWER = 1000;
	public static final int MSG_TESTING_CHOICE_ANSWER = 1001;
	public static final int MSG_TESTING_NEXT = 1002;

	public static final int MSG_MAIN_TIMER_OK = 1009;
	public static final int MSG_MAIN_UPDATE_OK = 1010;
	public static final int MSG_WORD_UNZIP_FINISHED = 1011;
	public static final int MSG_WORD_DECODEDATA_FINISHED = 1013;
	public static final int MSG_TEXT_ORAL_REC_FINISH = 1017;
	public static final int MSG_TEXT_ORAL_SAVE_FINISH = 1018;
	public static final int MSG_TEXT_ORAL_SAVE_FAILED = 1019;
	public static final int MSG_TEXT_ORAL_REC_FAILED = 1020;
	public static final int MSG_PICVIEW_CLICK = 1030;
	public static final int MSG_PICVIEW_CHANGE = 1031;
	public static final int MSG_PICVIEW_READY = 1032;
	public static final int MSG_ALIPAY_BACK = 1040;
	public static final int MSG_VIDEO_PLAY_LIST = 1051;
	public static final int MSG_VIDEO_PLAY_ONE = 1052;
	public static final int MSG_VIDEO_PLAY_COMPLETE = 1053;
	public static final int VIDEO_PLAY_UI_HO = 1;
	public static final int VIDEO_PLAY_UI_V = 2;

	public static final int CHALLENGE_COUNT = 20;//单词挑战题数

	public static boolean sdcard_ready = false;
	public static File fBaseDir;
	public static String umeng_channel;
	public static FinalDb db;

	final public static UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share", RequestType.SOCIAL);
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		context = getApplicationContext();
		db = FinalDb.create(context, false);
		
		try {
			Db.init(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String sdcard_state = Environment.getExternalStorageState();
		sdcard_ready = Environment.MEDIA_MOUNTED.equals(sdcard_state);
		getBaseDir();
		// 友盟意见反馈
		agent = new FeedbackAgent(this);
		agent.sync();
		getUmengChannel();

	}

	public static void getBaseDir() {
		if (sdcard_ready) {
			fBaseDir = context
					.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
		}
		if (fBaseDir == null || (!fBaseDir.exists() && !fBaseDir.mkdir()) || !fBaseDir.isDirectory()) {
			fBaseDir = new File(context.getFilesDir(),
					Environment.DIRECTORY_DOWNLOADS);
		}
	}

	public static File getImgDir() {
		File dir = new File(fBaseDir, "img");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	public static File getVideoDir() {
		File dir = new File(fBaseDir, "video");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	public static File getWordTestDir() {
		File dir = new File(fBaseDir, "wordtest");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	public static File getReciteDir() {
		File dir = new File(fBaseDir, "recite");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	public static String getDeviceInfo(Context context) {
		try {
			org.json.JSONObject json = new org.json.JSONObject();
			android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			String device_id = tm.getDeviceId();

			android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);

			String mac = wifi.getConnectionInfo().getMacAddress();
			json.put("mac", mac);

			if (TextUtils.isEmpty(device_id)) {
				device_id = mac;
			}
			if (TextUtils.isEmpty(device_id)) {
				device_id = android.provider.Settings.Secure.getString(
						context.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
			}
			json.put("device_id", device_id);
			return json.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static File getTextTestDir() {
		File dir = new File(fBaseDir, "texttest");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	public static File getOralDir() {
		File dir = new File(fBaseDir, "oral");
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}
	
	public static File getSectionCover(Section section) {
		return new File(getImgDir(), "section_" + section.id
				+ section.img.substring(section.img.lastIndexOf(".")));
	}
	
	public static File getSectionCoverFiltered(Section section) {
		return new File(getImgDir(), "section_filtered_" + section.id
				+ ".png");
	}
	
	public static File getSectionCoverGaused(Section section) {
		return new File(getImgDir(), "section_gaused_" + section.id
				+ ".png");
	}

	public static File getVideoCover(Video video) {
		return new File(getImgDir(), "video_" + video.id
				+ video.img.substring(video.img.lastIndexOf(".")));
	}

	public static File getVideoFile(String vid) {
		return new File(getVideoDir(), NCE2.VICKEY_SD_CARD + vid + ".mp4");
	}
	public static File getVideoTmpFile(String vid) {
		return new File(getVideoDir(), NCE2.VICKEY_SD_CARD + vid + ".tmp");
	}

	public static int getDensity() {
		return android.os.Build.VERSION.SDK_INT < 9 ? DisplayMetrics.DENSITY_HIGH
				: DisplayMetrics.DENSITY_XHIGH;
	}

	public void getUmengChannel() {
		ApplicationInfo appInfo;
		try {
			appInfo = this.getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);

			umeng_channel = appInfo.metaData.getString("UMENG_CHANNEL");
			if (StringUtil.isEmpty(umeng_channel)) {
				umeng_channel = appInfo.metaData.getInt("UMENG_CHANNEL") + "";
			}
			if (StringUtil.isEmpty(umeng_channel)) {
				umeng_channel = "default";
			}
		} catch (NameNotFoundException e) {
			umeng_channel = "";
		}

	}
}
