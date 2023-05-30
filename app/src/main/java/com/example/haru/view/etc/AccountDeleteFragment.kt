package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.databinding.FragmentAccountDeleteBinding

class AccountDeleteFragment : Fragment() {
    private lateinit var binding: FragmentAccountDeleteBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): AccountDeleteFragment {
            return AccountDeleteFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AccountDeleteFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountDeleteBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)

        binding.btnAccountDelete.setOnClickListener(ClickListener())
        binding.ivBackIconAccountDelete.setOnClickListener(ClickListener())
    }

    inner class ClickListener() : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconAccountDelete.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.btnAccountDelete.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, AccountDeleteDoubleCheckFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }
}