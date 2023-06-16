package com.example.haru.view.etc

import BaseActivity
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.haru.App
import com.example.haru.R
import com.example.haru.databinding.ActivityLoginBinding
import com.example.haru.databinding.FragmentEtcBinding
import com.example.haru.databinding.FragmentSnsBinding
import com.example.haru.utils.FormatDate
import com.example.haru.utils.SharedPrefsManager
import com.example.haru.utils.User
import com.example.haru.view.auth.LoginActivity
import com.example.haru.view.checklist.ChecklistTodayFragment
import com.example.haru.view.sns.EditProfileFragment
import com.example.haru.view.sns.FriendsListFragment
import com.example.haru.viewmodel.EtcViewModel
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt

class EtcFragment : Fragment() {
    private lateinit var binding: FragmentEtcBinding
    private var etcViewModel = EtcViewModel()

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): EtcFragment {
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
        // status bar height 조정
        (activity as BaseActivity).adjustTopMargin(binding.etcHeader.id)

        Log.e("20191627", User.toString())
        etcViewModel.getSnsInfo()
        etcViewModel.setTodayYearMonth()
        etcViewModel.calculateWithHaru()

        etcViewModel.profileImage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == "" || it == null)
                binding.ivProfile.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.profile_base_image)
            else Glide.with(this)
                .load(it)
                .into(binding.ivProfile)
        })

        // User Name -> nickname
        etcViewModel.name.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvName.text = it
        })

        etcViewModel.introduction.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvIntroduction.text = if (it == "") getString(R.string.SnsIntroduction) else it
        })

        etcViewModel.postCount.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvPostCount.text = it.toString()
        })

        etcViewModel.friendCount.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvFriendCount.text = it.toString()
        })

        etcViewModel.todayYearMonth.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvEtcDate.text = String.format(
                getString(R.string.textMyHaru),
                etcViewModel.todayYearMonth.value?.substring(0, 4),
                etcViewModel.todayYearMonth.value?.substring(4)
            )

            binding.tvEtcDateMonth.text = String.format(
                getString(R.string.textMonthMyHaru),
                etcViewModel.todayYearMonth.value!!.substring(4)
            )

        })

        etcViewModel.itemCount.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvCompletedTodoCount.text = if (it.first == null) "0" else it.first.toString()
            binding.tvTotalTodoCount.text = if (it.second == null) "0" else it.second.toString()

            Log.e("20191627", "pair $it")
            val percent = if (it.first != null && it.second != null) {
                if (it.second == 0) 0
                else (it.first!!.toFloat() / it.second!!.toFloat() * 100).roundToInt()
            } else
                0
            binding.tvPercent.text = String.format(getString(R.string.percent), percent)

            ObjectAnimator.ofInt(
                binding.progressbar,
                "progress",
                binding.progressbar.progress,
                percent
            ).setDuration(700).start()
        })

        etcViewModel.withHaru.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvWithHaru.text = it.toString()
        })


        binding.ivEtcDateLeft.setOnClickListener(ClickListener())
        binding.ivEtcDateRight.setOnClickListener(ClickListener())
        binding.settingIcon.setOnClickListener(ClickListener())

        binding.btnProfileEdit.setOnClickListener(ClickListener())
        binding.layoutFriend.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.settingIcon.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, SettingFragment(etcViewModel))
                        .addToBackStack(null)
                        .commit()
                }

                binding.ivEtcDateLeft.id -> etcViewModel.addSubTodayYearMonth(false)

                binding.ivEtcDateRight.id -> etcViewModel.addSubTodayYearMonth(true)

                binding.btnProfileEdit.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, EditProfileFragment(User.id))
                        .addToBackStack(null)
                        .commit()
                }

                binding.layoutFriend.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragments_frame, FriendsListFragment(User.id))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }


    }
}