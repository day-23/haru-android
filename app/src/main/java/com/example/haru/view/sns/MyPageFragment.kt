package com.example.haru.view.sns

import BaseActivity
import android.app.Dialog
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.*
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.MediaAdapter
import com.example.haru.view.adapter.MediaTagAdapter
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

interface MediaClick {
    fun onMediaClick(media: Media)

    fun onDeleteClick(position: Int)

    fun onBlockClick(position: Int)
}

class MyPageFragment(userId: String) : Fragment(), OnPostClickListener, OnMediaTagClicked,
    MediaClick, OnPostPopupClick {
    private lateinit var binding: FragmentSnsMypageBinding
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var mediaRecyclerView: RecyclerView
    private lateinit var tagRecyclerView: RecyclerView
    private lateinit var feedAdapter: SnsPostAdapter
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var tagAdapter: MediaTagAdapter
    private lateinit var mypageViewModel: MyPageViewModel
    private lateinit var snsViewModel: SnsViewModel
    private var isFeedClick = true
    private var isFullLoaded = false //게시글 페이지네이션이 끝났는지
    private var isDelete = false //친구 삭제인지 차단인지
    var deletedItem = Post()
    val userId = userId
    var userInfo = User()
    var isMyPage = false
    var friendStatus = 0
    var selectedTag: MediaTagAdapter.MediaTagViewHolder? = null
    var lastDate = ""
    var index = 0

    override fun onMediaClick(media: Media) {
        val dummyMedia = Post()
        val newFrag = DetailFragment(media, Post())
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        transaction.addToBackStack("snsmypage")
        transaction.commit()
    }

    override fun onBlockClick(position: Int) {
        if (position == 0) {
            val confirmFragment = MypageDeleteFriend(userInfo, isDelete, this)
            confirmFragment.show(parentFragmentManager, confirmFragment.tag)
        }
    }

    override fun onDeleteClick(position: Int) {
        if (position == 0 && isDelete) {
            requestDelFriend() //친구끊기
        } else if (position == 0 && !isDelete) {
            blockUser() //유저차단
        }
    }

    override fun onCommentClick(postitem: Post) {
        mypageViewModel.getUserInfo(userId)

        if (postitem.isTemplatePost != null) {
            val newFrag = CommentsFragment(postitem, com.example.haru.utils.User.id)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack("snsmypage")
            transaction.commit()
        } else {
            mypageViewModel.UserInfo.observe(viewLifecycleOwner) { user ->
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
                transaction.replace(R.id.fragments_frame, newFrag)
                transaction.addToBackStack("snsmypage")
                transaction.commit()
            }
        }
    }

    override fun onTotalCommentClick(post: Post) {
        val newFrag = CommentsFragment(post, com.example.haru.utils.User.id)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        transaction.addToBackStack("snsmypage")
        transaction.commit()
    }

    override fun onProfileClick(userId: String) {
        //Don't need to implement
    }

    override fun onSetupClick(userId: String, postId: String, item: Post) {
        deletedItem = item
        val fragment = PopupDeletePost(userId, postId, this)
        fragment.show(parentFragmentManager, fragment.tag)
    }

    override fun postPopupClicked(userId: String, postId: String, position: Int) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        if (position == 0) {
        } else if (position == 1) {
            if (User.id == userId) {//삭제확인창
                val fragment = PopupDeleteConfirm(userId, postId, this)
                fragment.show(parentFragmentManager, fragment.tag)
            }

        }
    }

    override fun PopupConfirm(userId: String, postId: String, position: Int) {
        if (position == 0) {
            Toast.makeText(requireContext(), "삭제 요청중...", Toast.LENGTH_SHORT).show()
            snsViewModel.deletePost(postId)
        }
    }

    override fun onTagClicked(tag: Tag, holder: MediaTagAdapter.MediaTagViewHolder) {
        holder.tag.setBackgroundResource(R.drawable.tag_btn_clicked)
        holder.tag.setTextColor(Color.parseColor("#fdfdfd"))
        if (holder != selectedTag) {
            mypageViewModel.getFirstTagMedia(userId, tag.id)
        }

        selectedTag?.tag?.setBackgroundResource(R.drawable.tag_btn_custom)
        selectedTag?.tag?.setTextColor(Color.parseColor("#191919"))

        if (holder == selectedTag) {
            mypageViewModel.getFirstMedia(userId)
            selectedTag = null
        } else {
            selectedTag = holder
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TAG", "MypageFragment - onCreate() called")
        mypageViewModel = ViewModelProvider(this).get(MyPageViewModel::class.java)
        snsViewModel = ViewModelProvider(this).get(SnsViewModel::class.java)
    }

    // status bar height 조정
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(SnsFragment.TAG, "sns onViewCreated: ")
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id)

        MainActivity.hideNavi(false)
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id)
        initProfile()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "MyPageFragment - onCreateView() called")
        binding = FragmentSnsMypageBinding.inflate(inflater, container, false)

        initProfile()
        mypageViewModel.getFirstFeed(userId)
        mypageViewModel.getFirstMedia(userId)
        mypageViewModel.getUserTags(userId)

        mypageViewModel.UserInfo.observe(viewLifecycleOwner) { user ->
            userInfo = user
            binding.profileName.text = user.name
            binding.profileIntroduction.text = user.introduction
            binding.profilePostCount.text = user.postCount.toString()
            binding.profileFriendsCount.text = user.friendCount.toString()
            friendStatus = user.friendStatus
            if (!isMyPage) { //타인의 페이지라면
                if (user.friendStatus == 0) {
                    binding.editProfile.setBackgroundResource(R.drawable.gradation_btn_custom)
                    binding.editProfile.setTextColor(Color.parseColor("#FDFDFD"))
                    binding.editProfile.text = "친구 신청"
                } else if (user.friendStatus == 1) {
                    binding.editProfile.setBackgroundResource(R.drawable.total_comment_index)
                    binding.editProfile.setTextColor(Color.parseColor("#646464"))
                    binding.editProfile.text = "신청 취소"
                } else if (user.friendStatus == 2) {
                    binding.editProfile.setBackgroundResource(R.drawable.total_comment_index)
                    binding.editProfile.setTextColor(Color.parseColor("#646464"))
                    binding.editProfile.text = "내 친구"
                } else if (user.friendStatus == 3) {
                    binding.editProfile.setBackgroundResource(R.drawable.total_comment_index)
                    binding.editProfile.setTextColor(Color.parseColor("#646464"))
                    binding.editProfile.text = "수락"
                }
            }

            if (user.profileImage == "" || user.profileImage == null)
                binding.profileImage.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.profile_base_image)
            else Glide.with(this)
                .load(user.profileImage)
                .into(binding.profileImage)
        }

        binding.select.bringToFront()
        binding.mypageScroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY >= binding.select.top) {
                binding.select.translationY = (scrollY - binding.select.top).toFloat()
            } else {
                binding.select.translationY = 0f
            }

            //스크롤이 끝까지 닿으면
            val isScrolledToBottom =
                scrollY >= binding.mypageScroll.getChildAt(0).measuredHeight - binding.mypageScroll.measuredHeight
            if (isScrolledToBottom) {
                if (isFeedClick && !isFullLoaded) {
                    mypageViewModel.getFeed(userId, lastDate)
                } else if (!isFeedClick && !isFullLoaded) {
                    mypageViewModel.getMedia(userId, lastDate)
                }
            }
        }

        feedRecyclerView = binding.feedRecycler
        feedAdapter = SnsPostAdapter(requireContext(), arrayListOf(), this)
        feedRecyclerView.adapter = feedAdapter
        val layoutManager = LinearLayoutManager(context)
        feedRecyclerView.layoutManager = layoutManager

        mediaRecyclerView = binding.mediaRecycler
        mediaLayout()
        mediaAdapter = MediaAdapter(requireContext(), arrayListOf(), this)
        mediaRecyclerView.adapter = mediaAdapter

        tagRecyclerView = binding.mediaTags
        tagAdapter = MediaTagAdapter(requireContext(), arrayListOf(), this)

        tagRecyclerView.adapter = tagAdapter
        tagRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        mypageViewModel.InitFeed.observe(viewLifecycleOwner) { feeds ->
            if (feeds.size > 0) {
                index = feeds.size - 1
                lastDate = feeds[index].createdAt
                feedAdapter.initList(feeds)
            }
        }

        snsViewModel.DeleteResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                feedAdapter.deletePost(deletedItem)
            }
        }

        mypageViewModel.NewFeed.observe(viewLifecycleOwner) { feeds ->
            if (feeds.size > 0) {
                index = feeds.size - 1
                lastDate = feeds[index].createdAt
                feedAdapter.newPage(feeds)
            } else {
                isFullLoaded = true
                Toast.makeText(context, "모든 게시글을 불러왔습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        mypageViewModel.FirstMedia.observe(viewLifecycleOwner) { medias ->
            if (medias.data.size > 0) {
                index = medias.data.size - 1
                lastDate = medias.data[index].createdAt
                mediaAdapter.firstPage(medias.data)
            }
        }

        mypageViewModel.Media.observe(viewLifecycleOwner) { medias ->
            if (medias.data.size > 0) {
                index = medias.data.size - 1
                lastDate = medias.data[index].createdAt
                mediaAdapter.newPage(medias.data)
            } else {
                isFullLoaded = true
                Toast.makeText(context, "모든 게시글을 불러왔습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        mypageViewModel.Tags.observe(viewLifecycleOwner) { tags ->
            tagAdapter.newPage(ArrayList(tags))
        }

        binding.editProfile.setOnClickListener {
            if (isMyPage) moveEditprofile(userId) // 내 페이지면 프로필 수정 이동
            else { //타인 페이지라면 친구 작업
                binding.editProfile.isClickable = false //전송 중 클릭못하도록
                if (friendStatus == 0) {
                    requestFriend() //친구 신청
                } else if (friendStatus == 1) {
                    requestUnFriend() //친구신청 취소
                } else if (friendStatus == 2) { //친구 삭제
                    isDelete = true
                    binding.editProfile.isClickable = true
                    val fragment = MypageDeleteFriend(userInfo, isDelete, this)
                    fragment.show(parentFragmentManager, fragment.tag)
                } else if (friendStatus == 3) {

                }
            }
        }

        binding.mypageSetup.setOnClickListener {
            isDelete = false
            val fragment = PopupBlockUser(
                this,
                mypageViewModel.UserInfo.value?.name,
                mypageViewModel.UserInfo.value?.profileImage
            )
            fragment.show(parentFragmentManager, fragment.tag)
        }

        binding.mypageShowFeed.setOnClickListener {
            feedClicked()
        }

        binding.mypageShowMedia.setOnClickListener {
            mediaClicked()
        }

        mypageViewModel.FriendRequest.observe(viewLifecycleOwner) { result ->
            if (result) {
                if (friendStatus == 0) { //신청 성공
                    friendStatus = 1
                    binding.editProfile.setBackgroundResource(R.drawable.total_comment_index)
                    binding.editProfile.setTextColor(Color.parseColor("#646464"))
                    binding.editProfile.text = "신청 취소"
                } else if (friendStatus == 1) { //신청 취소
                    friendStatus = 0
                    binding.editProfile.setBackgroundResource(R.drawable.gradation_btn_custom)
                    binding.editProfile.setTextColor(Color.parseColor("#FDFDFD"))
                    binding.editProfile.text = "친구 신청"
                } else if (friendStatus == 2) {
                    friendStatus = 0
                    binding.editProfile.setBackgroundResource(R.drawable.gradation_btn_custom)
                    binding.editProfile.setTextColor(Color.parseColor("#FDFDFD"))
                    binding.editProfile.text = "친구 신청"
                } else if (friendStatus == 3) {
                    friendStatus = 2
                    binding.editProfile.setBackgroundResource(R.drawable.total_comment_index)
                    binding.editProfile.setTextColor(Color.parseColor("#646464"))
                    binding.editProfile.text = "내 친구"
                }
            } else {
                Toast.makeText(requireContext(), "요청에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
            binding.editProfile.isClickable = true //결과 받고 클릭 가능하도록
        }

        binding.profileFriendsLayout.setOnClickListener {
            val newFrag = FriendsListFragment(userId)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack("snsmypage")
            transaction.commit()
        }

        binding.friendFeed.setOnClickListener {
            val fragmentManager = parentFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack("snsmain", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

        binding.lookAround.setOnClickListener {
            val fragmentManager = parentFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack("lookaround", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }

        binding.mypageBack.setOnClickListener {
            val fragmentManager = parentFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            }
        }

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

    fun moveEditprofile(userId: String) {
        val newFrag = EditProfileFragment(userId)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        transaction.addToBackStack(null)
        transaction.commit()
        true
    }

    fun requestFriend() { //친구신청
        mypageViewModel.requestFriend(Followbody(userId))
    }

    fun requestUnFriend() { //신청취소
        mypageViewModel.requestUnFriend(com.example.haru.utils.User.id, UnFollowbody(userId))
    }

    fun requestDelFriend() { //친구삭제
        mypageViewModel.requestDelFriend(DelFriendBody(userId))
    }

    fun acceptRequest() { //요청수락
        mypageViewModel.requestAccpet(Friendbody(userId))
    }

    fun blockUser() { //유저차단
        mypageViewModel.blockUser(BlockBody(userId))
    }

    fun showFriendTitle() {
        binding.snsMenu.setBackgroundResource(com.kakao.sdk.friend.R.color.white)
        binding.mypageBack.visibility = View.VISIBLE
        binding.mypageSetup.visibility = View.VISIBLE
        binding.mypageDenoteLayout.visibility = View.GONE
    }

    fun feedClicked() {
        isFeedClick = true
        isFullLoaded = false
        binding.feedRecycler.visibility = View.VISIBLE
        binding.mediaContainer.visibility = View.GONE
        binding.feedUnderline.setBackgroundResource(R.drawable.todo_table_selected)
        binding.mediaUnderline.setBackgroundColor(Color.parseColor("#fdfdfd"))
        binding.feedText.setTextColor(Color.parseColor("#1DAFFF"))
        binding.mediaText.setTextColor(Color.parseColor("#acacac"))
    }

    fun mediaClicked() {
        isFeedClick = false
        isFullLoaded = false
        binding.feedRecycler.visibility = View.GONE
        binding.mediaContainer.visibility = View.VISIBLE
        binding.mediaUnderline.setBackgroundResource(R.drawable.todo_table_selected)
        binding.feedUnderline.setBackgroundColor(Color.parseColor("#fdfdfd"))
        binding.mediaText.setTextColor(Color.parseColor("#1DAFFF"))
        binding.feedText.setTextColor(Color.parseColor("#acacac"))
    }

    fun initProfile() {
        if (userId == com.example.haru.utils.User.id) {
            isMyPage = true
            binding.editProfile.setBackgroundResource(R.drawable.total_comment_index)
            binding.editProfile.setTextColor(Color.parseColor("#646464"))
            binding.editProfile.text = "프로필 편집"
            binding.feedText.text = "내 피드"
            mypageViewModel.getUserInfo(com.example.haru.utils.User.id)
        } else {
            showFriendTitle()
            isMyPage = false
            binding.editProfile.setBackgroundResource(R.drawable.gradation_btn_custom)
            binding.editProfile.setTextColor(Color.parseColor("#FDFDFD"))
            binding.editProfile.text = "친구 신청"
            binding.feedText.text = "피드"
            mypageViewModel.getUserInfo(userId)
        }
    }

    fun mediaLayout() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1 // 각 아이템의 너비를 1로 설정
            }
        }
        mediaRecyclerView.layoutManager = gridLayoutManager

        mediaRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view) // item position
                val column = position % 3 // item column
                if (column == 3) outRect.set(0, 0, 0, 3)
                else outRect.set(0, 0, 3, 3)
            }
        })
    }
}

class MypageDeleteFriend(
    val targetItem: com.example.haru.data.model.User,
    val isDelete: Boolean,
    val listener: MediaClick
) :
    BottomSheetDialogFragment() {
    lateinit var popupbinding: PopupFriendDeleteConfirmBinding
    lateinit var blockbinding: PopupBlockConfirmBinding
    private var ratio: Int = 30

    init {
        ratio = if (isDelete) 45 else 37
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (isDelete) { //삭제창
            popupbinding = PopupFriendDeleteConfirmBinding.inflate(inflater)

            if (targetItem.profileImage == null || targetItem.profileImage == "") {
                popupbinding.ivProfileImage.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.profile_base_image)
            } else {
                Glide
                    .with(requireContext())
                    .load(targetItem.profileImage)
                    .into(popupbinding.ivProfileImage)
            }


            popupbinding.tvUserId.text = targetItem.name
            popupbinding.btnDeleteUser.setOnClickListener {
                listener.onDeleteClick(0)
                dismiss()
            }

            popupbinding.btnDeleteCancel.setOnClickListener {
                dismiss()
            }

            return popupbinding.root
        } else { //차단창
            blockbinding = PopupBlockConfirmBinding.inflate(inflater)

            blockbinding.btnBlockUser.setOnClickListener {
                listener.onDeleteClick(0)
                dismiss()
            }

            blockbinding.btnBlockCancel.setOnClickListener {
                dismiss()
            }
            return blockbinding.root
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
        return getWindowHeight() * ratio / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}

class PopupBlockUser(val listener: MediaClick, val name: String?, val profileImage: String?) :
    BottomSheetDialogFragment() {
    lateinit var binding: PopupSnsBlockUserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = PopupSnsBlockUserBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (profileImage == null || profileImage == "")
            binding.ivProfileImage.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.profile_base_image)
        else Glide.with(this)
            .load(profileImage)
            .into(binding.ivProfileImage)

        binding.tvUserName.text = name

        binding.btnBlockUser.setOnClickListener {
            listener.onBlockClick(0)
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
        return getWindowHeight() * 35 / 100
    }

    private fun getWindowHeight(): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return displayMetrics.heightPixels
    }
}