package com.simsimhan.promissu.promise.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.FragmentCreatePromise1Binding
import com.simsimhan.promissu.databinding.FragmentCreatePromise2Binding
import com.simsimhan.promissu.databinding.FragmentCreatePromise3Binding
import com.simsimhan.promissu.map.LocationSearchActivity
import com.simsimhan.promissu.util.NavigationUtil
import com.simsimhan.promissu.util.StringUtil
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_create_promise_1.view.*
import kotlinx.android.synthetic.main.fragment_create_promise_2.view.*
import kotlinx.android.synthetic.main.fragment_create_promise_3.view.*
import org.joda.time.DateTime
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*

class CreateFragment : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    private var pageKey: Int? = null
    private var username: String? = null
    private var now: DateTime? = null
    private var promiseTitle: TextInputEditText? = null
    private var startDateEditText: TextInputEditText? = null
    private var startTimeEditText: TextInputEditText? = null
    private var endDateEditText: TextInputEditText? = null
    private var endTimeEditText: TextInputEditText? = null
    private var promisePlace: TextInputEditText? = null
    private var x: Double = 0.toDouble()
    private var y: Double = 0.toDouble()
    private var locationText: String? = null
    private var startSelectedDate: DateTime? = null
    private var startSelectedDateTime: DateTime? = null
    private var endSelectedDate: DateTime? = null
    private var endSelectedDateTime: DateTime? = null
    private lateinit var binding: ViewDataBinding
    //    private lateinit var viewModel: CreateViewModel
    private val viewModel: CreateViewModel by sharedViewModel()

    companion object {
        fun newInstance(position: Int, title: String): Fragment {
            val fragment = CreateFragment()
            val args = Bundle()
            args.putInt("Page_key", position)
            args.putString("Title_key", title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            // get arguments and set here
            pageKey = arguments!!.getInt("Page_key")
        }

        username = PromissuApplication.diskCache!!.userName
        now = DateTime()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        when (pageKey) {
            0 -> setupTitleView(inflater, container)
            1 -> setupWhenView(inflater, container)
            2 -> setupLocationView(inflater, container)
        }

        return binding.root
    }


    private fun setupTitleView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCreatePromise1Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
            eventListener = this@CreateFragment.viewModel
        }
        val question = binding.root.create_question1_text
        question.text = Html.fromHtml(getString(R.string.create_question_1, username))
        promiseTitle = binding.root.promise_title_edit_text

    }

    private fun setupWhenView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCreatePromise2Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
        }
        val question = binding.root.create_question2_text
        question.text = Html.fromHtml(getString(R.string.create_question_2))
        startDateEditText = binding.root.promise_start_date.apply {
            setOnClickListener {
                val now = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog.newInstance(
                        this@CreateFragment,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.minDate = now
                if (endSelectedDate != null) {
                    val maxDate = Calendar.getInstance()
                    maxDate.set(endSelectedDate!!.year, endSelectedDate!!.monthOfYear - 1, endSelectedDate!!.dayOfMonth)
                    datePickerDialog.maxDate = maxDate
                }
                datePickerDialog.showYearPickerFirst(true)
                datePickerDialog.show(fragmentManager!!, "StartDatePickerDialog")
            }
        }

        startTimeEditText = binding.root.promise_start_time_edit_text.apply {
            setOnClickListener {
                if (startSelectedDate == null) {
                    Toast.makeText(requireContext(), "시작 일자를 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val timePickerDialog = TimePickerDialog.newInstance(this@CreateFragment,
                            now!!.hourOfDay,
                            now!!.minuteOfHour,
                            true
                    )
                    timePickerDialog.show(fragmentManager!!, "StartTimePickerDialog")
                }
            }
        }

        endDateEditText = binding.root.promise_end_date.apply {
            setOnClickListener {
                if (startSelectedDateTime == null) {
                    Toast.makeText(requireContext(), "약속 시작시간을 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val now = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog.newInstance(
                            this@CreateFragment,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH))

                    val startCalendar = Calendar.getInstance()
                    startCalendar.set(startSelectedDate!!.year, startSelectedDate!!.monthOfYear - 1, startSelectedDate!!.dayOfMonth)
                    datePickerDialog.minDate = startCalendar
                    datePickerDialog.showYearPickerFirst(true)
                    datePickerDialog.show(fragmentManager!!, "EndDatePickerDialog")
                }
            }
        }

        endTimeEditText = binding.root.promise_end_time_edit_text.apply {
            setOnClickListener {
                if (endSelectedDate == null) {
                    Toast.makeText(requireContext(), "종료 일자를 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
                } else {
                    val timePickerDialog = TimePickerDialog.newInstance(this@CreateFragment,
                            now!!.hourOfDay,
                            now!!.minuteOfHour,
                            true
                    )
                    timePickerDialog.show(fragmentManager!!, "EndTimePickerDialog")
                }
            }
        }


    }

    private fun setupLocationView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCreatePromise3Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
        }
        val question = binding.root.create_question3_text
        question.text = Html.fromHtml(getString(R.string.create_question_3))

        promisePlace = binding.root.promise_location_edit_text.apply {
            setOnClickListener {
                val intent = Intent(activity, LocationSearchActivity::class.java)
                startActivityForResult(intent, NavigationUtil.REQUEST_MAP_SEARCH)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NavigationUtil.REQUEST_MAP_SEARCH) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val location = data.getStringExtra("location")
                val locationName = data.getStringExtra("locationName")
                val x = data.getDoubleExtra("x", 0.0)
                val y = data.getDoubleExtra("y", 0.0)
                viewModel.setCreateInfo(y, x, location, locationName)
                setPromisePlace(location)
            } else {
                Toast.makeText(context, "약속 장소를 선택해주세요.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setPromisePlace(placeText: String) {
        Timber.d("setPromisePlace(): $placeText")
        if (promisePlace != null) {
            this.locationText = placeText
            promisePlace!!.setText(placeText)
        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        if (view!!.tag == "StartDatePickerDialog") {
            startSelectedDate = now!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            // get date shit shit and set
            if (startDateEditText != null) {
                startDateEditText!!.setText(year.toString() + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일")
            }
        } else {
            endSelectedDate = now!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)
            // get date shit shit and set
            if (endDateEditText != null) {
                endDateEditText!!.setText(year.toString() + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일")
            }
        }
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        if (view!!.tag == "StartTimePickerDialog") {
            startSelectedDateTime = now!!.withHourOfDay(hourOfDay)
                    .withMinuteOfHour(minute)
            if (startTimeEditText != null) {
                startTimeEditText!!.setText(StringUtil.addPaddingIfSingleDigit(hourOfDay) + ":" + StringUtil.addPaddingIfSingleDigit(minute))
            }
            val requestStartDateTime = startSelectedDate!!.withHourOfDay(startSelectedDateTime!!.hourOfDay).withMinuteOfHour(startSelectedDateTime!!.minuteOfHour).withSecondOfMinute(0).toDate()
            viewModel.setStartDateTime(requestStartDateTime)
        } else {
            endSelectedDateTime = now!!.withHourOfDay(hourOfDay)
                    .withMinuteOfHour(minute)
            if ((startSelectedDate == endSelectedDate) && (endSelectedDateTime!!.isBefore(startSelectedDateTime))) { //시작 종료일이 같고 종료시간이 시작시간보다 빠르다면
                Toast.makeText(requireContext(), "시작 시간보다 종료 시간이 늦도록 설정해주세요", Toast.LENGTH_SHORT).show()
            } else {
                if (endTimeEditText != null) {
                    endTimeEditText!!.setText(StringUtil.addPaddingIfSingleDigit(hourOfDay) + ":" + StringUtil.addPaddingIfSingleDigit(minute))
                }
                val requestEndDateTime = endSelectedDate!!.withHourOfDay(endSelectedDateTime!!.hourOfDay).withMinuteOfHour(endSelectedDateTime!!.minuteOfHour).toDate()
                viewModel.setEndDateTime(requestEndDateTime)
            }
        }
    }
}