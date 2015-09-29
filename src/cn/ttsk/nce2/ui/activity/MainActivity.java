package cn.ttsk.nce2.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import cn.ttsk.library.PreferencesUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;


public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		handler.sendEmptyMessageDelayed(0, 3000);
	}

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if (msg.what == 0) {
				into();
			}

		}

	};

	private void into() {
		if (PreferencesUtil.get(NCE2.USERFIRST, true)) {
			Intent mainIntent = new Intent(MainActivity.this,
					FirstInstall.class);
			MainActivity.this.startActivity(mainIntent);
			MainActivity.this.finish();
			PreferencesUtil.put(NCE2.USERFIRST, false);
		} else {

			Intent mainIntent = new Intent(MainActivity.this,
					CourseActivity.class);
			startActivity(mainIntent);
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initHeader() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initWidget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWidgetState() {
		// TODO Auto-generated method stub
		
	}
}
