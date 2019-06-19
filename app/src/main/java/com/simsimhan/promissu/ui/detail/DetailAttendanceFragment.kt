package com.simsimhan.promissu.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.simsimhan.promissu.databinding.FragmentDetailAttendanceBinding


class DetailAttendanceFragment : Fragment() {

    private lateinit var binding: ViewDataBinding
    private lateinit var viewModel: DetailViewModel

    companion object {
        fun newInstance(): Fragment {
            val fragment = DetailAttendanceFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!).get(DetailViewModel::class.java)

        binding = FragmentDetailAttendanceBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@DetailAttendanceFragment
        }


        (binding as FragmentDetailAttendanceBinding).detailAttendanceCl.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                activity!!.onBackPressed()
            }
            true
        }

        (binding as FragmentDetailAttendanceBinding).detailAttendanceFl.setOnTouchListener { v, event ->

            true
        }


        return binding.root

    }

}