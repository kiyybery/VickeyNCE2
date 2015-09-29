package cn.ttsk.nce2.ui.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.StringUtil;
import cn.ttsk.library.tencent.BaseUiListener;
import cn.ttsk.library.weibo.User;
import cn.ttsk.library.weibo.UsersAPI;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.Tencent;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends BaseActivity {

	public final static String TARGET = "login-target";
	
	public final static int TARGET_DOWNLOAD = 1;
	public final static int TARGET_WORDS = 2;
	public final static int TARGET_RECITE = 3;

	private final static int LOGINTYPE_QQ = 0;
	private final static int LOGINTYPE_SINA = 1;
	private Button btn_qq;
	private Button btn_sina;
	public Tencent mTencent;
	public static QQAuth mQQAuth;
	public static String mAppid;
	private UserInfo mInfo;
	private String openid, access_token, nickname, avatar_url;
	private WeiboAuth mWeiboAuth;
	private SsoHandler mSsoHandler;
	private int loginType;
	private int target;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mQQAuth = QQAuth.createInstance(NCE2.TENCENT_APP_ID,
				getApplicationContext());
		mTencent = Tencent.createInstance(NCE2.TENCENT_APP_ID,
				getApplicationContext());
		target = getIntent().getIntExtra(TARGET, 0);
		// regToWx();
		initHeader();
		initWidget();
		setWidgetState();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_login_qq:
			// TODO 统计【分享】点击量
						MobclickAgent.onEvent(this,
								"qq_login_id");
			qqLogin();
			break;
		case R.id.btn_login_sina:
			MobclickAgent.onEvent(this,
					"sina_login_id");
			sinaLogin();
			break;
		default:
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onResume(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	@Override
	public void initHeader() {
	}
	

	@Override
	public void initWidget() {
		btn_qq = (Button) findViewById(R.id.btn_login_qq);
		btn_sina = (Button) findViewById(R.id.btn_login_sina);
	}

	@Override
	public void setWidgetState() {
		btn_qq.setOnClickListener(this);
		btn_sina.setOnClickListener(this);
	}

	private void qqLogin() {
		if (mQQAuth.isSessionValid()) {
			mQQAuth.logout(this);
		}
		loginType = LOGINTYPE_QQ;
		startProgressBar("登录中...", new Thread(), true);
		LoginListener listener = new LoginListener(this);
		mTencent.login(this, "all", listener);
	}

	private void sinaLogin() {
		loginType = LOGINTYPE_SINA;
		startProgressBar("登录中...", new Thread(), false);
		mWeiboAuth = new WeiboAuth(LoginActivity.this, NCE2.SINA_APP_KEY,
				NCE2.SINA_REDIRECT_URL, NCE2.SINA_SCOPE);
		mSsoHandler = new SsoHandler(LoginActivity.this, mWeiboAuth);
		mSsoHandler.authorize(new AuthListener());

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (loginType) {
		case LOGINTYPE_QQ:
			mTencent.onActivityResult(requestCode, resultCode, data);
//			closeProgressBar();
			break;
		case LOGINTYPE_SINA:
			if (mSsoHandler != null) {
				mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			}
			break;
		}
	}

	private class LoginListener extends BaseUiListener {

		public LoginListener(BaseActivity context) {
			super(context);
			
		}

		@Override
		protected void doComplete(JSONObject values) {
			try {
				openid = values.getString("openid");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			try {
				access_token = values.getString("access_token");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			updateQQUserInfo();
		}
		@Override
		public void onCancel() {
			closeProgressBar();
			showToast("用户取消登录",
					Toast.LENGTH_SHORT);
		}
	}

	private void updateQQUserInfo() {
		if (mQQAuth == null || !mQQAuth.isSessionValid())
			return;
		BaseUiListener listener = new BaseUiListener(this) {

			@Override
			public void doComplete(JSONObject response) {
				
				try {
					nickname = response.getString("nickname");
				} catch (Exception e) {
					// TODO: handle exception
				}
				if (response.has("figureurl_qq_2")) {
					try {
						avatar_url = response.getString("figureurl_qq_2");
					} catch (JSONException e) {
					}
				} else if (response.has("figureurl_qq_1")) {
					try {
						avatar_url = response.getString("figureurl_qq_1");
					} catch (JSONException e) {
					}
				}
				doLogin();
			}
		};
		mInfo = new UserInfo(this, mQQAuth.getQQToken());
		mInfo.getUserInfo(listener);
	}

	private void doLogin() {
		String push_id = PreferencesUtil.get(NCE2.KEY_PUSH_ID, "");
		String typeString = "";
		switch (loginType) {
		case LOGINTYPE_QQ:
			typeString = "qzone";
			break;
		case LOGINTYPE_SINA:
			typeString = "sina";
			break;
		default:
			break;
		}
		Ion.with(getApplicationContext(), NCE2.SERVER_URL + "nce2user/bind")
				.setBodyParameter("device_id", getDeviceId())
				.setBodyParameter("type", typeString)
				.setBodyParameter("openid", openid)
				.setBodyParameter("uname", nickname)
				.setBodyParameter("avatar", avatar_url)
				.setBodyParameter("push_id", push_id).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						closeProgressBar();
						if (e != null) {
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
							return;
						}
						String code = result.get("code").getAsString();
						if ("200".equals(code)) {
							JsonObject jsonObject = result.get("msg")
									.getAsJsonObject();
							if (!jsonObject.has("auth")) {
								showToast(R.string.tips_ion_servererror,
										Toast.LENGTH_SHORT);
							} else {
								String auth = jsonObject.get("auth")
										.getAsString();
								if (StringUtil.isEmpty(auth)) {
									showToast("服务端返回空的auth", Toast.LENGTH_SHORT);
								} else {
									cn.ttsk.nce2.entity.UserInfo userInfo = new cn.ttsk.nce2.entity.UserInfo();
									userInfo.setId(jsonObject.get("id").getAsString());
									userInfo.setUname(nickname);
									userInfo.setAvatar(avatar_url);
									setUserInfo(userInfo);
									PreferencesUtil.put(NCE2.KEY_AUTH, auth);
									Intent intent;
									switch (target) {
									case TARGET_DOWNLOAD:
										intent = new Intent(getApplicationContext(), CourseActivity.class);
										intent.putExtras(getIntent().getExtras());
										startActivity(intent);
										break;
									case TARGET_RECITE:
										intent = new Intent(getApplicationContext(), TextOralTestActivity.class);
										intent.putExtras(getIntent().getExtras());
										startActivity(intent);
										break;
									case TARGET_WORDS:
										intent = new Intent(getApplicationContext(), WordsTestActivity.class);
										intent.putExtras(getIntent().getExtras());
										startActivity(intent);
										break;
									default:
										break;
									}
									finish();
								}
							}

						} else {
							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
							
						}
					}
				});
	}

	class AuthListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			closeProgressBar();
			showToast("用户取消登录", Toast.LENGTH_SHORT);
		}

		@Override
		public void onComplete(Bundle values) {

			Oauth2AccessToken accessToken = Oauth2AccessToken
					.parseAccessToken(values);
			updateSinaUserInfo(accessToken);
		}

		@Override
		public void onWeiboException(WeiboException e) {
			closeProgressBar();
			showToast("登录异常", Toast.LENGTH_SHORT);
			e.printStackTrace();
		}

	}

	private void updateSinaUserInfo(Oauth2AccessToken accessToken) {
		UsersAPI mUsersAPI = new UsersAPI(accessToken);
		long uid = Long.parseLong(accessToken.getUid());
		openid = String.valueOf(uid);
		RequestListener mlistener = new RequestListener() {

			@Override
			public void onWeiboException(WeiboException e) {
				closeProgressBar();
				showToast("获取用户信息异常", Toast.LENGTH_SHORT);
				e.printStackTrace();
				
			}

			@Override
			public void onComplete(String response) {
				if (!StringUtil.isEmpty(response)) {
					User user = User.parse(response);
					nickname = user.name;
					avatar_url = user.avatar_large;
					doLogin();
				}
			}
		};
		mUsersAPI.show(uid, mlistener);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(getApplicationContext(),
				CourseActivity.class);
		startActivity(intent);
		finish();
	}
}
