package cn.ttsk.nce2.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.ttsk.nce2.R;
import cn.ttsk.library.DateUtil;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.entity.WordsChartItem;

import com.koushikdutta.ion.Ion;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WordsChartsAdapter extends BaseAdapter {

	private Context context;
	private List<WordsChartItem> dataList = new ArrayList<WordsChartItem>();
	private int wc_right;
	private int wc_time;

	public WordsChartsAdapter(Context context) {
		this.context = context;
	}

	public WordsChartsAdapter(Context context, int wc_right, int wc_time) {
		super();
		this.context = context;
		this.wc_right = wc_right;
		this.wc_time = wc_time;
	}

	public void resetData(List<WordsChartItem> dataList) {
		this.dataList = dataList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public WordsChartItem getItem(int position) {
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
			convertView = View.inflate(context, R.layout.adapter_words_charts,
					null);
			RelayoutViewTool.relayoutViewWithScale(convertView,
					NCE2.screenWidthScale);
			viewHolder = new ViewHolder();
			viewHolder.ll_main = (LinearLayout) convertView
					.findViewById(R.id.ll_adapter_words_charts_main);
			viewHolder.tv_pos = (TextView) convertView
					.findViewById(R.id.tv_adapter_words_charts_pos);
			viewHolder.iv_avatar = (ImageView) convertView
					.findViewById(R.id.iv_adapter_words_charts_avatar);
			viewHolder.tv_name = (TextView) convertView
					.findViewById(R.id.tv_adapter_words_charts_name);
			viewHolder.tv_accuracy = (TextView) convertView
					.findViewById(R.id.tv_adapter_words_charts_accuracy);
			viewHolder.tv_duration = (TextView) convertView
					.findViewById(R.id.tv_adapter_words_charts_duration);
			viewHolder.ll_blank = (LinearLayout) convertView
					.findViewById(R.id.ll_adapter_words_charts_blank);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		WordsChartItem item = dataList.get(position);
		if (position % 2 == 0) {
			viewHolder.ll_main.setBackgroundColor(context.getResources()
					.getColor(R.color.words_charts_even));
		} else {
			viewHolder.ll_main.setBackgroundColor(context.getResources()
					.getColor(R.color.words_charts_odd));
		}
		if (item.is_blank) {
			viewHolder.ll_main.setVisibility(View.GONE);
			viewHolder.ll_blank.setVisibility(View.VISIBLE);
		} else if (item.is_self) {
			viewHolder.ll_main.setVisibility(View.VISIBLE);
			viewHolder.ll_blank.setVisibility(View.GONE);
			viewHolder.tv_pos.setText(item.rank + "");
			viewHolder.tv_name.setText(item.username);
			viewHolder.tv_accuracy.setText((wc_right * 100 / NCE2.CHALLENGE_COUNT) + "%");
			viewHolder.tv_duration.setText(DateUtil.getMsStr(wc_time * 1000));

			Ion.with(viewHolder.iv_avatar)
					.placeholder(
							context.getResources().getDrawable(
									R.drawable.adapter_words_charts_avatar))
					.load(item.avatar);
		} else {
			viewHolder.ll_main.setVisibility(View.VISIBLE);
			viewHolder.ll_blank.setVisibility(View.GONE);
			viewHolder.tv_pos.setText(position + 1 + "");
			viewHolder.tv_name.setText(item.username);
			viewHolder.tv_accuracy.setText((Integer.parseInt(item.right) * 100 / NCE2.CHALLENGE_COUNT) + "%");
			viewHolder.tv_duration.setText(DateUtil.getMsStr(Long.parseLong(item.duration) * 1000));
			Ion.with(viewHolder.iv_avatar)
					.placeholder(
							context.getResources().getDrawable(
									R.drawable.adapter_words_charts_avatar))
					.load(item.avatar);
		}
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll_main;
		TextView tv_pos;
		LinearLayout ll_blank;
		ImageView iv_avatar;
		TextView tv_name;
		TextView tv_accuracy;
		TextView tv_duration;
	}

}
