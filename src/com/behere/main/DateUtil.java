package com.behere.main; 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
 
/**
 * ��¥ ��� ���� ��ɵ��� ��Ƴ��� ��ƿ��Ƽ Ŭ����
 * 
 * ������ 4���� ���Ŀ� ���Ͽ� ��ȣ ��ȯ �� ���� �ð��� ��ȯ�Ѵ�.
 * SimpleString - yyyy/MM/dd
 * UserTypedString - ??
 * Calendar
 * TimeValue
 * 
 * yyyy-MM-dd HH:mm:ss
 * 
 * @author Claude
 * @see Original From Croute, http://croute.me/397
 * 
 */
public class DateUtil
{
	private static String[] dayString = {"��","��","ȭ","��","��","��","��"};
	
	//
	// Current Time(4)
	//
	public static String getSimpleString() {
		return getUserTypedString("yyyy/MM/dd");
	}
	public static String getUserTypedString(String format) {
		SimpleDateFormat sdfFormatter = new SimpleDateFormat(format);
		return sdfFormatter.format(Calendar.getInstance().getTime());
	}
	public static long getTimeValue() {
		return (Calendar.getInstance().getTimeInMillis());
	}
	public static Calendar getCalendar() {
		return (Calendar.getInstance());
	}
	
	
	public static String convUserTypedStringToDay(String date, String format) {
		Calendar cal = Calendar.getInstance();

		try {
			SimpleDateFormat sdfFormatter = new SimpleDateFormat(format);
			cal.setTime(sdfFormatter.parse(date));
		}
		catch(ParseException e) {
			e.printStackTrace();
		}
		
		return dayString[cal.get(Calendar.DAY_OF_WEEK)-1];
	}
	
	//
	// Convert into String(4)
	//
	public static String convTimeValueToSimpleString(long timeValue) {
		return convTimeValueToUserTypedString(timeValue, "yyyy/MM/dd");
	}
	public static String convTimeValueToUserTypedString(long timeValue, String format) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeValue);
		return convCalendarToUserTypedString(cal, format);
	}
	public static String convCalendarToSimpleString(Calendar cal) {
		return convCalendarToUserTypedString(cal, "yyyy/MM/dd");
	}
	public static String convCalendarToUserTypedString(Calendar cal, String format) {
		SimpleDateFormat sdfFormatter = new SimpleDateFormat(format);
		return sdfFormatter.format(cal.getTime());
	}
	
	//
	// Convert into Types(4)
	//
	public static long convSimpleStringToTimeValue(String date) {
        return convUserTypedStringToTimeValue(date, "yyyy/MM/dd");
	}
	public static Calendar convSimpleStringToCalendar(String date) {
		return convUserTypedStringToCalendar(date, "yyyy/MM/dd");
	}
	public static long convUserTypedStringToTimeValue(String date, String format) {
		Calendar cal = Calendar.getInstance();
        
        try
        {
        	SimpleDateFormat sdfFormatter = new SimpleDateFormat(format);
            cal.setTime(sdfFormatter.parse(date));
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        return cal.getTimeInMillis();
	}
	public static Calendar convUserTypedStringToCalendar(String date, String format) {
		Calendar cal = Calendar.getInstance();
        
        try
        {
        	SimpleDateFormat sdfFormatter = new SimpleDateFormat(format);
            cal.setTime(sdfFormatter.parse(date));
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }
        return cal;
	}
	
	/**
	 * �־��� Milliseconds �紵�� �ð��������� ������� ���ʰ� �������� ����Ѵ�.
	 * 
	 * @param timeValue
	 * @return
	 */
	public static long getSecondsPassedFrom(long timeValue) {
		return ((getTimeValue() - timeValue) / 1000);
	}
}
