package com.simsimhan.promissu.promise.create;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.simsimhan.promissu.PromissuApplication;
import com.simsimhan.promissu.R;
import com.simsimhan.promissu.cache.DiskCache;
import com.simsimhan.promissu.util.NavigationUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

public class CreatePromiseFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private int pageKey;
    private String username;
    private DateTime now;

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

    private TextInputEditText dateEditText;
    private TextInputEditText timeEditText;
    private TextInputEditText promisePlace;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (pageKey == 0) {
            view = inflater.inflate(R.layout.fragment_create_promise_1, container, false);
            TextView question = view.findViewById(R.id.create_question_text);
            question.setText(Html.fromHtml(getString(R.string.create_question_1, username)));

//            TextInputEditText inputEditText = view.findViewById(R.id.promise_title_layout);
//            EditText editText = view.findViewById(R.id.promise_title_edit_text);
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
            promisePlace.setOnClickListener(v -> NavigationUtil.openMapScreen(getActivity()));
        } else {
            view = inflater.inflate(R.layout.fragment_create_promise_3, container, false);
            TextView question = view.findViewById(R.id.create_question_text);
            question.setText(Html.fromHtml(getString(R.string.create_question_1, username)));

            TextInputEditText inputEditText = view.findViewById(R.id.promise_title_layout);
            EditText editText = view.findViewById(R.id.promise_title_edit_text);
        }
        return view;
    }

    public void setPromisePlace(String placeText) {
        Timber.d("setPromisePlace(): " + placeText + " " + promisePlace);

        if (promisePlace != null) {
            promisePlace.setText(placeText);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        // get date shit shit and set
        if (dateEditText != null) {
            dateEditText.setText(year + " " + (monthOfYear + 1) + " " + dayOfMonth);
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        DateTime selectedTime = now.withHourOfDay(hourOfDay)
                .withMinuteOfHour(minute);

        if (timeEditText != null) {
            timeEditText.setText(hourOfDay + ":" + minute);
        }


        // get time shit and set
    }
}
