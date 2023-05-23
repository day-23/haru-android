package com.example.haru.view.etc

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentProtectBinding

class ProtectFragment : Fragment() {
    private lateinit var binding: FragmentProtectBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): ProtectFragment {
            return ProtectFragment()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.ivBackIconProtect.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
//                binding.ivBackIconProtect.id -> {
//                    requireActivity().supportFragmentManager.popBackStack()
//                }
            }
        }
    }
}