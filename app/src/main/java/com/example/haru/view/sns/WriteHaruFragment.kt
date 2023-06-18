package com.example.haru.view.sns

import BaseActivity
import android.app.Dialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.haru.R
import com.example.haru.databinding.FragmentWriteHaruBinding
import com.example.haru.databinding.PopupSnsPostCancelBinding
import com.example.haru.view.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WriteHaruFragment : Fragment(), PostInterface {
    lateinit var binding: FragmentWriteHaruBinding

    override fun Postpopup(position: Int) {
        if (position == 0) {
            requireActivity().supportFragmentManager.popBackStack()
//            val backManager = parentFragmentManager
//            backManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//            val fragment = SnsFragment()
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragments_frame, fragment)
//                .commit()
        }

//        val fragmentManager = childFragmentManager
//        val fragment = fragmentManager.findFragmentById(R.id.write_haru_anchor)
//
//        if(fragment != null) {
//            val transaction = fragmentManager.beginTransaction()
//            transaction.remove(fragment)
//            transaction.commit()
//
//            if(position == 0){
//                val backManager = parentFragmentManager
//                backManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//                val fragment = SnsFragment()
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.fragments_frame, fragment)
//                    .commit()
//            }
//        }
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.writeHaruTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.writeHaruTitle.id)

        MainActivity.hideNavi(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteHaruBinding.inflate(inflater, container, false)

        binding.addpostCancel.setOnClickListener {

            val fragment = PopupWrite(this)
            fragment.show(parentFragmentManager, fragment.tag)
//            val fragmentManager = childFragmentManager
//            val transaction = fragmentManager.beginTransaction()
//            transaction.add(R.id.write_haru_anchor, fragment)
//            transaction.commit()
        }

        binding.writeHaruApply.setOnClickListener {

            if (binding.writeHaruContent.text.toString() != "") {
                val newFrag = WriteHaruTagFragment(binding.writeHaruContent.text.toString())
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.fragment_in,
                    R.anim.fragment_out,
                    R.anim.popstack_in,
                    R.anim.popstack_out
                ).replace(R.id.fragments_frame, newFrag)
                transaction.addToBackStack(null)
                transaction.commit()
            } else {
                Toast.makeText(requireContext(), "텍스트를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.writeHaruContent.addTextChangedListener(object :
            TextWatcher { // 소프트웨어 키보드의 스페이스바 감지
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null) {
                    binding.addpostCancel.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.cancel_icon)
                    binding.addpostCancel.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.date_text
                        )
                    )
                    return
                }

                val str = s.toString()
                if (str == "") {
                    binding.addpostCancel.background =
                        ContextCompat.getDrawable(requireContext(), R.drawable.cancel_icon)
                    binding.addpostCancel.backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.date_text
                        )
                    )
                    return
                }

                binding.addpostCancel.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.back_arrow)
                binding.addpostCancel.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.highlight
                    )
                )
            }

        })

        return binding.root
    }


}

class PopupWrite(val listener: PostInterface) : BottomSheetDialogFragment() {

    lateinit var popupbinding: PopupSnsPostCancelBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        popupbinding = PopupSnsPostCancelBinding.inflate(inflater)

        popupbinding.snsAddPostUnsave.setOnClickListener {
            listener.Postpopup(0)
            dismiss()
        }

        popupbinding.snsAddPostCancel.setOnClickListener {
            listener.Postpopup(1)
            dismiss()
        }

        return popupbinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from<View>(bottomSheet)
        val layoutParams = bottomSheet!!.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 27 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }

}
