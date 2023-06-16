package com.example.haru.view.sns

import BaseActivity
import UserViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat.animate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.haru.R
import com.example.haru.data.api.UserService
import com.example.haru.data.model.Post
import com.example.haru.data.repository.UserRepository
import com.example.haru.databinding.FragmentSnsBinding
import com.example.haru.databinding.PopupSnsPostCancelBinding
import com.example.haru.databinding.PopupSnsPostDeleteBinding
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel
import com.example.haru.viewmodel.UserViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface OnPostPopupClick {
    fun postPopupClicked(userId: String, postId: String, position: Int)
    fun PopupConfirm(userId: String, postId: String, position: Int)
}

class SnsFragment : Fragment(), OnPostClickListener, OnPostPopupClick {
    private lateinit var userViewModel: UserViewModel
    private lateinit var snsViewModel: SnsViewModel
    private lateinit var profileViewModel: MyPageViewModel
    private lateinit var binding: FragmentSnsBinding
    private var click = false
    private var postClicked = false
    private lateinit var snsPostAdapter: SnsPostAdapter
    var lastDate = ""
    var deletedItem : Post = Post()

    override fun onCommentClick(postitem: Post) {
        profileViewModel.getUserInfo(User.id)
        profileViewModel.UserInfo.observe(viewLifecycleOwner) { user ->
            val newFrag = AddCommentFragment(postitem.id, postitem.images, user)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
            if (!isSnsMainInBackStack)
                transaction.addToBackStack("snsmain")
            transaction.commit()
        }
    }

    override fun onTotalCommentClick(post: Post) {
        val newFrag = CommentsFragment(post, User.id)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
        if (!isSnsMainInBackStack)
            transaction.addToBackStack("snsmain")
        transaction.commit()
    }

    override fun onProfileClick(userId: String) {
        val newFrag = MyPageFragment(userId)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
        if (!isSnsMainInBackStack)
            transaction.addToBackStack("snsmain")
        transaction.commit()
    }

    override fun onSetupClick(userId: String, postId: String, item: Post) {
        deletedItem = item
        val fragment = PopupDeletePost(userId, postId, this)
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.sns_post_anchor, fragment)
        transaction.commit()
    }

    override fun postPopupClicked(userId: String, postId: String, position: Int) {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.sns_post_anchor)
        if (fragment != null) {
            MainActivity.hideNavi(false)
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
            if (position == 0) {
                //TODO:숨기기 혹은 수정하기
            } else if (position == 1) {
                if (User.id == userId) {
                    val fragment = PopupDeleteConfirm(userId, postId, this)
                    transaction.add(R.id.sns_post_anchor, fragment)
                }else{

                }
            }
        }
    }

    override fun PopupConfirm(userId: String, postId: String, position: Int) {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.sns_post_anchor)
        if (fragment != null) {
            MainActivity.hideNavi(false)
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
            if (position == 0) {
                Toast.makeText(requireContext(), "삭제 요청중...", Toast.LENGTH_SHORT).show()
                snsViewModel.deletePost(postId)
            }
        }
    }

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): SnsFragment {
            return SnsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SnsFragment - onCreate() called")
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
        profileViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)

    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "sns onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id, 1.1f)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id, 1.1f)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "SnsFragment - onCreateView() called")
        val manager = parentFragmentManager
        manager.clearBackStack("snsmain")
        binding = FragmentSnsBinding.inflate(inflater, container, false)
        binding.friendFeed.setTextColor(0xFF1DAFFF.toInt())
        val postRecycler = binding.postOfAll
        snsPostAdapter = SnsPostAdapter(requireContext(), arrayListOf(), this)
        snsViewModel.getFirstPosts()
        postRecycler.layoutManager = LinearLayoutManager(requireContext())
        postRecycler.adapter = snsPostAdapter

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!postRecycler.canScrollVertically(1)) {
                    snsViewModel.getPosts(lastDate)
                }
            }
        }
        postRecycler.addOnScrollListener(scrollListener)

        val refresher = binding.refreshPost
        refresher.setOnRefreshListener {
            refresher.isRefreshing = true
            snsViewModel.getFirstPosts()
            refresher.isRefreshing = false
        }

        snsViewModel.newPost.observe(viewLifecycleOwner) { newPost ->
            if (newPost.isNotEmpty()) {
                snsPostAdapter.newPage(newPost)
                getLastDate(newPost)
            } else Toast.makeText(context, "모든 게시글을 불러왔습니다.", Toast.LENGTH_SHORT).show()
        }

        snsViewModel.DeleteResult.observe(viewLifecycleOwner) { result ->
            if(result && deletedItem.id != ""){
                snsPostAdapter.deletePost(deletedItem)
            }
        }

        snsViewModel.Posts.observe(viewLifecycleOwner) { post ->
            if (post.isNotEmpty()) {
                snsPostAdapter.initList(post)
                getLastDate(post)
            } else Toast.makeText(context, "게시글이 없습니다..", Toast.LENGTH_SHORT).show()
        }

        binding.writeHaru.setOnClickListener {
            val newFrag = WriteHaruFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
            if (!isSnsMainInBackStack)
                transaction.addToBackStack("snsmain")
            transaction.commit()

            binding.writeHaru.visibility = View.GONE
            binding.addPost.setImageResource(R.drawable.add_sns)
            postClicked = false
        }

        //하루 옆 메뉴 클릭
        binding.menuButton.setOnClickListener {
            if (click == false) {
                binding.snsButtons.visibility = View.VISIBLE
                binding.menuButton.animate().rotation(0f)
                click = true
            } else {
                binding.snsButtons.visibility = View.GONE
                binding.menuButton.animate().rotation(-90f)
                click = false
            }
        }

        //내 피드 보기 클릭
        binding.myRecords.setOnClickListener {
            onProfileClick(User.id)
        }

        binding.addPost.setOnClickListener {
            if(postClicked) {
                val newFrag = AddPostFragment.newInstance()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.fragments_frame, newFrag)
                val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
                if (!isSnsMainInBackStack)
                    transaction.addToBackStack("snsmain")
                transaction.commit()

                binding.writeHaru.visibility = View.GONE
                binding.addPost.setImageResource(R.drawable.add_sns)
            } else {
                binding.writeHaru.visibility = View.VISIBLE
                binding.addPost.setImageResource(R.drawable.add_sns_post)
            }

            postClicked = !postClicked
        }

        //둘러보기 클릭
        binding.lookAround.setOnClickListener {
            val newFrag = LookAroundFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
            if (!isSnsMainInBackStack)
                transaction.addToBackStack("snsmain")
            transaction.commit()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/todos/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient())
            .build()

        val userService = retrofit.create(UserService::class.java)
        val userRepository = UserRepository()

        val viewModelFactory = UserViewModelFactory(userRepository)
        userViewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)



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

    fun getLastDate(items: ArrayList<Post>) {
        val index = items.size - 1
        lastDate = items[index].createdAt
    }

}

class PopupDeletePost(val userId: String, val postId: String, listener: OnPostPopupClick) :
    Fragment() {
    lateinit var popupbinding: PopupSnsPostDeleteBinding
    val listener = listener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        popupbinding = PopupSnsPostDeleteBinding.inflate(inflater, container, false)
        MainActivity.hideNavi(true)

        if (userId == User.id) {
            popupbinding.editOrHide.text = "게시글 수정하기"
            popupbinding.deleteOrReport.text = "게시글 삭제하기"
        }


        popupbinding.editOrHide.setOnClickListener {
            listener.postPopupClicked(userId, postId, 0)
        }

        popupbinding.deleteOrReport.setOnClickListener {
            listener.postPopupClicked(userId, postId, 1)
        }

        popupbinding.popupPostContainer.setOnClickListener {
            listener.postPopupClicked(userId, postId, 2)
        }

        return popupbinding.root
    }
}

class PopupDeleteConfirm(val userId: String, val postId: String, listener: OnPostPopupClick) :
    Fragment() {
    lateinit var popupbinding: PopupSnsPostCancelBinding
    val listener = listener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        popupbinding = PopupSnsPostCancelBinding.inflate(inflater, container, false)
        popupbinding.postCancelText.text = "게시글을 삭제할까요? 이 작업은 복원할 수 없습니다."

        MainActivity.hideNavi(true)
        popupbinding.snsAddPostUnsave.setOnClickListener {
            listener.PopupConfirm(userId, postId, 0)
        }

        popupbinding.snsAddPostCancel.setOnClickListener {
            listener.PopupConfirm(userId, postId, 1)
        }

        return popupbinding.root
    }
}