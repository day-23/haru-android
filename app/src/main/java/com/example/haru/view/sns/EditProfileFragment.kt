package com.example.haru.view.sns

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.databinding.FragmentEditProfileBinding

class EditProfileFragment: Fragment() {
    private lateinit var binding: FragmentEditProfileBinding

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : EditProfileFragment {
            return EditProfileFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SnsMypageFragment - onCreate() called")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "SnsFragment - onCreateView() called")

        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            val newFrag = MyPageFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        return binding.root
    }
}
