package cn.ttsk.nce2.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ttsk.library.AnimatedExpandableListView;
import cn.ttsk.library.AnimatedExpandableListView.AnimatedExpandableListAdapter;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.Course;
import cn.ttsk.nce2.entity.Section;

public class CourseExpandableListAdapter extends AnimatedExpandableListAdapter {
	private LayoutInflater inflater;
	private List<Section> sections = new ArrayList<Section>();
	private List<Course> courses = new ArrayList<Course>();
	private Context context;

	public CourseExpandableListAdapter(Context context, List<Course> courses) {
		inflater = LayoutInflater.from(context);
		this.courses = courses;
		this.context = context;
	}

	public void restsection(List<Section> section) {
		this.sections.clear();
		this.sections.addAll(section);
		this.notifyDataSetChanged();

	}

	// 返回父列表个数
	@Override
	public int getGroupCount() {
		return courses.size();
	}

	// 返回子列表个数
	@Override
	public int getRealChildrenCount(int groupPosition) {
		return sections.size() == 0 ? 0 : sections.size() + 2;
	}

	@Override
	public Course getGroup(int groupPosition) {
		if (courses != null && courses.size() > groupPosition) {
			return courses.get(groupPosition);
		} else {
			return null;
		}

	}

	@Override
	public Section getChild(int groupPosition, int childPosition) {
		if (sections != null && sections.size() > (childPosition - 1)) {
			return sections.get(childPosition - 1);
		} else {
			return null;
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {

		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		GroupHolder groupHolder = null;
		if (convertView == null) {
			groupHolder = new GroupHolder();
			convertView = inflater.inflate(R.layout.item_course_group, null);
			RelayoutViewTool.relayoutViewWithScale(convertView,
					NCE2.screenWidthScale);
			groupHolder.iv_icon = (ImageView) convertView
					.findViewById(R.id.iv_course_group_icon);
			groupHolder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_course_group_title);
			groupHolder.iv_arrow = (ImageView) convertView
					.findViewById(R.id.iv_course_group_arrow);
			groupHolder.rl_group = (RelativeLayout) convertView
					.findViewById(R.id.rl_course_group);
			convertView.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) convertView.getTag();
		}

		switch (groupPosition) {
		case 0:
			groupHolder.iv_icon.setImageResource(R.drawable.main_menu_1);
			groupHolder.rl_group.setBackgroundColor(context.getResources()
					.getColor(R.color.course_list_0));
			break;
		case 1:
			groupHolder.iv_icon.setImageResource(R.drawable.main_menu_2);
			groupHolder.rl_group.setBackgroundColor(context.getResources()
					.getColor(R.color.course_list_1));
			break;
		case 2:
			groupHolder.iv_icon.setImageResource(R.drawable.main_menu_3);
			groupHolder.rl_group.setBackgroundColor(context.getResources()
					.getColor(R.color.course_list_2));
			break;
		default:
			groupHolder.iv_icon.setImageResource(R.drawable.main_menu_4);
			groupHolder.rl_group.setBackgroundColor(context.getResources()
					.getColor(R.color.course_list_3));
			break;
		}

		groupHolder.tv_title.setText(getGroup(groupPosition).title);
		if (isExpanded)// ture is Expanded or false is not isExpanded
			groupHolder.iv_arrow.setImageResource(R.drawable.main_item_open);
		else
			groupHolder.iv_arrow.setImageResource(R.drawable.main_item_next);
		return convertView;
	}

	@Override
	public View getRealChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if (childPosition == 0) {
			convertView = inflater.inflate(R.layout.item_course_child_header,
					null);
			RelayoutViewTool.relayoutViewWithScale(convertView,
					NCE2.screenWidthScale);
		} else if (isLastChild) {
			convertView = inflater.inflate(R.layout.item_course_child_footer,
					null);
			RelayoutViewTool.relayoutViewWithScale(convertView,
					NCE2.screenWidthScale);
			View footer = convertView
					.findViewById(R.id.v_item_course_child_footer);
			switch (groupPosition) {
			case 0:
				footer.setBackgroundResource(R.drawable.item_course_bottom_1);
				break;
			case 1:
				footer.setBackgroundResource(R.drawable.item_course_bottom_2);
				break;
			case 2:
				footer.setBackgroundResource(R.drawable.item_course_bottom_3);
				break;
			case 3:
				footer.setBackgroundResource(R.drawable.item_course_bottom_4);
				break;
			default:
				break;
			}
		} else {
			ChildHolder childHolder;
			convertView = inflater.inflate(R.layout.item_course_child, null);
			RelayoutViewTool.relayoutViewWithScale(convertView,
					NCE2.screenWidthScale);
			childHolder = new ChildHolder();
			childHolder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_item_course_child_title);
			childHolder.iv_finish = (ImageView) convertView
					.findViewById(R.id.iv_item_course_child_finish);
			Section section = sections.get(childPosition - 1);
			if (section.status == Section.S_LOCKED) {
				childHolder.iv_finish.setVisibility(View.VISIBLE);
				childHolder.iv_finish
						.setImageResource(R.drawable.item_course_lock);
			} else if (section.status == Section.S_FINISHED) {
				childHolder.iv_finish.setVisibility(View.VISIBLE);
				childHolder.iv_finish
						.setImageResource(R.drawable.item_course_complete);
			} else {
				childHolder.iv_finish.setVisibility(View.GONE);
			}
			childHolder.tv_title.setText(section.title);
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return childPosition != 0;
	}

	class GroupHolder {
		TextView tv_title;
		ImageView iv_icon;
		ImageView iv_arrow;
		RelativeLayout rl_group;
	}

	class ChildHolder {
		TextView tv_title;
		ImageView iv_finish;
	}
}
