package cn.ttsk.nce2.ui.adapter;

import java.util.ArrayList;
import java.util.List;


import cn.ttsk.nce2.R;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.entity.Lrc;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TextReciteAdapter extends BaseAdapter {

	private List<Lrc> dataList = new ArrayList<Lrc>();
	private Context context;
	
	public TextReciteAdapter(Context context) {
		this.context = context;
	}
	
	public void resetData(List<Lrc> dataList) {
		this.dataList = dataList;
		notifyDataSetChanged();
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
			convertView = View.inflate(context, R.layout.adapter_text_recite,
					null);
			RelayoutViewTool.relayoutViewWithScale(convertView,
					NCE2.screenWidthScale);
			viewHolder = new ViewHolder();
			viewHolder.tv_lrc = (TextView) convertView.findViewById(R.id.tv_adapter_text_recite_lrc);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Lrc lrc = dataList.get(position);
		viewHolder.tv_lrc.setText(lrc.content);
		return convertView;
	}

	private class ViewHolder {
		TextView tv_lrc;
	}
}
