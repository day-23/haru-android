package com.example.haru.view.etc

import BaseActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.haru.App
import com.example.haru.R
import com.example.haru.databinding.FragmentAccountDeleteBinding
import com.example.haru.utils.SharedPrefsManager
import com.example.haru.utils.Tags
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.auth.LoginActivity
import com.example.haru.viewmodel.EtcViewModel

class AccountDeleteFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentAccountDeleteBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): AccountDeleteFragment {
            return AccountDeleteFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(Tags.log, "AccountDeleteFragment - onCreate() called")
        MainActivity.hideNavi(true)
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
        (activity as BaseActivity).adjustTopMargin(binding.accountDeleteHeaderTitle.id)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.accountDeleteHeaderTitle.id)

        etcViewModel.email.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvEmail.text = it
        })

        etcViewModel.introduction.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvAccountIntroduction.text = it
        })

        etcViewModel.name.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvAccountName.text = it
        })

        etcViewModel.profileImage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == "" || it == null)
                binding.ivProfileDelete.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.haru_fighting)
            else Glide.with(this)
                .load(it)
                .into(binding.ivProfileDelete)
        })

        binding.btnAccountDelete.setOnClickListener(ClickListener())
        binding.ivBackIconAccountDelete.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconAccountDelete.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.btnAccountDelete.id -> {
                    etcViewModel.deleteUserAccount {
                        if (it?.success == true) {
                            SharedPrefsManager.clear(App.instance)
                            User.clear()
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "계정을 삭제하는데 실패하였습니다..",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(Tags.log, "계정 삭제 실패")
                        }
                    }
                }
            }
        }
    }
}