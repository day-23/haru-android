package com.example.haru.viewmodel

import android.content.Context
import android.os.Build.VERSION_CODES.P
import android.provider.ContactsContract.Profile
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.AddPost
import com.example.haru.data.model.ExternalImages
import com.example.haru.data.model.Post
import com.example.haru.data.model.User
import com.example.haru.data.repository.PostRepository
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MyPageViewModel(): ViewModel() {
    private val ProfileRepository = ProfileRepository()
    private val PostRepository = PostRepository()

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

    private val _PostDone = MutableLiveData<Boolean>()
    val PostDone: LiveData<Boolean>
        get() = _PostDone

    private val _UserInfo = MutableLiveData<User>()
    val UserInfo: LiveData<User>
        get() = _UserInfo

    var profile_info = com.example.haru.data.model.Profile("", "", "", "")

    init {
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

    fun updateProfile(image: MultipartBody.Part) {
        viewModelScope.launch {
            ProfileRepository.editProfile(image) {
                if (it.id != "") profile_info = it
            }
            _Profile.value = profile_info
        }
    }

    fun getProfile() {
        viewModelScope.launch {
            ProfileRepository.getProfile {
                if (it.id != "") profile_info = it
            }
            _Profile.value = profile_info
        }
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
        var user = User("","","","",false,0,0,0)
        viewModelScope.launch {
            ProfileRepository.getUserInfo(targetId){
                if(it.id != ""){
                    user = it
                }
            }
            _UserInfo.value = user
        }
    }

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

    //MutableList로 바꿈
    fun convertMultiPart(context: Context): MutableList<MultipartBody.Part> {
        val images = ArrayList<ExternalImages>()
        val indexSet = _SelectedPosition.value
        val totalImage = _StoredImages.value
        if(indexSet != null && totalImage != null) {
            for (i in indexSet) {
                images.add(totalImage.get(i))
            }
        }

        val convertedImages = mutableListOf<MultipartBody.Part>()

        for (data in images) {
            val cursor = context.contentResolver.query(data.absuri, null, null, null, null)
            cursor?.use {
                it.moveToFirst()
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val imagePath = it.getString(columnIndex)
                val fileName = data.name.substringAfterLast('.')
                val fileExtension = "image/" + fileName

                val file = File(imagePath)
                Log.d("Image", "4 $file")

                val requestFile = RequestBody.create(MediaType.parse(fileExtension), file)
                val part = MultipartBody.Part.createFormData("image", fileName, requestFile)
                convertedImages.add(part)
            }
        }
        return convertedImages
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

    fun resetValue(){
        _SelectedPosition.value = arrayListOf()
        _StoredImages.value = arrayListOf()
    }
}