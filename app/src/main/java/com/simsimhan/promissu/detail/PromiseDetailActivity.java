package com.simsimhan.promissu.detail;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.simsimhan.promissu.BuildConfig;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.network.model.Promise;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PromiseDetailActivity extends AppCompatActivity implements MapView.MapViewEventListener, MapView.POIItemEventListener {
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "PromiseDetailActivity";

    // Decorators
//    private SelectedDayDecorator selectedDayDecorator;
//    private SundayDecorator sundayDecorator;
//    private SaturdayDecorator saturdayDecorator;
//    private DisabledDayDecorator disabledDayDecorator;
//    private EnabledDayDecorator enabledDayDecorator;
    private DateTime startDate, endDate, today;
//    private MaterialCalendarView materialCalendarView;
    private Toolbar toolbar;
    private Promise.Response promise;
    private TextView promiseDateLeft;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_promise);

        promise = getIntent().getParcelableExtra("promise");

//        materialCalendarView = findViewById(R.id.calendarView);
//        materialCalendarView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
//        materialCalendarView.setTopbarVisible(false);
//        materialCalendarView.setOnDateChangedListener((widget, date, selected) -> {
//            selectedDayDecorator.updateSelectedDate(date);
//            materialCalendarView.invalidateDecorators();
//            // update selected Date
////            updateCurrentDay(date);
//        });
//        selectedDayDecorator = new SelectedDayDecorator(null);
//        sundayDecorator = new SundayDecorator(this);
//        saturdayDecorator = new SaturdayDecorator(this);

        today = new DateTime();
        startDate = new DateTime(promise.getStartTime());
        endDate = new DateTime(promise.getEndTime());

//        materialCalendarView.state().edit()
//                .setMinimumDate(getStartDateMinusWeek())
//                .setMaximumDate(getEndDatePlusWeek())
//                .commit();
//
//
//        materialCalendarView.addDecorators(
//                selectedDayDecorator,
//                sundayDecorator,
//                saturdayDecorator
//        );


//        disabledDayDecorator = new DisabledDayDecorator(startDate, endDate, PromiseDetailActivity.this);
//        enabledDayDecorator = new EnabledDayDecorator(startDate, endDate, PromiseDetailActivity.this);
//        materialCalendarView.addDecorators(disabledDayDecorator, enabledDayDecorator);
//        materialCalendarView.setDateSelected(getCalendarDate(today), true);

        MapView mapView = new MapView(this);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.frame);
        mapViewContainer.addView(mapView);

        toolbar = findViewById(R.id.toolbar);
        promiseDateLeft = findViewById(R.id.promise_date_left);
        Days daysBetween = Days.daysBetween(today, startDate);
        promiseDateLeft.setText(daysBetween.getDays() + "일 남았어요!");

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(promise.getTitle());

        TextView promiseTime = findViewById(R.id.promise_time);
        promiseTime.setText(startDate.getHourOfDay() + ":" + startDate.getMinuteOfHour());

        TextView promiseLocation = findViewById(R.id.promise_location);
        promiseLocation.setText(promise.getLocation() + " 좌표: (" + promise.getLocation_lat() + " " + promise.getLocation_lon() + ")");

        TextView promiseMembers = findViewById(R.id.promise_user);
        promiseMembers.setText(String.valueOf(promise.getParticipants()));
    }

    private CalendarDay getCalendarDate(DateTime dateTime) {
        return CalendarDay.from(dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        materialCalendarView.removeDecorators();
    }

    private CalendarDay getStartDateMinusWeek() {
        DateTime startDateMinusWeek = startDate.minusWeeks(1);
        return CalendarDay.from(startDateMinusWeek.getYear(), startDateMinusWeek.getMonthOfYear(), startDateMinusWeek.getDayOfMonth());
    }

    public CalendarDay getEndDatePlusWeek() {
        DateTime endDatePlusWeek = endDate.plusWeeks(1);
        return CalendarDay.from(endDatePlusWeek.getYear(), endDatePlusWeek.getMonthOfYear(), endDatePlusWeek.getDayOfMonth() + 7);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}
