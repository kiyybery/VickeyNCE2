package com.mumian.view;

import java.util.ArrayList;
import java.util.List;

import cn.ttsk.nce2.entity.LyricContent;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class LyricViewOrial2 extends TextView {

	private float high;

	private float width;

	private Paint CurrentPaint;

	private Paint NotCurrentPaint;

	private float TextHigh = 50;

	private float TextSize;

	private int Index = 0;
	private int k;
	private int currenttiem = 10;
	private int dext;
	private List<LyricContent> mSentenceEntities = new ArrayList<LyricContent>();

	public void setSentenceEntities(List<LyricContent> mSentenceEntities) {
		this.mSentenceEntities = mSentenceEntities;
	}

	public LyricViewOrial2(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public LyricViewOrial2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	public LyricViewOrial2(Context context, AttributeSet attrs) {
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
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		if (canvas == null) {
			return;
		}

		TextSize = width / 25;
		CurrentPaint.setColor(Color.BLACK);
		NotCurrentPaint.setColor(Color.BLACK);

		CurrentPaint.setTextSize(TextSize);
		CurrentPaint.setTypeface(Typeface.SERIF);

		NotCurrentPaint.setTextSize(TextSize);
		NotCurrentPaint.setTypeface(Typeface.SERIF);

		try {
			String str = mSentenceEntities.get(Index).getLyric();
			

			if (str.length() < 50) {
				canvas.drawText(mSentenceEntities.get(Index).getLyric(),
						width / 2, high / 2, CurrentPaint);
			} else if (str.length() >= 50) {

				if (dext != Index) {
					currenttiem = 10;
					
				}
				canvas.drawText(mSentenceEntities.get(Index).getLyric(), width
						+ 100 - currenttiem, high / 2, CurrentPaint);
				currenttiem +=2;
				dext = Index;
				if (currenttiem > width * 2) {
					currenttiem = 10;
				}
			}
			// float tempY = high / 2;
			//
			// for (int i = Index - 1; i >= 0; i--) {
			//
			//
			// tempY = tempY - TextHigh;
			//
			// canvas.drawText(mSentenceEntities.get(i).getLyric(), width / 2,
			// tempY, NotCurrentPaint);
			//
			// }
			//
			// tempY = high / 2;
			//
			// for (int i = Index + 1; i < mSentenceEntities.size(); i++) {
			//
			// tempY = tempY + TextHigh;
			//
			// canvas.drawText(mSentenceEntities.get(i).getLyric(), width / 2,
			// tempY, NotCurrentPaint);
			//
			// }
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
		// System.out.println(index);
	}

	public float GetIndex() {
		return high;

	}

}
