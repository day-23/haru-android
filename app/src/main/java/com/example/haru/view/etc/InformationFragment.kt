package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.databinding.FragmentInformationBinding
import com.example.haru.view.MainActivity
import com.example.haru.view.customDialog.CustomPolicyDialog
import com.example.haru.viewmodel.EtcViewModel

class InformationFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentInformationBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): InformationFragment {
            return InformationFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EtcFragment.TAG, "InformationFragment - onCreate() called")
        MainActivity.hideNavi(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInformationBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)

        binding.termsOfService.setOnClickListener(ClickListener())
        binding.privacyPolicy.setOnClickListener(ClickListener())

        binding.ivBackIconInformation.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconInformation.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.termsOfService.id -> {
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragments_frame, CustomPolicyDialog(true))
                        .addToBackStack(null)
                        .commit()
                }

                binding.privacyPolicy.id -> {
                    requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragments_frame, CustomPolicyDialog(false))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }
}