package cn.ttsk.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.entity.LyricContent;


public class LyricView extends TextView {

	private float high;

	private float width;

	private Paint CurrentPaint;

	private Paint NotCurrentPaint;

	private float TextHigh =  80*NCE2.screenHeightScale;

	private float TextSize;

	private int Index = 0;
	private int k;
	private Set<String> set = new HashSet<String>();
	private List<LyricContent> mSentenceEntities = new ArrayList<LyricContent>();
	private String Lyricstr1;
	private String Lyricstr2;
	private Timer timer;
	private int currenttiem = 10;
	private int dext;
	private boolean string = true;

	public void setSentenceEntities(List<LyricContent> mSentenceEntities) {
		this.mSentenceEntities = mSentenceEntities;
	}

	public LyricView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public LyricView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	public LyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
		// TODO Auto-generated constructor stub
	}

	private void init() {
		setFocusable(true);

		CurrentPaint = new Paint();
		CurrentPaint.setAntiAlias(true);
		CurrentPaint.setTextAlign(Paint.Align.CENTER);

		NotCurrentPaint = new Paint();
		NotCurrentPaint.setAntiAlias(true);
		NotCurrentPaint.setTextAlign(Paint.Align.CENTER);

	}

	@Override
	protected void onDraw(final Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (canvas == null) {
			return;
		}

		TextSize = width / 20;
		CurrentPaint.setColor(Color.rgb(51, 51, 51));
		NotCurrentPaint.setColor(Color.rgb(102, 102, 102));

		CurrentPaint.setTextSize(TextSize);
		CurrentPaint.setTypeface(Typeface.SERIF);

		NotCurrentPaint.setTextSize(TextSize);
		NotCurrentPaint.setTypeface(Typeface.SERIF);
		
		try {
			String str = mSentenceEntities.get(Index).getLyric();
			
			if (string) {
				string = false;
				if (str.length() > 10) {
					String[] Lyric = str.split("");
					for (int i = 0; i < Lyric.length; i++) {
						if (dext != Index) {
							i = 0;
						}
						if (i >= 35) {
							Lyricstr2 += Lyric[i];
						} else if (i > 0 && i < 35) {
							Lyricstr1 += Lyric[i];
						}
					}

				}
			}
		
			if (str.length() < 35) {
				canvas.drawText(mSentenceEntities.get(Index).getLyric(),
						width / 2, high / 2, CurrentPaint);
			} else if (str.length() >= 35) {

				if (dext != Index) {
					currenttiem = 10;
					string = true;
				}
				canvas.drawText(mSentenceEntities.get(Index).getLyric(), width
						+ 100 - currenttiem, high / 2, CurrentPaint);
				currenttiem += 8;
				dext = Index;
				if (currenttiem > width * 2) {
					currenttiem = 10;
				}
			}

			float tempY = high / 2;

			for (int i = Index - 1; i >= 0; i--) {

				tempY = tempY - TextHigh;

				canvas.drawText(mSentenceEntities.get(i).getLyric(), width / 2,
						tempY, NotCurrentPaint);

			}

			tempY = high / 2;

			for (int i = Index + 1; i < mSentenceEntities.size(); i++) {

				tempY = tempY + TextHigh;

				canvas.drawText(mSentenceEntities.get(i).getLyric(), width / 2,
						tempY, NotCurrentPaint);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.high = h;
		this.width = w;
	}

	public void SetIndex(int index) {
		this.Index = index;
	}

	public float GetIndex() {
		return high;

	}

}
