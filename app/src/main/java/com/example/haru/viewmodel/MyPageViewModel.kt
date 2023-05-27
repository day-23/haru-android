package com.example.haru.viewmodel

import android.content.Context
import android.net.Uri
import android.os.Build.VERSION_CODES.P
import android.provider.ContactsContract.Profile
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.*
import com.example.haru.data.repository.PostRepository
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.data.repository.TodoRepository
import com.example.haru.data.repository.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MyPageViewModel(): ViewModel() {
    private val ProfileRepository = ProfileRepository()
    private val PostRepository = PostRepository()
    private val UserRepository = UserRepository()
    private val tagList: MutableList<String> = mutableListOf()
    var tag: String = ""

    private val _Profile = MutableLiveData<com.example.haru.data.model.Profile>()
    val Profile: LiveData<com.example.haru.data.model.Profile>
        get() = _Profile

    private val _Feed = MutableLiveData<ArrayList<Post>>()
    val MyFeed: LiveData<ArrayList<Post>>
        get() = _Feed

    private val _NewFeed = MutableLiveData<ArrayList<Post>>()
    val NewFeed: LiveData<ArrayList<Post>>
        get() = _NewFeed

    private val _Page = MutableLiveData<Int>()
    val Page: LiveData<Int>
        get() = _Page

    private val _StoredImages = MutableLiveData<ArrayList<ExternalImages>>()
    val StoredImages: LiveData<ArrayList<ExternalImages>>
        get() = _StoredImages

    private val _SelectedPosition = MutableLiveData<ArrayList<Int>>()
    val SelectedPosition: LiveData<ArrayList<Int>>
        get() = _SelectedPosition

    private val _SelectedImage = MutableLiveData<Int>()
    val SelectedImage: LiveData<Int>
        get() = _SelectedImage

    private val _PostDone = MutableLiveData<Boolean>()
    val PostDone: LiveData<Boolean>
        get() = _PostDone

    private val _UserInfo = MutableLiveData<User>()
    val UserInfo: LiveData<User>
        get() = _UserInfo

    private val _EditImage = MutableLiveData<MultipartBody.Part>()
    val EditImage: LiveData<MultipartBody.Part>
        get() = _EditImage

    private val _EditUri = MutableLiveData<Uri>()
    val EditUri: LiveData<Uri>
        get() = _EditUri

    private val _SelectedUri = MutableLiveData<ArrayList<ExternalImages>>()
    val SelectedUri: LiveData<ArrayList<ExternalImages>>
        get() = _SelectedUri

    private val _PostTagLiveData = MutableLiveData<List<String>>()
    val PostTagLiveData: LiveData<List<String>> = _PostTagLiveData

    //단일 사진 선택시 지난 사진의 인덱스
    private var lastImageIndex = -1

    init{
        _SelectedImage.value = -1
        _SelectedPosition.value = arrayListOf()
    }

    fun init_page(){
        _Page.value = 1
    }

    fun loadGallery(images: ArrayList<ExternalImages>) {
        Log.d("Image", "upload ---------------$images")
        _StoredImages.value = images
    }

    fun getGallery(): ArrayList<ExternalImages> {
        val images = _StoredImages.value!!
        Log.d("Image", "download -------------------$images")
        return images
    }
    fun addPage() {
        _Page.value = _Page.value?.plus(1)
    }

    fun getFeed(page: String, targetId:String) {
        var newPost: ArrayList<Post> = arrayListOf()
        var allPost = _Feed.value ?: arrayListOf()
        viewModelScope.launch {
            PostRepository.getMyFeed(page, targetId) {
                if (it.size > 0) { //get success
                    newPost = it
                    allPost.addAll(it)
                }
            }
            _NewFeed.value = newPost
            _Feed.value = allPost
        }
    }

    fun getUserInfo(targetId: String){
        var user = User("","","","",0,0,0,false)
        viewModelScope.launch {
            ProfileRepository.getUserInfo(targetId){
                if(it.id != ""){
                    user = it
                }
            }
            _UserInfo.value = user
        }
    }

    //커스텀 갤러리 선택한 사진 인덱스 표시를 위한 함수들
    fun addSelected(i: Int) {
        var newlist = _SelectedPosition.value
        if (newlist != null)
            newlist?.add(i)
        else newlist = arrayListOf(i)
        _SelectedPosition.value = newlist!!
    }
    fun delSelected(i: Int) {
        var newlist = _SelectedPosition.value
        newlist?.remove(i)
        _SelectedPosition.value = newlist!!
    }

    fun resetSelection(){
        _SelectedImage.value = -1
        _SelectedPosition.value = arrayListOf()
        lastImageIndex = -1
    }

    //커스텀 갤러리 단일 사진 선택을 위한 함수
    fun selectOnePicture(i : Int){
        lastImageIndex = SelectedImage.value ?: -1
        _SelectedImage. value = i
    }

    fun getLastImage(): Int {
        return lastImageIndex
    }

    //MutableList로 바꿈
    fun convertMultiPart(context: Context): MutableList<MultipartBody.Part> {
        val images = ArrayList<ExternalImages>()
        val indexSet = _SelectedPosition.value
        val indexOne = _SelectedImage.value
        val totalImage = _StoredImages.value
        if(indexSet!!.size > 0 && totalImage != null) {
            for (i in indexSet) {
                images.add(totalImage.get(i))
            }
        }else if(indexOne != null && indexOne != -1 && totalImage != null){
            images.add(totalImage.get(indexOne))
        }

        val convertedImages = mutableListOf<MultipartBody.Part>()

        for (data in images) {
            val cursor = context.contentResolver.query(data.absuri, null, null, null, null)
            cursor?.use {
                it.moveToFirst()
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val imagePath = it.getString(columnIndex)
                val fileName = data.name.substringAfterLast('.')
                val fileExtension = "image/$fileName"

                val file = File(imagePath)
                Log.d("Image", "4 $file")

                val requestFile = RequestBody.create(fileExtension.toMediaTypeOrNull(), file)
                val part = MultipartBody.Part.createFormData("image", fileName, requestFile)
                convertedImages.add(part)
            }
        }

        _SelectedUri.value = images
        return convertedImages
    }

    //선택한 사진들의 내부저장소 정보를 얻어옴
    fun getSelectImages() : ArrayList<ExternalImages>{
        return _SelectedUri.value ?: arrayListOf()
    }

    fun postRequest(images: MutableList<MultipartBody.Part>, content: String, hashtags: List<String>){
        val post = AddPost(images, content, hashtags)

        viewModelScope.launch {
            PostRepository.addPost(post) {
                if (it.id != "") { //get success
                    Log.d("TAG", "Success to Post!!")
                }
            }
            _PostDone.value = true
        }
    }

    fun editProfile(image: MultipartBody.Part, name: String, introduction: String){
        var user = User("","","","",0,0,0,false)
        viewModelScope.launch {
            ProfileRepository.editProfile(image, name, introduction){
                if(it.id != "") {
                    user = it
                    Log.d("TAG", "Success to Edit!")
                }
            }
            _UserInfo.value = user
        }
    }

    fun editProfileName(name:String, introduction: String){
        var user = User("","","","",0,0,0,false)
        viewModelScope.launch {
            ProfileRepository.editProfileName(name, introduction){
                if(it.id != ""){
                    user = it
                    Log.d("TAG", "Success to EditName!")
                }
                else{
                    Log.d("TAG", "Fail to Edit name")
                }
            }
            _UserInfo.value = user
        }
    }

    fun selectProfile(part: MultipartBody.Part, absuri: Uri){
        _EditImage.value = part
        _EditUri.value = absuri
    }
    fun resetValue(){
        _SelectedPosition.value = arrayListOf()
        _StoredImages.value = arrayListOf()
        _SelectedUri.value = arrayListOf()
        _SelectedImage.value = -1
        _PostTagLiveData.value = arrayListOf()
    }

    fun requestFollow(body: Followbody){
        viewModelScope.launch {
            UserRepository.requestFollowing(body){
                if(it){
                    Log.d("TAG","Success to follow")
                }else{
                    Log.d("TAG","Fail to follow")
                }
            }
        }
    }

    fun requestUnFollow(body: UnFollowbody){
        viewModelScope.launch {
            UserRepository.requestunFollowing(body){
                if(it){
                    Log.d("TAG","Success to unfollow")
                }else{
                    Log.d("TAG","Fail to unfollow")
                }
            }
        }
    }

    fun subTagList(item: String) {
        tagList.remove(item)
        _PostTagLiveData.value = tagList
    }

    fun addTagList(str: String): Boolean {
        tag = str
        if (tag == "" || tag.replace(" ", "") == "")
            return false
        tagList.add(tag.replace(" ", ""))
        _PostTagLiveData.value = tagList
        return true
    }

    fun getTagList(): List<String>{
        val tags = _PostTagLiveData.value
        if(tags != null){
            if(tags.size > 0)
                return tags
        }

        return arrayListOf()
    }
}