package com.simsimhan.promissu.promise.create

import android.text.Editable
import android.text.TextWatcher
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

//@BindingAdapter("setSearchView")
//fun setSearchView(searchView: MaterialSearchView, viewModel:CreateViewModel){
//    searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
//        override fun onQueryTextSubmit(query: String): Boolean {
//            viewModel.getRecommendedSearchItems(query)
//            return true
//        }
//
//        override fun onQueryTextChange(newText: String): Boolean {
//            if (suggestionAdapter.getCount() > 0) {
//                suggestionAdapter.replaceAll(ArrayList<LocationSearchItem>())
//                return true
//            }
//
//            return false
//        }
//    })
//}

@BindingAdapter("onTitleChanged")
fun onTitleChanged(textInput: TextInputEditText, listener: CreateEventListener) {
    textInput.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener.onTextChanged(s.toString())
        }
    })
}

@BindingAdapter("setErrorMessage")
fun setErrorMessage(textInput: TextInputLayout, title : String?){
    if(title?.length==1){
        textInput.isErrorEnabled = true
        textInput.error = "2글자 이상 입력해주세요"
    }else if(title.isNullOrEmpty() || title.length >=2){
        textInput.isErrorEnabled = false
    }
}