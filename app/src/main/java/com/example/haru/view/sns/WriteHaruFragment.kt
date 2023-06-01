package com.example.haru.view.sns

import BaseActivity
import UserViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.api.UserService
import com.example.haru.data.repository.UserRepository
import com.example.haru.databinding.FragmentSnsBinding
import com.example.haru.databinding.FragmentWriteHaruBinding
import com.example.haru.databinding.PopupSnsPostCancelBinding
import com.example.haru.utils.User
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.UserViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WriteHaruFragment : Fragment() ,PostInterface{
    lateinit var binding : FragmentWriteHaruBinding

    override fun Postpopup(position: Int) {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.write_haru_anchor)

        if(fragment != null) {
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()

            if(position == 0){
                val backManager = parentFragmentManager
                backManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragment = SnsFragment()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragments_frame, fragment)
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.writeHaruTitle.id)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).adjustTopMargin(binding.writeHaruTitle.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWriteHaruBinding.inflate(inflater, container, false)

        binding.addpostCancel.setOnClickListener {
            val fragment = PopupWrite(this)
            val fragmentManager = childFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.add(R.id.write_haru_anchor, fragment)
            transaction.commit()
        }

        binding.writeHaruApply.setOnClickListener {

            if(binding.writeHaruContent.text.toString() != "") {
                val newFrag = WriteHaruTagFragment(binding.writeHaruContent.text.toString())
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragments_frame, newFrag)
                transaction.addToBackStack(null)
                transaction.commit()
            }else{
                Toast.makeText(requireContext(), "텍스트를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }


}

class PopupWrite(listener: PostInterface) : Fragment() {

    lateinit var popupbinding : PopupSnsPostCancelBinding
    val listener = listener
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        popupbinding = PopupSnsPostCancelBinding.inflate(inflater, container, false)

        popupbinding.snsAddPostUnsave.setOnClickListener {
            listener.Postpopup(0)
        }

        popupbinding.snsAddPostCancel.setOnClickListener {
            listener.Postpopup(1)
        }

        return popupbinding.root
    }

}
