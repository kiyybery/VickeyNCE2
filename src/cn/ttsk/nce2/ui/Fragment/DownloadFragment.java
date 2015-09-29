package cn.ttsk.nce2.ui.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.ttsk.library.FileUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;

import cn.ttsk.nce2.entity.DownloadItem;

import cn.ttsk.nce2.ui.adapter.DownloadFragmentAdapter;

import cn.ttsk.nce2.ui.service.BufferVideoService;

public class DownloadFragment extends BaseFragment implements OnClickListener {

	public static String VID = "vid";
	public static String DOWNLOADED = "downloaded";
	public static String FULLSIZE = "fullsize";
	public static String id = null;
	public static final int TYPE_FINISHED = 1;
	public static final int TYPE_DOWNLOADING = 2;
	private Button btn_fragment_download_sccess_buffer;
	private Button btn_fragment_download_not_sccess_buffer;
	private ListView lv_fragment_download_sccess_listview;
	private LinearLayout ll_fragment_download_delete;
	public Button btn_fragment_download_delete;
	public TextView tv_main_title_TitleBtnRigh;
	public TextView tv_main_title_TitleBtnRigh_calloff;

	private List<DownloadItem> successlist;
	private List<DownloadItem> notsuccesslist;
	private DownloadFragmentAdapter adapter;
	private MyReceiver receiver;
	private int width;
	private int buffertype;
	public static List<String> deletelist = new ArrayList<String>();
	public static List<String> successdeletelist = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(NCE2.COURSE_RECEIVER_ACTION);
		getActivity().registerReceiver(receiver, filter);

		buffertype = TYPE_DOWNLOADING;
		if (!checkLogin()) {
			return;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
		if (notsuccesslist != null)
			notsuccesslist = null;
		if (successlist != null)
			successlist = null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_download, container,
				false);

		tv_main_title_TitleBtnRigh_calloff = (TextView) getActivity()
				.findViewById(R.id.tv_main_title_TitleBtnRigh_calloff);

		btn_fragment_download_sccess_buffer = (Button) view
				.findViewById(R.id.btn_fragment_download_sccess_buffer);
		btn_fragment_download_not_sccess_buffer = (Button) view
				.findViewById(R.id.btn_fragment_download_not_sccess_buffer);
		lv_fragment_download_sccess_listview = (ListView) view
				.findViewById(R.id.lv_fragment_download_sccess_listview);
		btn_fragment_download_delete = (Button) view
				.findViewById(R.id.btn_fragment_download_delete);

		tv_main_title_TitleBtnRigh = (TextView) getActivity().findViewById(
				R.id.tv_main_title_TitleBtnRigh);
		ll_fragment_download_delete = (LinearLayout) view
				.findViewById(R.id.ll_fragment_download_delete);
		tv_main_title_TitleBtnRigh.setVisibility(View.VISIBLE);
		btn_fragment_download_sccess_buffer.setOnClickListener(this);
		btn_fragment_download_not_sccess_buffer.setOnClickListener(this);
		tv_main_title_TitleBtnRigh.setOnClickListener(this);

		tv_main_title_TitleBtnRigh_calloff.setOnClickListener(this);

		btn_fragment_download_delete.setOnClickListener(this);
		lv_fragment_download_sccess_listview.setAdapter(adapter);

		notsuccesslist = NCE2.db.findAllByWhere(DownloadItem.class, "status!="
				+ DownloadItem.STATUS_FINISHED, "created");
		successlist = NCE2.db.findAllByWhere(DownloadItem.class, "status="
				+ DownloadItem.STATUS_FINISHED, "created");

		if (notsuccesslist == null) {
			notsuccesslist = new ArrayList<DownloadItem>();
		}
		if (successlist == null) {
			successlist = new ArrayList<DownloadItem>();
		}

		DisplayMetrics metric = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_fragment_download_sccess_buffer:// 已缓冲的代码
			// TODO 统计【已缓存】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"cached_id");
			SccessBuffer();
			break;
		case R.id.btn_fragment_download_not_sccess_buffer:// 缓冲中的代码
			// TODO 统计【正在缓存】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"caching_id");
			NotSccessBuffer();

			break;
		case R.id.tv_main_title_TitleBtnRigh:// 编辑按钮
			// TODO 统计【编辑】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"edit_id");
			Edit();
			break;
		case R.id.tv_main_title_TitleBtnRigh_calloff:// 取消编辑按钮
			CallOffEdit();
			break;
		case R.id.btn_fragment_download_delete:// 删除按钮
			DownloadDelete();
			// TODO 统计【删除】点击量
			MobclickAgent.onEvent(getActivity().getApplicationContext(),
					"delete_id");
			break;
		default:
			break;
		}

	}

	// 点击已缓冲的时候执行的方法
	public void SccessBuffer() {
		btn_fragment_download_sccess_buffer.setTextColor(Color
				.rgb(20, 204, 178));
		btn_fragment_download_not_sccess_buffer.setTextColor(Color.rgb(51, 51,
				51));
		ll_fragment_download_delete.setVisibility(View.GONE);
		btn_fragment_download_not_sccess_buffer
				.setBackgroundResource(R.drawable.btn_download_buffer_noselect);
		btn_fragment_download_sccess_buffer
				.setBackgroundResource(R.drawable.btn_download_buffer_select);
		showList(TYPE_FINISHED, false);
	}

	/*
	 * 点击正在缓冲的时候执行的方法
	 */
	public void NotSccessBuffer() {
		btn_fragment_download_sccess_buffer.setTextColor(Color.rgb(51, 51, 51));
		btn_fragment_download_not_sccess_buffer.setTextColor(Color.rgb(20, 204,
				178));
		ll_fragment_download_delete.setVisibility(View.GONE);
		btn_fragment_download_not_sccess_buffer
				.setBackgroundResource(R.drawable.btn_download_buffer_select);
		btn_fragment_download_sccess_buffer
				.setBackgroundResource(R.drawable.btn_download_buffer_noselect);
		showList(TYPE_DOWNLOADING, false);
	}

	// 点击编辑按钮的时候执行的方法

	public void Edit() {
		showList(buffertype, true);
	}

	// 取消编辑按钮
	public void CallOffEdit() {
		showList(buffertype, false);
	}

	// 点击删除按钮的时候执行的方法
	public void DownloadDelete() {

		tv_main_title_TitleBtnRigh_calloff.setVisibility(View.GONE);
		tv_main_title_TitleBtnRigh.setVisibility(View.VISIBLE);
		ll_fragment_download_delete.setVisibility(View.GONE);

		if (buffertype == TYPE_FINISHED) {
			for (String tmpId : successdeletelist) {
				DownloadItem tmpItem = NCE2.db.findById(tmpId,
						DownloadItem.class);
				if (tmpItem != null) {
					NCE2.db.delete(tmpItem);
					FileUtil.delete(new File(tmpItem.tmpFile), true);
					FileUtil.delete(new File(tmpItem.file), true);
				}
			}
			showList(TYPE_FINISHED, false);
		} else {
			for (String tmpId : successdeletelist) {
				DownloadItem tmpItem = NCE2.db.findById(tmpId,
						DownloadItem.class);
				if (tmpItem != null) {
					NCE2.db.delete(tmpItem);
					Intent intent = new Intent(getApplicationContext(),
							BufferVideoService.class);
					intent.putExtra(BufferVideoService.SID, tmpItem.section_id);
					intent.putExtra(BufferVideoService.VID, tmpItem.video_id);
					intent.putExtra(BufferVideoService.ACTION,
							BufferVideoService.ACTION_DELETE);
					getApplicationContext().startService(intent);
				}
			}
			showList(TYPE_DOWNLOADING, false);
		}

	}

	public void showList(int type, Boolean editMode) {
		switch (type) {
		case TYPE_FINISHED:
			buffertype = TYPE_FINISHED;
			successlist = NCE2.db.findAllByWhere(DownloadItem.class, "status="
					+ DownloadItem.STATUS_FINISHED, "created");
			adapter = new DownloadFragmentAdapter(successlist, getActivity(),
					DownloadFragmentAdapter.TYPE_FINISHED, width, 0,
					editMode ? 1 : 2);
			lv_fragment_download_sccess_listview.setAdapter(adapter);
			if (successlist.size() > 0) {
				if (editMode) {
					tv_main_title_TitleBtnRigh_calloff.setVisibility(View.VISIBLE);
					tv_main_title_TitleBtnRigh.setVisibility(View.GONE);
					ll_fragment_download_delete.setVisibility(View.VISIBLE);
				} else {
					tv_main_title_TitleBtnRigh_calloff.setVisibility(View.GONE);
					tv_main_title_TitleBtnRigh.setVisibility(View.VISIBLE);
					ll_fragment_download_delete.setVisibility(View.GONE);
				}
			} else {
				tv_main_title_TitleBtnRigh_calloff.setVisibility(View.GONE);
				tv_main_title_TitleBtnRigh.setVisibility(View.GONE);
				ll_fragment_download_delete.setVisibility(View.GONE);
			}
			break;
		case TYPE_DOWNLOADING:
			buffertype = TYPE_DOWNLOADING;
			notsuccesslist = NCE2.db.findAllByWhere(DownloadItem.class,
					"status!=" + DownloadItem.STATUS_FINISHED, "created");
			adapter = new DownloadFragmentAdapter(notsuccesslist,
					getActivity(), DownloadFragmentAdapter.TYPE_DOWNLOADING,
					width, 0, editMode ? 1 : 2);
			lv_fragment_download_sccess_listview.setAdapter(adapter);
			tv_main_title_TitleBtnRigh_calloff.setVisibility(View.GONE);
			tv_main_title_TitleBtnRigh.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String vid = intent.getStringExtra(VID);
			long downloaded = intent.getLongExtra(DOWNLOADED, 0);
			long fullsize = intent.getLongExtra(FULLSIZE, 0);
			if (downloaded == fullsize || buffertype == TYPE_DOWNLOADING) {
				showList(buffertype, false);
			}
		}
	}

	@Override
	public void onResume() {
		showList(buffertype, false);
		super.onResume();
		MobclickAgent.onResume(getActivity());
	}

	@Override
	public void onPause() {
		notsuccesslist = null;
		successlist = null;
		super.onPause();
		MobclickAgent.onResume(getActivity());
	}
	
	
}
