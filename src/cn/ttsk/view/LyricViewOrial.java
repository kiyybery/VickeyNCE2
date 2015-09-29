package cn.ttsk.view;

import java.util.ArrayList;
import java.util.List;

import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.LyricContent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class LyricViewOrial extends TextView {

	private float high;

	private float width;

	private Paint CurrentPaint;

	private Paint NotCurrentPaint;

	private float TextHigh =80*NCE2.screenHeightScale;

	private float TextSize;

	private int Index = 0;
	private int k;
	private int currenttiem = 10;
	private int dext;
	private List<LyricContent> mSentenceEntities = new ArrayList<LyricContent>();

	private Context context;

	public void setSentenceEntities(List<LyricContent> mSentenceEntities) {
		this.mSentenceEntities = mSentenceEntities;
	}

	public LyricViewOrial(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}

	public LyricViewOrial(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
		// TODO Auto-generated constructor stub
	}

	public LyricViewOrial(Context context, AttributeSet attrs) {
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
		CurrentPaint.setColor(Color.rgb(51, 51, 51));
		NotCurrentPaint.setColor(Color.rgb(102, 102, 102));

		CurrentPaint.setTextSize(TextSize);
		CurrentPaint.setTypeface(Typeface.SERIF);

		NotCurrentPaint.setTextSize(TextSize);
		NotCurrentPaint.setTypeface(Typeface.SERIF);

		try {
			String str = mSentenceEntities.get(Index).getLyric();

			Drawable drawable = getResources().getDrawable(
					R.drawable.oral_item_bg);
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					R.drawable.oral_item_bg);
			if (str.length() < 50) {

//				NinePatchDrawable drawable1 = getBitmap(bmp);
//				drawable1.setBounds(10, (int) ((high / 2) - TextHigh),
//						(int) (width), (int) (high / 2));
//				drawable1.draw(canvas);
				// canvas.drawBitmap(
				// getBitmap(resizeBitmap(drawableToBitmap(drawable),
				// (int) (width), (int) ((high / 2) - TextHigh))),
				// 10, (high / 2) - TextHigh, CurrentPaint);

				canvas.drawText(mSentenceEntities.get(Index).getLyric(),
						width / 2, high / 2, CurrentPaint);
			} else if (str.length() >= 50) {

				if (dext != Index) {
					currenttiem = 10;

				}
//				NinePatchDrawable drawable1 = getBitmap(bmp);
//				drawable1.setBounds(10, (int) ((high / 2) - TextHigh),
//						(int) (width), (int) (high / 2));
//				drawable1.draw(canvas);
				canvas.drawText(mSentenceEntities.get(Index).getLyric(), width
						+ 100 - currenttiem, high / 2, CurrentPaint);
				currenttiem += 2;
				dext = Index;
				if (currenttiem > width * 2) {
					currenttiem = 10;
				}
			}
			float tempY = high / 2;

			// for (int i = Index - 1; i >= 0; i--) {

			if (Index >= 1) {
				tempY = tempY - TextHigh;

				canvas.drawText(mSentenceEntities.get(Index - 1).getLyric(),
						width / 2, tempY, NotCurrentPaint);
			}
			// }

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
		// System.out.println(index);
	}

	public float GetIndex() {
		return high;

	}

	// drawable 类型转化为bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	// 创建自己想要的bitmap的大小。
	public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h) {
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int newWidth = w;
			int newHeight = h;
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {
			return null;
		}
	}

	public static Bitmap resizeBitmap(String path, int width, int height) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.outWidth = width;
		options.outHeight = height;

		Bitmap bmp = BitmapFactory.decodeFile(path, options);
		options.inSampleSize = options.outWidth / height;
		options.inJustDecodeBounds = false;
		bmp = BitmapFactory.decodeFile(path, options);
		return bmp;
	}

	public NinePatchDrawable getBitmap(Bitmap bitmap) {

		if (bitmap != null) {
			int density = android.os.Build.VERSION.SDK_INT < 9 ? DisplayMetrics.DENSITY_HIGH
					: DisplayMetrics.DENSITY_XHIGH;
			bitmap.setDensity(density);
			byte[] chunk = bitmap.getNinePatchChunk();
			if (NinePatch.isNinePatchChunk(chunk)) {
				NinePatchDrawable drawable = new NinePatchDrawable(
						context.getResources(), bitmap, chunk, new Rect(), null);
				return drawable;
			}
		}
		return null;

	}

	public void setContext(Context context) {
		this.context = context;
	}
}
