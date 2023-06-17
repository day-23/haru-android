package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.haru.R
import com.example.haru.data.model.ExternalImages
import com.example.haru.viewmodel.MyPageViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddPostAdapter (val context: Context,
                      private var itemList: ArrayList<ExternalImages>,
                      private val myPageViewModel: MyPageViewModel): RecyclerView.Adapter<AddPostAdapter.AddPostViewHolder>(){

    var multiSelect = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddPostAdapter.AddPostViewHolder{
        val view = LayoutInflater.from(context)
            .inflate(R.layout.addpost_gallery_image, parent, false)
        return AddPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: AddPostAdapter.AddPostViewHolder, position: Int) {
        Glide.with(context).load(itemList[position].absuri).into(holder.image)
        var clicked = false

        if(multiSelect){
            holder.selectcircle.visibility = View.VISIBLE

        }else{
            holder.selectcircle.visibility = View.INVISIBLE
        }

        holder.selected.visibility = View.INVISIBLE
        holder.index.visibility = View.INVISIBLE
        holder.image.setColorFilter(null)

        holder.image.setOnClickListener {
            if(multiSelect) {
                if (clicked) {
                    myPageViewModel.imageList.remove(
                        myPageViewModel.imageList[
                                myPageViewModel.selectedPostionList.indexOf(position)
                        ]
                    )

                    myPageViewModel.selectedPostionList.remove(position)
                } else {
                    myPageViewModel.imageList.add(
                        itemList[position].copy()
                    )

                    myPageViewModel.selectedPostionList.add(position)
                }
            } else {
                myPageViewModel.selectedPostionList = arrayListOf(position)
                myPageViewModel.imageList = arrayListOf(itemList[position].copy())
            }

            clicked = pictureClicked(holder, clicked, position)
        }

//        holder.cropButton.setOnClickListener {
//            myPageViewModel.getCrop(itemList[position])
//            clicked = pictureClicked(holder, clicked, position, true)
//        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun setMultiSelect(): Boolean{
        if(multiSelect) multiSelect = false
        else multiSelect = true
        return multiSelect
    }

    fun pictureClicked(holder: AddPostViewHolder, clicked : Boolean, position: Int) : Boolean{
        if(multiSelect) {
            if (clicked) {
                holder.selected.visibility = View.INVISIBLE
                holder.index.visibility = View.INVISIBLE
                holder.image.setColorFilter(null)
                myPageViewModel.delSelected(position)
                myPageViewModel.deleteImage(itemList[position])
                return false
            } else {
                holder.selected.visibility = View.VISIBLE
                holder.index.visibility = View.VISIBLE
                holder.image.setColorFilter(Color.argb(127, 0, 0, 0), PorterDuff.Mode.SRC_OVER)
                myPageViewModel.setImages(itemList[position])
                myPageViewModel.addSelected(position)
                return true
            }
        }else{
            myPageViewModel.selectOnePicture(position)
            myPageViewModel.setImage(itemList[position])
            return false
        }
    }

    inner class AddPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.findViewById<ImageView>(R.id.image)
        var selected = itemView.findViewById<ImageView>(R.id.selected_circle)
        var index = itemView.findViewById<TextView>(R.id.select_index)
        var selectcircle = itemView.findViewById<ImageView>(R.id.image_select)
        var cropButton = itemView.findViewById<ImageView>(R.id.crop_button)
    }
}
