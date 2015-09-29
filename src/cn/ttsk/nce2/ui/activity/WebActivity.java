package cn.ttsk.nce2.ui.activity;



import cn.ttsk.nce2.R;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends BaseActivity {
	private WebView wv_tribeweb;
	private String url;
	private String title;
	public static String URL = "url";
	public static String TITLE = "title";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);
		initHeader();
		initWidget();
		setWidgetState();
	}

	public void onClick(View v) {

	}

	public void initHeader() {
		initHeaderWidget();
		setTitleVisibleState(true);
		setTitle("网页");
		setBtnLeftVisibleState(true);
		addBtnLeftListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void initWidget() {
		wv_tribeweb = (WebView) findViewById(R.id.wv_web);
	}

	public void setWidgetState() {
		wv_tribeweb.getSettings().setJavaScriptEnabled(true);
		wv_tribeweb.setWebViewClient(new MyWebViewClient());
	}

	@Override
	protected void onStart() {
		super.onStart();
		url = getIntent().getStringExtra(URL);
		title = getIntent().getStringExtra(TITLE);
//		if (title != null) {
//			setTitle(title);
//		}
		if(url != null){
			wv_tribeweb.loadUrl(url);
		}
	}
	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	         return false;
	    }
	}
}
