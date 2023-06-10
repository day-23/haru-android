package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentAlarmBinding
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.viewmodel.EtcViewModel

class AlarmFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentAlarmBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): AlarmFragment {
            return AlarmFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EtcFragment.TAG, "AlarmFragment - onCreate() called")
        MainActivity.hideNavi(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlarmBinding.inflate(inflater)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreference = requireContext().getSharedPreferences("ApplyData", 0)
        val editor = sharedPreference.edit()

        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
        binding.ivBackIconAlarm.setOnClickListener(ClickListener())

        binding.commentAlarmSwitch.isChecked = User.alarmAprove

        binding.commentAlarmSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            User.alarmAprove = isChecked
            editor.putBoolean("alarmAprove", User.alarmAprove)
        }
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconAlarm.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }
}