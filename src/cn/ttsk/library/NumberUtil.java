package cn.ttsk.library;

import java.math.BigDecimal;

public class NumberUtil {
	//float 转换至 int 小数四舍五入
    public static int convertFloatToInt(float sourceNum) {
        BigDecimal bigDecimal = new BigDecimal(sourceNum);
        return bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }
}
