package com.example.haru.view.sns

import com.example.haru.R
import com.example.haru.data.model.Post

interface OnPostClickListener {
    fun onCommentClick(postitem: Post)

    fun onTotalCommentClick(postId: String)

    fun onProfileClick(userId:String)

    fun onSetupClick(userId: String, postId: String, position: Int)
}