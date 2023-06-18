package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentDisplayBinding
import com.example.haru.viewmodel.EtcViewModel

class DisplayFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentDisplayBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): DisplayFragment {
            return DisplayFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EtcFragment.TAG, "DisplayFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDisplayBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.displayHeaderTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.displayHeaderTitle.id)

        binding.ivBackIconDisplay.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconDisplay.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }
}