package cn.ttsk.nce2.ui.activity;


import cn.ttsk.nce2.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;



public class FirstInstall extends BaseActivity{

private ViewPager viewpager;
	
	private ImageView[] tips = new ImageView[3]; 
	LinearLayout lv;
	
	private ImageView[] mimageviews = new ImageView[3];
	
	private int imageid[];

	private ImageView iv_button;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		View  view = LayoutInflater.from(this).inflate(R.layout.firstinstall,null);
//		RelayoutViewTool.relayoutViewWithScale(view,
//				VickeyNCE.screenWidthScale);
		setContentView(view);
		imageid = new int[]{R.drawable.welcome_1,R.drawable.welcome_2,R.drawable.welcome_3};
		lv = (LinearLayout)findViewById(R.id.ll_firstinstall_img);
		viewpager=(ViewPager)findViewById(R.id.vp_fisrtinstall_install_id);
		iv_button=(ImageView)findViewById(R.id.btn_firstinstall_intent);
		
		for(int i=0;i<3;i++){
			ImageView imageView = new ImageView(this);  
			mimageviews[i] = imageView;  
            imageView.setBackgroundResource(imageid[i]); 
			
		}
		
		//将点点加载到数组中
				for(int j =0;j<3;j++){
					ImageView imageview = new ImageView(this);
					imageview.setLayoutParams(new LayoutParams(10, 10));
					tips[j]=imageview;
					
					if(j == 0){  
//		                tips[j].setBackgroundResource(R.drawable.activity_item_select);  
		            }else{  
//		                tips[j].setBackgroundResource(R.drawable.activity_item_no_select);  
		            } 
					
					 LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,    
			                    LayoutParams.WRAP_CONTENT));  
			            layoutParams.leftMargin = 5;  
			            layoutParams.rightMargin = 5;  
			            lv.addView(imageview, layoutParams);  
					
				}
				
				ViewPagetAdapter vpadapter = new ViewPagetAdapter();
				viewpager.setAdapter(vpadapter);
				
				viewpager.setOnPageChangeListener(new OnPageChangeListener() {
					
					
					
					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onPageSelected(int arg0) {
						// TODO Auto-generated method stub
					
					
						 if(arg0==2){
							
							 iv_button.setVisibility(View.VISIBLE);
						 }else{
							 iv_button.setVisibility(View.GONE);
						 }
						//tips[arg0].setBackgroundResource(R.drawable.psu); 
//						setImageBackground(arg0%mimageviews.length);
						
				
					}
					
				
				});
				
				 DisplayMetrics metric = new DisplayMetrics();
			        getWindowManager().getDefaultDisplay().getMetrics(metric);
			        int width = metric.widthPixels;     // 屏幕宽度（像素）
			        int height = metric.heightPixels;   // 屏幕高度（像素）
//			        RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) iv_button.getLayoutParams();
//			        params.setMarginStart((height/5)*4);
//			        iv_button.setLayoutParams(params);
			        iv_button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {

						Intent mainIntent = new Intent(FirstInstall.this, CourseActivity.class);  
		                FirstInstall.this.startActivity(mainIntent);  
		                FirstInstall.this.finish();
					}
				});
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
	
	class ViewPagetAdapter extends PagerAdapter{

		// 获取当前窗口界面数
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		// 初始化position的位置
		@Override
		public Object instantiateItem(View container, int position) {
				 ((ViewPager)container).addView(mimageviews[position % mimageviews.length], 0);
				
				
			return mimageviews[position % mimageviews.length];
		}

		// 销毁position的界面
		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			//super.destroyItem(container, position, object);
			((ViewPager)container).removeView(mimageviews[position % mimageviews.length]);
		}

		// 判断是否由对象生成界面
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}
		/** 
	     * 设置选中的tip的背景 
	     * @param selectItems 
	     */  
//	    private void setImageBackground(int selectItems){  
//	        for(int i=0; i<tips.length; i++){  
//	            if(i == selectItems){  
//	                tips[i].setBackgroundResource(R.drawable.activity_item_select); 
//	            }else{  
//	                tips[i].setBackgroundResource(R.drawable.activity_item_no_select);  
//	            }  
//	        }  
//	    } 

}
