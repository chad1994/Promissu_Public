package com.simsimhan.promissu.ui.detail.decorators;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.simsimhan.promissu.R;

import org.joda.time.DateTime;

import androidx.core.content.ContextCompat;

public class DisabledDayDecorator implements DayViewDecorator {
    private final DateTime startDate;
    private final DateTime endDate;
    private final Context context;

    public DisabledDayDecorator(DateTime startDate, DateTime endDate, Context context) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay date) {
        return !date.isInRange(
                CalendarDay.from(startDate.getYear(), startDate.getMonthOfYear(), startDate.getDayOfMonth()),
                CalendarDay.from(endDate.getYear(), endDate.getMonthOfYear(), endDate.getDayOfMonth()));
    }

    @Override
    public void decorate(DayViewFacade view) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_calendar_round_disabled);
        if (background != null) {
            view.setSelectionDrawable(background);
            view.setDaysDisabled(true);
        }
    }
}
