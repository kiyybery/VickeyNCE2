package cn.ttsk.library;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * 重新布局工具
 * 
 * @author {YueJinbiao}
 */
public class RelayoutViewTool {

	/**
	 * 将原视图 宽高，padding，margin 按比例重新布
	 * 
	 * @param view
	 * @param scale
	 */
	public static void relayoutViewWithScale(View view, float scale) {

		if (view == null) {
			return;
		}

		resetViewLayoutParams(view, scale);

		if (view instanceof ViewGroup) {
			View[] where = null;
			try {
				Field field = ViewGroup.class.getDeclaredField("mChildren");
				field.setAccessible(true);
				where = (View[]) field.get(view);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (where != null) {
				for (View child : where) {
					relayoutViewWithScale(child, scale);
				}
			}
		}
	}

	private static void resetViewLayoutParams(View view, float scale) {

		if (view instanceof TextView) {
			resetTextSize((TextView) view, scale);
		}

		int pLeft = convertFloatToInt(view.getPaddingLeft() * scale);
		int pTop = convertFloatToInt(view.getPaddingTop() * scale);
		int pRight = convertFloatToInt(view.getPaddingRight() * scale);
		int pBottom = convertFloatToInt(view.getPaddingBottom() * scale);

		view.setPadding(pLeft, pTop, pRight, pBottom);

		LayoutParams params = view.getLayoutParams();
		if ("test_relayout".equals(view.getTag())) {
			Log.i("relayout", "test_relayout/" + params);
		}
		if (params == null) {
			return;
		}
		if (params.width > 0) {
			params.width = convertFloatToInt(params.width * scale);
		}
		if (params.height > 0) {
			params.height = convertFloatToInt(params.height * scale);
		}

		if (params instanceof MarginLayoutParams) {
			MarginLayoutParams mParams = (MarginLayoutParams) params;
			if (mParams.leftMargin > 0) {
				mParams.leftMargin = convertFloatToInt(mParams.leftMargin
						* scale);
			}
			if (mParams.rightMargin > 0) {
				mParams.rightMargin = convertFloatToInt(mParams.rightMargin
						* scale);
			}
			if (mParams.topMargin > 0) {
				mParams.topMargin = convertFloatToInt(mParams.topMargin * scale);
			}
			if (mParams.bottomMargin > 0) {
				mParams.bottomMargin = convertFloatToInt(mParams.bottomMargin
						* scale);
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private static void resetTextSize(TextView textView, float scale) {
		float size = textView.getTextSize(); // lineSpacingExtra
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * scale);
		float spacingExtra = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			try {
				spacingExtra = textView.getLineSpacingExtra();
			} catch (NoSuchMethodError e) {

			}
		}
		textView.setLineSpacing(spacingExtra * scale, 1f);
	}

	public static void SetTextviewSize(TextView textView, float scale) {
		float size = textView.getTextSize(); // lineSpacingExtra
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size * scale);
		float spacingExtra = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			try {
				spacingExtra = textView.getLineSpacingExtra();
			} catch (NoSuchMethodError e) {

			}
		}
		textView.setLineSpacing(spacingExtra * scale, 1f);
	}

	// float 转换int 小数四舍五入
	public static int convertFloatToInt(float sourceNum) {
		BigDecimal bigDecimal = new BigDecimal(sourceNum);
		return bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
	}
}
