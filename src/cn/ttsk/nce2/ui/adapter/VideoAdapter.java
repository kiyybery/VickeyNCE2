package cn.ttsk.nce2.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.SectionItem;
import cn.ttsk.nce2.entity.Video;

public class VideoAdapter extends BaseAdapter {
	private List<SectionItem> dataList = new ArrayList<SectionItem>();
	private Context context;
	private int selectedPosition = -1;
	SectionItem sItem;
	public void setSelectedPosition(int position) {
		selectedPosition = position;
	}

	public VideoAdapter(Context context, List<SectionItem> dataList) {
		this.context = context;
		this.dataList = dataList;
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.adapter_video_list,
					null);
			RelayoutViewTool.relayoutViewWithScale(convertView,
					NCE2.screenWidthScale);
			viewHolder = new ViewHolder();
			viewHolder.videoTv = (TextView) convertView
					.findViewById(R.id.videoTv);
			viewHolder.iv_video_list_icon = (ImageView) convertView
					.findViewById(R.id.iv_video_list_icon);
			viewHolder.layout = (LinearLayout) convertView
					.findViewById(R.id.layout);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		sItem = dataList.get(position);
		switch (sItem.t) {
		case 1:
			viewHolder.iv_video_list_icon.setImageResource(R.drawable.video_list_icon1_noselect);
			break;
		case 3:
			viewHolder.iv_video_list_icon.setImageResource(R.drawable.video_list_icon2_noselect);
			break;
		case 4:
			viewHolder.iv_video_list_icon.setImageResource(R.drawable.video_list_icon3_noselect);
			break;
		default:
			break;
		}

		
		viewHolder.videoTv.setText(sItem.title);
		// 设置选中效果
		if (selectedPosition == position) {
			viewHolder.videoTv.setTextColor(context.getResources()
					.getColor(R.color.video_list_color));
			switch (sItem.t) {
			case 1:
				viewHolder.iv_video_list_icon.setImageResource(R.drawable.video_list_icon1_select);
				break;
			case 3:
				viewHolder.iv_video_list_icon.setImageResource(R.drawable.video_list_icon2_select);
				break;
			case 4:
				viewHolder.iv_video_list_icon.setImageResource(R.drawable.video_list_icon3_select);
				break;
			default:
				break;
			}
			
		} else {
			viewHolder.videoTv.setTextColor(Color.BLACK);
			viewHolder.layout.setBackgroundColor(Color.WHITE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView videoTv;
		ImageView iv_video_list_icon;
		LinearLayout layout;
	}

}
