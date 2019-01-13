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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CreatePromiseFragment extends Fragment {
    private int pageKey;
    private String username;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (pageKey == 0) {
            view = inflater.inflate(R.layout.fragment_create_promise_1, container, false);
            TextView question = view.findViewById(R.id.create_question_text);
            question.setText(Html.fromHtml(getString(R.string.create_question_1, username)));

//            TextInputEditText inputEditText = view.findViewById(R.id.promise_title_layout);
//            EditText editText = view.findViewById(R.id.promise_title_edit_text);
        } else if (pageKey == 1)  {
            view = inflater.inflate(R.layout.fragment_create_promise_2, container, false);
            TextView question = view.findViewById(R.id.create_question_text);
            question.setText(Html.fromHtml(getString(R.string.create_question_2)));

//            TextInputEditText inputEditText = view.findViewById(R.id.promise_title_layout);
//            EditText editText = view.findViewById(R.id.promise_title_edit_text);
        } else {
            view = inflater.inflate(R.layout.fragment_create_promise_3, container, false);
            TextView question = view.findViewById(R.id.create_question_text);
            question.setText(Html.fromHtml(getString(R.string.create_question_1, username)));

            TextInputEditText inputEditText = view.findViewById(R.id.promise_title_layout);
            EditText editText = view.findViewById(R.id.promise_title_edit_text);
        }
        return view;
    }
}
