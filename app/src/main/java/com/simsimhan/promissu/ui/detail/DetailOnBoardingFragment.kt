package com.simsimhan.promissu.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.simsimhan.promissu.databinding.FragmentDetailOnBoardingBinding

class DetailOnBoardingFragment : Fragment(){

    private lateinit var binding : FragmentDetailOnBoardingBinding

    companion object {
        fun newInstance(): Fragment {
            val fragment = DetailOnBoardingFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetailOnBoardingBinding.inflate(inflater,container,false).apply{
            lifecycleOwner = this@DetailOnBoardingFragment
        }

        binding.detailOnboardingCloseBtn.setOnClickListener {
            activity!!.onBackPressed()
        }

        binding.detailOnboardingCl.setOnTouchListener { _, _ ->
            true
        }

        return binding.root
    }

}