package com.example.haru.view.sns

import BaseActivity
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.*
import com.example.haru.databinding.FragmentSnsMypageBinding
import com.example.haru.databinding.PopupFriendDeleteConfirmBinding
import com.example.haru.databinding.PopupSnsPostCancelBinding
import com.example.haru.databinding.PopupSnsPostDeleteBinding
import com.example.haru.utils.User
import com.example.haru.view.MainActivity
import com.example.haru.view.adapter.MediaAdapter
import com.example.haru.view.adapter.MediaTagAdapter
import com.example.haru.view.adapter.SnsPostAdapter
import com.example.haru.viewmodel.MyPageViewModel
import com.example.haru.viewmodel.SnsViewModel

interface MediaClick{
    fun onMediaClick(media: Media)

    fun onDeleteClick(position: Int)
}

class MyPageFragment(userId: String) : Fragment(), OnPostClickListener, OnMediaTagClicked, MediaClick, OnPostPopupClick{
    private lateinit var binding: FragmentSnsMypageBinding
    private lateinit var feedRecyclerView: RecyclerView
    private lateinit var mediaRecyclerView: RecyclerView
    private lateinit var tagRecyclerView: RecyclerView
    private lateinit var feedAdapter: SnsPostAdapter
    private lateinit var mediaAdapter: MediaAdapter
    private lateinit var tagAdapter: MediaTagAdapter
    private lateinit var mypageViewModel: MyPageViewModel
    private lateinit var snsViewModel : SnsViewModel
    private var isFeedClick = true
    private var isFullLoaded = false //게시글 페이지네이션이 끝났는지
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

    override fun onDeleteClick(position: Int) {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.mypage_popup_anchor)
        if (fragment != null) {
            MainActivity.hideNavi(false)
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(fragment)
            transaction.commit()
        }
        if(position == 0){
            mypageViewModel.FriendRequest.observe(viewLifecycleOwner){result ->
                if(result){
                    requestDelFriend() //친구끊기
                }
            }
        }
    }
    override fun onCommentClick(postitem: Post) {
        mypageViewModel.getUserInfo(userId)

        if(postitem.isTemplatePost != null) {
            val newFrag = CommentsFragment(postitem, com.example.haru.utils.User.id)
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_frame, newFrag)
            transaction.addToBackStack("snsmypage")
            transaction.commit()
        }else{
            mypageViewModel.UserInfo.observe(viewLifecycleOwner){ user ->
                val newFrag = AddCommentFragment(postitem.id, postitem.images, postitem.likedCount, postitem.commentCount, user)
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
        val fragmentManager = childFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.mypage_popup_anchor, fragment)
        transaction.commit()
    }

    override fun postPopupClicked(userId: String, postId: String, position: Int) {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.mypage_popup_anchor)
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
                    transaction.add(R.id.mypage_popup_anchor, fragment)
                }
            }
        }
    }

    override fun PopupConfirm(userId: String, postId: String, position: Int) {
        val fragmentManager = childFragmentManager
        val fragment = fragmentManager.findFragmentById(R.id.mypage_popup_anchor)
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

    override fun onTagClicked(tag: Tag, holder : MediaTagAdapter.MediaTagViewHolder) {
        holder.tag.setBackgroundResource(R.drawable.tag_btn_clicked)
        holder.tag.setTextColor(Color.parseColor("#fdfdfd"))
        if(holder != selectedTag) {
            mypageViewModel.getFirstTagMedia(userId, tag.id)
        }
        selectedTag?.tag?.setBackgroundResource(R.drawable.tag_btn_custom)
        selectedTag?.tag?.setTextColor(Color.parseColor("#191919"))

        if(holder == selectedTag){
            mypageViewModel.getFirstMedia(userId)
            selectedTag = null
        }else {
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
    }

    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).adjustTopMargin(binding.snsMenu.id)
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

        mypageViewModel.UserInfo.observe(viewLifecycleOwner){ user ->
            userInfo = user
            binding.profileName.text = user.name
            binding.profileIntroduction.text = user.introduction
            binding.profilePostCount.text = user.postCount.toString()
            binding.profileFriendsCount.text = user.friendCount.toString()
            friendStatus = user.friendStatus
            if(!isMyPage) { //타인의 페이지라면
                if (user.friendStatus == 0) {
                    binding.editProfile.text = "친구 신청"
                } else if (user.friendStatus == 1) {
                    binding.editProfile.text = "신청 취소"
                } else if (user.friendStatus == 2){
                    binding.editProfile.text = "내 친구"
                } else if (user.friendStatus == 3){
                    binding.editProfile.text = "수락"
                }
            }

            if(user.profileImage != "") {
                Log.d("TAG", "${user.profileImage}")
                Glide.with(this)
                    .load(user.profileImage)
                    .into(binding.profileImage)
            }
        }

        binding.select.bringToFront()
        binding.mypageScroll.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY >= binding.select.top) {
                binding.select.translationY = (scrollY - binding.select.top).toFloat()
            }else{
                binding.select.translationY = 0f
            }

            //스크롤이 끝까지 닿으면
            val isScrolledToBottom = scrollY >= binding.mypageScroll.getChildAt(0).measuredHeight - binding.mypageScroll.measuredHeight
            if(isScrolledToBottom){
                if(isFeedClick && !isFullLoaded){
                    mypageViewModel.getFeed(userId, lastDate)
                }else if(!isFeedClick && !isFullLoaded){
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
        tagRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        mypageViewModel.InitFeed.observe(viewLifecycleOwner){feeds ->
            if(feeds.size > 0) {
                index = feeds.size - 1
                lastDate = feeds[index].createdAt
                feedAdapter.initList(feeds)
            }
        }

        snsViewModel.DeleteResult.observe(viewLifecycleOwner) {result ->
            if(result){
                feedAdapter.deletePost(deletedItem)
            }
        }

        mypageViewModel.NewFeed.observe(viewLifecycleOwner){feeds ->
            if(feeds.size > 0) {
                index = feeds.size - 1
                lastDate = feeds[index].createdAt
                feedAdapter.newPage(feeds)
            }else {
                isFullLoaded = true
                Toast.makeText(context, "모든 게시글을 불러왔습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        mypageViewModel.FirstMedia.observe(viewLifecycleOwner){medias ->
            if(medias.data.size > 0) {
                index = medias.data.size - 1
                lastDate = medias.data[index].createdAt
                mediaAdapter.firstPage(medias.data)
            }
        }

        mypageViewModel.Media.observe(viewLifecycleOwner){medias ->
            if(medias.data.size > 0){
                index = medias.data.size - 1
                lastDate = medias.data[index].createdAt
                mediaAdapter.newPage(medias.data)
            }else{
                isFullLoaded = true
                Toast.makeText(context, "모든 게시글을 불러왔습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        mypageViewModel.Tags.observe(viewLifecycleOwner){tags ->
            tagAdapter.newPage(ArrayList(tags))
        }

        binding.editProfile.setOnClickListener {
            if(isMyPage) moveEditprofile(userId) // 내 페이지면 프로필 수정 이동
            else{ //타인 페이지라면 친구 작업
                binding.editProfile.isClickable = false //전송 중 클릭못하도록
                if (friendStatus == 0) {
                    requestFriend() //친구 신청
                } else if (friendStatus == 1) {
                    requestUnFriend() //친구신청 취소
                } else if (friendStatus == 2){ //친구 삭제
                    MainActivity.hideNavi(true)
                    val fragment = MypageDeleteFriend(userInfo, this)
                    val fragmentManager = childFragmentManager
                    val transaction = fragmentManager.beginTransaction()
                    transaction.add(R.id.mypage_popup_anchor, fragment)
                    transaction.commit()
                } else if(friendStatus == 3){

                }
            }
        }

        binding.mypageShowFeed.setOnClickListener {
            feedClicked()
        }

        binding.mypageShowMedia.setOnClickListener {
            mediaClicked()
        }

        mypageViewModel.FriendRequest.observe(viewLifecycleOwner){ result ->
            if(result){
                if(friendStatus == 0) { //신청 성공
                    friendStatus = 1
                    binding.editProfile.text = "신청 취소"
                }else if(friendStatus == 1){ //신청 취소
                    friendStatus = 0
                    binding.editProfile.text = "친구 신청"
                }else if(friendStatus == 2){
                    friendStatus = 0
                    binding.editProfile.text = "친구 신청"
                }else if(friendStatus == 3){
                    friendStatus = 2
                    binding.editProfile.text = "내 친구"
                }
            }else{
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

    fun moveEditprofile(userId: String){
        val newFrag = EditProfileFragment(userId)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragments_frame, newFrag)
        transaction.addToBackStack("snsmypage")
        transaction.commit()
        true
    }

    fun requestFriend(){ //친구신청
        mypageViewModel.requestFriend(Followbody(userId))
    }

    fun requestUnFriend(){ //신청취소
        mypageViewModel.requestUnFriend(com.example.haru.utils.User.id, UnFollowbody(userId))
    }

    fun requestDelFriend(){ //친구삭제
        mypageViewModel.requestDelFriend(DelFriendBody(userId))
    }

    fun acceptRequest(){ //요청수락
        mypageViewModel.requestAccpet(Friendbody(userId))
    }

    fun showFriendTitle(){
        binding.snsMenu.setBackgroundResource(com.kakao.sdk.friend.R.color.white)
        binding.mypageBack.visibility = View.VISIBLE
        binding.mypageSetup.visibility = View.VISIBLE
    }

    fun feedClicked(){
        isFeedClick = true
        isFullLoaded = false
        binding.feedRecycler.visibility = View.VISIBLE
        binding.mediaContainer.visibility = View.GONE
        binding.feedUnderline.setBackgroundResource(R.drawable.todo_table_selected)
        binding.mediaUnderline.setBackgroundColor(Color.parseColor("#fdfdfd"))
        binding.feedText.setTextColor(Color.parseColor("#1DAFFF"))
        binding.mediaText.setTextColor(Color.parseColor("#acacac"))
    }

    fun mediaClicked(){
        isFeedClick = false
        isFullLoaded = false
        binding.feedRecycler.visibility = View.GONE
        binding.mediaContainer.visibility = View.VISIBLE
        binding.mediaUnderline.setBackgroundResource(R.drawable.todo_table_selected)
        binding.feedUnderline.setBackgroundColor(Color.parseColor("#fdfdfd"))
        binding.mediaText.setTextColor(Color.parseColor("#1DAFFF"))
        binding.feedText.setTextColor(Color.parseColor("#acacac"))
    }

    fun initProfile(){
        if(userId == com.example.haru.utils.User.id){
            isMyPage = true
            binding.editProfile.text = "프로필 편집"
            mypageViewModel.getUserInfo(com.example.haru.utils.User.id)
        }else{
            showFriendTitle()
            isMyPage = false
            binding.editProfile.text = "친구 신청"
            mypageViewModel.getUserInfo(userId)
        }
    }

    fun mediaLayout(){
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
                if(column == 3) outRect.set(0, 0, 0, 3)
                else outRect.set(0,0,3,3)
            }
        })
    }
}

class MypageDeleteFriend(val targetItem : com.example.haru.data.model.User, val listener : MediaClick) :
    Fragment() {
    lateinit var popupbinding: PopupFriendDeleteConfirmBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        popupbinding = PopupFriendDeleteConfirmBinding.inflate(inflater, container, false)

        Glide
            .with(requireContext())
            .load(targetItem.profileImage)
            .into(popupbinding.popupProfileImg)

        popupbinding.popupDelTargetName.text = targetItem.name

        popupbinding.deleteFriendConfirm.setOnClickListener {
            listener.onDeleteClick(0)
        }

        popupbinding.cancelDeleteFriend.setOnClickListener {
            listener.onDeleteClick(1)
        }

        return popupbinding.root
    }
}