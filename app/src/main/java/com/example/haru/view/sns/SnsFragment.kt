package com.example.haru.view.sns

import BaseActivity
import UserViewModelFactory
import android.app.Dialog
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
    private var startBoolean = false
    private lateinit var snsPostAdapter: SnsPostAdapter
    var lastDate = ""

    var deletedItem : Post = Post()
    var postitem = Post()
    var commentClicked = false
    var postAllLoaded = false
    override fun onCommentClick(postitems: Post) {
        commentClicked = true
        postitem = postitems
        profileViewModel.getUserInfo(postitem.user.id)
    }

    override fun onTotalCommentClick(post: Post) {
        val newFrag = CommentsFragment(post, User.id)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.fragment_in,
            R.anim.fragment_out,
            R.anim.popstack_in,
            R.anim.popstack_out
        ).replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
        if (!isSnsMainInBackStack)
            transaction.addToBackStack("snsmain")
        transaction.commit()
    }

    override fun onProfileClick(userId: String) {
        val newFrag = MyPageFragment(userId)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.fragment_in,
            R.anim.fragment_out,
            R.anim.popstack_in,
            R.anim.popstack_out
        ).replace(R.id.fragments_frame, newFrag)
        val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
        if (!isSnsMainInBackStack)
            transaction.addToBackStack("snsmain")
        transaction.commit()
    }

    override fun onSetupClick(userId: String, postId: String, item: Post) {
        deletedItem = item
        val fragment = PopupDeletePost(userId, postId, this)
        fragment.show(parentFragmentManager, fragment.tag)
//        val fragment = PopupDeletePost(userId, postId, this)
//        val fragmentManager = childFragmentManager
//        val transaction = fragmentManager.beginTransaction()
//        transaction.add(R.id.sns_post_anchor, fragment)
//        transaction.commit()
    }

    override fun postPopupClicked(userId: String, postId: String, position: Int) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        if (position == 0) {
            if (User.id == userId) {//수정하기

            } else {//이 게시글 숨기기
                snsViewModel.hidePost(postId)
            }
        } else if (position == 1) {
            if (User.id == userId) {//삭제확인창
                val fragment = PopupDeleteConfirm(userId, postId, this)
                fragment.show(parentFragmentManager, fragment.tag)
//                transaction.add(R.id.sns_post_anchor, fragment)
//                transaction.addToBackStack(null)
//                transaction.commit()
            } else { //신고하기
                snsViewModel.reportPost(postId)
            }
        }
//        val fragmentManager = childFragmentManager
//        val fragment = fragmentManager.findFragmentById(R.id.sns_post_anchor)
//        if (fragment != null) {
//            Log.e("20191627", "123")
//            MainActivity.hideNavi(false)
//            val transaction = fragmentManager.beginTransaction()
//        }
    }

    override fun PopupConfirm(userId: String, postId: String, position: Int) {
        if (position == 0) {
            Toast.makeText(requireContext(), "삭제 요청중...", Toast.LENGTH_SHORT).show()
            snsViewModel.deletePost(postId)
        }
//        requireActivity().supportFragmentManager.popBackStack()

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
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id)
        startBoolean = true
        MainActivity.hideNavi(false)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id)
    }

    @SuppressLint("ClickableViewAccessibility")
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
        snsViewModel.getFirstFeeds()
        postRecycler.layoutManager = LinearLayoutManager(requireContext())
        postRecycler.adapter = snsPostAdapter

        val scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(click){
                    fadeOutAndHideView(binding.snsButtons)
                    binding.menuButton.animate().rotation(-90f)
                    click = false
                }
                if(postClicked){
                    fadeOutAndHideView(binding.drawHaru)
                    binding.addPost.setImageResource(R.drawable.add_sns)
                    postClicked = false
                }
                if (dy < 0){
                    fadeInAndShowView(binding.addPost)
                }

                if (!postRecycler.canScrollVertically(1)) {
                    if(!postAllLoaded) {
                        snsViewModel.getFeeds(lastDate)
                        fadeOutAndHideView(binding.addPost)
                    }
                }
            }
        }
        postRecycler.addOnScrollListener(scrollListener)

        val refresher = binding.refreshPost
        refresher.setOnRefreshListener {
            refresher.isRefreshing = true
            postAllLoaded = false
            snsViewModel.getFirstFeeds()
            refresher.isRefreshing = false
        }

        snsViewModel.newPost.observe(viewLifecycleOwner) { newPost ->
            if (newPost.isNotEmpty()) {
                snsPostAdapter.newPage(newPost)
                getLastDate(newPost)
            } else {
                postAllLoaded = true
                Toast.makeText(context, "모든 게시글을 불러왔습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        snsViewModel.DeleteResult.observe(viewLifecycleOwner) { result ->
            if (result && deletedItem.id != "") {
                snsPostAdapter.deletePost(deletedItem)
            }
        }

        profileViewModel.UserInfo.observe(viewLifecycleOwner) { user ->
            if(commentClicked){
                commentClicked = false
                val newFrag = AddCommentFragment(
                    postitem.isTemplatePost,
                    postitem.content,
                    postitem.id,
                    postitem.images,
                    postitem.likedCount,
                    postitem.commentCount,
                    user
                )
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.fragment_in,
                    R.anim.fragment_out,
                    R.anim.popstack_in,
                    R.anim.popstack_out
                ).replace(R.id.fragments_frame, newFrag)
                val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
                if (!isSnsMainInBackStack)
                    transaction.addToBackStack("snsmain")
                transaction.commit()
            }
        }

        snsViewModel.Posts.observe(viewLifecycleOwner) { post ->
            if (post.isNotEmpty()) {
                snsPostAdapter.initList(post)
                getLastDate(post)
            } else {
                postAllLoaded = true
                Toast.makeText(context, "게시글이 없습니다..", Toast.LENGTH_SHORT).show()
            }
        }

        binding.drawHaru.setOnClickListener {
            val newFrag = AddPostFragment.newInstance()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.fragment_in,
                R.anim.fragment_out,
                R.anim.popstack_in,
                R.anim.popstack_out
            ).replace(R.id.fragments_frame, newFrag)
            val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
            if (!isSnsMainInBackStack)
                transaction.addToBackStack("snsmain")
            transaction.commit()

            binding.drawHaru.visibility = View.GONE
            binding.addPost.setImageResource(R.drawable.add_sns)
            postClicked = false
        }

        //하루 옆 메뉴 클릭
        binding.menuButton.setOnClickListener {
            if (!click) {
                fadeInAndShowView(binding.snsButtons)
                binding.menuButton.animate().rotation(0f)
                click = true
            } else {
                fadeOutAndHideView(binding.snsButtons)
                binding.menuButton.animate().rotation(-90f)
                click = false
            }
        }

        binding.snsHaruTitle.setOnClickListener {
            binding.menuButton.performClick()
        }

        //내 피드 보기 클릭
        binding.myRecords.setOnClickListener {
            onProfileClick(User.id)
        }

        binding.addPost.setOnClickListener {
            if (postClicked) {
                val newFrag = WriteHaruFragment()
                val transaction = parentFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.fragment_in,
                    R.anim.fragment_out,
                    R.anim.popstack_in,
                    R.anim.popstack_out
                ).replace(R.id.fragments_frame, newFrag)
                val isSnsMainInBackStack = isFragmentInBackStack(parentFragmentManager, "snsmain")
                if (!isSnsMainInBackStack)
                    transaction.addToBackStack("snsmain")
                transaction.commit()

                binding.drawHaru.visibility = View.GONE
                binding.addPost.setImageResource(R.drawable.add_sns)
            } else {

                binding.drawHaru.visibility = View.INVISIBLE
                scaleAndShowView(binding.drawHaru)
                binding.addPost.setImageResource(R.drawable.add_sns_post)
            }

            postClicked = !postClicked
        }

        //둘러보기 클릭
        binding.lookAround.setOnClickListener {
            val newFrag = LookAroundFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.fragment_in,
                R.anim.fragment_out,
                R.anim.popstack_in,
                R.anim.popstack_out
            ).replace(R.id.fragments_frame, newFrag)
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

    //점점 사라지는 애니메이션
    fun fadeOutAndHideView(view: View) {
        val fadeOutAnimation = AlphaAnimation(1.0f, 0.0f)
        fadeOutAnimation.duration = 400 // 1 second

        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        view.startAnimation(fadeOutAnimation)
    }

    //점점 나타나는 애니메이션
    fun fadeInAndShowView(view: View) {
        val fadeOutAnimation = AlphaAnimation(0.0f, 1.0f)
        fadeOutAnimation.duration = 400

        fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        view.startAnimation(fadeOutAnimation)
    }

    //크기 커지는 애니메이션
    private fun scaleAndShowView(view: View) {
        val scaleAnimation = ScaleAnimation(
            0.0f, 1.0f,  // Start and end scale X
            0.0f, 1.0f,  // Start and end scale Y
            Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot X (center horizontally)
            Animation.RELATIVE_TO_SELF, 0.5f   // Pivot Y (center vertically)
        )
        scaleAnimation.duration = 300

        scaleAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {}

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        view.startAnimation(scaleAnimation)
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

class PopupDeletePost(val userId: String, val postId: String, val listener: OnPostPopupClick) :
    BottomSheetDialogFragment() {
    private lateinit var binding: PopupSnsPostDeleteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PopupSnsPostDeleteBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userId == User.id) {
            binding.editOrHide.text = "게시글 수정하기"
            binding.tvDelete.text = "게시글 삭제하기"
        }


        binding.editOrHide.setOnClickListener {
            Log.e("20191627", "click0")
            listener.postPopupClicked(userId, postId, 0)
            dismiss()
        }

        binding.deleteOrReport.setOnClickListener {
            Log.e("20191627", "click1")
            listener.postPopupClicked(userId, postId, 1)
            dismiss()
        }

        binding.popupPostContainer.setOnClickListener {
            Log.e("20191627", "click2")
            listener.postPopupClicked(userId, postId, 2)
            dismiss()
        }
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
        return getWindowHeight() * 23 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}

class PopupDeleteConfirm(val userId: String, val postId: String, val listener: OnPostPopupClick) :
    BottomSheetDialogFragment() {
    lateinit var popupbinding: PopupSnsPostCancelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        popupbinding = PopupSnsPostCancelBinding.inflate(inflater, container, false)

        return popupbinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        popupbinding.snsAddPostUnsave.setOnClickListener {
            Log.e("20191627", "click0")
            listener.PopupConfirm(userId, postId, 0)
            dismiss()
        }

        popupbinding.snsAddPostCancel.setOnClickListener {
            Log.e("20191627", "click1")
            listener.PopupConfirm(userId, postId, 1)
            dismiss()
        }
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