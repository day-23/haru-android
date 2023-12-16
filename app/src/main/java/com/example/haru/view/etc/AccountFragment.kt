package com.example.haru.view.etc

import BaseActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.databinding.FragmentAccountBinding
import com.example.haru.utils.Tags
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.sns.EditProfileFragment
import com.example.haru.viewmodel.EtcViewModel

class AccountFragment(val etcViewModel: EtcViewModel) : Fragment() {
    private lateinit var binding: FragmentAccountBinding

    companion object {
        const val TAG: String = "로그"

        fun newInstance(etcViewModel: EtcViewModel): AccountFragment {
            return AccountFragment(etcViewModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(Tags.log, "AccountFragment - onCreate() called")
        MainActivity.hideNavi(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.accountHeaderTitle.id)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.accountHeaderTitle.id)

        etcViewModel.getSnsInfo()

        etcViewModel.email.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvEmail.text = it
        })

        etcViewModel.name.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvName.text = it
        })

        etcViewModel.haruId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvHaruId.text = it
        })

        etcViewModel.profileImage.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it == "" || it == null)
                binding.ivCircleProfileImage.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.profile_base_image)
            else Glide.with(this)
                .load(it)
                .into(binding.ivCircleProfileImage)
        })

        etcViewModel.introduction.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvAccountIntroduction.text = it
        })

        etcViewModel.name.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.tvAccountName.text = it
        })


        binding.ivBackIconAccount.setOnClickListener(ClickListener())
        binding.tvAccountDelete.setOnClickListener(ClickListener())
        binding.ivProfileEdit.setOnClickListener(ClickListener())
        binding.tvName.setOnClickListener(ClickListener())
    }

    inner class ClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            when (v?.id) {
                binding.ivBackIconAccount.id -> {
                    requireActivity().supportFragmentManager.popBackStack()
                }

                binding.tvAccountDelete.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fragment_in,
                            R.anim.fragment_out,
                            R.anim.popstack_in,
                            R.anim.popstack_out
                        ).replace(R.id.fragments_frame, AccountDeleteFragment(etcViewModel))
                        .addToBackStack(null)
                        .commit()
                }

                binding.tvName.id, binding.ivProfileEdit.id -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.fragment_in,
                            R.anim.fragment_out,
                            R.anim.popstack_in,
                            R.anim.popstack_out
                        ).replace(R.id.fragments_frame, EditProfileFragment(User.id))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }
}