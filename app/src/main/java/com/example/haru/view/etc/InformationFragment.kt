package com.example.haru.view.etc

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentInformationBinding

class InformationFragment : Fragment() {
    private lateinit var binding: FragmentInformationBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): InformationFragment {
            return InformationFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EtcFragment.TAG, "InformationFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInformationBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBackIconInformation.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconInformation.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }
}