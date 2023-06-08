package com.example.haru.view.etc

import BaseActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.haru.App
import com.example.haru.databinding.FragmentAccountDeleteDoubleCheckBinding
import com.example.haru.utils.SharedPrefsManager
import com.example.haru.view.MainActivity
import com.example.haru.view.auth.LoginActivity
import com.example.haru.viewmodel.EtcViewModel

class AccountDeleteDoubleCheckFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentAccountDeleteDoubleCheckBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): AccountDeleteDoubleCheckFragment {
            return AccountDeleteDoubleCheckFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "AccountDeleteDoubleCheckFragment - onCreate() called")
        MainActivity.hideNavi(true)
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

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivCancelAccountDelete.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.btnAccountDeleteComplete.id -> {
                    etcViewModel.deleteUserAccount {
                        if (it?.success == true) {
                            SharedPrefsManager.clear(App.instance)
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "계정을 삭제하는데 실패하였습니다..",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e("20191627", "계정 삭제 실패")
                        }
                    }
                }
            }
        }

    }
}