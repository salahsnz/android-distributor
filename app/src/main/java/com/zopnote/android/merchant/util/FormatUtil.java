package com.zopnote.android.merchant.util;

import android.content.Context;
import android.net.ParseException;
import android.text.format.DateUtils;

import com.zopnote.android.merchant.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by nmohideen on 15/02/18.
 */

public class FormatUtil {
    private static final boolean DEBUG = false;

    private static TimeZone LOCAL_TIMEZONE = TimeZone.getTimeZone("Asia/Calcutta");

    public static final DecimalFormat AMOUNT_FORMAT = new DecimalFormat("#,##0.##");
    public static final DecimalFormat AMOUNT_FORMAT_WITH_ZERO_DECIMALS = new DecimalFormat("#,##0.00");

    public static final SimpleDateFormat DATE_FORMAT_DMY = new SimpleDateFormat("d MMM yyyy");
    public static final SimpleDateFormat DATE_FORMAT_DMMY = new SimpleDateFormat("d MMMM yyyy");
    public static final SimpleDateFormat DATE_FORMAT_MMMM = new SimpleDateFormat("MMMM");
    public static final SimpleDateFormat DATE_FORMAT_DAY = new SimpleDateFormat("d");
    public static final SimpleDateFormat DATE_FORMAT_DM = new SimpleDateFormat("d MMMM");
    public static final SimpleDateFormat DATE_FORMAT_DMMM = new SimpleDateFormat("d MMM");
    public static final SimpleDateFormat DATE_FORMAT_DMMY_WITH_SEPARATOR = new SimpleDateFormat("d/MM/yyyy");
    public static final SimpleDateFormat DATE_FORMAT_DMMMY_HH_MM = new SimpleDateFormat("d MMM yyyy hh:mm a");
    public static final SimpleDateFormat DATE_FORMAT_DMMM_HH_MM = new SimpleDateFormat("d MMM hh:mm a");
    public static final SimpleDateFormat DATE_FORMAT_DMMM_HH_MM_SS = new SimpleDateFormat("d MMM hh:mm:ss a");
    public static final SimpleDateFormat DATE_FORMAT_D_MMM = new SimpleDateFormat("d-MMM");
    public static final SimpleDateFormat DATE_FORMAT_YYY_MM_DD_T_HH_MM_SS_SSS_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final SimpleDateFormat DATE_FORMAT_MDY = new SimpleDateFormat("MMM d, yyyy");
    public static final SimpleDateFormat TIME_FORMAT_COMPACT = new SimpleDateFormat("hh:mm a");
    public static final SimpleDateFormat DATE_FORMAT_MMM_D = new SimpleDateFormat("MMM d");
    public static final SimpleDateFormat DATE_FORMAT_MMM = new SimpleDateFormat("MMM");

    public static final String getRupeePrefixedAmount(Context context, Double amount, DecimalFormat formatter) {
        return context.getResources().getString(
                R.string.amount_with_rupee_prefix,
                formatter.format(amount)
                );
    }

    public static String formatLocalDate(SimpleDateFormat dateFormat, Date date) {
        dateFormat.setTimeZone(LOCAL_TIMEZONE);
        return dateFormat.format(date);
    }

    public static Calendar getLocalCalender(){
        Calendar calendar = Calendar.getInstance(LOCAL_TIMEZONE);
        return calendar;
    }

    public static Calendar getLocalCalenderNoTime() {
        Calendar calendar = Calendar.getInstance(LOCAL_TIMEZONE);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    public static String formatTimeAgo(long time){
        CharSequence ago = "";
        DATE_FORMAT_YYY_MM_DD_T_HH_MM_SS_SSS_Z.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {

            long now = System.currentTimeMillis();
             ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return ago.toString();
    }

    public static String getDayOfWeekDisplayName(int dayOfWeek){
        Calendar calendar = getLocalCalender();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
        return dayName;
    }
}
