package com.example.haru.view.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.example.haru.utils.GetPastDate
import com.example.haru.utils.User
import com.example.haru.view.sns.CommentsFragment
import com.example.haru.view.sns.MyPageFragment
import com.example.haru.view.sns.OnPostClickListener
import com.example.haru.view.sns.SnsFragment
import com.example.haru.viewmodel.SnsViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kakao.k.p
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat

class SnsPostAdapter(
    val context: Context,
    private var itemList: ArrayList<Post> = ArrayList(),
    private val listener: OnPostClickListener
) : RecyclerView.Adapter<SnsPostAdapter.SnsPostViewHolder>() {

    private lateinit var snsViewModel: SnsViewModel
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SnsPostViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_sns_post, parent, false)
        snsViewModel =
            ViewModelProvider(context as ViewModelStoreOwner).get(SnsViewModel::class.java)
        return SnsPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: SnsPostViewHolder, position: Int) {
        val adapter = PicturesPagerAdapter(holder.itemView.context, itemList[position].images)

        val item = itemList[position]

        if (item.user.id != User.id) {
            holder.totalcomment.visibility = View.GONE
        } else {
            holder.totalcomment.visibility = View.VISIBLE
        }
        val date = GetPastDate.getPastDate(item.createdAt)
        holder.daysAgo.setText(date)

        if (item.isTemplatePost != null) {
            holder.content.text = ""
            holder.content.visibility = View.GONE
            holder.templateText.text = item.content
            holder.templateText.setTextColor(Color.parseColor(item.isTemplatePost))
        } else {
            holder.templateText.text = ""
            holder.content.visibility =
                if (item.content.replace(" ", "") == "") View.GONE
                else View.VISIBLE
            holder.content.text = item.content
        }

        holder.picture.adapter = adapter
        holder.userid.text = item.user.name
        holder.likedcount.text = item.likedCount.toString()
        holder.commentcount.text = item.commentCount.toString()

        holder.setup.setOnClickListener {
            Log.d("20191668", "set up")
            listener.onSetupClick(item.user.id, item.id, item)
        }

        val pictureIndex = adapter.itemCount
        val text = "${position + 1}/${pictureIndex}"
        holder.index.text = text


        holder.picture.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val pictureIndex = adapter.itemCount
                val text = "${position + 1}/${pictureIndex}"
                holder.index.text = text
            }
        })

        holder.profileImg.setOnClickListener {
            listener.onProfileClick(item.user.id)
        }

        holder.userid.setOnClickListener {
            listener.onProfileClick(item.user.id)
        }

        holder.totalcomment.setOnClickListener {
            Log.d("TAG", "post id sended -------------${item.id}")
            listener.onTotalCommentClick(item)
        }

        holder.comment.setOnClickListener {
            listener.onCommentClick(item)
        }

        if (itemList[position].user.profileImage != null) {
            Glide.with(holder.itemView.context)
                .load(item.user.profileImage)
                .into(holder.profileImg)
        } else {
            holder.profileImg.setImageResource(R.drawable.default_profile)
        }


        if (item.isLiked) {
            holder.likeBtn.setImageResource(R.drawable.check_like)

        } else {
            holder.likeBtn.setImageResource(R.drawable.uncheck_like)
        }

        if (item.isCommented) {
            holder.comment.setImageResource(R.drawable.comment_filled)
        } else {
            holder.comment.setImageResource(R.drawable.comment)
        }

        if (item.user.isAllowFeedLike == 0 || (item.user.isAllowFeedLike == 1 && item.user.friendStatus != 2))
            return

        holder.likeBtn.setOnClickListener {
            if (itemList[position].isLiked) {
                holder.likeBtn.setImageResource(R.drawable.uncheck_like)
                itemList[position].isLiked = false
                itemList[position].likedCount -= 1
                holder.likedcount.text = itemList[position].likedCount.toString()

            } else {
                holder.likeBtn.setImageResource(R.drawable.check_like)
                itemList[position].isLiked = true
                itemList[position].likedCount += 1
                holder.likedcount.text = itemList[position].likedCount.toString()
            }
            snsViewModel.likeAction(item.id)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun initList(post: ArrayList<Post>) {
        itemList = post
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun newPage(post: ArrayList<Post>) {
        itemList.addAll(post)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deletePost(item: Post) {
        itemList.remove(item)
        notifyDataSetChanged()
    }

    inner class SnsPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userid = itemView.findViewById<TextView>(R.id.user_id)
        var profileImg = itemView.findViewById<CircleImageView>(R.id.post_profile)
        var daysAgo = itemView.findViewById<TextView>(R.id.days_ago)
        var picture = itemView.findViewById<ViewPager2>(R.id.post_picture)
        var content = itemView.findViewById<TextView>(R.id.post_contents)
        var likeBtn = itemView.findViewById<ImageView>(R.id.button_like)
        var likedcount = itemView.findViewById<TextView>(R.id.liked_count)
        var commentcount = itemView.findViewById<TextView>(R.id.post_comment_count)
        var index = itemView.findViewById<TextView>(R.id.picture_index)
        var comment = itemView.findViewById<ImageView>(R.id.button_comment)
        var setup = itemView.findViewById<ImageView>(R.id.post_setup)
        var totalcomment = itemView.findViewById<ImageView>(R.id.post_total_comment)
        var templateText = itemView.findViewById<TextView>(R.id.template_text)
    }
}