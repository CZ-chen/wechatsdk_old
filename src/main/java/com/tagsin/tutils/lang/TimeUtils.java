package com.tagsin.tutils.lang;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimeUtils {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("yyMMdd");
    
    public static final long MIN_MILLIS = 60000;
    public static final long HOUR_MILLIS = 60*MIN_MILLIS;
    public static final long DAY_MILLIS = 24*HOUR_MILLIS;
    public static final int HOUR_SECONDS = 60*60;
    public static final int DAY_SECONDS = HOUR_SECONDS*24;
    
    private static String dateStr;
    private static String shortDateStr;
    private static int hour;
    private static int min;
    private static long todayBegin;

    private TimeUtils() {
    }

    static {
        calculate();
        Timer syncADTimer = new Timer("SYNC_AD_Timer");
        syncADTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                calculate();
            }
        }, 0, 1000);
    }

    private synchronized static void calculate() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        dateStr = DATE_FORMAT.format(c.getTime());
        shortDateStr = SHORT_DATE_FORMAT.format(c.getTime());
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        todayBegin = c.getTimeInMillis();
    }

    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_PATTERN = "yyyyMMdd";

    public final static String MIN_PATTERN = "HHmm";
    public final static String HOUR_PATTERN = "HH";
	

    public static String dateToString(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        } else {
            return null;
        }
    }

    public static Date stringToDate(String dateStr, String pattern) throws ParseException {
        if (dateStr != null) {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.parse(dateStr);
        } else {
            return null;
        }
    }

	public static String getDateStr() {
		return dateStr;
	}

	public static void setDateStr(String dateStr) {
		TimeUtils.dateStr = dateStr;
	}

	public static String getShortDateStr() {
		return shortDateStr;
	}

	public static void setShortDateStr(String shortDateStr) {
		TimeUtils.shortDateStr = shortDateStr;
	}

	public static int getHour() {
		return hour;
	}

	public static void setHour(int hour) {
		TimeUtils.hour = hour;
	}

	public static int getMin() {
		return min;
	}

	public static void setMin(int min) {
		TimeUtils.min = min;
	}

	public static long getTodayBegin() {
		return todayBegin;
	}

	public static void setTodayBegin(long todayBegin) {
		TimeUtils.todayBegin = todayBegin;
	}
    
}