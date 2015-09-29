package cn.ttsk.nce2.ui.Fragment;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ttsk.library.RoundedImageView;
import cn.ttsk.library.StringUtil;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.UserInfo;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.DevReply;
import com.umeng.fb.model.Reply;

public class ConversationFrament extends BaseFragment implements
		OnClickListener {
	private static final String TAG = ConversationFrament.class.getName();
	private FeedbackAgent agent;
	private Conversation defaultConversation;
	private ReplyListAdapter adapter;
	private ListView replyListView;
	RelativeLayout header;
	int headerHeight;
	int headerPaddingOriginal;
	EditText userReplyContentEdit;
	// private ImageView iv_section_title_back;
	// private TextView tv_section_title_title;
	private ImageView umeng_fb_send;
	private UserInfo userInfo;
	RoundedImageView headImageView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.umeng_fb_activity_conversation,
				container, false);

		// iv_section_title_back = (ImageView) view
		// .findViewById(R.id.iv_section_title_back);
		// iv_section_title_back.setOnClickListener(this);
		// tv_section_title_title = (TextView) view
		// .findViewById(R.id.tv_section_title_title);
		// tv_section_title_title.setText("意见反馈");
		userInfo = getUserInfo();
		umeng_fb_send = (ImageView) view.findViewById(R.id.umeng_fb_send);
		umeng_fb_send.setOnClickListener(this);
		replyListView = (ListView) view.findViewById(R.id.umeng_fb_reply_list);

		userReplyContentEdit = (EditText) view
				.findViewById(R.id.umeng_fb_reply_content);
		init();
		return view;
	}

	void sync() {
		Conversation.SyncListener listener = new Conversation.SyncListener() {

			@Override
			public void onSendUserReply(List<Reply> replyList) {
				adapter.notifyDataSetChanged();
			}

			@Override
			public void onReceiveDevReply(List<DevReply> replyList) {
			}
		};
		defaultConversation.sync(listener);
	}

	class ReplyListAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater mInflater;

		public ReplyListAdapter(Context context) {
			this.mContext = context;
			mInflater = LayoutInflater.from(mContext);
		}

		/**
		 * 
		 * @param content
		 *            提示用户的内容
		 * @return
		 */
		private DevReply createInitDevReply(String content) {

			try {
				Class c = com.umeng.fb.model.DevReply.class;
				Constructor constructor = c.getDeclaredConstructor(new Class[] {
						String.class, String.class, String.class, String.class,
						String.class });
				constructor.setAccessible(true);
				DevReply devReply = (DevReply) constructor
						.newInstance(new Object[] { content, "appkey",
								"userid", "feedback_id", "user_name" });
				return devReply;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public int getCount() {
			List<Reply> replyList = defaultConversation.getReplyList();
			return (replyList == null) ? 1 : replyList.size() + 1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			this.mInflater = (LayoutInflater) this.mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final Reply reply;
			if (position == 0) {
				// 自定义提示内容
				reply = createInitDevReply("欢迎您对产品的使用提出意见和建议，由于反馈过多，可能会不能及时回复，请谅解。");
			} else {
				reply = defaultConversation.getReplyList().get(position - 1);

			}

			if (reply instanceof DevReply) {
				convertView = this.mInflater.inflate(
						R.layout.umeng_fb_list_left, null);

			} else {
				convertView = this.mInflater.inflate(
						R.layout.umeng_fb_list_right, null);
				headImageView =(RoundedImageView) convertView.findViewById(R.id.headImageView);
				
				SettingTitle(headImageView);

			}

			TextView replyDate = (TextView) convertView
					.findViewById(R.id.umeng_fb_reply_date);

			TextView replyContent = (TextView) convertView
					.findViewById(R.id.umeng_fb_reply_content);
			replyDate.setText(SimpleDateFormat.getDateTimeInstance().format(
					reply.getDatetime()));
			replyContent.setText(reply.getContent());

			return convertView;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return defaultConversation.getReplyList().get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return position;
		}

	}
	public void SettingTitle(final RoundedImageView v) {
		if (userInfo != null) {
			if (!StringUtil.isEmpty(userInfo.avatar)) {
				Ion.with(getActivity()).load(userInfo.avatar).asBitmap()
						.setCallback(new FutureCallback<Bitmap>() {

							@Override
							public void onCompleted(Exception e, Bitmap bitmap) {
								v.setImageBitmap(bitmap);
							}
						});
			}
		} else {
			
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.iv_section_title_back:
			getActivity().finish();
			break;
		case R.id.umeng_fb_send:

			String content = userReplyContentEdit.getEditableText().toString()
					.trim();
			if (TextUtils.isEmpty(content))
				return;

			userReplyContentEdit.getEditableText().clear();

			defaultConversation.addUserReply(content);
			// adapter.notifyDataSetChanged();

			// scoll to the end of listview after updating the
			// conversation.
			// replyList.setSelection(adapter.getCount()-1);

			sync();

			// hide soft input window after sending.
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null)
				imm.hideSoftInputFromWindow(
						userReplyContentEdit.getWindowToken(), 0);

			break;
		default:
			break;
		}

	}

	private void init() {
		try {
			agent = new FeedbackAgent(getActivity());
			defaultConversation = agent.getDefaultConversation();
			adapter = new ReplyListAdapter(getActivity());
			replyListView.setAdapter(adapter);
			sync();
			userReplyContentEdit.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					String sentStr = userReplyContentEdit.getText().toString()
							.trim();

					if (TextUtils.isEmpty(sentStr))
						umeng_fb_send
								.setImageResource(R.drawable.feedback_send_btn_inactive);
					else
						umeng_fb_send
								.setImageResource(R.drawable.feedback_send_btn_active);

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			getActivity().finish();
		}
	}

}
