//package com.simsimhan.promissu.ui.detail.decorators;
//
//import android.content.Context;
//import android.text.style.TextAppearanceSpan;
//
//import com.prolificinteractive.materialcalendarview.CalendarDay;
//import com.prolificinteractive.materialcalendarview.DayViewDecorator;
//import com.prolificinteractive.materialcalendarview.DayViewFacade;
//import com.simsimhan.promissu.R;
//
//import org.threeten.bp.DayOfWeek;
//
//public class SaturdayDecorator implements DayViewDecorator {
//    private final Context context;
//
//    public SaturdayDecorator(Context context) {
//        this.context = context;
//    }
//
//
//    @Override
//    public boolean shouldDecorate(CalendarDay day) {
//        final DayOfWeek weekDay = day.getDate().getDayOfWeek();
//        return weekDay == DayOfWeek.SATURDAY;
//    }
//
//    @Override
//    public void decorate(DayViewFacade dayViewFacade) {
//        dayViewFacade.addSpan(new TextAppearanceSpan(context, R.style.saturdayTextAppearance));
//    }
//}
