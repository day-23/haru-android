package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.databinding.FragmentAccountBinding
import com.example.haru.viewmodel.EtcViewModel

class AccountFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentAccountBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel) : AccountFragment {
            return AccountFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AccountFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)

        etcViewModel.email.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvEmail.text = it
        })

        etcViewModel.name.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvName.text = it
        })

        etcViewModel.haruId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvHaruId.text = it
        })

        binding.ivBackIconAccount.setOnClickListener(ClickListener())
        binding.accountDelete.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconAccount.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.accountDelete.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, AccountDeleteFragment(etcViewModel))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }
}