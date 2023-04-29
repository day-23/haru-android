package com.example.haru.view.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.Post
import com.example.haru.data.model.SnsPost
import com.example.haru.data.model.timetable_data
import com.example.haru.viewmodel.SnsViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*

class SnsPostAdapter(val context: Context,
                     private var itemList: ArrayList<Post> = ArrayList()): RecyclerView.Adapter<SnsPostAdapter.SnsPostViewHolder>(){

    private lateinit var snsViewModel: SnsViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsPostViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_sns_post, parent, false)
        snsViewModel = ViewModelProvider(context as ViewModelStoreOwner).get(SnsViewModel::class.java)
        return SnsPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnsPostViewHolder, position: Int) {
        val adapter = PicturesPagerAdapter(holder.itemView.context, itemList[position].images)
        holder.picture.adapter = adapter
        holder.userid.text = itemList[position].user.name
        holder.content.text = itemList[position].content

        val pictureIndex = adapter.itemCount
        var text = ""
        var delayJob: Job? = null // 코루틴 취소를 위한 Job 변수
        holder.picture.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                delayJob?.cancel()
                if(text != "" || position != 0) {
                    holder.index.alpha = 1f
                    text = "${position + 1}/${pictureIndex}"
                    holder.index.text = text
                    delayJob = CoroutineScope(Dispatchers.Main).launch {
                        delay(2000L) // 2초 동안 코루틴을 멈춤
                        holder.index.alpha = 0f
                    }
                }
            }
        })

        if(itemList[position].user.profileImage != null) {
            Log.d("AAAAA", "${itemList[position].user.profileImage}")
            Glide.with(holder.itemView.context)
                .load(itemList[position].user.profileImage)
                .into(holder.profileImg)
        }

        if(itemList[position].isLiked){
            holder.likeBtn.setImageResource(R.drawable.liked)
        }
        else{
            holder.likeBtn.setImageResource(R.drawable.likedyet)
        }

        holder.likeBtn.setOnClickListener{
            if(itemList[position].isLiked){
                holder.likeBtn.setImageResource(R.drawable.likedyet)
                itemList[position].isLiked = false
            }else{
                holder.likeBtn.setImageResource(R.drawable.liked)
                itemList[position].isLiked = true
            }
            snsViewModel.likeAction(itemList[position].id)

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun initList(post : ArrayList<Post>){
        itemList = post
        notifyDataSetChanged()
    }

    fun newPage(post: ArrayList<Post>){
        itemList.addAll(post)
        notifyDataSetChanged()
    }

    inner class SnsPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userid = itemView.findViewById<TextView>(R.id.user_id)
        var profileImg = itemView.findViewById<CircleImageView>(R.id.post_profile)
        var picture = itemView.findViewById<ViewPager2>(R.id.post_picture)
        var content = itemView.findViewById<TextView>(R.id.post_contents)
        var likeBtn = itemView.findViewById<ImageView>(R.id.button_like)
        var index = itemView.findViewById<Button>(R.id.picture_index)
    }
}