package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentProtectBinding
import com.example.haru.view.sns.SnsFragment
import com.example.haru.viewmodel.EtcViewModel

class ProtectFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentProtectBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): ProtectFragment {
            return ProtectFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EtcFragment.TAG, "ProtectFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProtectBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)

        etcViewModel.isPublicAccount.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.switchAccountPublic.isChecked = it
        })

        etcViewModel.isPostBrowsingEnabled.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.switchBrowsingEnabled.isChecked = it
        })

        etcViewModel.isAllowSearch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.switchAllowSearch.isChecked = it
        })


        binding.switchAccountPublic.setOnClickListener(ClickListener())
        binding.ivBackIconProtect.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconProtect.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.switchAccountPublic.id -> {
                    etcViewModel.submitIsPublicAccount {
                        if (it?.success != true)
                            binding.switchAccountPublic.isChecked = !binding.switchAccountPublic.isChecked
                    }
                }

                binding.switchBrowsingEnabled.id -> {

                }

                binding.switchAllowSearch.id -> {

                }
            }
        }
    }
}