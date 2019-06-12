package com.simsimhan.promissu.ui.detail.decorators;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class SelectedDayDecorator implements DayViewDecorator {
    //    private final int color;
    private CalendarDay daySelected;

    public SelectedDayDecorator(CalendarDay daySelected) {
        this.daySelected = daySelected;
//        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day != null && daySelected != null && day.equals(daySelected);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
//        view.addSpan(new ForegroundColorSpan(color));
    }

    public void updateSelectedDate(CalendarDay date) {
        daySelected = date;
    }
}