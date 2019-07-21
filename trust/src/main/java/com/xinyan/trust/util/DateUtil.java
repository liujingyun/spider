package com.xinyan.trust.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class DateUtil {

	

	private DateUtil() {
		throw new IllegalAccessError("Utility class");
	}

	public static synchronized Timestamp getNowTimeStamp() {
		return new Timestamp(getTime());
	}

	public static int day() {
		Calendar now = getInstance();
		return now.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取系统当前日期,返回long类型格式数据
	 *
	 * @return
	 */
	public static long getTime() {
		Calendar now = getInstance();
		return now.getTimeInMillis();
	}

	/**
	 * 获取系统距离今天的日期,如day为-1,则返回一天前的当前时间
	 *
	 * @param day
	 * @return 返回long类型格式数据
	 */
	public static long getTimeSpecifyDay(int day) {
		Calendar now = getInstance();
		now.add(Calendar.DATE, day);
		return now.getTimeInMillis();
	}

	/**
	 * 获取系统距离指定时间day天的日期,如day为-1,则返回一天前的当前时间
	 *
	 * @param day
	 * @return 返回long类型格式数据
	 */
	public static long getTimeSpecifyDay(long time, int day) {
		Calendar now = getInstance();
		now.setTimeInMillis(time);
		now.add(Calendar.DATE, day);
		return now.getTimeInMillis();
	}

	/**
	 * 获取dd/MM/yyyy格式日期
	 *
	 * @param time
	 * @return
	 */
	public static String getDMY(long time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date(time);
		String timestr = format.format(date);
		return timestr;
	}

	/**
	 * 获取系统距离今天的日期,hour为-1,则返回1小时前的当前时间
	 *
	 * @param hour
	 * @return 返回long类型格式数据
	 */
	public static long getTimeSpecifyHour(int hour) {
		Calendar now = getInstance();
		now.add(Calendar.HOUR, hour);
		return now.getTimeInMillis();
	}

	/**
	 * 获取系统距离今天的日期,min-1,则返回1分钟前的当前时间
	 *
	 * @param min
	 * @return 返回long类型格式数据
	 */
	public static long getTimeSpecifyMin(int min) {
		Calendar now = getInstance();
		now.add(Calendar.MINUTE, min);
		return now.getTimeInMillis();
	}

	private static Calendar getInstance() {
		return Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
	}

	public static String getStringTime() {
		return getTime() + "";
	}

	/**
	 * @Description 封装long时间类型转为字符串
	 * @params
	 */
	public static String getFormatDate(long date, String pattern) {
		Date datetime = new Date(date);
		SimpleDateFormat dateFromat = new SimpleDateFormat(pattern);
		return dateFromat.format(datetime);
	}

	public static String getFormatDate(String pattern) {
		Calendar now = getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(now.getTime());
	}

	public static String getFormatDate(String pattern, int year) {
		Calendar now = getInstance();
		now.set(Calendar.YEAR, now.get(Calendar.YEAR) + year);
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(now.getTime());
	}

	public static String getFormatDateMonth(String pattern, int month) {
		Calendar now = getInstance();
		now.set(Calendar.MONTH, now.get(Calendar.MONTH) + month);
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(now.getTime());
	}

	public static List<String> getLastSixMonth(String pattern) {
		List<String> dates = new ArrayList<>();
		for (int i = -1; i >= -6; i--) {
			String date = getFormatDateMonth(pattern, i);
			dates.add(date);
		}
		return dates;
	}

	public static List<String> getLastSixMonth() {
		return getLastSixMonth("yyyyMM");
	}

	public static Date parseDate(String date, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		try {
			if (StringUtils.isEmpty(date)) {
				return null;
			}
			return dateFormat.parse(date);
		} catch (ParseException e) {
			log.error("系统异常:" + e.getMessage());
		}
		return null;
	}

	public static Long getDayStart() {
		Calendar calendar = getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime().getTime();
	}

	public static Long getDayEnd() {
		Calendar calendar = getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		return calendar.getTime().getTime();
	}

	public static String getDateFormatToday(String pattern) {
		Calendar now = getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(now.getTime());
	}

	public static String getDateFormatYesterDay(String pattern) {
		Calendar now = getInstance();
		now.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR) - 1);
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(now.getTime());
	}

	public static Long getDateBeforeMinutes(Long date, int minute) {
		Date temp = new Date(date);
		Calendar calendar = getInstance();
		calendar.setTime(temp);
		calendar.add(Calendar.MINUTE, -minute);
		return calendar.getTimeInMillis();
	}

	public static Long getDateAfterMinutes(Long date, int minute) {
		return getDateBeforeMinutes(date, -minute);
	}

	/**
	 * 加Long数据(秒数)转为对应的Calendar对象
	 *
	 * @param time
	 * @return
	 */
	private static Calendar getCalendarByLong(Long time) {
		Date date = new Date(time);
		Calendar now = getInstance();
		now.setTime(date);
		return now;
	}

	/**
	 * 比较两个long类型日期是否是同一天
	 *
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static boolean isTheSameDay(Long time1, Long time2) {
		Calendar day1 = getCalendarByLong(time1);
		Calendar day2 = getCalendarByLong(time2);
		int day1Year = day1.get(Calendar.YEAR);
		int day2Year = day2.get(Calendar.YEAR);
		if (day1Year != day2Year) {
			return false;
		}
		int day1Day = day1.get(Calendar.DAY_OF_YEAR);
		int day2Day = day2.get(Calendar.DAY_OF_YEAR);
		if (day1Day != day2Day) {
			return false;
		}
		return true;
	}

	/**
	 * 比较time1比time2是不是早step天
	 *
	 * @param time1
	 * @param time2
	 * @param step
	 * @return
	 */
	public static boolean compareByStep(Long time1, Long time2, int step) {
		Calendar day1 = getCalendarByLong(time1);
		Calendar day2 = getCalendarByLong(time2);
		int day1Day = day1.get(Calendar.DAY_OF_YEAR);
		int day2Day = day2.get(Calendar.DAY_OF_YEAR);
		if (day2Day - day1Day == step) {
			return true;
		}
		return false;
	}

	/**
	 * 24小时制,返回time1小时数，比time2小时数早几小时
	 *
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int compareByHour(Long time1, Long time2) {
		Calendar day1 = getCalendarByLong(time1);
		Calendar day2 = getCalendarByLong(time2);
		int hour1 = day1.get(Calendar.HOUR_OF_DAY);
		int hour2 = day2.get(Calendar.HOUR_OF_DAY);
		if (hour2 - hour1 < 0) {
			return -1;
		}
		return hour2 - hour1;
	}

	/**
	 * 60分钟制,返回time1分钟数，比time2分钟数早几分钟
	 *
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int compareByMinute(Long time1, Long time2) {
		Calendar day1 = getCalendarByLong(time1);
		Calendar day2 = getCalendarByLong(time2);
		int minute1 = day1.get(Calendar.MINUTE);
		int minute2 = day2.get(Calendar.MINUTE);
		if (minute2 - minute1 < 0) {
			return -1;
		}
		return minute2 - minute1;
	}

	/**
	 * 获取13位时间戳(字符串形式)
	 */
	public static String getTimeStamp13() {
		return System.currentTimeMillis() + "";
	}

	/**
	 * 获取10位时间戳(字符串形式)
	 */
	public static String getTimeStamp10() {
		String str = getTimeStamp13();
		return str.substring(0, str.length() - 3);
	}

	/**
	 * 将指定格式的时间转换成时间戳
	 */
	public static String getTimeStamp(String pattern, String pattternTime) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String dateStr = "";
		try {
			Date date = format.parse(pattternTime);
			dateStr = date.getTime() + "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateStr;
	}

	public static String getFormatDay(Calendar calendar, String pattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(calendar.getTime());
	}

	public static String getFirstDay(Calendar calendar, String pattern) {
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(calendar.getTime());
	}

	public static String getLastDay(Calendar calendar, String pattern) {
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		return dateFormat.format(calendar.getTime());
	}

	public static String getFormatDay(String time,String patternOld,String patternNew) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(patternOld);
		Calendar calendar=getInstance();
		try {
			calendar.setTime(dateFormat.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		dateFormat=new SimpleDateFormat(patternNew);
		return dateFormat.format(calendar.getTime());
	}

	public static String getFormatDayLast(String time,String patternOld,String patternNew) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(patternOld);
		Calendar calendar=getInstance();
		try {
			calendar.setTime(dateFormat.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		dateFormat=new SimpleDateFormat(patternNew);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return dateFormat.format(calendar.getTime());
	}

	public static void main(String[] args) {
		// long time1 = getTime();
		// long time2 = getTime();
		// int i = compareByMinute(time1, time2);
		// System.out.println(i);
		long thisTime = System.currentTimeMillis();
		System.out.println(getDMY(thisTime));
		long lastTime = getTimeSpecifyDay(thisTime, -31);
		System.out.println(getDMY(lastTime));
		thisTime = lastTime;
		lastTime = getTimeSpecifyDay(thisTime, -31);
		System.out.println(getDMY(lastTime));
		System.out.println(getTimeStamp13());
		System.out.println(getTimeStamp10());
		System.out.println();
		System.out.println(getDMY(Long.parseLong("844534800000")));
		System.out.println();
		System.out.println(getTimeStamp("dd/MM/yyyy", "06/10/1996"));

		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.msXXX");
		System.out.println(sdf.format(d));
	}

}