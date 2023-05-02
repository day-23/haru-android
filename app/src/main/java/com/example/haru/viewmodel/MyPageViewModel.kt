package com.example.haru.viewmodel

import android.os.Build.VERSION_CODES.P
import android.provider.ContactsContract.Profile
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.ExternalImages
import com.example.haru.data.model.Post
import com.example.haru.data.repository.PostRepository
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.data.repository.TodoRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

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

    var profile_info = com.example.haru.data.model.Profile("", "", "", "")

    init {
        getProfile()
        _Page.value = 1
    }

    fun loadGallery(images: ArrayList<ExternalImages>){
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

    fun getFeed(page: String) {
        var newPost: ArrayList<Post> = arrayListOf()
        var allPost = _Feed.value ?: arrayListOf()
        viewModelScope.launch {
            PostRepository.getMyFeed(page) {
                if (it.size > 0) { //get success
                    newPost = it
                    allPost.addAll(it)
                }
            }
            _NewFeed.value = newPost
            _Feed.value = allPost
        }
    }

    fun addSelected(i : Int){
        var newlist = _SelectedPosition.value
        if(newlist != null)
            newlist?.add(i)
        else newlist = arrayListOf(i)
        _SelectedPosition.value = newlist!!
    }

    fun delSelected(i : Int){
        var newlist = _SelectedPosition.value
        newlist?.remove(i)
        _SelectedPosition.value = newlist!!
    }
}