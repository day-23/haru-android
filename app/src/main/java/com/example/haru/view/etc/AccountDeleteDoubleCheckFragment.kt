package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.databinding.FragmentAccountDeleteDoubleCheckBinding

class AccountDeleteDoubleCheckFragment : Fragment() {
    private lateinit var binding : FragmentAccountDeleteDoubleCheckBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance() : AccountDeleteDoubleCheckFragment {
            return AccountDeleteDoubleCheckFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AccountDeleteDoubleCheckFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDeleteDoubleCheckBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)

        binding.ivCancelAccountDelete.setOnClickListener(ClickListener())
        binding.btnAccountDeleteComplete.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener{
        override fun onClick(v: View?) {
            when(v?.id){
                binding.ivCancelAccountDelete.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.btnAccountDeleteComplete.id -> {

                }
            }
        }

    }
}