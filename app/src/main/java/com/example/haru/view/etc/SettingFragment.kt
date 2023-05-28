package com.example.haru.view.etc

import BaseActivity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.App
import com.example.haru.R
import com.example.haru.databinding.FragmentSettingBinding
import com.example.haru.utils.SharedPrefsManager
import com.example.haru.utils.User
import com.example.haru.view.auth.LoginActivity
import com.example.haru.viewmodel.EtcViewModel

class SettingFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): SettingFragment {
            return SettingFragment(etcViewModel)
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(EtcFragment.TAG, "SettingFragment - onCreate() called")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater)
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.headerTitle.id)

        Log.e("20191627", User.toString())

        binding.ivBackIconSetting.setOnClickListener(ClickListener())

        binding.layoutAccount.setOnClickListener(ClickListener())

        binding.layoutProtect.setOnClickListener(ClickListener())

        binding.layoutDisplay.setOnClickListener(ClickListener())

        binding.layoutAlarm.setOnClickListener(ClickListener())

        binding.layoutInformation.setOnClickListener(ClickListener())

        binding.logout.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconSetting.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.layoutAccount.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, AccountFragment(etcViewModel))
                        .addToBackStack(null)
                        .commit()
                }

                binding.layoutProtect.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, ProtectFragment(etcViewModel))
                        .addToBackStack(null)
                        .commit()
                }

                binding.layoutDisplay.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, DisplayFragment(etcViewModel))
                        .addToBackStack(null)
                        .commit()
                }

                binding.layoutAlarm.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, AlarmFragment(etcViewModel))
                        .addToBackStack(null)
                        .commit()
                }

                binding.layoutInformation.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, InformationFragment(etcViewModel))
                        .addToBackStack(null)
                        .commit()
                }

                binding.logout.id -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes") { dialog, which ->
                            // User confirmed logout, perform the logout operation
                            SharedPrefsManager.clear(App.instance)
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                        .setNegativeButton("No") { dialog, which ->
                            // User cancelled logout, just close the dialog
                            dialog.dismiss()
                        }
                        .show()
                }
            }
        }
    }
}