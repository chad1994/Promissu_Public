package com.simsimhan.promissu.ui.promise.create

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.simsimhan.promissu.PromissuApplication
import com.simsimhan.promissu.R
import com.simsimhan.promissu.databinding.FragmentCreatePromise1Binding
import com.simsimhan.promissu.databinding.FragmentCreatePromise2TempBinding
import com.simsimhan.promissu.databinding.FragmentCreatePromise3Binding
import com.simsimhan.promissu.network.model.Promise
import com.simsimhan.promissu.ui.map.LocationSearchActivity
import com.simsimhan.promissu.util.NavigationUtil
import com.simsimhan.promissu.util.StringUtil
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_create_promise_1.view.*
import kotlinx.android.synthetic.main.fragment_create_promise_2_temp.view.*
import kotlinx.android.synthetic.main.fragment_create_promise_3.view.*
import org.joda.time.DateTime
import org.joda.time.Minutes
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CreateFragment : Fragment(), DatePickerDialog.OnDateSetListener, NumberPicker.OnValueChangeListener {


    private var pageKey: Int? = null
    private var response: Promise.Response? = null
    private var username: String? = null
    private var now: DateTime? = null
    private var dateEditText: TextInputEditText? = null
    private var hourEditText: TextInputEditText? = null
    private var minuteEditText: TextInputEditText? = null
    private var latenessEditText: TextInputEditText? = null
    //    private var endDateEditText: TextInputEditText? = null
//    private var endTimeEditText: TextInputEditText? = null
    private var promisePlace: TextInputEditText? = null
    private var x: Double = 0.toDouble()
    private var y: Double = 0.toDouble()
    private var locationText: String? = null
    private var selectedDate: DateTime? = null
    private var selectedDateTime: DateTime? = null
    private var selectedLatenessTime: DateTime? = null
    //    private var endSelectedDate: DateTime? = null
//    private var endSelectedDateTime: DateTime? = null
    private lateinit var binding: ViewDataBinding
    //    private lateinit var viewModel: CreateViewModel
    private val viewModel: CreateViewModel by sharedViewModel()

    companion object {
        fun newInstance(position: Int): Fragment {
            val fragment = CreateFragment()
            val args = Bundle()
            args.putInt("Page_key", position)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(position: Int, response: Promise.Response): Fragment {
            val fragment = CreateFragment()
            val args = Bundle()
            args.putInt("Page_key", position)
            args.putParcelable("Response", response)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        if (arguments != null) {
            // get arguments and set here
            pageKey = arguments!!.getInt("Page_key")
            response = arguments!!.getParcelable("Response")
        }


        username = PromissuApplication.diskCache!!.userName
        now = DateTime()
    }

    override fun onResume() {
        super.onResume()
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        when (pageKey) {
            0 -> setupTitleView(inflater, container)
            1 -> setupWhenView2(inflater, container)
            2 -> setupLocationView(inflater, container)
        }

        return binding.root
    }

    private fun setupTitleView(inflater: LayoutInflater, container: ViewGroup?) {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        binding = FragmentCreatePromise1Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
            eventListener = this@CreateFragment.viewModel
        }
        val question = binding.root.create_question1_text
        question.text = Html.fromHtml(getString(R.string.create_question_1, username))
        if (response != null) {
            binding.root.promise_title_edit_text.setText(response!!.title)
            viewModel.setTitle(response!!.title)
        }
    }

    private fun setupWhenView2(inflater: LayoutInflater, container: ViewGroup?) {

        binding = FragmentCreatePromise2TempBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
            eventListener = this@CreateFragment.viewModel
        }


        val question = binding.root.create_question2_text_temp
        question.text = Html.fromHtml(getString(R.string.create_question_2))

//        binding.root.apply {
//            setOnClickListener {
//                showNumberPicker(this@CreateFragment.view!!, "약속시간을 설정해주세요", "0~60분", 300, 0, 10, 30, "분")
//            }

        dateEditText = binding.root.promise_start_date_temp.apply {
            setOnClickListener {
                val now = Calendar.getInstance()
                val datePickerDialog = DatePickerDialog.newInstance(
                        this@CreateFragment,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH))
                now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.minDate = now
                datePickerDialog.showYearPickerFirst(false)
                datePickerDialog.show(fragmentManager!!, "DatePickerDialog")
            }
        }

        hourEditText = binding.root.promise_start_hour.apply {
            setOnClickListener {
                showNumberPicker(this@CreateFragment.view!!, "약속시간을 설정해주세요", "0~23시", 23, 0, 1, 12, "시")
            }
        }

        minuteEditText = binding.root.promise_start_minute.apply {
            setOnClickListener {
                showNumberPicker(this@CreateFragment.view!!, "약속시간을 설정해주세요", "0~60분", 59, 0, 1, 30, "분")
            }
        }

        latenessEditText = binding.root.promise_start_lateness.apply {
            setOnClickListener {
                showNumberPicker(this@CreateFragment.view!!, "약속시간을 설정해주세요", "0~60분", 60, 0, 5, 30, "분+")
            }
        }
    }

//    private fun setupWhenView(inflater: LayoutInflater, container: ViewGroup?) {
//        binding = FragmentCreatePromise2Binding.inflate(inflater, container, false).apply {
//            lifecycleOwner = this@CreateFragment
//            viewModel = this@CreateFragment.viewModel
//            eventListener = this@CreateFragment.viewModel
//        }
//
//        val question = binding.root.create_question2_text
//        question.text = Html.fromHtml(getString(R.string.create_question_2))
//        dateEditText = binding.root.promise_start_date.apply {
//            setOnClickListener {
//                val now = Calendar.getInstance()
//                val datePickerDialog = DatePickerDialog.newInstance(
//                        this@CreateFragment,
//                        now.get(Calendar.YEAR),
//                        now.get(Calendar.MONTH),
//                        now.get(Calendar.DAY_OF_MONTH))
//                now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
//                datePickerDialog.minDate = now
//                datePickerDialog.showYearPickerFirst(false)
//                datePickerDialog.show(fragmentManager!!, "StartDatePickerDialog")
//            }
//        }
//
//        startTimeEditText = binding.root.promise_start_time_edit_text.apply {
//            setOnClickListener {
//                if (selectedDate == null) {
//                    Toast.makeText(requireContext(), "시작 일자를 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
//                } else {
//                    val timePickerDialog = TimePickerDialog.newInstance(this@CreateFragment,
//                            now!!.hourOfDay,
//                            now!!.minuteOfHour,
//                            true
//                    )
//                    timePickerDialog.show(fragmentManager!!, "StartTimePickerDialog")
//                }
//            }
//        }
//
//        endDateEditText = binding.root.promise_end_date.apply {
//            setOnClickListener {
//                if (selectedDateTime == null) {
//                    Toast.makeText(requireContext(), "약속 시작시간을 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
//                } else {
//                    val now = Calendar.getInstance()
//                    val datePickerDialog = DatePickerDialog.newInstance(
//                            this@CreateFragment,
//                            now.get(Calendar.YEAR),
//                            now.get(Calendar.MONTH),
//                            now.get(Calendar.DAY_OF_MONTH))
//
//                    val startCalendar = Calendar.getInstance()
//                    startCalendar.set(selectedDate!!.year, selectedDate!!.monthOfYear - 1, selectedDate!!.dayOfMonth)
//                    datePickerDialog.minDate = startCalendar
//                    datePickerDialog.showYearPickerFirst(true)
//                    datePickerDialog.show(fragmentManager!!, "EndDatePickerDialog")
//                }
//            }
//        }
//
//        endTimeEditText = binding.root.promise_end_time_edit_text.apply {
//            setOnClickListener {
//                if (endSelectedDate == null) {
//                    Toast.makeText(requireContext(), "종료 일자를 먼저 선택 해주세요", Toast.LENGTH_SHORT).show()
//                } else {
//                    val timePickerDialog = TimePickerDialog.newInstance(this@CreateFragment,
//                            now!!.hourOfDay,
//                            now!!.minuteOfHour,
//                            true
//                    )
//                    timePickerDialog.show(fragmentManager!!, "EndTimePickerDialog")
//                }
//            }
//        }
//
//        if (response != null) {
//            dateEditText!!.setText("${response!!.start_datetime.year + 1900}년 ${response!!.start_datetime.month + 1}월 ${response!!.start_datetime.date}일")
//            startTimeEditText!!.setText("${StringUtil.addPaddingIfSingleDigit(response!!.start_datetime.hours)}:${StringUtil.addPaddingIfSingleDigit(response!!.start_datetime.minutes)}")
//            endDateEditText!!.setText("${response!!.end_datetime.year + 1900}년 ${response!!.end_datetime.month + 1}월 ${response!!.end_datetime.date}일")
//            endTimeEditText!!.setText("${StringUtil.addPaddingIfSingleDigit(response!!.end_datetime.hours)}:${StringUtil.addPaddingIfSingleDigit(response!!.end_datetime.minutes)}")
//            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
//            val formattedstartDate = sdf.format(response!!.start_datetime)
//            val formattedendDate = sdf.format(response!!.end_datetime)
//            viewModel.setStartDateTime(formattedstartDate)
//            viewModel.setEndDateTime(formattedendDate)
//            selectedDate = now!!.withYear(response!!.start_datetime.year + 1900).withMonthOfYear(response!!.start_datetime.month + 1).withDayOfMonth(response!!.start_datetime.date)
//            selectedDateTime = DateTime(response!!.start_datetime)
//            endSelectedDate = now!!.withYear(response!!.end_datetime.year + 1900).withMonthOfYear(response!!.end_datetime.month + 1).withDayOfMonth(response!!.end_datetime.date)
//            endSelectedDateTime = DateTime(response!!.end_datetime)
//
//        }
//
//    }

    private fun setupLocationView(inflater: LayoutInflater, container: ViewGroup?) {
        binding = FragmentCreatePromise3Binding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@CreateFragment
            viewModel = this@CreateFragment.viewModel
            eventListener = this@CreateFragment.viewModel
        }
        val question = binding.root.create_question3_text
        question.text = Html.fromHtml(getString(R.string.create_question_3))

        promisePlace = binding.root.promise_location_edit_text.apply {
            setOnClickListener {
                val intent = Intent(activity, LocationSearchActivity::class.java)
                startActivityForResult(intent, NavigationUtil.REQUEST_MAP_SEARCH)
            }
        }

        if (response != null) {
            viewModel.setCreateInfo(response!!.location_lat, response!!.location_lon, response!!.location, response!!.location_name)
            setPromisePlace(response!!.location)
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
//                Toast.makeText(context, "약속 장소를 선택해주세요.", Toast.LENGTH_LONG).show()
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
//        if (view!!.tag == "StartDatePickerDialog") {
        selectedDate = now!!.withYear(year).withMonthOfYear(monthOfYear + 1).withDayOfMonth(dayOfMonth)

        if (dateEditText != null) {
            dateEditText!!.setText(year.toString() + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일")
        }
        hourEditText!!.text = null
        minuteEditText!!.text = null
        latenessEditText!!.text = null
        selectedDateTime = null
        selectedLatenessTime = null
        viewModel.setStartDateTime(null)
        viewModel.setEndDateTime(null)
    }

//    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
//        if (view!!.tag == "StartTimePickerDialog") {
//            selectedDateTime = now!!.withHourOfDay(hourOfDay)
//                    .withMinuteOfHour(minute)
//            if ((selectedDate!!.isEqual(now)) && ((now!!.hourOfDay + 1 > hourOfDay) || ((now!!.hourOfDay + 1 == hourOfDay) && (now!!.minuteOfHour >= minute)))) {
//                Toast.makeText(requireContext(), "최소 1시간 이후로 설정해주세요", Toast.LENGTH_SHORT).show()
//            } else {
//                if (startTimeEditText != null) {
//                    startTimeEditText!!.setText(StringUtil.addPaddingIfSingleDigit(hourOfDay) + ":" + StringUtil.addPaddingIfSingleDigit(minute))
//                }
//                val requestStartDateTime = selectedDate!!.withHourOfDay(selectedDateTime!!.hourOfDay).withMinuteOfHour(selectedDateTime!!.minuteOfHour).withSecondOfMinute(0).toDate()
//                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
//                val formattedDate = sdf.format(requestStartDateTime)
//                viewModel.setStartDateTime(formattedDate)
//                // end 시간을 설정 후 start 변경 시 end 초기화
//                endTimeEditText!!.text = null
//                endDateEditText!!.text = null
//                endSelectedDate = null
//                endSelectedDateTime = null
//                viewModel.setEndDateTime(null)
//            }
//        } else {
//            endSelectedDateTime = now!!.withHourOfDay(hourOfDay)
//                    .withMinuteOfHour(minute)
//            if ((selectedDate == endSelectedDate) && (endSelectedDateTime!!.isBefore(selectedDateTime))) { //시작 종료일이 같고 종료시간이 시작시간보다 빠르다면
//                Toast.makeText(requireContext(), "시작 시간보다 종료 시간이 늦도록 설정해주세요", Toast.LENGTH_SHORT).show()
//            } else if ((selectedDate == endSelectedDate) && (Minutes.minutesBetween(selectedDateTime, endSelectedDateTime).minutes < 60)) {
//                Toast.makeText(requireContext(), "약속시간이 적어도 1시간 이상이 되도록 설저해주세요", Toast.LENGTH_SHORT).show()
//            } else {
//                if (endTimeEditText != null) {
//                    endTimeEditText!!.setText(StringUtil.addPaddingIfSingleDigit(hourOfDay) + ":" + StringUtil.addPaddingIfSingleDigit(minute))
//                }
//                val requestEndDateTime = endSelectedDate!!.withHourOfDay(endSelectedDateTime!!.hourOfDay).withMinuteOfHour(endSelectedDateTime!!.minuteOfHour).withSecondOfMinute(0).toDate()
//                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
//                val formattedDate = sdf.format(requestEndDateTime)
//                viewModel.setEndDateTime(formattedDate)
//            }
//        }
//    }

    override fun onValueChange(picker: NumberPicker?, oldVal: Int, newVal: Int) {
        if (oldVal == 0) {
            Toast.makeText(context, "시!" + newVal, Toast.LENGTH_SHORT).show()

            selectedDateTime = now!!.withHourOfDay(newVal)
            if (hourEditText != null) {
                hourEditText!!.setText(StringUtil.addPaddingIfSingleDigit(newVal)+"시")
            }
            val requestStartDateTime = selectedDate!!.withHourOfDay(selectedDateTime!!.hourOfDay).withMinuteOfHour(selectedDateTime!!.minuteOfHour).withSecondOfMinute(0).toDate()
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
            val formattedDate = sdf.format(requestStartDateTime)
            viewModel.setStartDateTime(formattedDate)

            //TODO : 지각시간 갱신

        } else if (oldVal == 1) {
            Toast.makeText(context, "분!" + newVal, Toast.LENGTH_SHORT).show()

            selectedDateTime = now!!.withMinuteOfHour(newVal)
            if (minuteEditText != null) {
                minuteEditText!!.setText(StringUtil.addPaddingIfSingleDigit(newVal)+"분")
            }
            val requestStartDateTime = selectedDate!!.withHourOfDay(selectedDateTime!!.hourOfDay).withMinuteOfHour(selectedDateTime!!.minuteOfHour).withSecondOfMinute(0).toDate()
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
            val formattedDate = sdf.format(requestStartDateTime)
            viewModel.setStartDateTime(formattedDate)

            //TODO : 지각시간 갱신

        } else {
            Toast.makeText(context, "지각!" + newVal, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNumberPicker(view: View, title: String, subtitle: String, maxvalue: Int, minvalue: Int, step: Int, defValue: Int, unit: String) {
        val newFragment = NumberPickerFragment()
        val bundle = Bundle(6)
        bundle.putString("title", title)
        bundle.putString("subtitle", subtitle)
        bundle.putInt("maxvalue", maxvalue)
        bundle.putInt("minvalue", minvalue)
        bundle.putInt("step", step)
        bundle.putInt("defValue", defValue)
        bundle.putString("unit", unit)
        newFragment.arguments = bundle

        newFragment.setValueChangeListener(this)
        newFragment.show(fragmentManager!!, "number picker")
    }
}