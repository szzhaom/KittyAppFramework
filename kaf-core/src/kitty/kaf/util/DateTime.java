package kitty.kaf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateTime implements java.io.Serializable {
	private static final long serialVersionUID = -4450686779048588508L;

	GregorianCalendar calendar;

	public static void main(String[] a) {

	}

	public DateTime() {
		calendar = new GregorianCalendar();
	}

	public DateTime(Calendar calendar) {
		this.calendar = new GregorianCalendar();
		this.calendar.setTimeInMillis(calendar.getTimeInMillis());
	}

	public DateTime(String time, String format) throws java.text.ParseException {
		this(new SimpleDateFormat(format).parse(time));
	}

	public DateTime(long timeInMillis) {
		calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMillis);
	}

	public DateTime(Date date) {
		calendar = new GregorianCalendar();
		if (date == null)
			calendar.setTimeInMillis(0);
		else
			calendar.setTime(date);
	}

	public DateTime(int year, int month, int day) {
		calendar = new GregorianCalendar(year, month - 1, day, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	public DateTime(int year, int month, int day, int hour, int minute, int sec, int millisec) {
		calendar = new GregorianCalendar(year, month - 1, day, hour, minute, sec);
		calendar.set(Calendar.MILLISECOND, millisec);
	}

	public DateTime(int hour, int minute, int sec, int millisec) {
		calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, sec);
		calendar.set(Calendar.MILLISECOND, millisec);
	}

	public static String toDaetTimeString(Date time) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
	}

	public static String toDaetTimeString(long time) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
	}

	public String toString() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}

	public boolean after(DateTime t) {
		return calendar.getTime().after(t.getTime());
	}

	public boolean before(DateTime t) {
		return calendar.getTime().before(t.getTime());
	}

	static public Date parseDate(String date, String format) {
		if (date == null)
			return null;
		try {
			return new SimpleDateFormat(format).parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public Date getTime() {
		return calendar.getTime();
	}

	public void setTime(Date date) {
		calendar.setTime(date);
	}

	public java.sql.Date getSqlTime() {
		return new java.sql.Date(calendar.getTimeInMillis());
	}

	public DateTime copy() {
		return new DateTime(calendar.getTime());
	}

	public DateTime addYears(int years) {
		DateTime ret = copy();
		ret.calendar.add(Calendar.YEAR, years);
		return ret;
	}

	public DateTime addMonths(int months) {
		DateTime ret = copy();
		ret.calendar.add(Calendar.MONTH, months);
		return ret;
	}

	public DateTime addDays(int days) {
		DateTime ret = copy();
		ret.calendar.add(Calendar.DAY_OF_MONTH, days);
		return ret;
	}

	public DateTime addHours(int hours) {
		DateTime ret = copy();
		ret.calendar.add(Calendar.HOUR_OF_DAY, hours);
		return ret;
	}

	public DateTime addMinutes(int minutes) {
		DateTime ret = copy();
		ret.calendar.add(Calendar.MINUTE, minutes);
		return ret;
	}

	public DateTime addSeconds(int seconds) {
		DateTime ret = copy();
		ret.calendar.add(Calendar.SECOND, seconds);
		return ret;
	}

	public DateTime addMilliSeconds(int milliseconds) {
		DateTime ret = copy();
		ret.calendar.add(Calendar.MILLISECOND, milliseconds);
		return ret;
	}

	public long getTimeInMillis() {
		return calendar.getTimeInMillis();
	}

	public void setTimeInMillis(long timeInMillis) {
		calendar.setTimeInMillis(timeInMillis);
	}

	public void set(int year, int month, int day) {
		calendar.set(year, month - 1, day);
	}

	public void set(int hour, int minute, int second, int millisecond) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, millisecond);
	}

	public int getDay() {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getFullDay() {
		return Integer.parseInt(format("yyyyMMdd"));
	}

	public int getFullMonth() {
		return Integer.parseInt(format("yyyyMM"));
	}

	public DateTime setDay(int day) {
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return this;
	}

	public int getHour() {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public DateTime setHour(int hour) {
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		return this;
	}

	public int getMilliSecond() {
		return calendar.get(Calendar.MILLISECOND);
	}

	public void setMilliSecond(int milliSecond) {
		calendar.set(Calendar.MILLISECOND, milliSecond);
	}

	public int getMinute() {
		return calendar.get(Calendar.MINUTE);
	}

	public DateTime setMinute(int minute) {
		calendar.set(Calendar.MINUTE, minute);
		return this;
	}

	public int getMonth() {
		return calendar.get(Calendar.MONTH) + 1;
	}

	public DateTime setMonth(int month) {
		calendar.set(Calendar.MONTH, month - 1);
		return this;
	}

	public int getSecond() {
		return calendar.get(Calendar.SECOND);
	}

	public DateTime setSecond(int second) {
		calendar.set(Calendar.SECOND, second);
		return this;
	}

	public int getYear() {
		return calendar.get(Calendar.YEAR);
	}

	public boolean isLeapYear() {
		return isLeapYear(getYear());
	}

	static int[] m = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

	/**
	 * 1秒包含的毫秒数
	 */
	static final long SECOND_MILLISECONDS = 1000;
	/**
	 * 1分钟包含的毫秒数
	 */
	static final long MINUTE_MILLISECONDS = SECOND_MILLISECONDS * 60;
	/**
	 * 1小时包含的毫秒数
	 */
	static final long HOUR_MILLISECONDS = MINUTE_MILLISECONDS * 60;
	/**
	 * 1日包含的毫秒数
	 */
	static final long DAY_MILLISECONDS = HOUR_MILLISECONDS * 24;
	/**
	 * 1周包含的毫秒数
	 */
	static final long WEEK_MILLISECONDS = DAY_MILLISECONDS * 7;
	/**
	 * 1月包含的毫秒数
	 */
	static final double MONTH_MILLISECONDS = DAY_MILLISECONDS * 30.4375;
	/**
	 * 1年包含的毫秒数
	 */
	static final double YEAR_MILLISECONDS = DAY_MILLISECONDS * 365.25;

	static public double milliSecondsBetween(long src, long dst) {
		return Math.abs(src - dst);
	}

	static public double secondsBetween(long src, long dst) {
		return milliSecondsBetween(src, dst) / SECOND_MILLISECONDS;
	}

	static public double minutesBetween(long src, long dst) {
		return milliSecondsBetween(src, dst) / MINUTE_MILLISECONDS;
	}

	static public double hoursBetween(long src, long dst) {
		return milliSecondsBetween(src, dst) / HOUR_MILLISECONDS;
	}

	static public double daysBetween(long src, long dst) {
		return milliSecondsBetween(src, dst) / DAY_MILLISECONDS;
	}

	static public double weeksBetween(long src, long dst) {
		return milliSecondsBetween(src, dst) / WEEK_MILLISECONDS;
	}

	static public double monthsBetween(long src, long dst) {
		return (milliSecondsBetween(src, dst) / MONTH_MILLISECONDS);
	}

	static public double yearsBetween(long src, long dst) {
		return (milliSecondsBetween(src, dst) / HOUR_MILLISECONDS);
	}

	static public double milliSecondsBetween(Date src, Date dst) {
		return milliSecondsBetween(src.getTime(), dst.getTime());
	}

	static public double secondsBetween(Date src, Date dst) {
		return secondsBetween(src.getTime(), dst.getTime());
	}

	static public double minutesBetween(Date src, Date dst) {
		return minutesBetween(src.getTime(), dst.getTime());
	}

	static public double hoursBetween(Date src, Date dst) {
		return hoursBetween(src.getTime(), dst.getTime());
	}

	static public double daysBetween(Date src, Date dst) {
		return daysBetween(src.getTime(), dst.getTime());
	}

	static public double weeksBetween(Date src, Date dst) {
		return weeksBetween(src.getTime(), dst.getTime());
	}

	static public double monthsBetween(Date src, Date dst) {
		return monthsBetween(src.getTime(), dst.getTime());
	}

	static public double yearsBetween(Date src, Date dst) {
		return yearsBetween(src.getTime(), dst.getTime());
	}

	public int getDaysInMonth() {
		return getDaysInMonth(getYear(), getMonth());
	}

	public int getDaysInYear() {
		if (isLeapYear())
			return 366;
		else
			return 365;
	}

	public DateTime setYear(int year) {
		calendar.set(Calendar.YEAR, year);
		return this;
	}

	public String format(String format) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(this.getTime());
	}

	public double milliSecondsBetween(DateTime t) {
		return milliSecondsBetween(this.getTime(), t.getTime());
	}

	public double secondsBetween(DateTime t) {
		return secondsBetween(this.getTime(), t.getTime());
	}

	public double minutesBetween(DateTime t) {
		return minutesBetween(this.getTime(), t.getTime());
	}

	public double hoursBetween(DateTime t) {
		return hoursBetween(this.getTime(), t.getTime());
	}

	public double daysBetween(DateTime t) {
		return daysBetween(this.getTime(), t.getTime());
	}

	/**
	 * 获取天数（忽略时间参数，只计日期）
	 * 
	 * @param t
	 *            另一时间
	 * @return 天数
	 */
	public long daysBetweenIngoreTime(DateTime t) {
		DateTime n = new DateTime(getYear(), getMonth(), getDay());
		DateTime n1 = new DateTime(t.getYear(), t.getMonth(), t.getDay());
		return (long) n.daysBetween(n1);
	}

	public double monthsBetween(DateTime t) {
		return monthsBetween(this.getTime(), t.getTime());
	}

	public double yearsBetween(DateTime t) {
		return yearsBetween(this.getTime(), t.getTime());
	}

	static public boolean isLeapYear(int year) {
		return (year % 4 == 0) && (((year % 100) != 0) || ((year % 400) == 0));
	}

	static public int getDaysInMonth(int year, int month) {
		if (month == 2 && isLeapYear(year))
			return 29;
		return m[month - 1];
	}

	static public int getDaysInYear(int year) {
		if (isLeapYear(year))
			return 365;
		else
			return 366;
	}

	/**
	 * 分钟数转换为时分秒
	 * 
	 * @param time
	 * @return
	 */
	static public String minuteToDHMS(long time) {
		if (time == 0)
			return "0分钟";
		long min = time / 60;
		if (min == 0)
			min = 1;
		if (min < 60)
			return min + "分钟";
		String ret = "";
		long h = min / 60;
		min %= 60;
		long d = h / 24;
		h %= 24;
		if (d > 0) {
			ret = d + "天";
			if (h > 0) {
				ret += h + "小时";
			} else if (min > 0)
				ret += "零" + min + "分钟";
		} else {
			if (h > 0) {
				ret = h + "小时";
				if (min > 0)
					ret += min + "分钟";
			} else
				ret = min + "分钟";
		}
		return ret;
	}

	public boolean isSameDay(DateTime secondTime) {
		return getYear() == secondTime.getYear() && getMonth() == secondTime.getMonth()
				&& getDay() == secondTime.getDay();
	}

	/**
	 * 检查目标日期是否是当前日期的前一天
	 * 
	 * @param yesterday
	 *            前一天
	 * @return true 是前一天 false 不是前一天
	 */
	public boolean isYesterday(DateTime yesterday) {
		if (yesterday.before(this)) {
			return daysBetweenIngoreTime(yesterday) == 1;
		} else
			return false;
	}

	public boolean isSameMinute(DateTime secondTime) {
		return getYear() == secondTime.getYear() && getMonth() == secondTime.getMonth()
				&& getDay() == secondTime.getDay() && getHour() == secondTime.getHour()
				&& getMinute() == secondTime.getMinute();
	}
}
