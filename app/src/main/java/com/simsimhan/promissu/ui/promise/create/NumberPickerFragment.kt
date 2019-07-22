package com.simsimhan.promissu.ui.promise.create

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment


class NumberPickerFragment : DialogFragment() {
    private lateinit var valueChangeListener: NumberPicker.OnValueChangeListener
    lateinit var title: String   //dialog 제목
    lateinit var subtitle: String    //dialog 부제목
    var minvalue: Int = 0   //입력가능 최소값
    var maxvalue: Int = 0  //입력가능 최대값
    var step: Int = 0   //선택가능 값들의 간격
    var defvalue: Int = 0 //dialog 시작 숫자 (현재값)
    var unit: String = " "

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val numberPicker = NumberPicker(activity)

        title = arguments!!.getString("title")
        subtitle = arguments!!.getString("subtitle")
        minvalue = arguments!!.getInt("minvalue")
        maxvalue = arguments!!.getInt("maxvalue")
        step = arguments!!.getInt("step")
        defvalue = arguments!!.getInt("defValue")
        unit = arguments!!.getString("unit")

        val myValues = getArrayWithSteps(minvalue, maxvalue, step, unit)

        numberPicker.minValue = 0
        numberPicker.maxValue = (maxvalue - minvalue) / step
        numberPicker.displayedValues = myValues
        numberPicker.value = (defvalue - minvalue) / step
        numberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        numberPicker.dividerDrawable

        val builder = AlertDialog.Builder(activity)
        builder.setTitle(title)
        builder.setMessage(subtitle)
        builder.setPositiveButton("OK") { _, _ ->
            if (unit == "시")
                valueChangeListener.onValueChange(numberPicker,
                        0, numberPicker.value) //oldval 0 = 시, 1 = 분
            else if(unit== "분")
                valueChangeListener.onValueChange(numberPicker,
                        1, numberPicker.value)
            else
                valueChangeListener.onValueChange(numberPicker,
                        2, numberPicker.value*step)
            dismiss()
        }
        builder.setNegativeButton("CANCEL") { _, _ ->
            dismiss()
        }
        builder.setView(numberPicker)

        return builder.create()
    }

    fun getValueChangeListener(): NumberPicker.OnValueChangeListener {
        return valueChangeListener
    }

    fun setValueChangeListener(valueChangeListener: NumberPicker.OnValueChangeListener) {
        this.valueChangeListener = valueChangeListener
    }


    private fun getArrayWithSteps(min: Int, max: Int, step: Int, unit: String): Array<String?> {
        val number_of_array = (max - min) / step + 1

        val result = arrayOfNulls<String>(number_of_array)

        for (i in 0 until number_of_array) {
            result[i] = (min + step * i).toString() + unit
        }
        return result
    }

}