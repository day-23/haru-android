package com.example.haru.view.sns

import com.example.haru.R

interface OnPostClickListener {
    fun onCommentClick(postitem: ArrayList<com.example.haru.data.model.Profile>)

    fun onTotalCommentClick(postId: String)
}