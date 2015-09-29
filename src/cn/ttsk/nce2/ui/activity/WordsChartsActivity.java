package cn.ttsk.nce2.ui.activity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ttsk.nce2.R;
import cn.ttsk.library.DateUtil;
import cn.ttsk.library.Db;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.library.RelayoutViewTool;
import cn.ttsk.library.StringUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.entity.UserInfo;
import cn.ttsk.nce2.entity.WordsChartItem;
import cn.ttsk.nce2.ui.adapter.WordsChartsAdapter;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
/**
 * 单词挑战排行榜
 * @author Administrator
 *
 */
public class WordsChartsActivity extends BaseActivity {

	public static final String SID = "sid";
	public static final String RIGHT = "wc_right";
	public static final String TIME = "wc_time";
	public static final String RANK = "wc_rank";
	private ListView lv_charts;
	private WordsChartsAdapter adapter;
	private List<WordsChartItem> personList = new ArrayList<WordsChartItem>();
	private String wt_id;
	private int wc_right;
	private int wc_time;
	private Boolean isLoading = false;
	private int my_rank;
	private View header;
	private TextView tv_words_chart_rank; // 在排行榜的位置。
	private TextView tv_words_charts_result_accuracy;// 成绩。
	private TextView tv_words_charts_result_duration;// 闯关的时间
	private TextView tv_words_charts_result_time; // 现在的系统时间。
	private Button btn_words_charts_again; // 再次挑战按钮。
	private TextView tv_words_chart;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_words_charts);
		wt_id = getIntent().getStringExtra(SID);
		wc_right = getIntent().getIntExtra(RIGHT, 0);
		wc_time = getIntent().getIntExtra(TIME, 0);
		my_rank = getIntent().getIntExtra(RANK, 99);

		if (wt_id != null) {
			PreferencesUtil.put(NCE2.COURSE_BUFFER_GRADE, wc_right);
			PreferencesUtil.put(NCE2.COURSE_BUFFER_TIME, wc_time);
		}
		initHeader();
		initWidget();
		setWidgetState();

		if (StringUtil.isEmpty(wt_id)) {
			wt_id = "1";
		}
		getData();
		draw();
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void initHeader() {
	}

	@Override
	public void initWidget() {
		header = View.inflate(WordsChartsActivity.this,
				R.layout.item_words_charts_header, null);
		RelayoutViewTool.relayoutViewWithScale(header, NCE2.screenWidthScale);
		lv_charts = (ListView) findViewById(R.id.lv_words_charts);
		lv_charts.addHeaderView(header);
		tv_words_chart_rank = (TextView) header
				.findViewById(R.id.tv_words_chart_rank);
		tv_words_charts_result_accuracy = (TextView) header
				.findViewById(R.id.tv_words_charts_result_accuracy);
		tv_words_charts_result_time = (TextView) header
				.findViewById(R.id.tv_words_charts_result_time);
		tv_words_charts_result_duration = (TextView) header
				.findViewById(R.id.tv_words_charts_result_duration);
		btn_words_charts_again = (Button) header
				.findViewById(R.id.btn_words_charts_again);
		tv_words_chart = (TextView) header
				.findViewById(R.id.tv_words_chart);
		if (wt_id == null) {
			wc_right = PreferencesUtil.get(NCE2.COURSE_BUFFER_GRADE,
					0);
			wc_time = PreferencesUtil
					.get(NCE2.COURSE_BUFFER_TIME, 0);

		}
		int grade = wc_right * 100 / NCE2.CHALLENGE_COUNT;
		if(grade>70)
			tv_words_chart.setText(getResources().getString(R.string.grade_title_a));
		if(grade<70)
			tv_words_chart.setText(getResources().getString(R.string.grade_title_b));
		
		tv_words_charts_result_accuracy.setText((wc_right * 100 / NCE2.CHALLENGE_COUNT) + "%");
		
		tv_words_charts_result_duration.setText(DateUtil.getMsStr(wc_time * 1000));
		String timedata = DateUtil.getDateStr(new Date());
		tv_words_charts_result_time.setText(timedata);
		adapter = new WordsChartsAdapter(getApplicationContext(), wc_right,
				wc_time);
		lv_charts.setAdapter(adapter);
		btn_words_charts_again.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(WordsChartsActivity.this,
						WordsTestActivity.class);
				intent.putExtra(WordsTestActivity.TYPE, WordsTestActivity.TYPE_CHALLENGE);
				intent.putExtra(WordsTestActivity.SID, wt_id);

				startActivity(intent);
				finish();
				return;

			}
		});
	}

	@Override
	public void setWidgetState() {
		// TODO Auto-generated method stub

	}

	private void getData() {
		if (isLoading) {
			return;
		}
		isLoading = true;
		startProgressBar(R.string.common_waiting_please, new Thread(), false);
		Ion.with(WordsChartsActivity.this, NCE2.SERVER_URL + "nce2word/rank")
				.setBodyParameter("device_id", getDeviceId())
				.setBodyParameter("auth", getAuth())
				.setBodyParameter("sid", wt_id)
				.asJsonObject().setCallback(new FutureCallback<JsonObject>() {

					@Override
					public void onCompleted(Exception e, JsonObject result) {
						isLoading = false;
						closeProgressBar();
						if (result == null) {
							showToast(R.string.tips_ion_exception,
									Toast.LENGTH_SHORT);
							return;
						}
						int code = result.get("code").getAsInt();
						if (code == 200) {
							JsonObject msg = result.get("msg")
									.getAsJsonObject();
							my_rank = msg.get("rank").getAsInt();
							Type type = new TypeToken<List<WordsChartItem>>() {
							}.getType();
							personList = gson.fromJson(msg.get("list"), type);
							if (personList != null && personList.size() > 0
									&& my_rank > 0) {
								if (my_rank < 11) {
									WordsChartItem item = personList
											.get(my_rank - 1);
									item.is_self = true;
									item.rank = my_rank;
								} else {
									if (my_rank != 11) {
										WordsChartItem blank = new WordsChartItem();
										blank.is_blank = true;
										personList.add(blank);
									}
									WordsChartItem myItem = new WordsChartItem();
									myItem.is_self = true;
									UserInfo userInfo = getUserInfo();
									myItem.avatar = userInfo.avatar;
									myItem.username = userInfo.uname;
									myItem.rank = my_rank;
									personList.add(myItem);
									WordsChartItem blank2 = new WordsChartItem();
									blank2.is_blank = true;
									personList.add(blank2);
								}
							}
							draw();
						} else if (code == 401) {
							forceLogin();
						} else {
							showToast(result.get("msg").getAsString(),
									Toast.LENGTH_SHORT);
						}
					}
				});
	}

	private void draw() {
		if (personList != null) {
			adapter.resetData(personList);
			if (wt_id == null) {
				wc_right = PreferencesUtil.get(
						NCE2.COURSE_BUFFER_BEIDANCI_RANKING, 0);
			}
			tv_words_chart_rank.setText(my_rank + "");
			if (wt_id != null) {
				PreferencesUtil.get(
						NCE2.COURSE_BUFFER_BEIDANCI_RANKING, my_rank);
			}
		}
	}

}
