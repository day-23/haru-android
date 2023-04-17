package com.example.haru.view.sns

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.model.SnsPost
import com.example.haru.databinding.FragmentSnsMypageBinding
import com.example.haru.view.adapter.MyFeedAdapter
import com.example.haru.view.adapter.SnsPostAdapter

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentSnsMypageBinding
    private lateinit var myFeedRecyclerView: RecyclerView
    private lateinit var feedAdapter: SnsPostAdapter
    private lateinit var mediaAdapter: MyFeedAdapter
    private var click = false

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : MyPageFragment {
            return MyPageFragment()
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

        binding = FragmentSnsMypageBinding.inflate(inflater, container, false)
        val dummy = arrayListOf<SnsPost>(SnsPost("","",""),SnsPost("","",""),SnsPost("","",""),SnsPost("","",""),SnsPost("","",""))
        myFeedRecyclerView = binding.feedRecycler
        myFeedRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        feedAdapter = SnsPostAdapter(requireContext(), dummy)
        myFeedRecyclerView.adapter = feedAdapter

        binding.menuButton.setOnClickListener {
            if(click == false){
                binding.snsButtons.visibility = View.VISIBLE
                click = true
            }
            else{
                binding.snsButtons.visibility = View.GONE
                click = false
            }
        }

        binding.editProfile.setOnClickListener {
            val newFrag = EditProfileFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        binding.friendFeed.setOnClickListener {
            val newFrag = SnsFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack(null)
            transaction.commit()
            true
        }

        return binding.root
    }
}