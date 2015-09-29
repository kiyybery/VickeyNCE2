package cn.ttsk.nce2.ui.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.library.StringUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.UserInfo;
import cn.ttsk.nce2.ui.activity.CourseActivity;
import cn.ttsk.nce2.ui.activity.LoginActivity;
import cn.ttsk.nce2.wxapi.WXEntryActivity;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.CallbackConfig.ICallbackListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

@SuppressLint("ResourceAsColor")
public class LeftSlidingMenuFragment extends BaseFragment implements
		OnClickListener {

	private Dialog dialog;
	private View yixinBtnLayout;
	private View circleBtnLayout;
	private View settingBtnLayout;
	private View sharedBtnLayout;
	private ImageView ivHead;
	private View feedbackBtnLayout;
	private TextView tvNickname;
	private TextView tv_main_title_TitleBtnRigh;
	private TextView tv_main_title_TitleBtnRigh_calloff;
	private TextView ivTitleName;
	private ImageView notification_indicator;

	private ImageView rlCommenTitleBG;

	private TextView toolbox_title;
	private ImageView notification_indicator_buffer;
	private TextView toolbox_title_buffer;
	private ImageView notification_indicator_setting;
	private TextView toolbox_title_setting;
	private ImageView notification_indicator_fangui;
	private TextView toolbox_title_fangui;
	private ImageView notification_indicator_share;
	private TextView toolbox_title_share;
	private LinearLayout llRoot;
	private UserInfo userInfo;
	RelativeLayout rl_top_bar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_left_fragment, container,
				false);
		RelayoutViewTool.relayoutViewWithScale(view, NCE2.screenWidthScale);
		yixinBtnLayout = view.findViewById(R.id.yixinBtnLayout);
		yixinBtnLayout.setOnClickListener(this);
		circleBtnLayout = view.findViewById(R.id.circleBtnLayout);
		circleBtnLayout.setOnClickListener(this);
		settingBtnLayout = view.findViewById(R.id.settingBtnLayout);
		settingBtnLayout.setOnClickListener(this);
		sharedBtnLayout = view.findViewById(R.id.sharedBtnLayout);
		sharedBtnLayout.setOnClickListener(this);
		ivHead = (ImageView) view.findViewById(R.id.iv_main_left_head);
		ivHead.setOnClickListener(this);

		feedbackBtnLayout = view.findViewById(R.id.feedbackBtnLayout);
		feedbackBtnLayout.setOnClickListener(this);
		tvNickname = (TextView) view.findViewById(R.id.tv_main_left_nickname);
		tv_main_title_TitleBtnRigh = (TextView) getActivity().findViewById(
				R.id.tv_main_title_TitleBtnRigh);
		tv_main_title_TitleBtnRigh_calloff = (TextView) getActivity()
				.findViewById(R.id.tv_main_title_TitleBtnRigh_calloff);
		ivTitleName = (TextView) getActivity().findViewById(R.id.ivTitleName);
		rlCommenTitleBG = (ImageView) getActivity().findViewById(
				R.id.rlCommenTitleBG);
		rl_top_bar = (RelativeLayout) getActivity().findViewById(
				R.id.rl_top_bar);
		rl_top_bar.setVisibility(View.GONE);
		notification_indicator = (ImageView) view
				.findViewById(R.id.notification_indicator);
		toolbox_title = (TextView) view.findViewById(R.id.toolbox_title);
		notification_indicator_buffer = (ImageView) view
				.findViewById(R.id.notification_indicator_buffer);
		toolbox_title_buffer = (TextView) view
				.findViewById(R.id.toolbox_title_buffer);
		notification_indicator_setting = (ImageView) view
				.findViewById(R.id.notification_indicator_setting);
		toolbox_title_setting = (TextView) view
				.findViewById(R.id.toolbox_title_setting);
		notification_indicator_fangui = (ImageView) view
				.findViewById(R.id.notification_indicator_fangui);
		toolbox_title_fangui = (TextView) view
				.findViewById(R.id.toolbox_title_fangui);
		notification_indicator_share = (ImageView) view
				.findViewById(R.id.notification_indicator_share);
		toolbox_title_share = (TextView) view
				.findViewById(R.id.toolbox_title_share);

		return view;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void onClick(View v) {
		Fragment newContent = null;

		switch (v.getId()) {
		case R.id.yixinBtnLayout:

			// TODO 统计【首页】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"home_id");
			newContent = new PicViewFragment();
			rl_top_bar.setVisibility(View.GONE);
			PreferencesUtil.put(NCE2.CACHEKEY_MAIN_FRAGMENT,
					CourseActivity.FRAGMENT_COURSE);
			yixinBtnLayout.setSelected(true);
			circleBtnLayout.setSelected(false);
			settingBtnLayout.setSelected(false);
			sharedBtnLayout.setSelected(false);
			feedbackBtnLayout.setSelected(false);

			// rlCommenTitleBG.setBackgroundColor(R.color.title_home_bar);

			tv_main_title_TitleBtnRigh_calloff.setVisibility(View.GONE);
			tv_main_title_TitleBtnRigh.setVisibility(View.GONE);

			// ivTitleName.setText("");
			// rlCommenTitleBG.setBackgroundResource(R.drawable.image_home_bg);
			notification_indicator
					.setImageResource(R.drawable.sidebar_icon1_select);
			toolbox_title.setTextColor(Color.rgb(20, 204, 178));
			notification_indicator_buffer
					.setImageResource(R.drawable.sidebar_icon2_noselect);
			if (userInfo == null) {
				toolbox_title_buffer.setTextColor(getResources().getColor(
						R.color.main_left_text_gray));
			} else {
				toolbox_title_buffer.setTextColor(getResources().getColor(
						R.color.main_left_text_normal));
			}
			notification_indicator_setting
					.setImageResource(R.drawable.sidebar_icon3_noselect);
			toolbox_title_setting.setTextColor(Color.WHITE);
			notification_indicator_fangui
					.setImageResource(R.drawable.sidebar_icon4_noselect);
			toolbox_title_fangui.setTextColor(Color.WHITE);
			notification_indicator_share
					.setImageResource(R.drawable.sidebar_icon5_noselect);
			toolbox_title_share.setTextColor(Color.WHITE);
			break;
		case R.id.circleBtnLayout:
			// TODO 统计【缓存】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"cache_id");
			if (!checkDownloadLogin()) {

				return;
			}

			newContent = new DownloadFragment();
			rl_top_bar.setVisibility(View.VISIBLE);
			PreferencesUtil.put(NCE2.CACHEKEY_MAIN_FRAGMENT,
					CourseActivity.FRAGMENT_DOWNLOAD);
			yixinBtnLayout.setSelected(false);
			circleBtnLayout.setSelected(true);
			settingBtnLayout.setSelected(false);
			sharedBtnLayout.setSelected(false);
			feedbackBtnLayout.setSelected(false);
			tv_main_title_TitleBtnRigh.setVisibility(View.VISIBLE);
			tv_main_title_TitleBtnRigh_calloff.setVisibility(View.GONE);
			ivTitleName.setText(R.string.title_buffer);
			rlCommenTitleBG.setBackgroundResource(R.drawable.common_title_bg);
			notification_indicator
					.setImageResource(R.drawable.sidebar_icon1_noselect);
			toolbox_title.setTextColor(Color.WHITE);
			notification_indicator_buffer
					.setImageResource(R.drawable.sidebar_icon2_select);
			toolbox_title_buffer.setTextColor(Color.rgb(20, 204, 178));
			notification_indicator_setting
					.setImageResource(R.drawable.sidebar_icon3_noselect);
			toolbox_title_setting.setTextColor(Color.WHITE);
			notification_indicator_fangui
					.setImageResource(R.drawable.sidebar_icon4_noselect);
			toolbox_title_fangui.setTextColor(Color.WHITE);
			notification_indicator_share
					.setImageResource(R.drawable.sidebar_icon5_noselect);
			toolbox_title_share.setTextColor(Color.WHITE);

			break;
		case R.id.settingBtnLayout:
			// TODO 统计【设置】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"setting_id");
			newContent = new SettingFragment();
			rl_top_bar.setVisibility(View.VISIBLE);
			PreferencesUtil.put(NCE2.CACHEKEY_MAIN_FRAGMENT,
					CourseActivity.FRAGMENT_SETTING);

			sharedBtnLayout.setSelected(false);
			yixinBtnLayout.setSelected(false);
			circleBtnLayout.setSelected(false);
			settingBtnLayout.setSelected(true);
			feedbackBtnLayout.setSelected(false);
			tv_main_title_TitleBtnRigh_calloff.setVisibility(View.GONE);
			tv_main_title_TitleBtnRigh.setVisibility(View.GONE);
			ivTitleName.setText(R.string.title_setting);
			rlCommenTitleBG.setBackgroundResource(R.drawable.common_title_bg);
			// SettingLogn();
			notification_indicator
					.setImageResource(R.drawable.sidebar_icon1_noselect);
			toolbox_title.setTextColor(Color.WHITE);
			notification_indicator_buffer
					.setImageResource(R.drawable.sidebar_icon2_noselect);
			if (userInfo == null) {
				toolbox_title_buffer.setTextColor(getResources().getColor(
						R.color.main_left_text_gray));
			} else {
				toolbox_title_buffer.setTextColor(getResources().getColor(
						R.color.main_left_text_normal));
			}
			notification_indicator_setting
					.setImageResource(R.drawable.sidebar_icon3_select);
			toolbox_title_setting.setTextColor(Color.rgb(20, 204, 178));
			notification_indicator_fangui
					.setImageResource(R.drawable.sidebar_icon4_noselect);
			toolbox_title_fangui.setTextColor(Color.WHITE);
			notification_indicator_share
					.setImageResource(R.drawable.sidebar_icon5_noselect);
			toolbox_title_share.setTextColor(Color.WHITE);
			break;
		case R.id.iv_main_left_head:
			// TODO 统计【登录】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"login_id");

			goLogin();
			break;
		case R.id.feedbackBtnLayout:
			// TODO 统计【反馈】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"feedback_id");
			sharedBtnLayout.setSelected(false);
			feedbackBtnLayout.setSelected(true);
			yixinBtnLayout.setSelected(false);
			circleBtnLayout.setSelected(false);
			settingBtnLayout.setSelected(false);
			rlCommenTitleBG.setBackgroundResource(R.drawable.common_title_bg);
			newContent = new ConversationFrament();
			rl_top_bar.setVisibility(View.VISIBLE);
			PreferencesUtil.put(NCE2.CACHEKEY_MAIN_FRAGMENT,
					CourseActivity.FRAGMENT_FEEDBACK);
			tv_main_title_TitleBtnRigh_calloff.setVisibility(View.GONE);
			tv_main_title_TitleBtnRigh.setVisibility(View.GONE);
			ivTitleName.setText(R.string.title_feedback);
			notification_indicator
					.setImageResource(R.drawable.sidebar_icon1_noselect);
			toolbox_title.setTextColor(Color.WHITE);
			notification_indicator_buffer
					.setImageResource(R.drawable.sidebar_icon2_noselect);
			if (userInfo == null) {
				toolbox_title_buffer.setTextColor(getResources().getColor(
						R.color.main_left_text_gray));
			} else {
				toolbox_title_buffer.setTextColor(getResources().getColor(
						R.color.main_left_text_normal));
			}
			notification_indicator_setting
					.setImageResource(R.drawable.sidebar_icon3_noselect);
			toolbox_title_setting.setTextColor(Color.WHITE);
			notification_indicator_fangui
					.setImageResource(R.drawable.sidebar_icon4_select);
			toolbox_title_fangui.setTextColor(Color.rgb(20, 204, 178));
			notification_indicator_share
					.setImageResource(R.drawable.sidebar_icon5_noselect);
			toolbox_title_share.setTextColor(Color.WHITE);
			break;
		case R.id.sharedBtnLayout:
			// TODO 统计【分享】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"share_id");
			sharedBtnLayout.setSelected(true);
			feedbackBtnLayout.setSelected(false);
			// homeBtnLayout.setSelected(false);
			circleBtnLayout.setSelected(false);
			settingBtnLayout.setSelected(false);

			NCE2.mController.getConfig().removePlatform(SHARE_MEDIA.RENREN,
					SHARE_MEDIA.DOUBAN);
			NCE2.mController.getConfig().removePlatform(SHARE_MEDIA.EMAIL,
					SHARE_MEDIA.GOOGLEPLUS);
			NCE2.mController.getConfig().removePlatform(SHARE_MEDIA.TENCENT,
					SHARE_MEDIA.GOOGLEPLUS);

			addWXPlatform();
			addQQPlatform();
			addQZonePlatform();
			
			NCE2.mController.setShareContent(getString(R.string.share_content)
					+ "\n" + getString(R.string.share_url));

			NCE2.mController.openShare(getActivity(), false);
			NCE2.mController.registerListener(snsPostListener);
//			NCE2.mController.registerListener(new SnsPostListener() {
//
//				@Override
//				public void onComplete(SHARE_MEDIA arg0, int stCode,
//						SocializeEntity arg2) {
//					// TODO Auto-generated method stub
//					if (stCode == 200) {
//						Toast.makeText(getActivity(), "分享成功",
//								Toast.LENGTH_SHORT).show();
//					} else {
////						Toast.makeText(getActivity(),
////								"分享失败 : error code : " + stCode,
////								Toast.LENGTH_SHORT).show();
//						Toast.makeText(getActivity(),
//								"取消分享",
//								Toast.LENGTH_SHORT).show();
//					}
//				}
//
//				@Override
//				public void onStart() {
//					// TODO Auto-generated method stub
//
//				}
//			});
//			
			notification_indicator
					.setImageResource(R.drawable.sidebar_icon1_noselect);
			toolbox_title.setTextColor(Color.WHITE);
			notification_indicator_buffer
					.setImageResource(R.drawable.sidebar_icon2_noselect);
			if (userInfo == null) {
				toolbox_title_buffer.setTextColor(getResources().getColor(
						R.color.main_left_text_gray));
			} else {
				toolbox_title_buffer.setTextColor(getResources().getColor(
						R.color.main_left_text_normal));
			}
			notification_indicator_setting
					.setImageResource(R.drawable.sidebar_icon3_noselect);
			toolbox_title_setting.setTextColor(Color.WHITE);
			notification_indicator_fangui
					.setImageResource(R.drawable.sidebar_icon4_noselect);
			toolbox_title_fangui.setTextColor(Color.WHITE);
			notification_indicator_share
					.setImageResource(R.drawable.sidebar_icon5_select);
			toolbox_title_share.setTextColor(Color.rgb(20, 204, 178));
			break;
		default:
			break;
		}

		if (newContent != null)
			switchFragment(newContent);

	}
	final SnsPostListener snsPostListener = new SnsPostListener() {

		@Override
		public void onStart() {
			Toast.makeText(getActivity(), "分享开始", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onComplete(SHARE_MEDIA platform, int eCode,
				SocializeEntity entity) {
			if (eCode == 200) {
				Toast.makeText(getActivity(), "分享成功",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(),
						"取消分享",
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		CourseActivity ra = (CourseActivity) getActivity();
		ra.switchContent(fragment);

	}
	

	public void goLogin() {
		UserInfo userInfo = getUserInfo();
		if (userInfo == null) {
			Intent intent = new Intent(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void onResume() {
		userInfo = getUserInfo();
		if (userInfo != null) {
			if (!StringUtil.isEmpty(userInfo.uname)) {
				tvNickname.setText(userInfo.uname);
			}
			if (!StringUtil.isEmpty(userInfo.avatar)) {
				Ion.with(getActivity()).load(userInfo.avatar).asBitmap()
						.setCallback(new FutureCallback<Bitmap>() {

							@Override
							public void onCompleted(Exception e, Bitmap bitmap) {
								ivHead.setImageBitmap(bitmap);
							}
						});
			} else {
				ivHead.setImageResource(R.drawable.adapter_words_charts_avatar);
			}
		} else {
			tvNickname.setText("");
			ivHead.setImageResource(R.drawable.sidebar_login);
		}
		if (userInfo == null) {
			toolbox_title_buffer.setTextColor(getResources().getColor(
					R.color.main_left_text_gray));
		} else {
			toolbox_title_buffer.setTextColor(getResources().getColor(
					R.color.main_left_text_normal));
		}
		super.onResume();
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onResume(getActivity());
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 *  
	 */
	private void addQQPlatform() {
		// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(),
				NCE2.TENCENT_APP_ID, NCE2.TENCENT_APP_SECRET);
		qqSsoHandler.setTargetUrl(getString(R.string.share_url));
		qqSsoHandler.addToSocialSDK();

	}

	// 分享到Qzone
	private void addQZonePlatform() {
		// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(),
				NCE2.TENCENT_APP_ID, NCE2.TENCENT_APP_SECRET);
		qZoneSsoHandler.setTargetUrl(getString(R.string.share_url));
		qZoneSsoHandler.addToSocialSDK();

	}

		// 设置新浪SSO handler
		// NCE2.mController.getConfig().setSsoHandler(new SinaSsoHandler());

	private void addWXPlatform() {
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(getActivity(), NCE2.WX_APP_ID);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(),
				NCE2.WX_APP_ID);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

}
