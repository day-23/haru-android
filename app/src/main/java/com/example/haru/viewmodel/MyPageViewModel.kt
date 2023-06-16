package com.example.haru.viewmodel

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.haru.data.model.*
import com.example.haru.data.repository.PostRepository
import com.example.haru.data.repository.ProfileRepository
import com.example.haru.data.repository.UserRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.security.AccessController.getContext

class MyPageViewModel() : ViewModel() {
    private val ProfileRepository = ProfileRepository()
    private val PostRepository = PostRepository()
    private val UserRepository = UserRepository()
    private val tagList: MutableList<String> = mutableListOf()
    var tag: String = ""

    private val _Profile = MutableLiveData<com.example.haru.data.model.Profile>()
    val Profile: LiveData<com.example.haru.data.model.Profile>
        get() = _Profile

    private val _InitFeed = MutableLiveData<ArrayList<Post>>()
    val InitFeed: LiveData<ArrayList<Post>>
        get() = _InitFeed

    private val _NewFeed = MutableLiveData<ArrayList<Post>>()
    val NewFeed: LiveData<ArrayList<Post>>
        get() = _NewFeed

    private val _Page = MutableLiveData<Int>()
    val Page: LiveData<Int>
        get() = _Page

    private val _StoredImages = MutableLiveData<ArrayList<ExternalImages>>()
    val StoredImages: LiveData<ArrayList<ExternalImages>>
        get() = _StoredImages

    private val _Images = MutableLiveData<ArrayList<ExternalImages>>()
    val Images: LiveData<ArrayList<ExternalImages>>
        get() = _Images

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

    private val _searchUser = MutableLiveData<User?>()
    val searchUser: LiveData<User?> get() = _searchUser

    private val _EditImage = MutableLiveData<MultipartBody.Part>()
    val EditImage: LiveData<MultipartBody.Part>
        get() = _EditImage

    private val _EditUri = MutableLiveData<Uri>()
    val EditUri: LiveData<Uri>
        get() = _EditUri

    private val _AfterCrop = MutableLiveData<ExternalImages?>()
    val AfterCrop: LiveData<ExternalImages?>
        get() = _AfterCrop

    private val _BeforeCrop = MutableLiveData<ExternalImages?>()
    val BeforeCrop: LiveData<ExternalImages?>
        get() = _BeforeCrop

    private val _SelectedUri = MutableLiveData<ArrayList<ExternalImages>>()
    val SelectedUri: LiveData<ArrayList<ExternalImages>>
        get() = _SelectedUri

    private val _PostTagLiveData = MutableLiveData<List<String>>()
    val PostTagLiveData: LiveData<List<String>> = _PostTagLiveData

    private val _FriendRequest = MutableLiveData<Boolean>()
    val FriendRequest: LiveData<Boolean> = _FriendRequest

    private val _PostRequest = MutableLiveData<Boolean>()
    val PostRequest: LiveData<Boolean> = _PostRequest

    private val _SearchedFriends = MutableLiveData<FriendsResponse>()
    val SearchedFriends: LiveData<FriendsResponse> = _SearchedFriends

    private val _SearchedRequests = MutableLiveData<FriendsResponse>()
    val SearchedRequests: LiveData<FriendsResponse> = _SearchedRequests

    private val _Friends = MutableLiveData<FriendsResponse>()
    val Friends: LiveData<FriendsResponse> = _Friends

    private val _FirstFriends = MutableLiveData<FriendsResponse>()
    val FirstFriends: LiveData<FriendsResponse> = _FirstFriends

    private val _FirstRequests = MutableLiveData<FriendsResponse>()
    val FirstRequests: LiveData<FriendsResponse> = _FirstRequests

    private val _Requests = MutableLiveData<FriendsResponse>()
    val Requests: LiveData<FriendsResponse> = _Requests

    private val _Media = MutableLiveData<MediaResponse>()
    val Media: LiveData<MediaResponse> = _Media

    private val _FirstMedia = MutableLiveData<MediaResponse>()
    val FirstMedia: LiveData<MediaResponse> = _FirstMedia

    private val _Tags = MutableLiveData<List<Tag>>()
    val Tags: LiveData<List<Tag>> = _Tags

    private val _Templates = MutableLiveData<ArrayList<com.example.haru.data.model.Profile>>()
    val Templates: LiveData<ArrayList<com.example.haru.data.model.Profile>> = _Templates


    //단일 사진 선택시 지난 사진의 인덱스
    private var lastImageIndex = -1

    init {
        _SelectedImage.value = -1
        _SelectedPosition.value = arrayListOf()
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

    fun getImageInfo(index: Int): ExternalImages {
        val images = _StoredImages.value!!
        return images[index]
    }

    fun getFeed(targetId: String, lastCreatedAt: String) {
        var newPost: ArrayList<Post> = arrayListOf()
        viewModelScope.launch {
            PostRepository.getMyFeed(targetId, lastCreatedAt) {
                if (it.size > 0) { //get success
                    newPost = it
                }
            }
            _NewFeed.value = newPost
        }
    }

    fun getFirstFeed(targetId: String) {
        var initPost: ArrayList<Post> = arrayListOf()
        viewModelScope.launch {
            PostRepository.getFirstMyFeed(targetId) {
                if (it.size > 0) {
                    Log.d("20191668", "first feed $it")
                    initPost = it
                }
            }
            _InitFeed.value = initPost
        }
    }

    fun getFirstMedia(targetId: String) {
        var newMedia = MediaResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            PostRepository.getFirstMedia(targetId) {
                if (it.data.size > 0) { //get success
                    newMedia = it
                }
            }
            if (newMedia.success) {
                _FirstMedia.value = newMedia
            }
        }
    }

    fun getMedia(targetId: String, lastCreatedAt: String) {
        var newMedia = MediaResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            PostRepository.getMedia(targetId, lastCreatedAt) {
                if (it.data.size > 0) { //get success
                    newMedia = it
                }
            }
            if (newMedia.success) {
                _FirstMedia.value = newMedia
            }
        }
    }

    fun getFirstTagMedia(targetId: String, tagId: String) {
        var newMedia = MediaResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            PostRepository.getFirstTagMedia(targetId, tagId) {
                if (it.data != null) { //get success
                    newMedia = it
                }
            }
            if (newMedia.success) {
                _FirstMedia.value = newMedia
            }
        }
    }

    fun getTemplates() {
        var templates = arrayListOf<com.example.haru.data.model.Profile>()
        viewModelScope.launch {
            PostRepository.getTemplates {
                if (it.size > 0) {
                    templates = it
                }
            }
            _Templates.value = templates
        }
    }

    fun getUserInfo(targetId: String) {
        var user = User("", "", "", "", 0, 0, 0, false)
        viewModelScope.launch {
            ProfileRepository.getUserInfo(targetId) {
                if (it.id != "") {
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

    fun resetSelection() {
        _SelectedImage.value = -1
        _SelectedPosition.value = arrayListOf()
        lastImageIndex = -1
        _Images.value = arrayListOf()
        _BeforeCrop.value = null
    }

    //커스텀 갤러리 단일 사진 선택을 위한 함수
    fun selectOnePicture(i: Int) {
        lastImageIndex = SelectedImage.value ?: -1
        _SelectedImage.value = i
    }

    fun getLastImage(): Int {
        return lastImageIndex
    }

    //MutableList로 바꿈
    fun convertMultiPart(context: Context): MutableList<MultipartBody.Part> {
//        val images = ArrayList<ExternalImages>()
//        val indexSet = _SelectedPosition.value
//        val indexOne = _SelectedImage.value
//        val totalImage = _StoredImages.value
//        if (indexSet!!.size > 0 && totalImage != null) {
//            for (i in indexSet) {
//                images.add(totalImage.get(i))
//            }
//        } else if (indexOne != null && indexOne != -1 && totalImage != null) {
//            images.add(totalImage.get(indexOne))
//        }
        val images = Images.value
        val convertedImages = mutableListOf<MultipartBody.Part>()

        if (images != null) {
            for (data in images) {
                val cursor = context.contentResolver.query(data.absuri, null, null, null, null)
                Log.d("CropImages", "cursor : $cursor")
                Log.d("CropImages", "${data.absuri}")
                cursor?.use {
                    it.moveToFirst()
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    val imagePath = it.getString(columnIndex)
                    val fileName = data.name.substringAfterLast('.')
                    val fileExtension = "image/$fileName"

                    val file = File(imagePath)
                    Log.d("CropImages", "file : $file")
                    val requestFile = RequestBody.create(fileExtension.toMediaTypeOrNull(), file)
                    val part = MultipartBody.Part.createFormData("image", fileName, requestFile)
                    convertedImages.add(part)
                }
                if (cursor == null) {
                    // File path of the cache image
                    val imagePath = data.absuri.toString()

                    // Create a File object from the image path
                    val imageFile = File(imagePath)

                    Log.d("CropImages", "image file $imageFile")

                    if (imageFile.exists()) {
                        // Create a RequestBody from the image file
                        val imageRequestBody =
                            RequestBody.create("image/*".toMediaTypeOrNull(), imageFile)

                        // Create a MultipartBody.Part from the image RequestBody
                        val imagePart = MultipartBody.Part.createFormData(
                            "image",
                            imageFile.name,
                            imageRequestBody
                        )
                        convertedImages.add(imagePart)
                    }
                }
            }
        } else {
            Log.d("CropImages", "image is null")
        }
        Log.d("CropImages", "$convertedImages")
        _SelectedUri.value = images ?: arrayListOf()
        return convertedImages
    }

    //선택한 사진들의 내부저장소 정보를 얻어옴
    fun getSelectImages(): ArrayList<ExternalImages> {
        return _Images.value ?: arrayListOf()
    }

    fun postRequest(
        images: MutableList<MultipartBody.Part>,
        content: String,
        hashtags: List<String>
    ) {
        val post = AddPost(images, content, hashtags)

        viewModelScope.launch {
            PostRepository.addPost(post) {
                if (it.id != "") { //get success
                    Log.d("TAG", "Success to Post")
                }
            }
            _PostDone.value = true
        }
    }

    fun templateRequest(templateBody: Template) {
        var postResult = false

        viewModelScope.launch {
            PostRepository.addTemplate(templateBody) {
                if (it) {
                    Log.d("TAG", "Success to post Template")
                    postResult = it
                } else {
                    Log.d("TAG", "Fail to post Template")
                }
            }
            _PostRequest.value = postResult
        }
    }

    fun editProfile(
        image: MultipartBody.Part,
        name: String,
        introduction: String,
        callback: () -> Unit
    ) {
        var user = User("", "", "", "", 0, 0, 0, false)
        viewModelScope.launch {
            ProfileRepository.editProfile(image, name, introduction) {
                if (it.id != "") {
                    user = it
                    Log.d("TAG", "Success to Edit!")
                }
                _UserInfo.postValue(user)
                callback()
            }
//            _UserInfo.value = user
        }
    }

    fun editProfileName(name: String, introduction: String, callback: () -> Unit) {
        var user = User("", "", "", "", 0, 0, 0, false)
        viewModelScope.launch {
            ProfileRepository.editProfileName(name, introduction) {
                if (it.id != "") {
                    user = it
                    Log.d("TAG", "Success to EditName!")
                } else {
                    Log.d("TAG", "Fail to Edit name")
                }
                _UserInfo.postValue(user)
                callback()
            }
        }
    }

    fun selectProfile(part: MultipartBody.Part, absuri: Uri) {
        _EditImage.value = part
        _EditUri.value = absuri
    }

    fun resetValue() {
        _BeforeCrop.value = null
        _AfterCrop.value = null
        _Images.value = arrayListOf()
        _SelectedPosition.value = arrayListOf()
        _StoredImages.value = arrayListOf()
        _SelectedUri.value = arrayListOf()
        _SelectedImage.value = -1
        _PostTagLiveData.value = arrayListOf()
    }

    fun requestFriend(body: Followbody) {
        var result = false
        viewModelScope.launch {
            UserRepository.requestFriend(body) {
                if (it) {
                    Log.d("TAG", "Success to request Friend")
                    result = it
                } else {
                    Log.d("TAG", "Fail to request Friend")
                }
            }
            _FriendRequest.value = result
        }
    }

    fun requestUnFriend(targetId: String, body: UnFollowbody) {
        var result = false
        viewModelScope.launch {
            UserRepository.requestunFriend(targetId, body) {
                if (it) {
                    Log.d("TAG", "Success to request UnFriend")
                    result = it
                } else {
                    Log.d("TAG", "Fail to request UnFriend")
                }
            }
            _FriendRequest.value = result
        }
    }

    fun requestDelFriend(body: DelFriendBody) {
        var result = false
        viewModelScope.launch {
            UserRepository.requestDelFriend(body) {
                if (it) {
                    Log.d("TAG", "Success to request Delete Friend")
                    result = it
                } else {
                    Log.d("TAG", "Fail to request Delete Friend")
                }
            }
            _FriendRequest.value = result
        }
    }

    fun requestAccpet(body: Friendbody) {
        var result = false
        viewModelScope.launch {
            UserRepository.acceptFriend(body) {
                if (it) {
                    Log.d("TAG", "Success to accept Friend")
                    result = it
                } else {
                    Log.d("TAG", "Fail to accept Friend")
                }
            }
            _FriendRequest.value = result
        }
    }

    fun blockUser(body: BlockBody) {
        var result = false
        viewModelScope.launch {
            UserRepository.blockUser(body) {
                if (it) {
                    Log.d("TAG", "Success to block User")
                    result = it
                } else {
                    Log.d("TAG", "Fail to block User")
                }
            }
            _FriendRequest.value = result
        }
    }

    fun getFriendsList(targetId: String, lastCreatedAt: String) {
        var Friends = FriendsResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            UserRepository.requestFriendsList(targetId, lastCreatedAt) {
                if (it.success) {
                    Friends = it
                }
            }
            _Friends.value = Friends
        }
    }

    fun getFirstFriendsList(targetId: String) {
        var Friends = FriendsResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            UserRepository.requestFirstFriendsList(targetId) {
                if (it.success) {
                    Friends = it
                }
            }
            _FirstFriends.value = Friends
        }
    }

    fun getFirstFriendsRequestList(targetId: String) {
        var Requests = FriendsResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            UserRepository.getFirstRequestList(targetId) {
                if (it.success) {
                    Requests = it
                }
            }
            _FirstRequests.value = Requests
        }
    }

    fun getFriendsRequestList(targetId: String, lastCreatedAt: String) {
        var Requests = FriendsResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            UserRepository.getRequestList(targetId, lastCreatedAt) {
                if (it.success) {
                    Requests = it
                }
            }
            _Requests.value = Requests
        }
    }

    //미디어 태그 구하기
    fun getUserTags(targetId: String) {
        var tags = listOf<Tag>()
        viewModelScope.launch {
            PostRepository.getUserTags(targetId) {
                if (it.size > 0) {
                    tags = it
                }
            }
            _Tags.value = tags
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

    fun getTagList(): List<String> {
        val tags = _PostTagLiveData.value
        if (tags != null) {
            if (tags.size > 0)
                return tags
        }

        return arrayListOf()
    }

    fun getSearchUserInfo(targetId: String, callback: () -> Unit) {
        viewModelScope.launch {
            UserRepository.getSearchUserInfo(targetId) {
                if (it?.success == true) {
                    _searchUser.postValue(it.data!!)
                } else _searchUser.postValue(User())
                callback()
            }
        }
    }

    fun clearSearch() {
        _searchUser.value = null
    }

    fun searchOnFriends(targetId: String, name: String) {
        var Friends = FriendsResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            UserRepository.searchOnFriend(targetId, name) {
                if (it.success) {
                    Friends = it
                }
            }
            _SearchedFriends.value = Friends
        }
    }

    fun searchOnRequests(targetId: String, name: String) {
        var Friends = FriendsResponse(false, arrayListOf(), pagination())
        viewModelScope.launch {
            UserRepository.searchOnRequests(targetId, name) {
                if (it.success) {
                    Friends = it
                }
            }
            _SearchedRequests.value = Friends
        }
    }

    fun setImage(image: ExternalImages) {
        _Images.value = arrayListOf(image)
    }

    fun setImages(image: ExternalImages) {
        var temp = _Images.value
        if (!temp.isNullOrEmpty())
            temp?.add(image)
        else {
            temp = arrayListOf(image)
        }
        _Images.value = temp!!
    }

    fun deleteImage(image: ExternalImages) {
        var temp = _Images.value
        if (temp.isNullOrEmpty()) {
            temp!!.remove(image)
            _Images.value = temp!!
        }
    }

    fun getCropResult(image: Uri) {
        var temp = _BeforeCrop.value
        if (temp != null) {
            temp.absuri = image
            _AfterCrop.value = temp!!
        } else {
            Log.d("ImageCrop", "null")
        }
    }

    fun getCrop(image: ExternalImages) {
        _BeforeCrop.value = image
    }
}