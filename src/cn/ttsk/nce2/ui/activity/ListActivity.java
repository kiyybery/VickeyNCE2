package cn.ttsk.nce2.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import cn.ttsk.nce2.R;

public class ListActivity extends BaseActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_list);
		String a=getIntent().getStringExtra("TAG");
		TextView tv = (TextView) findViewById(R.id.tv);
		tv.setText(a);
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
}
