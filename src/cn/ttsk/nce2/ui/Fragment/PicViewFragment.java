package cn.ttsk.nce2.ui.Fragment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.android.app.sdk.AliPay;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttsk.library.BoxBlur;
import cn.ttsk.library.ImageUtil;
import cn.ttsk.library.NetWorkUtil;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.RollerView;
import cn.ttsk.library.UIHelper;
import cn.ttsk.library.alipay.Result;
import cn.ttsk.library.slidingmenu.SlidingMenu;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.OrderInfo;
import cn.ttsk.nce2.entity.RollerPic;
import cn.ttsk.nce2.entity.Section;
import cn.ttsk.nce2.entity.SectionProgress;
import cn.ttsk.nce2.entity.Video;
import cn.ttsk.nce2.ui.activity.CourseActivity;
import cn.ttsk.nce2.ui.activity.VideoViewPlayingActivity;

public class PicViewFragment extends BaseFragment implements OnClickListener {
	protected SlidingMenu mSlidingMenu;
	private ImageButton btn_id;
	private RollerView rvSections;
	private TextView tvTitle;
	private List<Section> sections = new ArrayList<Section>();
	private List<Bitmap> mBitmaps = new ArrayList<Bitmap>();
	private List<Bitmap> mBlurBitmaps = new ArrayList<Bitmap>();
	private Dialog dialog;

	private Boolean downloadingCourse = false, downloadingWords = false,
			downloadingExt = false;
	private int downloadingImgs = 0;
	private Map<File, String> imgs;
	private int newVersion;
	private Boolean waitForRefresh = false;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NCE2.MSG_PICVIEW_CHANGE:
				changeText();
				break;
			case NCE2.MSG_PICVIEW_READY:
				draw();
				break;
			default:
				break;
			}
		}
	};

	public OnClickListener picListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (sections.size() > rvSections.currIndex) {
				final Section section = sections.get(rvSections.currIndex);
				if (Section.S_LOCKED == section.status) {
					if (!checkLogin()) {
						return;
					}
					int days = (int) Math.ceil((float)(section.free_time - System
							.currentTimeMillis()) / 86400000.0f);
					DecimalFormat df = new DecimalFormat("0.00");
					dialog = UIHelper.buildConfirm(getActivity(), days
							+ "天之后自动解锁", "付费" + df.format(section.price) + "提前学习", "等待"
							+ days + "天再学习", new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							buyCourse(section.id);
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
				} else {
					List<Video> videos = NCE2.db.findAllByWhere(Video.class,
							"sid=\"" + section.id + "\"");
					Intent intent = new Intent(getApplicationContext(),
							VideoViewPlayingActivity.class);
					intent.putExtra(VideoViewPlayingActivity.SID, section.id);
					intent.putExtra(VideoViewPlayingActivity.VID,
							videos.get(0).id);
					startActivity(intent);
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		sections = NCE2.db.findAllByWhere(Section.class, "1=1", "id");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_picview, container,
				false);
		rvSections = (RollerView) view
				.findViewById(R.id.rv_fragment_picview_img);
		tvTitle = (TextView) view.findViewById(R.id.tv_fragement_picview_title);
		btn_id = (ImageButton) view.findViewById(R.id.btn_id);
		btn_id.setOnClickListener(this);
		setWidgetStats();
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		rvSections.screenWidth = dm.widthPixels;
		rvSections.screenHeight = dm.heightPixels;
		rvSections.ratio = rvSections.screenWidth / rvSections.screenHeight;
		rvSections.requestFocus();
		rvSections.setFocusableInTouchMode(true);
		getData();

		// CourseActivity ra = (CourseActivity) getActivity();
		// ra.initSlidingMenu();
		return view;
	}

	public void setWidgetStats() {
		rvSections.setOnClickListener(picListener);
	}

	public void draw() {
		if (getView() == null) return;
		List<RollerPic> pics = new ArrayList<RollerPic>();
		for (Section section : sections) {
			RollerPic pic = new RollerPic();
			pic.file = NCE2.getSectionCoverFiltered(section);
			pic.gauseFile = NCE2.getSectionCoverGaused(section);
			pic.locked = section.status == Section.S_LOCKED;
			pics.add(pic);
		}
		rvSections.setData(pics, handler);
	}

	public void getData() {
		if (sections == null || sections.size() == 0) {
			downloadData();
		} else {
			draw();
			if (NetWorkUtil.checkWifiConnection(getApplicationContext())) {
				checkUpdate(false);
			}
		}
	}

	private void downloadData() {
		if (!NetWorkUtil.networkCanUse(getApplicationContext())) {
			dialog = UIHelper.buildAlert(getActivity(),
					getString(R.string.title_alert),
					getString(R.string.tips_firstdownload_no_network),
					getString(R.string.firstdownload_no_network_button),
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(
									"android.settings.WIFI_SETTINGS");
							startActivity(intent);
						}
					});
		} else if (!NetWorkUtil.checkWifiConnection(getApplicationContext())) {
			dialog = UIHelper.buildConfirm(getActivity(),
					getString(R.string.tips_firstdownload_no_wifi),
					getString(R.string.firstdownload_no_wifi_button_left),
					getString(R.string.firstdownload_no_wifi_button_right),
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							checkUpdate(true);
						}
					}, new OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
							Intent intent = new Intent(
									"android.settings.WIFI_SETTINGS");
							startActivity(intent);
						}
					});
		} else {
			checkUpdate(true);
		}
	}
	//TODO 第一次加载数据
	public void getSections(String courseUrl) {
		downloadingCourse = true;
		startProgressBar("正在初始化数据，请稍候..", new Thread(), false);
		Ion.with(getActivity(), courseUrl).asString()
				.setCallback(new FutureCallback<String>() {

					@Override
					public void onCompleted(Exception e, String result) {
						if (e != null) {
							e.printStackTrace();
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
							downloadingCourse = false;
							finishDownloading();
							return;
						}

						Gson gson = new Gson();
						Type type = new TypeToken<List<Section>>() {
						}.getType();
						List<Section> tmpSections = gson.fromJson(result, type);
						saveSections(tmpSections);
						getExtInfo(true);
					}
				});
	}

	public void getExtInfo(Boolean block) {
		downloadingExt = true;
		if (block) {
			startProgressBar("更新课程数据中", new Thread(), false);
		}
		Ion.with(getActivity(), NCE2.SERVER_URL + "nce2/extinfo")
				.setBodyParameter("auth", getAuth())
				.setBodyParameter("device_id", getDeviceId())
				.setBodyParameter("channel", NCE2.umeng_channel).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							e.printStackTrace();
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
							downloadingExt = false;
							finishDownloading();
							return;
						}
						int code = result.get("code").getAsInt();
						if (code == 200) {

							JsonObject msg = result.get("msg")
									.getAsJsonObject();
							Gson gson = new Gson();
							Type type = new TypeToken<List<Section>>() {
							}.getType();
							List<Section> channelSections = gson.fromJson(
									msg.get("sections"), type);
							List<Section> buyedSections = gson.fromJson(
									msg.get("buyed"), type);
							Type typeProgress = new TypeToken<List<SectionProgress>>() {
							}.getType();
							List<SectionProgress> progresses = gson.fromJson(
									msg.get("progress"), typeProgress);
							extCourses(channelSections, buyedSections,
									progresses);
						} else {
							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
							downloadingExt = false;
							finishDownloading();
						}
					}
				});
	}

	private void extCourses(List<Section> channelSections,
			List<Section> buyedSections, List<SectionProgress> progresses) {
		if (channelSections != null && channelSections.size() > 0) {
			for (Section tmpSection : channelSections) {
				Section oldSection = NCE2.db.findById(tmpSection.id,
						Section.class);
				if (oldSection == null) {
					NCE2.db.save(tmpSection);
				} else {
					NCE2.db.update(tmpSection);
				}
				if (tmpSection.videos != null) {
					for (Video video : tmpSection.videos) {

						Video oldVideo = NCE2.db
								.findById(video.id, Video.class);
						video.sid = tmpSection.id;
						if (oldVideo == null) {
							NCE2.db.save(video);
						} else {
							NCE2.db.update(video);
						}
					}
				}
			}
		}
		if (buyedSections != null && buyedSections.size() > 0) {
			for (Section tmpSection : buyedSections) {
				Section oldSection = NCE2.db.findById(tmpSection.id,
						Section.class);
				if (oldSection == null) {
					NCE2.db.save(tmpSection);
				} else {
					NCE2.db.update(tmpSection);
				}
				if (tmpSection.videos != null) {
					for (Video video : tmpSection.videos) {

						Video oldVideo = NCE2.db
								.findById(video.id, Video.class);
						video.sid = tmpSection.id;
						if (oldVideo == null) {
							NCE2.db.save(video);
						} else {
							NCE2.db.update(video);
						}
					}
				}
			}
		}
		if (progresses != null && progresses.size() > 0) {
			for (SectionProgress progress : progresses) {
				Section tmpSection = NCE2.db.findById(progress.id,
						Section.class);
				if (tmpSection != null) {
					if (progress.status == SectionProgress.STATUS_FINISHED) {
						tmpSection.status = Section.S_FINISHED;
						NCE2.db.update(tmpSection);
					}
				}
			}
		}
		sections = NCE2.db.findAllByWhere(Section.class, "1=1", "id");
		downloadingExt = false;
		finishDownloading();
	}

	public void saveSections(List<Section> sections) {

		imgs = new HashMap<File, String>();
		if (sections != null && sections.size() > 0) {
			for (Section tmpSection : sections) {
				Section oldSection = NCE2.db.findById(tmpSection.id,
						Section.class);
				if (oldSection == null) {
					NCE2.db.save(tmpSection);
				} else {
					NCE2.db.update(tmpSection);
				}
				imgs.put(NCE2.getSectionCover(tmpSection), tmpSection.img);
				if (tmpSection.videos != null) {
					for (Video video : tmpSection.videos) {

						Video oldVideo = NCE2.db
								.findById(video.id, Video.class);
						video.sid = tmpSection.id;
						if (oldVideo == null) {
							NCE2.db.save(video);
						} else {
							NCE2.db.update(video);
						}
					}
				}
				
			}
		}
		this.sections = sections;
		downloadingCourse = false;
		downloadImgs();
	}

	private void checkUpdate(Boolean block) {
		if (block) {
			startProgressBar(R.string.check_update, new Thread(), true);
		}
		Ion.with(getActivity(), NCE2.SERVER_URL + "nce2/checkupdate2")
				.setMultipartParameter("auth", "")
				.setMultipartParameter("device_id", getDeviceId())
				.asJsonObject().setCallback(new FutureCallback<JsonObject>() {
					
					@Override
					public void onCompleted(Exception e, JsonObject result) {
						closeProgressBar();
						if (e != null) {
							e.printStackTrace();
							return;
						}
						int code = result.get("code").getAsInt();
						if (code == 200) {
							JsonObject msg = result.get("msg")
									.getAsJsonObject();
							newVersion = msg.get("ver").getAsInt();
							int lastUpdate = PreferencesUtil.get(
									NCE2.CACHEKEY_UPDATE_VERSION, 0);
							if (lastUpdate < newVersion) {
								updateData(msg.get("url").getAsString());
							} else {
								getExtInfo(false);
							}
						} else {
							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
						}
					}
				});
	}

	private void updateData(String courseUrl) {
		getSections(courseUrl);
	}
	
	private void downloadImgs() {
		if (sections.size() == 0) {
			sections = NCE2.db.findAllByWhere(Section.class, "1=1", "id");
		}
		startProgressBar("正在初始化数据，请稍候..", new Thread(), true);
		final BitmapFactory.Options opts=new BitmapFactory.Options();
		opts.inTempStorage = new byte[100 * 1024];
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		final Bitmap shadow  = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.pic_shadow), null, opts);
		final Bitmap blurShadow = Bitmap.createBitmap(shadow.getWidth(), shadow.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas blurCanvas = new Canvas(blurShadow);
		blurCanvas.drawARGB(0, 0, 0, 0);
		
		for (final Section section : sections) {
			downloadingImgs++;
			String url = section.img;
			File file = NCE2.getSectionCover(section);
			Ion.with(getApplicationContext(), url).write(file)
					.setCallback(new FutureCallback<File>() {
						@Override
						public void onCompleted(Exception e, File file) {
							Bitmap bitmap;
							FileInputStream in;
							File shadowFile = NCE2.getSectionCoverFiltered(section);
							File blurFile = NCE2.getSectionCoverGaused(section);
							try {
								in = new FileInputStream(file);
								bitmap = BitmapFactory.decodeFileDescriptor(in.getFD(), null, opts);
								in.close();
								Bitmap shadowBitmap = ImageUtil.addShadowBitmap(bitmap, shadow);
								BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(shadowFile));
								shadowBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
								out.flush();
								out.close();

								Bitmap blurBitmap = BoxBlur.filter(ImageUtil.addShadowBitmap(
										bitmap, blurShadow));
								BufferedOutputStream outBlur = new BufferedOutputStream(
										new FileOutputStream(blurFile));
								blurBitmap.compress(Bitmap.CompressFormat.PNG, 90,
										outBlur);
								outBlur.flush();
								outBlur.close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							downloadingImgs--;
							finishDownloading();
						}
					});
		}

	}

	private void finishDownloading() {
		if (!downloadingCourse && !downloadingWords && downloadingImgs == 0
				&& !downloadingExt) {
			closeProgressBar();
			PreferencesUtil.put(NCE2.CACHEKEY_UPDATE_VERSION, newVersion);
			draw();
		}
	}

	private void buyCourse(final String sid) {
		Ion.with(getApplicationContext(), NCE2.SERVER_URL + "nce2pay/getorder")
				.setBodyParameter("auth", getAuth())
				.setBodyParameter("device_id", getDeviceId())
				.setBodyParameter("channel", NCE2.umeng_channel)
				.setBodyParameter("sid", sid).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						if (e != null) {
							e.printStackTrace();
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
							return;
						}
						int code = result.get("code").getAsInt();
						switch (code) {
						case 200:
							Gson gson = new Gson();
							OrderInfo orderInfo = gson.fromJson(
									result.get("msg"), OrderInfo.class);
							goPay(orderInfo);
							break;
						case 401:
							forceLogin();
						default:
							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
							break;
						}
					}
				});
	}

	private void goPay(final OrderInfo orderInfo) {
		new Thread() {
			public void run() {
				String order = cn.ttsk.library.alipay.Order.buildOrder(
						orderInfo.out_trade_no, orderInfo.subject,
						orderInfo.body, orderInfo.total_fee);

				AliPay alipay = new AliPay(getActivity(), alipayHandler);
				Result result = new Result(orderInfo.out_trade_no,
						alipay.pay(order));
				Message msg = new Message();
				msg.what = NCE2.MSG_ALIPAY_BACK;
				msg.obj = result;
				alipayHandler.sendMessage(msg);
			}
		}.start();
	}

	Handler alipayHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case NCE2.MSG_ALIPAY_BACK:
				Result result = (Result) msg.obj;
				if (result.getResultCode() != Result.RESULT_OK) {
					showToast(result.getResultStatus(), Toast.LENGTH_LONG);
				} else {
					checkPayResult(result.getOutTradeNo());
				}
				break;
			default:
				break;
			}
		};
	};

	private void checkPayResult(String billId) {
		startProgressBar("检查订单支付状态", new Thread(), true);
		Ion.with(getApplicationContext(),
				NCE2.SERVER_URL + "nce2pay/checkorder")
				.setBodyParameter("auth", getAuth())
				.setBodyParameter("device_id", getDeviceId())
				.setBodyParameter("bill_id", billId).asJsonObject()
				.setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						closeProgressBar();
						if (e != null) {
							e.printStackTrace();
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
							return;
						}
						int code = result.get("code").getAsInt();
						switch (code) {
						case 200:
							showToast("订单支付成功，开始更新课程", Toast.LENGTH_SHORT);
							checkUpdate(true);
							break;
						case 401:
							forceLogin();
						default:
							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
							break;
						}
					}
				});
	}

	public void changeText() {
		if (sections.size() > rvSections.currIndex) {
			Section section = sections.get(rvSections.currIndex);
			tvTitle.setText(section.title);
			Log.e("...............", ""+section.img);
		}
	}


	@Override
	public void onResume() {
		if (rvSections != null) {
			rvSections.onResume();
			if (waitForRefresh) {
				waitForRefresh = false;
				rvSections.needUpdate = true;
			}
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		if (rvSections != null)
			rvSections.onPause();
		waitForRefresh = true;
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_id:
			((CourseActivity) getActivity()).mSlidingMenu.showMenu(true);
			break;

		default:
			break;
		}
	}
}
