package cn.ttsk.nce2.ui.adapter;

import java.io.FileInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.DownloadItem;
import cn.ttsk.nce2.entity.Section;
import cn.ttsk.nce2.ui.Fragment.DownloadFragment;
import cn.ttsk.nce2.ui.activity.VideoViewPlayingActivity;
import cn.ttsk.nce2.ui.service.BufferVideoService;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DownloadFragmentAdapter extends BaseAdapter {

	public static final int TYPE_FINISHED = 1;
	public static final int TYPE_DOWNLOADING = 2;
	private List<DownloadItem> videoList = new ArrayList<DownloadItem>();
	private Context context;
	private int type;// 1表示已经缓存完成的，2表示正在缓存的
	private int width;
	private TextView tv_main_title_TitleBtnRigh;
	private Button btn_fragment_download_delete;
	private int bianji;// 1表示点击编辑启动的适配器。默认为0；
	private int delete;
	DecimalFormat df = new DecimalFormat("0.00");

	public DownloadFragmentAdapter(List<DownloadItem> videoList,
			Context context, int type, int width, int bianj, int delete) {
		super();
		this.videoList = videoList;
		this.context = context;
		this.type = type;
		this.width = width;
		this.bianji = bianj;
		this.delete = delete;
	}

	public void resetData(List<DownloadItem> videoList) {
		this.videoList = videoList;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return videoList.size();
	}

	@Override
	public Object getItem(int pos) {
		// TODO Auto-generated method stub
		return videoList.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		// TODO Auto-generated method stub
		return pos;
	}

	@Override
	public View getView(final int pos, View convertView, ViewGroup group) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context,
					R.layout.adapter_download_fragment, null);
			RelayoutViewTool.relayoutViewWithScale(convertView,
					NCE2.screenWidthScale);

			viewHolder = new ViewHolder();
			viewHolder.tv_lesson = (TextView) convertView
					.findViewById(R.id.tv_adapter_downloadfragment_lesson);
			viewHolder.rl_down = (RelativeLayout) convertView
					.findViewById(R.id.rl_adapter_downloadfragment_down);
			viewHolder.btn_continue = (Button) convertView
					.findViewById(R.id.btn_adapter_downloadfragment_continue);
			viewHolder.btn_stop = (Button) convertView
					.findViewById(R.id.btn_adapter_downloadfragment_stop);
			viewHolder.tv_size = (TextView) convertView
					.findViewById(R.id.tv_adapter_downloadfragment_size);
			viewHolder.cbx_choose_before = (CheckBox) convertView
					.findViewById(R.id.cbx_adapter_downloadfragment_choose_before);
			viewHolder.cbx_choose_after = (CheckBox) convertView
					.findViewById(R.id.cbx_adapter_downloadfragment_choose_after);
			viewHolder.cbx_choose_before1 = (CheckBox) convertView
					.findViewById(R.id.cbx_adapter_downloadfragment_choose_before1);
			viewHolder.cbx_choose_after1 = (CheckBox) convertView
					.findViewById(R.id.cbx_adapter_downloadfragment_choose_after1);
			viewHolder.ll_not_success = (LinearLayout) convertView
					.findViewById(R.id.ll_adapter_downloadfragment_not_success);
			viewHolder.ll_success = (LinearLayout) convertView
					.findViewById(R.id.ll_adapter_downloadfragment_success);
			viewHolder.iv_progress_point = (ImageView) convertView
					.findViewById(R.id.iv_adapter_downloadfragment_progress_point);
			viewHolder.rl_checkbox = (RelativeLayout) convertView
					.findViewById(R.id.rl_adapter_downloadfragment_checkbox);
			viewHolder.rl_checkbox1 = (RelativeLayout) convertView
					.findViewById(R.id.rl_adapter_downloadfragment_checkbox1);
			viewHolder.iv_cover = (ImageView) convertView
					.findViewById(R.id.iv_adapter_downloadfragment_cover);
			viewHolder.iv_cover_success = (ImageView) convertView
					.findViewById(R.id.iv_adapter_downloadfragment_cover_success);
			viewHolder.tv_lesson_success = (TextView) convertView
					.findViewById(R.id.tv_adapter_downloadfragment_lesson_success);
			viewHolder.tv_size_success = (TextView) convertView
					.findViewById(R.id.tv_adapter_downloadfragment_size_success);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		final DownloadItem item = videoList.get(pos);
		viewHolder.ll_success.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (type == 1) {
					if (delete != 1) {
						Intent intent = new Intent(context,
								VideoViewPlayingActivity.class);
						intent.putExtra(VideoViewPlayingActivity.SID,
								item.section_id);
						intent.putExtra(VideoViewPlayingActivity.VID,
								item.video_id);
						context.startActivity(intent);
					}
				}
			}
		});

		switch (type) {
		case TYPE_FINISHED:// 已缓冲
			viewHolder.ll_not_success.setVisibility(View.GONE);
			viewHolder.ll_success.setVisibility(View.VISIBLE);
			switch (delete) {
			case Section.S_DELETE:
				viewHolder.rl_checkbox.setVisibility(View.VISIBLE);
				break;
			case Section.S_NOTEDALETE:
				viewHolder.rl_checkbox.setVisibility(View.INVISIBLE);
				break;

			default:
				break;
			}
			break;
		case TYPE_DOWNLOADING:// 缓存中
			viewHolder.ll_success.setVisibility(View.GONE);
			viewHolder.ll_not_success.setVisibility(View.VISIBLE);

			switch (delete) {
			case Section.S_DELETE:
				viewHolder.rl_checkbox1.setVisibility(View.VISIBLE);
				viewHolder.rl_down.setVisibility(View.GONE);
				break;
			case Section.S_NOTEDALETE:
				viewHolder.rl_checkbox1.setVisibility(View.GONE);
				viewHolder.rl_down.setVisibility(View.VISIBLE);
				switch (item.status) {
				case DownloadItem.STATUS_DOWNLOADING:
					viewHolder.btn_continue.setVisibility(View.VISIBLE);
					viewHolder.btn_stop.setVisibility(View.GONE);
					break;
				case DownloadItem.STATUS_PAUSED:
				case DownloadItem.STATUS_FAILED:
					viewHolder.btn_continue.setVisibility(View.GONE);
					viewHolder.btn_stop.setVisibility(View.VISIBLE);
				default:
					break;
				}
				break;
			default:
				break;
			}
			break;

		default:
			break;
		}

		viewHolder.tv_lesson.setText(item.title);
		viewHolder.tv_lesson_success.setText(item.title);
		if (item.status == DownloadItem.STATUS_FINISHED) {
			if (item.size > 0) {
				float size = item.size * 1.0f / 1024 / 1024;
				viewHolder.tv_size_success.setText(df.format(size) + "M");
			} else {
				viewHolder.tv_size_success.setText("");
			}
		} else {
			float percent = item.downloaded * 1.0f / item.size;
			RelativeLayout.LayoutParams params1 = (LayoutParams) viewHolder.iv_progress_point
					.getLayoutParams();
			params1.width = (int) (((width / 5) * 2.5) * percent);
			viewHolder.iv_progress_point.setLayoutParams(params1);
			if (item.size > 0) {
				float downloaded = item.downloaded * 1.0f / 1024 / 1024;
				float size = item.size * 1.0f / 1024 / 1024;
				viewHolder.tv_size.setText(df.format(downloaded) + "M/"
						+ df.format(size) + "M");
			} else {
				viewHolder.tv_size.setText("");
			}

		}
		try {
			FileInputStream in = new FileInputStream(item.getVideoCover());
			Bitmap bitmap = BitmapFactory.decodeStream(in);

			if (bitmap == null) {
				throw new Exception("File empty");
			}
			bitmap.setDensity(NCE2.getDensity());
			viewHolder.iv_cover.setImageBitmap(bitmap);
			viewHolder.iv_cover_success.setImageBitmap(bitmap);
		} catch (Exception e) {
			// TODO: handle exception
		}

		viewHolder.btn_continue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				viewHolder.btn_continue.setVisibility(View.GONE);
				viewHolder.btn_stop.setVisibility(View.VISIBLE);

				Clickdowning(item);
			}
		});
		viewHolder.btn_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				viewHolder.btn_continue.setVisibility(View.VISIBLE);
				viewHolder.btn_stop.setVisibility(View.GONE);
				ClickcontinueBuffer(item);

			}
		});
		viewHolder.rl_checkbox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (viewHolder.cbx_choose_after.getVisibility() == View.GONE) {
					viewHolder.cbx_choose_before.setVisibility(View.GONE);
					viewHolder.cbx_choose_after.setVisibility(View.VISIBLE);

					String id = item.video_id;
					DownloadFragment.successdeletelist.add(id);
				} else if (viewHolder.cbx_choose_after.getVisibility() == View.VISIBLE) {

					viewHolder.cbx_choose_before.setVisibility(View.VISIBLE);
					viewHolder.cbx_choose_after.setVisibility(View.GONE);
					for (int i = 0; i < DownloadFragment.successdeletelist
							.size(); i++) {
						if (item.video_id
								.equals(DownloadFragment.successdeletelist
										.get(i))) {
							DownloadFragment.successdeletelist
									.remove(item.video_id);
						}
					}
				}

			}
		});
		viewHolder.cbx_choose_before.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewHolder.cbx_choose_before.setVisibility(View.GONE);
				viewHolder.cbx_choose_after.setVisibility(View.VISIBLE);

				String id = item.video_id;
				DownloadFragment.successdeletelist.add(id);
			}
		});

		viewHolder.cbx_choose_after.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				viewHolder.cbx_choose_before.setVisibility(View.VISIBLE);
				viewHolder.cbx_choose_after.setVisibility(View.GONE);
				for (int i = 0; i < DownloadFragment.successdeletelist.size(); i++) {
					if (item.video_id.equals(DownloadFragment.successdeletelist
							.get(i))) {
						DownloadFragment.successdeletelist
								.remove(item.video_id);
					}
				}
			}
		});
		viewHolder.rl_checkbox1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (viewHolder.cbx_choose_after1.getVisibility() == View.GONE) {
					viewHolder.cbx_choose_before1.setVisibility(View.GONE);
					viewHolder.cbx_choose_after1.setVisibility(View.VISIBLE);

					String id = item.video_id;
					DownloadFragment.deletelist.add(id);
				} else if (viewHolder.cbx_choose_after1.getVisibility() == View.VISIBLE) {

					viewHolder.cbx_choose_before1.setVisibility(View.VISIBLE);
					viewHolder.cbx_choose_after1.setVisibility(View.GONE);
					for (int i = 0; i < DownloadFragment.deletelist.size(); i++) {
						if (item.video_id.equals(DownloadFragment.deletelist
								.get(i))) {
							DownloadFragment.deletelist.remove(item.video_id);
						}
					}
				}

			}
		});
		viewHolder.cbx_choose_before1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				viewHolder.cbx_choose_before1.setVisibility(View.GONE);
				viewHolder.cbx_choose_after1.setVisibility(View.VISIBLE);

				String id = item.video_id;
				DownloadFragment.deletelist.add(id);
			}
		});
		viewHolder.cbx_choose_after1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				viewHolder.cbx_choose_before1.setVisibility(View.VISIBLE);
				viewHolder.cbx_choose_after1.setVisibility(View.GONE);
				for (int i = 0; i < DownloadFragment.deletelist.size(); i++) {
					if (item.video_id.equals(DownloadFragment.deletelist.get(i))) {
						DownloadFragment.deletelist.remove(item.video_id);
					}
				}

			}
		});

		return convertView;
	}

	private class ViewHolder {

		TextView tv_lesson;
		RelativeLayout rl_down;
		RelativeLayout rl_checkbox;
		RelativeLayout rl_checkbox1;
		Button btn_continue;
		Button btn_stop;
		ImageView iv_progress_point;
		TextView tv_size;
		CheckBox cbx_choose_before;
		CheckBox cbx_choose_after;
		CheckBox cbx_choose_before1;
		CheckBox cbx_choose_after1;
		LinearLayout ll_not_success;
		LinearLayout ll_success;
		TextView tv_main_title_TitleBtnRight;
		ImageView iv_cover;
		ImageView iv_cover_success;
		TextView tv_lesson_success;
		TextView tv_size_success;
	}

	// 点击继续缓冲的时候执行的方法
	public void ClickcontinueBuffer(DownloadItem item) {
		Intent intent = new Intent(context, BufferVideoService.class);
		intent.putExtra(BufferVideoService.SID, item.section_id);
		intent.putExtra(BufferVideoService.VID, item.video_id);
		intent.putExtra(BufferVideoService.ACTION,
				BufferVideoService.ACTION_CONTINUE);
		context.startService(intent);
	}

	// 点击下载中的时候执行的方法
	public void Clickdowning(DownloadItem item) {

		Intent intent = new Intent(context, BufferVideoService.class);
		intent.putExtra(BufferVideoService.SID, item.section_id);
		intent.putExtra(BufferVideoService.VID, item.video_id);
		intent.putExtra(BufferVideoService.ACTION,
				BufferVideoService.ACTION_STOP);
		context.startService(intent);
	}
}
