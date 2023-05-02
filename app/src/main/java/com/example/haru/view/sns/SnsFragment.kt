package com.example.haru.view.sns

import UserViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.api.UserService
import com.example.haru.data.model.SnsPost
import com.example.haru.data.repository.UserRepository
import com.example.haru.databinding.FragmentSnsBinding
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.view.adapter.TimetableAdapter
import com.example.haru.view.timetable.TodotableFragment
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel
import com.example.haru.viewmodel.UserViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SnsFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var snsViewModel: SnsViewModel
    private lateinit var binding: FragmentSnsBinding
    private var click = false
    private lateinit var snsPostAdapter: SnsPostAdapter

    companion object{
        const val TAG : String = "로그"

        fun newInstance() : SnsFragment {
            return SnsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SnsFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "SnsFragment - onCreateView() called")

        binding = FragmentSnsBinding.inflate(inflater, container, false)
        val postRecycler = binding.postOfAll
        snsPostAdapter = SnsPostAdapter(requireContext())

        postRecycler.layoutManager = LinearLayoutManager(requireContext())
        postRecycler.adapter = snsPostAdapter

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!postRecycler.canScrollVertically(1)) {
                    snsViewModel.addPage()
                    Toast.makeText(context, "새 페이지 불러오는 중....", Toast.LENGTH_SHORT).show()
                }
            }
        }
        postRecycler.addOnScrollListener(scrollListener)

        snsViewModel.Page.observe(viewLifecycleOwner){page ->
            val pagestr = page.toString()
            snsViewModel.getPosts(pagestr)
        }

        snsViewModel.newPost.observe(viewLifecycleOwner){newPost ->
            snsPostAdapter.newPage(newPost)
            if(newPost.size == 0) Toast.makeText(context, "모든 게시글을 불러왔습니다.", Toast.LENGTH_SHORT).show()
        }

        //하루 옆 메뉴 클릭
        binding.menuButton.setOnClickListener{
            if(click == false){
                binding.snsButtons.visibility = View.VISIBLE
                click = true
            }
            else{
                binding.snsButtons.visibility = View.GONE
                click = false
            }
        }

        //내 피드 보기 클릭
        binding.myRecords.setOnClickListener {
            val newFrag = MyPageFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
            if(!isSnsMainInBackStack)
                transaction.addToBackStack("snsmain")
            transaction.commit()
        }

        binding.addPost.setOnClickListener {
            val newFrag = AddPostFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
            if(!isSnsMainInBackStack)
                transaction.addToBackStack("snsmain")
            transaction.commit()
        }

        //둘러보기 클릭
        binding.lookAround.setOnClickListener {

        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/todos/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()

        val userService = retrofit.create(UserService::class.java)
        val userRepository = UserRepository(userService)

        val viewModelFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)



        val userId = 1// Replace with actual user ID
        userViewModel.fetchUser(userId)

        return binding.root
    }

    fun isFragmentInBackStack(fragmentManager: FragmentManager, tag: String): Boolean {
        for (i in 0 until fragmentManager.backStackEntryCount) {
            val backStackEntry = fragmentManager.getBackStackEntryAt(i)
            if (backStackEntry.name == tag) {
                return true
            }
        }
        return false
    }

}