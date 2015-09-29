package cn.ttsk.nce2.ui.activity;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.ttsk.nce2.R;

public class AboutActivity extends BaseActivity {
private TextView tv_activity_about_version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_about);
		tv_activity_about_version = (TextView) findViewById(R.id.tv_activity_about_version);
		getVersion();
	}
	
	private void getVersion() {
		String verName = "";
		try {
			verName = getPackageManager().getPackageInfo(
					getApplicationContext().getPackageName(), 0).versionName;
			tv_activity_about_version.setText("版本号：" + verName);
		} catch (NameNotFoundException e) {
		}
	}
	@Override
	public void onClick(View arg0) {
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
