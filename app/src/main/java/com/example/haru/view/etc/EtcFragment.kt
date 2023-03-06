package com.example.haru.view.etc

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.haru.R

class EtcFragment : Fragment() {
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
        Log.d(TAG, "EtcFragment - onCreateView() called")

        return inflater.inflate(R.layout.fragment_etc, container, false)
    }

}