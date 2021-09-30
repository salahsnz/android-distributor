package com.zopnote.android.merchant.managesubscription;

import android.content.Context;

import com.zopnote.android.merchant.R;
import com.zopnote.android.merchant.data.model.Pause;
import com.zopnote.android.merchant.data.model.StatusEnum;
import com.zopnote.android.merchant.util.FormatUtil;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class SubscriptionUtil {

    public static boolean isOverlappingPause(Calendar pauseStartDateCalender,
                                             Calendar pauseEndDateCalender,
                                             List<Pause> pauses, String pauseId) {
        if(pauses == null){
            return false;
        }

        if (pauses.isEmpty()) {
            return false;
        }

        Calendar startDateCalender = FormatUtil.getLocalCalender();
        startDateCalender.setTime(pauseStartDateCalender.getTime());

        Calendar endDateCalender = FormatUtil.getLocalCalender();
        if (pauseEndDateCalender != null) {
            endDateCalender.setTime(pauseEndDateCalender.getTime());
        }else {
            //hack to simply processing logic for indefinite pause
            endDateCalender.set(2099, 11, 31, 0, 0, 0);
        }

        for (Pause pause : pauses) {

            if (pause.getPauseStatus().name().equalsIgnoreCase(StatusEnum.INACTIVE.name())) {
                //ignore inactive pauses
                continue;
            }

            if(pauseId != null && pause.getId().equals(pauseId)){
                //for update pause, don't consider self
                continue;
            }
            //get start date
            Date existingPauseStartDate = pause.getPauseStartDate();
            Calendar existingPauseStartDateCalender = FormatUtil.getLocalCalender();
            existingPauseStartDateCalender.setTime(existingPauseStartDate);

            //get end date
            Date existingPauseEndDate = pause.getPauseEndDate();
            Calendar existingPauseEndDateCalender = FormatUtil.getLocalCalender();
            if (existingPauseEndDate != null) {
                existingPauseEndDateCalender.setTime(existingPauseEndDate);
            } else {
                //hack to simply processing logic for indefinite pause
                existingPauseEndDateCalender.set(2099, 11, 31, 0, 0, 0);
            }

            //return when you find one overlapping case
            if (startDateCalender.getTime().compareTo(existingPauseStartDateCalender.getTime()) < 0 &&
                    endDateCalender.getTime().compareTo(existingPauseStartDateCalender.getTime()) < 0) {
                //start & end < existing start

            } else if (startDateCalender.getTime().compareTo(existingPauseEndDateCalender.getTime()) > 0 &&
                    endDateCalender.getTime().compareTo(existingPauseEndDateCalender.getTime()) > 0) {
                //start & end > existing end
            } else {
                return true;
            }
        }
        return false;
    }


    public static String getPauseDurationText(Pause pause, Context context) {
        String startDate;
        String pauseDurationText;

        if(pause.getPauseEndDate() != null){
            boolean datesInSameMonth;
            boolean datesInSameYear;

            Calendar pauseStartDateCalendar = FormatUtil.getLocalCalender();
            pauseStartDateCalendar.setTime(pause.getPauseStartDate());

            Calendar pauseEndDateCalender = FormatUtil.getLocalCalender();
            pauseEndDateCalender.setTime(pause.getPauseEndDate());

            if(pause.getPauseStartDate().compareTo(pause.getPauseEndDate()) == 0){
                //one day pause
                startDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, pause.getPauseStartDate());
                pauseDurationText = context.getResources().getString(R.string.single_day_pause_duration_label, startDate);
            }else {
                if(pauseStartDateCalendar.get(Calendar.MONTH) == pauseEndDateCalender.get(Calendar.MONTH)){
                    datesInSameMonth = true;
                }else{
                    datesInSameMonth = false;
                }

                if(pauseStartDateCalendar.get(Calendar.YEAR) == pauseEndDateCalender.get(Calendar.YEAR)){
                    datesInSameYear = true;
                }else{
                    datesInSameYear = false;
                }

                if( datesInSameMonth && datesInSameYear){
                    //day
                    startDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DAY, pause.getPauseStartDate());
                }else if( ! datesInSameMonth && datesInSameYear){
                    //day and month
                    startDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DM, pause.getPauseStartDate());
                }else{
                    //day, month, year
                    startDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, pause.getPauseStartDate());
                }

                String endDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, pause.getPauseEndDate());
                pauseDurationText = context.getResources().getString(R.string.pause_duration_label, startDate,endDate);
            }

        }else{
            //indefinite pause
            startDate = FormatUtil.formatLocalDate(FormatUtil.DATE_FORMAT_DMY, pause.getPauseStartDate());
            pauseDurationText = context.getResources().getString(R.string.indefinite_pause_duration_label, startDate);
        }
        return pauseDurationText;
    }

    public static boolean isDateInCurrentMonth(Date date) {
        Calendar givenDateCalender = FormatUtil.getLocalCalender();
        givenDateCalender.setTime(date);

        Calendar localCalender = FormatUtil.getLocalCalender();
        localCalender.setTime(new Date());

        if (givenDateCalender.get(Calendar.YEAR) == localCalender.get(Calendar.YEAR)) {
            if (givenDateCalender.get(Calendar.MONTH) == localCalender.get(Calendar.MONTH)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDateInUpcomingMonth(Date date) {
        Calendar givenDateCalender = FormatUtil.getLocalCalender();
        givenDateCalender.setTime(date);

        Calendar calender = FormatUtil.getLocalCalender();
        calender.setTime(new Date());

        if(date.after(calender.getTime())){
            return true;
        }

        return false;
    }

    public static void sortPausesAscending(List<Pause> pauses){
        if(pauses != null){
            Collections.sort(pauses, new Comparator<Pause>() {
                @Override
                public int compare(Pause o1, Pause o2) {
                    return o1.getPauseStartDate().compareTo(o2.getPauseStartDate());
                }
            });
        }
    }
}
