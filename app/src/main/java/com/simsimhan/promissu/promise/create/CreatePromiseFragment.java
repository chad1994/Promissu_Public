package com.simsimhan.promissu.promise.create;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.map.MapSearchActivity;
import com.simsimhan.promissu.util.StringUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static com.simsimhan.promissu.util.NavigationUtil.REQUEST_MAP_SEARCH;

public class CreatePromiseFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private int pageKey;
    private String username;
    private DateTime now;

    private TextInputEditText dateEditText;
    private TextInputEditText timeEditText;
    private TextInputEditText promisePlace;
    private RadioButton radioButtonFirst;
    private RadioButton radioButtonSecond;
    private RadioButton radioButtonThird;
    private TextView createPromiseTimer;
    private TextInputEditText promiseTitle;

    private DateTime selectedDate;
    private DateTime selectedDateTime;
    private int waitTime = 0;
    private double x;
    private double y;
    private String locationText;


    public static Fragment newInstance(int position, String title) {
        CreatePromiseFragment fragment = new CreatePromiseFragment();
        Bundle args = new Bundle();
        args.putInt("Page_key", position);
        args.putString("Title_key", title);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            // get arguments and set here
            pageKey = getArguments().getInt("Page_key");
        }

        username = PromissuApplication.getDiskCache().getUserName();
        now = new DateTime();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;

        if (pageKey == 0) {
            view = inflater.inflate(R.layout.fragment_create_promise_1, container, false);
            TextView question = view.findViewById(R.id.create_question_text);
            question.setText(Html.fromHtml(getString(R.string.create_question_1, username)));

            promiseTitle = view.findViewById(R.id.promise_title_edit_text);
        } else if (pageKey == 1) {
            view = inflater.inflate(R.layout.fragment_create_promise_2, container, false);
            TextView question = view.findViewById(R.id.create_question_text);
            question.setText(Html.fromHtml(getString(R.string.create_question_2)));

            dateEditText = view.findViewById(R.id.promise_date);
            dateEditText.setOnClickListener(v -> {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        CreatePromiseFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));


                datePickerDialog.showYearPickerFirst(true);
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            });

            timeEditText = view.findViewById(R.id.promise_time_edit_text);
            timeEditText.setOnClickListener(v -> {
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(CreatePromiseFragment.this,
                        now.getHourOfDay(),
                        now.getMinuteOfHour(),
                        true
                );

                timePickerDialog.show(getFragmentManager(), "TimePickerDialog");
            });

            promisePlace = view.findViewById(R.id.promise_location_edit_text);
            promisePlace.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MapSearchActivity.class);
                startActivityForResult(intent, REQUEST_MAP_SEARCH);
            });
        } else {
            view = inflater.inflate(R.layout.fragment_create_promise_3, container, false);
            TextView question = view.findViewById(R.id.create_question_text);
            question.setText(getString(R.string.create_question_3));
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);
            radioButtonFirst = view.findViewById(R.id.rg_btn1);
            radioButtonSecond = view.findViewById(R.id.rg_btn2);
            radioButtonThird = view.findViewById(R.id.rg_btn3);
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                Activity activity = getActivity();
                if (activity == null
                        || radioButtonFirst == null
                        || radioButtonSecond == null
                        || radioButtonThird == null) return;

                radioButtonFirst.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                radioButtonSecond.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
                radioButtonThird.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));

                if (checkedId != -1) {
                    RadioButton radioButton = group.findViewById(checkedId);
                    radioButton.setTextColor(ContextCompat.getColor(activity, R.color.white));

                    setPromiseTime(checkedId);
                }

            });

            AppCompatButton createPromiseButton = view.findViewById(R.id.create_promise_button);
            createPromiseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreatePromiseActivity activity = ((CreatePromiseActivity)getActivity());
                    activity.createPromise();
                }
            });
            createPromiseTimer = view.findViewById(R.id.create_question_timer);
            addFromCurrentTime(0);

        }
        return view;
    }

    private void setPromiseTime(final int checkedId) {
        final int first = radioButtonFirst.getId();
        final int second = radioButtonSecond.getId();
        final int third = radioButtonThird.getId();

        if (checkedId == first) {
            addFromCurrentTime(60);
        } else if (checkedId == second) {
            addFromCurrentTime(120);
        } else if (checkedId == third) {
            addFromCurrentTime(480);
        } else {
            Toast.makeText(getContext(), "대기 시간을 선택해주세요!", Toast.LENGTH_LONG).show();
        }
    }

    private void addFromCurrentTime(int addingTime) {
        if (createPromiseTimer != null) {
            waitTime = addingTime;
            DateTime newDate = now.plusMinutes(addingTime);
            createPromiseTimer.setText(newDate.getHourOfDay() + ":" + newDate.getMinuteOfHour());
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MAP_SEARCH) {
            if (resultCode == RESULT_OK && data != null) {
                String name = data.getStringExtra("selected_name");
                String address = data.getStringExtra("selected_address");
                x = data.getDoubleExtra("selected_x", 0);
                y = data.getDoubleExtra("selected_y", 0);

                setPromisePlace(address + " (" + name + ")");
            } else {
                Toast.makeText(getContext(), "약속 장소를 선택해주세요.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setPromisePlace(String placeText) {
        Timber.d("setPromisePlace(): " + placeText);

        if (promisePlace != null) {
            this.locationText = placeText;
            promisePlace.setText(placeText);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = now.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth);
        // get date shit shit and set
        if (dateEditText != null) {
            dateEditText.setText(year + " " + (monthOfYear + 1) + " " + dayOfMonth);
        }
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        selectedDateTime = now.withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute);

        if (timeEditText != null) {
            timeEditText.setText(StringUtil.addPaddingIfSingleDigit(hourOfDay) + ":" + StringUtil.addPaddingIfSingleDigit(minute));
        }


        // get time shit and set
    }

    String getTitle() {
        if (promiseTitle != null) {
            return String.valueOf(promiseTitle.getText());
        }

        return "";
    }

    DateTime getStartTime() {
        if (selectedDate == null || selectedDateTime == null) return null;

        return selectedDate.withHourOfDay(selectedDateTime.getHourOfDay()).withMinuteOfHour(selectedDateTime.getMinuteOfHour());
    }

    double getLocationX() {
        return x;
    }

    double getLocationY() {
        return y;
    }

    String getLocationText() {
        return locationText;
    }

    int getWaitTime() {
        return waitTime;
    }
}
