package com.example.haru.view.etc

import BaseActivity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.example.haru.App
import com.example.haru.R
import com.example.haru.databinding.ActivityLoginBinding
import com.example.haru.databinding.FragmentEtcBinding
import com.example.haru.databinding.FragmentSnsBinding
import com.example.haru.utils.FormatDate
import com.example.haru.utils.SharedPrefsManager
import com.example.haru.utils.User
import com.example.haru.view.auth.LoginActivity
import com.example.haru.view.checklist.ChecklistTodayFragment
import com.example.haru.viewmodel.EtcViewModel
import java.util.*

class EtcFragment : Fragment() {
    private lateinit var binding: FragmentEtcBinding
    private var etcViewModel = EtcViewModel()

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): EtcFragment {
            return EtcFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "EtcFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEtcBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // status bar height 조정
        (activity as BaseActivity).adjustTopMargin(binding.etcHeader.id)

        // User Name
        binding.tvName.text = User.name

        // User Introduction
        binding.tvIntroduction.text = "SNS User 필요"

        // User Post Count
//        binding.tvPostCount.text =

        // User Friend Count
//        binding.tvFriendCount.text =

        // User Completed Todo Count
//        binding.tvCompletedTodoCount.text =

        // User Total Todo Count
//        binding.tvTotalTodoCount.text =

        etcViewModel.setTodayYearMonth()

        etcViewModel.todayYearMonth.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvEtcDate.text =
                etcViewModel.todayYearMonth.value?.substring(0, 4) + "년 " +
                        etcViewModel.todayYearMonth.value?.substring(4) + "월 나의 하루"
            binding.tvEtcDateMonth.text = etcViewModel.todayYearMonth.value!!.substring(4) + "월"
        })


        binding.ivEtcDateLeft.setOnClickListener(ClickListener())
        binding.ivEtcDateRight.setOnClickListener(ClickListener())

        binding.settingIcon.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.settingIcon.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, SettingFragment())
                        .addToBackStack(null)
                        .commit()
                }

                binding.ivEtcDateLeft.id -> etcViewModel.addSubTodayYearMonth(false)

                binding.ivEtcDateRight.id -> etcViewModel.addSubTodayYearMonth(true)
            }
        }


    }
}