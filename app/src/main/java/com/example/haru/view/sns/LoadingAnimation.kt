package com.example.haru.view.sns

import BaseActivity
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.databinding.LoadingLayoutBinding


class LoadingAnimation : Fragment() {
    private lateinit var binding: LoadingLayoutBinding
    private val loop by lazy {
        Handler(Looper.getMainLooper())
    }
    private var step = 0
    private var dropWaterAnimation: ObjectAnimator? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "LoadingAnimation - onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "LoadingAnimation - onCreateView() called")

        binding = LoadingLayoutBinding.inflate(inflater)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BaseActivity).adjustTopMargin(binding.loadingHeader.id)

        binding.waterPath.post {
            dropWaterAnimation = ObjectAnimator.ofFloat(
                binding.waterDrop,
                "translationY",
                binding.waterDrop.y,
                binding.waterPath.y
            )
            dropWaterAnimation!!.duration = 500
            startAnimation()
        }

    }

    private fun startAnimation() {
        Log.e("20191627", "$step")

        val drawable = when (step) {
            1 -> R.drawable.loading_animation_step1
            2 -> R.drawable.loading_animation_step2
            3 -> R.drawable.loading_animation_step3
            4 -> R.drawable.loading_animation_step4
            5 -> R.drawable.loading_animation_step5
            6 -> R.drawable.loading_animation_step6
            7 -> R.drawable.loading_animation_step7
            else -> R.drawable.loading_animation_step0
        }
        binding.ivLoading.background = ContextCompat.getDrawable(requireContext(), drawable)
        if (step != 8)
            step++
        else {
            binding.waterDrop.visibility = View.VISIBLE
            dropWaterAnimation!!.start()
            step = 0
        }
        loop.postDelayed(::startAnimation, 500)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.loadingHeader.id)
    }


    fun dismiss(callback: () -> Unit) {
        Log.e("20191627", "dismiss")
        loop.removeCallbacksAndMessages(null)
        Handler(Looper.getMainLooper()).postDelayed({
            requireActivity().supportFragmentManager.popBackStack()
            callback()
        }, 500)
    }

    // 이렇게 호출
//    val loading = LoadingAnimation()
//
//    requireActivity().supportFragmentManager.beginTransaction()
//    .add(R.id.fragments_frame, loading)
//    .addToBackStack(null)
//    .commit()

    // 통신이 끝나면 loading.dismiss() 호출
}