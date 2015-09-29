package cn.ttsk.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.graphics.PorterDuff.Mode;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewConfiguration;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Scroller;

public class PicView extends SurfaceView implements SurfaceHolder.Callback {
	private static final float ROTATE_X = 20;
	private static final int MOVE_LIMIT = 10; // 抖动
	private static final float[] BASE_SCALE = new float[] { 1f, 0.75f, 0.5f, 0f };
	private static final float[] BASE_HEIGHT = new float[4];

	private Camera mCamera = new Camera();
	private Matrix mMatrix = new Matrix();
	private Paint mPaint = new Paint();
	private int mCurPos = 0;
	private int mWidth, mHeight;
	private int BIG_PIC_W;
	private int BIG_PIC_H;
	private int PAGE_SPACE;
	private Rect mRect = new Rect();
	private int mCurY;
	private boolean mIsAutoScroll;
	private boolean mIsTouching;
	private boolean mIsRepeat = true;
	private OnItemClickListener mOnItemClickListener;
	private ObservePositionListener mObservePositionListener;

	private ScrollerHelper mScrollerHelper;
	private GestureDetector mGestureDetector;

	private Handler mHandler = new Handler();
	private Runnable mDrawFrame = new Runnable() {
		@Override
		public void run() {
			mHandler.removeCallbacks(mDrawFrame);
			drawFrame();
		}
	};

	private DataSource mDataSource;

	public interface DataSource {
		Bitmap getBitmap(int w, int h, int pos, int level);

		int getCount();
	}

	public PicView(Context context) {
		this(context, null);
	}
	
	public void setSize(int width, int height) {
		BIG_PIC_W = width;
		BIG_PIC_H = height;
	}

	public PicView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PicView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		getHolder().addCallback(this);
		setZOrderOnTop(true);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		mPaint.setAntiAlias(true);
		mScrollerHelper = new ScrollerHelper(context);
		mGestureDetector = new GestureDetector(context, mGestureListener);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		mWidth = width;
		mHeight = height;
		width = BIG_PIC_W < BIG_PIC_H ? BIG_PIC_W : BIG_PIC_H;

		PAGE_SPACE = BIG_PIC_H;
		mRect.set(width / 2 - BIG_PIC_W / 2, height / 2 - BIG_PIC_H / 2, width
				/ 2 + BIG_PIC_W / 2, height / 2 + BIG_PIC_H / 2);

		initBaseHeight();
		drawFrame();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mHandler.removeCallbacks(mDrawFrame);
	}

	private void initBaseHeight() {
		RectF rect = new RectF();
		BASE_HEIGHT[0] = BIG_PIC_H / 2;
		for (int i = 1; i < BASE_HEIGHT.length; i++) {
			mMatrix.reset();
			mCamera.save();
			mCamera.rotateX(ROTATE_X);
			mCamera.getMatrix(mMatrix);
			mCamera.restore();
			mMatrix.preTranslate(-BIG_PIC_W / 2, -BIG_PIC_H / 2);
			mMatrix.postScale(BASE_SCALE[i], BASE_SCALE[i]);
			rect.set(0, 0, BIG_PIC_W, BIG_PIC_H);
			mMatrix.mapRect(rect);
			BASE_HEIGHT[i] = rect.height() / 2 + BASE_HEIGHT[i - 1];
		}
	}

	public void setCurPos(int pos) {
		mCurPos = pos;
		drawFrame();
	}

	public int getCurPos() {
		return mCurPos;
	}

	public void setDataSource(DataSource source) {
		mDataSource = source;
	}

	public DataSource getDataSource() {
		return mDataSource;
	}

	public void setRepeat(boolean repeat) {
		mIsRepeat = repeat;
	}

	public boolean isRepeat() {
		return mIsRepeat;
	}

	public void setItemClickListener(OnItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	public void setObservePositionListener(ObservePositionListener listener) {
		mObservePositionListener = listener;
	}

	public interface OnItemClickListener {
		void onItemClick(int pos);
	}

	public interface ObservePositionListener {
		void onObservePosition(int pos);
	}

	private void drawFrame() {
		if (mDataSource == null) {
			return;
		}

		Bitmap b = null;
		Canvas c = null;
		try {
			c = getHolder().lockCanvas();
			c.drawColor(Color.TRANSPARENT, Mode.CLEAR);

			boolean posted = false;
			if (mIsAutoScroll) {
				if (mScrollerHelper.computeScrollOffset()) {
					mHandler.post(mDrawFrame);
					posted = true;
				} else {
					mIsAutoScroll = false;
				}
				mCurY = mScrollerHelper.getCurrY();
				if (mCurY == mScrollerHelper.getFinalY()) {
					mScrollerHelper.abortAnimation();
					mIsAutoScroll = false;
				}
			}

			int pos = mCurPos;
			if (Math.abs(mCurY) >= PAGE_SPACE) {
				pos += mCurY / PAGE_SPACE;
				if (!mIsRepeat) {
					if (pos < 0) {
						pos = 0;
						mCurY = 1 - PAGE_SPACE;
						if (mIsAutoScroll) {
							mScrollerHelper.abortAnimation();
							mIsAutoScroll = false;
						}
					} else if (pos > mDataSource.getCount() - 1) {
						pos = mDataSource.getCount() - 1;
						mCurY = PAGE_SPACE - 1;
						if (mIsAutoScroll) {
							mScrollerHelper.abortAnimation();
							mIsAutoScroll = false;
						}
					}
				}

				if (!mIsAutoScroll) {
					mCurPos = pos;
				}
				mCurY %= PAGE_SPACE;
			}
			if (mCurY != 0 && !mIsTouching && !posted) {
				boolean bounce = !mIsRepeat
						&& ((mCurPos == 0 && mCurY < -PAGE_SPACE / 2) || (mCurPos == mDataSource
								.getCount() - 1 && mCurY > PAGE_SPACE / 2));
				if (bounce) {
					mScrollerHelper.startScroll(mCurY, PAGE_SPACE,
							mCurY < 0 ? 1000 : -1000);
				} else {
					mScrollerHelper.startScroll(mCurY, PAGE_SPACE, 0);
				}
				mIsAutoScroll = true;
				mHandler.post(mDrawFrame);
			}

			int realCurPos = pos;
			int w, h;
			int lv;
			float sc, dc, rx;
			float progress = 1.0f * mCurY / PAGE_SPACE;
			Bitmap tmp = null;
			float[] tmpVals = null;
			// 小图上
			for (int i = BASE_SCALE.length - 1; i >= 1; i--) {
				if (progress > 0 && i == BASE_SCALE.length - 1) {
					continue;
				}
				if (!mIsRepeat && pos - i < 0) {
					continue;
				}
				lv = i;
				if (i == 1 && progress < -0.5f) {
					lv = 0;
				}
				b = mDataSource.getBitmap(mWidth, mHeight, pos - i, lv);
				if (b == null) {
					break;
				}
				w = b.getWidth();
				h = b.getHeight();

				sc = BASE_SCALE[i] - progress
						* (BASE_SCALE[i - 1] - BASE_SCALE[i]);
				if (progress <= 0) {
					float last = i == 1 ? 0 : BASE_HEIGHT[i - 2];
					dc = (1 + progress) * (BASE_HEIGHT[i - 1] - last) + last;
				} else {
					dc = progress * (BASE_HEIGHT[i] - BASE_HEIGHT[i - 1])
							+ BASE_HEIGHT[i - 1];
				}
				if (i == 1 && progress <= 0) {
					rx = -ROTATE_X * (1 + progress);
				} else {
					rx = -ROTATE_X;
				}

				mMatrix.reset();
				mCamera.save();
				mCamera.rotateX(rx);
				mCamera.getMatrix(mMatrix);
				mCamera.restore();
				mMatrix.preTranslate(-w / 2, -h / 2);
				mMatrix.postScale(sc * BIG_PIC_W / w, sc * BIG_PIC_H / h);
				mMatrix.postTranslate(mWidth / 2, mHeight / 2 - dc);
				if (i == 1 && progress < -0.5f) {
					tmp = b;
					tmpVals = new float[9];
					mMatrix.getValues(tmpVals);
					realCurPos--;
				} else {
					c.drawBitmap(b, mMatrix, mPaint);
				}
			}

			// 小图下
			for (int i = BASE_SCALE.length - 1; i >= 1; i--) {
				// for (int i = 1; i < BASE_SCALE.length; i++) {
				if (progress < 0 && i == BASE_SCALE.length - 1) {
					continue;
				}
				if (!mIsRepeat && pos + i >= mDataSource.getCount()) {
					continue;
				}
				lv = i;
				if (i == 1 && progress > 0.5f) {
					lv = 0;
				}
				b = mDataSource.getBitmap(mWidth, mHeight, pos + i, lv);
				if (b == null) {
					break;
				}
				w = b.getWidth();
				h = b.getHeight();

				sc = BASE_SCALE[i] + progress
						* (BASE_SCALE[i - 1] - BASE_SCALE[i]);
				if (progress > 0) {
					float last = i == 1 ? 0 : BASE_HEIGHT[i - 2];
					dc = (1 - progress) * (BASE_HEIGHT[i - 1] - last) + last;
				} else {
					dc = -progress * (BASE_HEIGHT[i] - BASE_HEIGHT[i - 1])
							+ BASE_HEIGHT[i - 1];
				}
				if (i == 1 && progress > 0) {
					rx = ROTATE_X * (1 - progress);
				} else {
					rx = ROTATE_X;
				}

				mMatrix.reset();
				mCamera.save();
				mCamera.rotateX(rx);
				mCamera.getMatrix(mMatrix);
				mCamera.restore();
				mMatrix.preTranslate(-w / 2, -h / 2);
				mMatrix.postScale(sc * BIG_PIC_W / w, sc * BIG_PIC_H / h);
				mMatrix.postTranslate(mWidth / 2, mHeight / 2 + dc);
				if (i == 1 && progress > 0.5f) {
					tmp = b;
					tmpVals = new float[9];
					mMatrix.getValues(tmpVals);
					realCurPos++;
				} else {
					c.drawBitmap(b, mMatrix, mPaint);
				}
			}

			// 大图
			if (tmp != null && tmpVals != null) {
				lv = 1;
			} else {
				lv = 0;
			}
			b = mDataSource.getBitmap(mWidth, mHeight, pos, lv);
			if (b != null) {
				w = b.getWidth();
				h = b.getHeight();
				mMatrix.reset();
				mCamera.save();
				mCamera.rotateX(-ROTATE_X * progress);
				mCamera.getMatrix(mMatrix);
				mCamera.restore();
				sc = 1 - Math.abs((1 - BASE_SCALE[1]) * progress);
				mMatrix.preTranslate(-w / 2, -h / 2);
				mMatrix.postScale(sc * BIG_PIC_W / w, sc * BIG_PIC_H / h);
				mMatrix.postTranslate(mWidth / 2, mHeight / 2 - progress
						* BIG_PIC_H / 2);
				c.drawBitmap(b, mMatrix, mPaint);
			}

			if (tmp != null && tmpVals != null) {
				mMatrix.reset();
				mMatrix.setValues(tmpVals);
				c.drawBitmap(tmp, mMatrix, mPaint);
			}
			if (mObservePositionListener != null) {
				mObservePositionListener.onObservePosition(realCurPos);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (c != null) {
				getHolder().unlockCanvasAndPost(c);
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mIsAutoScroll) {
			mGestureDetector.onTouchEvent(event);
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (Math.abs(mCurY) < MOVE_LIMIT
						&& mRect.contains((int) event.getX(),
								(int) event.getY())) {
					if (mOnItemClickListener != null) {
						mOnItemClickListener.onItemClick(mCurPos);
					}
				}

				mIsTouching = false;
				if (!mIsAutoScroll && Math.abs(mCurY) > 0) {
					drawFrame();
				}
			} else {
				mIsTouching = true;
			}
		}
		return true;
	}

	private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			mCurY = (int) (mCurY + distanceY);
			drawFrame();
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			mScrollerHelper.startScroll(mCurY, PAGE_SPACE, -velocityY);
			mIsAutoScroll = true;
			drawFrame();
			return true;
		}
	};

	private class ScrollerHelper {
		private float INFLEXION = 0.35f;
		private float mFlingFriction = ViewConfiguration.getScrollFriction();
		private float DECELERATION_RATE = (float) (Math.log(0.78) / Math
				.log(0.9));
		private float mPpi;
		private float mPhysicalCoeff;
		private Scroller mScroller;

		@SuppressLint("NewApi")
		public ScrollerHelper(Context context) {
			mPpi = context.getResources().getDisplayMetrics().density * 160.0f;
			mPhysicalCoeff = SensorManager.GRAVITY_EARTH * 39.37f * mPpi
					* 0.84f;

			mScroller = new Scroller(context, null, false);
		}

		public void abortAnimation() {
			mScroller.abortAnimation();
		}

		public int getFinalY() {
			return mScroller.getFinalY();
		}

		public void startScroll(int startY, int space, float v) {
			final double l = Math.log(INFLEXION * Math.abs(v)
					/ (mFlingFriction * mPhysicalCoeff));
			int duration = (int) (1000.0 * Math.exp(l
					/ (DECELERATION_RATE - 1.0f)));
			duration = duration <= 0 ? 250 : duration;
			int dy = (int) (mFlingFriction * mPhysicalCoeff * Math
					.exp(DECELERATION_RATE / (DECELERATION_RATE - 1.0f) * l));

			if (space / 2 > Math.abs(dy + startY)) {
				dy = -startY;
			} else {
				if (v < 0) {
					dy = -dy;
				}
				int fy = dy + startY;
				int mod = fy % space;
				if (-mod > space / 2) {
					dy = dy - space - mod;
				} else if (mod > space / 2) {
					dy = dy + space - mod;
				} else {
					dy = dy - mod;
				}
			}
			mScroller.startScroll(0, startY, 0, dy, duration);
		}

		public boolean computeScrollOffset() {
			return mScroller.computeScrollOffset();
		}

		public int getCurrY() {
			return mScroller.getCurrY();
		}
	}

}
