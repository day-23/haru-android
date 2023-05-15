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

        binding.btnLogout.setOnClickListener {
            Log.d(TAG, "onCreateView: 로그아웃 버튼 클릭")

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

        return binding.root
    }
}