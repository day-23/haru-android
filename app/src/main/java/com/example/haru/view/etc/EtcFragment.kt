package com.example.haru.view.etc

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.example.haru.App
import com.example.haru.R
import com.example.haru.databinding.ActivityLoginBinding
import com.example.haru.databinding.FragmentEtcBinding
import com.example.haru.databinding.FragmentSnsBinding
import com.example.haru.utils.SharedPrefsManager
import com.example.haru.view.auth.LoginActivity
import com.example.haru.view.checklist.ChecklistTodayFragment

class EtcFragment : Fragment() {
    private lateinit var binding: FragmentEtcBinding

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : EtcFragment {
            return EtcFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "EtcFragment - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEtcBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingIcon.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener{
        override fun onClick(v: View?) {
            when(v?.id){
                binding.settingIcon.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, SettingFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }


    }
}