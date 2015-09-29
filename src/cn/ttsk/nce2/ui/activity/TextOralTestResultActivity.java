package cn.ttsk.nce2.ui.activity;

import java.io.File;
import java.util.Random;

import cn.ttsk.library.NumberUtil;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;

import com.baidu.cyberplayer.utils.r;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.umeng.socialize.bean.RequestType;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class TextOralTestResultActivity extends BaseActivity implements
		OnClickListener {
	public final static String TOTR_TITLE = "totr_title";
	public final static String TOTR_ID_FILE = "totr_id_file";
	public final static String TOTR_ID = "totr_id";
	public final static String TOTR_SHARE_URL = "share_url";
	public final static String TOTR_GRADE = "totr_grade";
	private Button but_text_oral_testresult_share;
	private Button but_text_oral_testresult_save;

	private String totr_id;
	private String share_url;
	private String totr_title;
	private ImageView iv_text_oral_testresult_play;
	private File totr_id_file;
	private int grade;
	private TextView tv_text_oral_testresult_grade;
	private TextView tv_ext_oral_testresult_Ranking;
	final UMSocialService mController = UMServiceFactory.getUMSocialService(
			"com.umeng.share", RequestType.SOCIAL);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_oral_testresult);
		totr_id_file = (File) getIntent().getSerializableExtra(TOTR_ID_FILE);
		totr_id = getIntent().getStringExtra(TOTR_ID);
		share_url = getIntent().getStringExtra(TOTR_SHARE_URL);
		totr_title = getIntent().getStringExtra(TOTR_TITLE);
		grade = getIntent().getIntExtra(TOTR_GRADE, 0);
		initHeader();
		initWidget();
		setWidgetState();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.but_text_oral_testresult_share:
			Intestshare();
			break;

		case R.id.iv_text_oral_testresult_play:

			CommitFuwuqi();
			break;
		default:
			break;
		}
	}

	@Override
	public void initHeader() {
	}

	@Override
	public void initWidget() {

		but_text_oral_testresult_share = (Button) findViewById(R.id.but_text_oral_testresult_share);
		iv_text_oral_testresult_play = (ImageView) findViewById(R.id.iv_text_oral_testresult_play);
		tv_text_oral_testresult_grade = (TextView) findViewById(R.id.tv_text_oral_testresult_grade);
		tv_ext_oral_testresult_Ranking = (TextView) findViewById(R.id.tv_ext_oral_testresult_Ranking);
	}

	@Override
	public void setWidgetState() {

		but_text_oral_testresult_share.setOnClickListener(this);
		iv_text_oral_testresult_play.setOnClickListener(this);
		tv_text_oral_testresult_grade.setText(grade + "");
		tv_ext_oral_testresult_Ranking.setText("打败了" + getPos(grade) + "%的用户");
	}

	// 点击分享的按钮的时候调用的方法
	public void Intestshare() {
		NCE2.mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
				SHARE_MEDIA.DOUBAN);
		NCE2.mController.getConfig().removePlatform(SHARE_MEDIA.EMAIL,
				SHARE_MEDIA.GOOGLEPLUS);
		NCE2.mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT,
				SHARE_MEDIA.GOOGLEPLUS);

		addWXPlatform();
		addQQPlatform();
		addQZonePlatform();
		NCE2.mController.setShareContent(getString(R.string.text_oraltest_content)
				+ "\n" + getString(R.string.share_url));
		NCE2.mController.openShare(TextOralTestResultActivity.this, false);
		NCE2.mController.registerListener(snsPostListener);
	}
	final SnsPostListener snsPostListener = new SnsPostListener() {

		@Override
		public void onStart() {
			Toast.makeText(getApplicationContext(), "分享开始", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onComplete(SHARE_MEDIA platform, int eCode,
				SocializeEntity entity) {
			if (eCode == 200) {
				Toast.makeText(getApplicationContext(), "分享成功",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"取消分享",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	public void IntentPlay() {
		Intent intent = new Intent(getApplicationContext(),
				TextOralFinishActivity.class);
		intent.putExtra(TextOralFinishActivity.TOTA_ID_FILE, totr_id_file);
		intent.putExtra(TextOralFinishActivity.TOTA_TITLE, totr_title);
		intent.putExtra(TextOralFinishActivity.TOFA_ID, totr_id);
		startActivity(intent);
	}

	// 点击一下的时候向服务器提交数据的方法
	public void CommitFuwuqi() {
		Ion.with(TextOralTestResultActivity.this,
				NCE2.SERVER_URL + "/nce2oral/upload")
				.setMultipartParameter("device_id", getDeviceId())
				.setMultipartParameter("auth", getAuth())
				.setMultipartParameter("recite_id", totr_id)
				.setMultipartFile("mp3", totr_id_file).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
							return;
						}
						int code = result.get("code").getAsInt();
						switch (code) {
						case 200:
							IntentPlay();
							break;
						case 401:
							showToast("请先登录后再进行练习", Toast.LENGTH_SHORT);
							forceLogin();
							break;
						default:
							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
							break;
						}
					}
				});

	}

	private int getPos(int grade) {
		int lastPos = PreferencesUtil.get(NCE2.CACHEKEY_WORD_POS, 0);
		int lastGrade = PreferencesUtil.get(NCE2.CACHEKEY_WORD_GRADE, 0);
		int pos;
		if (lastPos == 0) {
			Random random = new Random();
			pos = NumberUtil.convertFloatToInt(grade * 1.0f
					* (91.0f + random.nextInt(6)) / 100.0f);
		} else {
			pos = NumberUtil.convertFloatToInt(grade * 1.0f / lastGrade
					* lastPos * 1.01f);
		}
		PreferencesUtil.put(NCE2.CACHEKEY_WORD_GRADE, grade);
		PreferencesUtil.put(NCE2.CACHEKEY_WORD_POS, pos);
		return pos;
	}

	private void addQQPlatform() {
		// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this,
				NCE2.TENCENT_APP_ID, NCE2.TENCENT_APP_SECRET);
		qqSsoHandler.setTargetUrl(getString(R.string.share_url));
		qqSsoHandler.addToSocialSDK();

	}

	// 分享到Qzone
	private void addQZonePlatform() {
		// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this,
				NCE2.TENCENT_APP_ID, NCE2.TENCENT_APP_SECRET);
		qZoneSsoHandler.setTargetUrl(getString(R.string.share_url));
		qZoneSsoHandler.addToSocialSDK();

	}

	private void addWXPlatform() { // 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(
				TextOralTestResultActivity.this, NCE2.WX_APP_ID);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(
				TextOralTestResultActivity.this, NCE2.WX_APP_ID);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}
}
