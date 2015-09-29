package cn.ttsk.library;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by IntelliJ IDEA. User: daocren Date: 11-5-30 Time: 下午5:27
 */
public class DateUtil {

	private static final DateFormat FORMATOR = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final DateFormat FORMATOR_YMD = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final DateFormat FORMATOR_SIMPLE = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	private static final DateFormat FORMATOR_DATE = new SimpleDateFormat(
			"yyyyMMdd");
	private static final DateFormat FORMATOR_YMDHm = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	private static final DateFormat FORMATOR_MD = new SimpleDateFormat(
			"MM-dd HH:mm");
	private static final DateFormat FORMATOR_MDHM = new SimpleDateFormat(
			"MM/dd HH:mm");
	private static final DateFormat FORMATOR_TIME = new SimpleDateFormat(
			"HH:mm");
	private static final DateFormat FORMATOR_TIME_Ms = new SimpleDateFormat(
			"mm:ss");
	private static final DateFormat FORMATOR_YMDH_TZ = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	private static final DateFormat FORMATOR_MDW_HM = new SimpleDateFormat(
			"MM月dd日 E HH:mm");
	private static final DateFormat FORMATOR_W = new SimpleDateFormat("E");
	private static final DateFormat FORMATOR_YMDW_E_HM = new SimpleDateFormat(
			"yyyy-MM-dd E HH:mm");
	private static final DateFormat FORMATOR_h = new SimpleDateFormat("HH");
	private static final DateFormat FORMATOR_m = new SimpleDateFormat("mm");
	private static final DateFormat FORMATOR_md = new SimpleDateFormat("MM-dd");

	/**
	 * 根据 yyyy-MM-dd HH:mm:ss 格式获取日期字符串
	 * 
	 * @param date
	 * @return String
	 */
	public static String getString(Date date) {
		return FORMATOR.format(date);
	}

	public static String getStringYMD(Date date) {
		return FORMATOR_YMD.format(date);
	}

	public static String getStringTime(Date date) {
		return FORMATOR_TIME.format(date);
	}

	/**
	 * 根据 yyyy-MM-dd 格式获取日期字符串
	 * 
	 * @param date
	 * @return String
	 */
	public static String getSimpleString(Date date) {
		return FORMATOR_SIMPLE.format(date);
	}

	/**
	 * 根据 yyyyMMdd 格式获取日期字符串
	 * 
	 * @param date
	 * @return String
	 */
	public static String getDateString(Date date) {
		return FORMATOR_DATE.format(date);
	}

	/**
	 * 根据long值获取时间字符串，long值中不包含毫秒信息
	 * 
	 * @param time
	 * @return
	 */
	public static String getString(long time) {
		return FORMATOR.format(new Date(time * 1000));

	}

	/**
	 * 根据long值获取时间字符串，long值中不包含毫秒信息
	 * 
	 * @param time
	 * @return
	 */
	public static String getString_new(long time) {
		// return FORMATOR.format(new Date(time * 1000));
		return FORMATOR.format(new Date(time));
	}

	/**
	 * 根据long值获取时间字符串，long值中不包含毫秒信息
	 * 
	 * @param time
	 * @return
	 */
	public static String getStringByMD(long time) {
		return FORMATOR_MD.format(new Date(time));
	}

	public static String getStringByMDHM(long time) {
		return FORMATOR_MDHM.format(new Date(time * 1000));
	}

	/**
	 * 根据字符串生成日期
	 * 
	 * @param dateStr
	 *            yyyy-MM-dd HH:mm:ss 格式的字符串
	 * @return Date
	 */
	public static Date getDate(String dateStr) {
		try {
			return FORMATOR.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 转换字符串格式
	 * 
	 * @param dateStr
	 *            yyyy-MM-dd HH:mm:ss 格式的字符串转为 yyyy-MM-dd HH:mm
	 * @return Date
	 */
	public static String getDateStr(String dateStr) {
		try {
			Date date = FORMATOR.parse(dateStr);
			return FORMATOR_YMDHm.format(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 转换字符串格式
	 * 
	 * @param dateStr
	 *            yyyy-MM-dd T HH:mm:ss Z 格式的字符串转为 yyyy-MM-dd HH:mm
	 * @return Date
	 */
	public static String getDateStrFormT(String dateStr) {
		try {
			Date date = FORMATOR_YMDH_TZ.parse(dateStr);
			return FORMATOR_YMDHm.format(date);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * @param created
	 * @param expire
	 * @return
	 */
	public static boolean isExpired(String created, long expire) {
		if (expire != 0) {
			long createTime = 0;
			try {
				createTime = Long.parseLong(created);
			} catch (NumberFormatException e) {

			}
			long expireDate = new Date(createTime).getTime() + expire * 24 * 60
					* 60 * 1000;
			return expireDate < new Date().getTime();
		}
		return false;
	}

	/**
	 * 根据 yyyy-MM-dd HH:mm 格式获取日期Long
	 * 
	 * @param str
	 * @return String
	 */
	public static long getLongForm(String str) {
		long date = 0;
		try {
			date = (FORMATOR_YMDHm.parse(str).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 根据 yyyy-MM-dd hh:mm 格式获取日期字符串
	 * 
	 * @param time
	 * @return String
	 */
	public static String getSimpleStringFormLong(long time) {
		return FORMATOR_YMDHm.format(new Date(time * 1000));
	}

	/**
	 * 根据 yyyy-MM-dd T hh:mm Z格式获取日期字符串
	 * 
	 * @param time
	 * @return String
	 */
	public static String getDateStringFormLong(long time) {
		return FORMATOR_YMDH_TZ.format(new Date(time));
	}

	public static String getDateWeekStringFromLong(long time) {
		return FORMATOR_MDW_HM.format(new Date(time * 1000));
	}

	public static String getWeekStringFromLong(long time) {
		return FORMATOR_W.format(new Date(time * 1000));
	}

	public static String getYMDEHMStringFromLong(long time) {
		return FORMATOR_YMDW_E_HM.format(new Date(time * 1000));
	}

	/**
	 * 根据 yyyy-MM-dd T hh:mm Z格式获取日期字符串
	 * 
	 * @param date
	 * @return String
	 */
	public static String getDateStringFormDate(Date date) {
		return FORMATOR_YMDH_TZ.format(date);
	}

	/**
	 * 根据 yyyy-MM-dd HH:mm 格式获取日期Long
	 * 
	 * @param str
	 * @return String
	 */
	public static long getLongFormTZ(String str) {
		long date = 0;
		try {
			date = (FORMATOR_YMDH_TZ.parse(str).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/*
	 * 取本周7天的第一天（周一的日期）
	 */
	public static String getNowWeekBegin() {
		int mondayPlus;
		Calendar cd = Calendar.getInstance();
		// 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1; // 因为按中国礼拜一作为第一天所以这里减1
		if (dayOfWeek == 1) {
			mondayPlus = 0;
		} else {
			mondayPlus = 1 - dayOfWeek;
		}
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(Calendar.DATE, mondayPlus);
		Date monday = currentDate.getTime();
		DateFormat df = DateFormat.getDateInstance();
		String preMonday = df.format(monday);
		return preMonday + "T00:00:00Z";
	}

	/**
	 * 得到本月的第一天
	 * 
	 * @return
	 */
	public static String getMonthFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getStringYMD(calendar.getTime()) + "T00:00:00Z";
	}

	/**
	 * 得到前三个月的第一天
	 * 
	 * @return
	 */
	public static String getBeforeThreeMonthFirstDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendar.add(Calendar.MONTH, -2);
		return getStringYMD(calendar.getTime()) + "T00:00:00Z";
	}

	public static void main(String arg[]) {
		long str = getLongForm("2013-1-15 20:10");
	}

	public static String getMinDate(ArrayList<String> dateList) {
		long max = Long.MIN_VALUE;
		for (int i = 0; i < dateList.size(); i++) {
			String dateStr = getDateStrFormT(dateList.get(i));
			long l = getLongForm(dateStr);
			if (l > max) {
				max = l;
			}
		}
		return getString_new(max);
	}

	public static String getMsStr(long time) {

		return FORMATOR_TIME_Ms.format(time);
	}

	public static String getDateStr(Date date) {
		return FORMATOR_YMD.format(date);
	}

	public static String getHour(long time) {
		return FORMATOR_h.format(new Date(time));
	}

	public static String getMinute(long time) {
		return FORMATOR_m.format(new Date(time));
	}

	public static String getHour(Date date) {
		return FORMATOR_h.format(date);
	}

	public static String getMinute(Date date) {
		return FORMATOR_m.format(date);
	}

	public static String getMd(long time) {
		return FORMATOR_md.format(new Date(time));
	}

	public static String getTimeStr(int s) {
		String timeStr = "";
		long create_time = (long) s * 1000;
		long now_time = new Date().getTime();
		long one_hour = 60 * 60 * 1000;
		long one_minute = 60 * 1000;
		if (create_time > (now_time - one_minute)) {
			timeStr = "刚刚";
		} else if (create_time > (now_time - one_hour)
				&& create_time < (now_time - one_minute)) {
			timeStr = ((now_time - create_time)/ one_minute) + "分钟前";
		} else if (create_time > getTodayStartTime()
				&& create_time < (now_time - one_hour)) {
			timeStr = ((now_time - create_time) / one_hour) + "小时前";
		} else if (create_time < getTodayStartTime()
				&& create_time > getYestodayStartTime()) {
			timeStr = "昨天";
		} else if (create_time < getYestodayStartTime()) {
			timeStr = getMd(create_time);
		}
		return timeStr;
	}

	public static Long getTodayStartTime() {
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, 0);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime().getTime();
	}

	public static Long getYestodayStartTime() {
		Calendar todayStart = Calendar.getInstance();
		todayStart.set(Calendar.HOUR_OF_DAY, -24);
		todayStart.set(Calendar.MINUTE, 0);
		todayStart.set(Calendar.SECOND, 0);
		todayStart.set(Calendar.MILLISECOND, 0);
		return todayStart.getTime().getTime();
	}
}
