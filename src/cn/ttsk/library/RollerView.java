package cn.ttsk.library;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import cn.ttsk.nce2.entity.RollerPic;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;

public class RollerView extends GLSurfaceView {
	public float screenWidth;// 屏幕宽带
	public float screenHeight;// 屏幕高度
	public float ratio;// 屏幕宽高比
	private final float TOUCH_SCALE_FACTOR = 0.2f;// 角度缩放比例
	private SceneRenderer mRenderer;// 场景渲染器

	float mPreviousX;// 上次按下位置x坐标
	float mPreviousY;// 上次按下位置y坐标
	long previousTime;// 上次按下的时间
	boolean isCheck = false;// 是否点击查看图片
	boolean isMove = false;
	public static final float CENTER_X = 0f;// 场景中心的X坐标
	public static final float CENTER_Y = 0f;// 场景中心的Y坐标
	public static final float CENTER_Z = -11.2f;// 场景中心的Z坐标
	public static boolean threadWork;// 惯性线程工作标志位
	public float photo_angle_span;// 照片间角度跨度
	public float width;
	public float height;
	public float imgWidth = 2.0f;
	public float imgHeight;
	public List<RollerPic> pics = new ArrayList<RollerPic>();

	private List<Integer> textureIds = new ArrayList<Integer>();// n张照片纹理id数组
	private List<Integer> blurTextureIds = new ArrayList<Integer>();//模糊效果的图片纹理
	float xAngle = 0;// 总场景旋转角度(-180~180)
	float xAngleCount = 0;// 总场景旋转角度(0~360)
	public int currIndex = 0;// 当前选中的索引
	float xAngleV = 0;// 总场景角度变化速度
	float xAngleVPrev = 0;// 总场景角度变化速度前一个值
	float xAngleA = 0;// 总场景角度加速度变化速度
	private float xAngleMin;//场景最小角度
	private float xAngleMax;//场景最大角度
	private float minAngle;//判断图片切换的最小角度差
	private float thisAngle;//当前图片的准确角度
	private float nextAngle;//下一张图的场景角度
	private float prevAngle;//上一张图的场景角度
	private Handler handler;
	public Boolean needUpdate = false, updateImage = false;//是否在更新图片
	private float yOffset;//控件在Y轴的偏移量
	private Boolean isReady = false;//图片是否已重新加载
	private int lockId;//加锁图标的textureId
	private float lockWidth;//加锁图标在投影面的宽度
	private float lockHeight;//加锁图标在投影面的高度
	private float scaling;//投影面图片和实际图片的图片比例，用于各种缩放计算

	public RollerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		mRenderer = new SceneRenderer(); // 创建场景渲染器
		setZOrderOnTop(true);
		setRenderer(mRenderer); // 设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);// 设置渲染模式为主动渲染
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		// 启动一个线程根据当前的角速度旋转场景
		threadWork = true;
		new Thread() {
			public void run() {
				while (threadWork) {
					if (Float.isNaN(xAngle) || Float.isNaN(xAngleV)) {
						throw new RuntimeException("yangle " + xAngle
								+ "yAngleV=" + xAngleV);
					}
					// 根据角速度计算新的场景旋转角度
					xAngle += xAngleV;
					if (xAngle < xAngleMin) {
						xAngle = 0;
					} else if (xAngle > xAngleMax) {
						xAngle = xAngleMax;
					}
					if (Float.isNaN(xAngle)) {
						throw new RuntimeException("yangle" + xAngle);
					}
					// 将角度规格化到0～360之间
					xAngle = (xAngle + 360) % 360;
					if (Float.isNaN(xAngle) || Float.isNaN(xAngleV)) {
						throw new RuntimeException("yangle " + xAngle
								+ "yAngleV=" + xAngleV);
					}
					// 若当前手指已经抬起则角速度衰减
					if (!isMove) {
						xAngleV = xAngleV * 0.7f;
						
					}
					// 若 角速度小于阈值则归0
					if (Math.abs(xAngleV) < 5.0f) {
						if (Math.abs(xAngle - thisAngle) < 5) {
							xAngleV = 0;
							xAngle = thisAngle;
						} else if (xAngle > thisAngle) {
							xAngleV = -5.0f;
						} else {
							xAngleV = 5.0f;
						}
					}
					requestRender();
					if (xAngleV > 0) {
						if (currIndex < (textureIds.size() -1) && xAngle > nextAngle) {
							currIndex++;
							prevAngle += photo_angle_span;
							nextAngle += photo_angle_span;
							thisAngle += photo_angle_span;
							Message msg = new Message();
							msg.what = NCE2.MSG_PICVIEW_CHANGE;
							handler.sendMessage(msg);
						}
					} else if (xAngleV < 0) {
						if (currIndex > 0 && xAngle < prevAngle) {
							currIndex--;
							prevAngle -= photo_angle_span;
							nextAngle -= photo_angle_span;
							thisAngle -= photo_angle_span;
							Message msg = new Message();
							msg.what = NCE2.MSG_PICVIEW_CHANGE;
							handler.sendMessage(msg);
						}
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	public void setData(List<RollerPic> pics, Handler hander) {
		this.pics = pics;
		this.photo_angle_span = 30.0f;
		scaling = imgWidth / width;
		this.xAngleMax = photo_angle_span * (pics.size() -1);
		this.xAngleMin = 0;
		this.currIndex = 0;
		this.handler = hander;
		this.minAngle = photo_angle_span / 2.0f;
		this.thisAngle = 0;
		this.nextAngle = photo_angle_span - minAngle;
		this.prevAngle = -photo_angle_span + minAngle;
		needUpdate = true;
	}

	// 触摸事件回调方法
	@Override
	public boolean onTouchEvent(MotionEvent e) {

		if (isCheck) {// 若在detail中不处理触控事件
			return true;
		}

		float x = e.getX();// 获取触控点X坐标
		float y = e.getY();// 获取触控点Y坐标
		float dx = x - mPreviousX;// 计算X向触控位移
		float dy = y - mPreviousY;// 计算Y向触控位移
		long currTime = System.currentTimeMillis();// 获取当前时间戳
		long timeSpan = (currTime - previousTime) / 10;// 计算两次触控事件之间的时间差

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isMove = false;
			break;
		case MotionEvent.ACTION_MOVE:
			if (Math.abs(dx) > 5 || Math.abs(dy) > 5) {// 触控位移大于阈值则进入移动状态
				isMove = true;
			}
			if (isMove) {// 若在移动状态则计算角度变化速度
				if (timeSpan != 0) {//向上为正，向下为负
					xAngleVPrev = xAngleV;
					xAngleV =  - dy * TOUCH_SCALE_FACTOR / timeSpan;
					xAngleA = xAngleV - xAngleVPrev;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			// 若在非移动状态且角度速度为0则看选中的是哪幅照片被点击
			if (!isMove && xAngleV < 0.05f) {
				for (int i=0; i<textureIds.size(); i++) {
					float textureAngle = (xAngle - i * photo_angle_span);
					if (minAngle > Math.abs(textureAngle)) {
						currIndex = i;
						if ((y+yOffset) > (screenHeight - height) / 2 && (y + yOffset) < (screenHeight + height) / 2) {
							performClick();
						}
					}
				}
			}
			isMove = false;
			break;
		}
		mPreviousX = x;// 记录触控笔位置
		mPreviousY = y;// 记录触控笔位置
		previousTime = currTime;// 记录此次时间
		requestRender();
		return true;
	}
	private class SceneRenderer implements GLSurfaceView.Renderer {
		private Board board;

		@Override
		public void onDrawFrame(GL10 gl) {
			if (!isReady && handler != null) {
				isReady = true;
				Message msg = new Message();
				msg.what = NCE2.MSG_PICVIEW_CHANGE;
				handler.sendMessage(msg);
			}
			if (needUpdate && !updateImage) {
				updateImage = true;
				textureIds.clear();
				blurTextureIds.clear();
				scaling = 0;
				FileInputStream is;
				Bitmap bitmap;
				Bitmap defaultBitmap = null;
				Bitmap defaultBlurBitmap = null;
				BitmapFactory.Options opts=new BitmapFactory.Options();
				opts.inTempStorage = new byte[100 * 1024];
				opts.inPreferredConfig = Bitmap.Config.RGB_565;
				opts.inPurgeable = true;
				opts.inInputShareable = true;
				for (RollerPic pic : pics) {
					try {
						is = new FileInputStream(pic.file);
						bitmap = BitmapFactory.decodeFileDescriptor(is.getFD());
						is.close();
						if (scaling == 0) {
							width = bitmap.getWidth();
							height = bitmap.getHeight();
							scaling = imgWidth / width;
							imgHeight = height * scaling;
						}
						textureIds.add(initTexture(gl, bitmap));
						is = new FileInputStream(pic.gauseFile);
						bitmap = BitmapFactory.decodeFileDescriptor(is.getFD());
						is.close();
						blurTextureIds.add(initTexture(gl, bitmap));
						bitmap.recycle();
					} catch (Exception e) {
						e.printStackTrace();
						if (defaultBitmap == null) {
							defaultBitmap = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.cover_shadow), null, opts);
							defaultBlurBitmap = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.cover_gaused), null, opts);
							if (scaling == 0) {
								width = defaultBitmap.getWidth();
								height = defaultBitmap.getHeight();
								scaling = imgWidth / width;
								imgHeight = height * scaling;
							}
						}
						textureIds.add(initTexture(gl, defaultBitmap));
						blurTextureIds.add(initTexture(gl, defaultBlurBitmap));
					}
					
				}
				bitmap = BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.picview_lock), null, opts);
				lockWidth = (float)bitmap.getWidth() * scaling;
				lockHeight = (float)bitmap.getHeight() * scaling;
				lockId = initTexture(gl, bitmap);
				
				bitmap.recycle();
				if (defaultBitmap != null && !defaultBitmap.isRecycled()) {
					defaultBitmap.recycle();
				}
				if (defaultBlurBitmap != null && !defaultBlurBitmap.isRecycled()) {
					defaultBlurBitmap.recycle();
				}
				needUpdate = false;
				updateImage = false;
			}
			if (textureIds.size() == 0) return;
			// 清除颜色缓存于深度缓存
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			// 设置当前矩阵为模式矩阵
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			// 设置当前矩阵为单位矩阵
			gl.glLoadIdentity();

			// 显示照片组，可触控旋转选择
			gl.glPushMatrix();
			xAngle = xAngle % 360;
			gl.glTranslatef(CENTER_X, CENTER_Y, CENTER_Z);
			int min = currIndex > 1 ? currIndex - 2 : 0;
			int max = currIndex < textureIds.size() -3 ? currIndex +2 : textureIds.size()-1;
			
			for (int i = max; i> currIndex; i--) {
				float angle = (xAngle - i * photo_angle_span) % 360;
				gl.glPushMatrix();
				float currentY, currentZ;
				if (angle > photo_angle_span) {
					currentY = (float) (imgHeight /2f * (Math.pow(angle /30.0f, 1.1)));
					currentZ = (float) (imgHeight * (4 - angle / 30.0f));
					gl.glTranslatef(0, currentY, currentZ);
					gl.glRotatef(photo_angle_span, 1, 0, 0);
				} else if (angle >= 0) {
					currentY = (float) (imgHeight /2f * (Math.pow(angle /30.0f, 1.1)));
					currentZ = (float) (imgHeight * (4 - angle / 30.0f));
					gl.glTranslatef(0, currentY, currentZ);
					gl.glRotatef(angle, 1, 0, 0);
				} else if (angle >= -photo_angle_span) {
					currentY = (float) (-imgHeight /2f * (Math.pow(Math.abs(angle /30.0f), 1.1)));
					currentZ = (float) (imgHeight * (4 + angle / 30.0f));
					gl.glTranslatef(0, currentY, currentZ);
					gl.glRotatef(angle, 1, 0, 0);
				} else {
					currentY = (float) (-imgHeight /2f * (Math.pow(Math.abs(angle /30.0f), 1.1)));
					currentZ = (float) (imgHeight * (4 + angle / 30.0f));
					gl.glTranslatef(0, currentY, currentZ);
					gl.glRotatef(-photo_angle_span, 1, 0, 0);
				}
//				MLog.e("rollerview", i+","+angle+","+currentY+","+currentZ);
				board = new Board(-imgWidth / 2, -imgHeight / 2
						, imgWidth,
						imgHeight);
				// 用于显示照片的纹理矩形
				if (minAngle > Math.abs(angle)) {
					board.drawSelf(gl, textureIds.get(i));
				} else {
					board.drawSelf(gl, blurTextureIds.get(i));
				}
				gl.glPopMatrix();
			}
			for (int i = min; i <= currIndex; i++) {
				float angle = (xAngle - i * photo_angle_span) % 360; 
				gl.glPushMatrix();
				float currentY, currentZ;
				if (angle > photo_angle_span) {
					currentY = (float) (imgHeight /2f * (Math.pow(angle /30.0f, 1.1)));
					currentZ = (float) (imgHeight * (4 - angle / 30.0f));
					gl.glTranslatef(0, currentY, currentZ);
					gl.glRotatef(photo_angle_span, 1, 0, 0);
				} else if (angle >= 0) {
					currentY = (float) (imgHeight /2f * (Math.pow(angle /30.0f, 1.1)));
					currentZ = (float) (imgHeight * (4 - angle / 30.0f));
					gl.glTranslatef(0, currentY, currentZ);
					gl.glRotatef(angle, 1, 0, 0);
				} else if (angle > -photo_angle_span) {
					currentY = (float) (-imgHeight /2f * (Math.pow(Math.abs(angle /30.0f), 1.1)));
					currentZ = (float) (imgHeight * (4 + angle / 30.0f));
					gl.glTranslatef(0, currentY, currentZ);
					gl.glRotatef(angle, 1, 0, 0);
				} else {
					currentY = (float) (-imgHeight /2f * (Math.pow(Math.abs(angle /30.0f), 1.1)));
					currentZ = (float) (imgHeight * (4 + angle / 30.0f));
					gl.glTranslatef(0, currentY, currentZ);
					gl.glRotatef(-photo_angle_span, 1, 0, 0);
				}
				//MLog.e("rollerview", i+","+angle+","+currentY+","+currentZ);
				board = new Board(-imgWidth / 2, -imgHeight / 2
						, imgWidth,
						imgHeight);
				// 用于显示照片的纹理矩形
				if (minAngle > Math.abs(angle)) {
					board.drawSelf(gl, textureIds.get(i));
				} else {
					board.drawSelf(gl, blurTextureIds.get(i));
				}
				gl.glPopMatrix();
			}
			
			if (currIndex >= 0 &&  pics.size() > currIndex) {
				gl.glPushMatrix();
				gl.glTranslatef(0, 0, imgHeight*5);
				if (pics.get(currIndex).locked) {
					board = new Board(-lockWidth /2, -lockHeight / 2
							, lockWidth, lockHeight);
					board.drawSelf(gl, lockId);
				}
				gl.glPopMatrix();
			}
			
			gl.glPopMatrix();
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// 设置视窗大小及位置
			gl.glViewport(0, 0, width,height);
			// 设置当前矩阵为投影矩阵
			gl.glMatrixMode(GL10.GL_PROJECTION);
			// 设置当前矩阵为单位矩阵
			gl.glLoadIdentity();
			// 调用此方法计算产生透视投影矩阵
			ratio = width * 1.0f / height;
			gl.glFrustumf(-ratio, ratio, -1.1f, 0.9f, 3, 10);
			// 设置为关闭背面剪裁
			gl.glDisable(GL10.GL_CULL_FACE);
			yOffset = screenHeight - height;
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// 关闭抗抖动
			gl.glDisable(GL10.GL_DITHER);
			// 设置特定Hint项目的模式，这里为设置为使用快速模式
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			// 设置屏幕背景色黑色RGBA
			gl.glClearColor(0, 0, 0, 0);
			// 启用深度测试
			gl.glEnable(GL10.GL_DEPTH_TEST);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glEnable(GL10.GL_BLEND);
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		}
	}

	// 初始化纹理
	public int initTexture(GL10 gl, Bitmap bitmap)// textureId
	{
		// 生成纹理ID
		int[] textures = new int[1];
		
//		Log.e("############################", ""+textures.length);
		gl.glGenTextures(1, textures, 0);
		int currTextureId = textures[0];
		Log.e("$$$$$$$$$$$$$$$$$$$$$$$$$$$$", ""+currTextureId);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		

		return currTextureId;
	}

	// 可能被拾取选中照片的描述信息存储对象所属类
	public class CandidateDis implements Comparable<CandidateDis> {
		float currAngleSpan;// 此幅照片与270度角的夹角
		float currAngle;// 此幅照片的角度
		int index;// 此幅照片的索引

		public CandidateDis(float currAngleSpan, float currAngle, int index) {
			this.currAngleSpan = currAngleSpan;
			this.currAngle = currAngle;
			this.index = index;
		}

		@Override
		public int compareTo(CandidateDis another) {
			// 比较两幅照片谁与270度的夹角小
			if (this.currAngleSpan < another.currAngleSpan) {
				return -1;
			}
			if (this.currAngleSpan == another.currAngleSpan) {
				return 0;
			}
			if (this.currAngleSpan > another.currAngleSpan) {
				return 1;
			}
			return 0;
		}

		public boolean isInXRange(float x, float y)// xy为NEAR面上的触控坐标
		{
			return false;
		}
	}

	// 显示照片的纹理矩形
	public class Board {
		private FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
		private FloatBuffer mTextureBuffer;// 顶点纹理数据缓冲
		int vCount = 0;// 顶点数
		public float width;
		public float height;

		public Board(float left, float bottom, float width, float height) {
			this.width = width;
			this.height = height;
			// 顶点坐标数据的初始化================begin============================
			vCount = 6;
			float vertices[] = new float[] { left, bottom + height, 0, left,
					bottom, 0, left + width, bottom + height, 0,

					left + width, bottom + height, 0, left, bottom, 0,
					left + width, bottom, 0 };

			// 创建顶点坐标数据缓冲
			// vertices.length*4是因为一个整数四个字节
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
			mVertexBuffer = vbb.asFloatBuffer();// 转换为int型缓冲
			mVertexBuffer.put(vertices);// 向缓冲区中放入顶点坐标数据
			mVertexBuffer.position(0);// 设置缓冲区起始位置
			// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
			// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
			// 顶点坐标数据的初始化================end============================

			// 顶点着色数据的初始化================begin============================
			float textures[] = new float[]// 顶点颜色值数组，每个顶点4个色彩值RGBA
			{ 0, 0, 0, 1, 1, 0,

			1, 0, 0, 1, 1, 1 };
			// 创建顶点纹理数据缓冲
			ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length * 4);
			cbb.order(ByteOrder.nativeOrder());// 设置字节顺序
			mTextureBuffer = cbb.asFloatBuffer();// 转换为int型缓冲
			mTextureBuffer.put(textures);// 向缓冲区中放入顶点着色数据
			mTextureBuffer.position(0);// 设置缓冲区起始位置
			// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
			// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
			// 顶点着色数据的初始化================end============================
		}

		public void drawSelf(GL10 gl, int texId) {
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// 启用顶点坐标数组
			// 为画笔指定顶点坐标数据
			gl.glVertexPointer(3, // 每个顶点的坐标数量为3 xyz
					GL10.GL_FLOAT, // 顶点坐标值的类型为 GL_FLOAT
					0, // 连续顶点坐标数据之间的间隔
					mVertexBuffer // 顶点坐标数据
			);

			// 开启纹理
			gl.glEnable(GL10.GL_TEXTURE_2D);
			//启用阴影平滑
			gl.glShadeModel(GL10.GL_SMOOTH);
			// 允许使用纹理ST坐标缓冲
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			// 为画笔指定纹理ST坐标缓冲
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
			// 绑定当前纹理
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
			

			// 绘制图形
			gl.glDrawArrays(GL10.GL_TRIANGLES, // 以三角形方式填充
					0, // 开始点编号
					vCount // 顶点数量
			);

			// 关闭纹理
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
	}
}
