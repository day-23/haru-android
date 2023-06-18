package com.example.haru.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.haru.R
import com.example.haru.data.model.FriendInfo
import com.example.haru.utils.User
import com.example.haru.utils.User.id
import com.example.haru.view.sns.OnFriendClicked
import de.hdodenhof.circleimageview.CircleImageView

class FriendsListAdapter(
    val context: Context,
    private var itemList: ArrayList<FriendInfo> = arrayListOf(),
    val listOwner: String,
    val onFriendClicked: OnFriendClicked
) : RecyclerView.Adapter<FriendsListAdapter.FriendsListViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsListAdapter.FriendsListViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_searched_user, parent, false)
        return FriendsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsListViewHolder, position: Int) {
        hideButtons(holder)
        if (User.id != itemList[position].id)
            showButtons(holder, itemList[position].friendStatus!!)

        Log.e("20191627", "${itemList[position].profileImageUrl}")

        if (itemList[position].profileImageUrl == null
            || itemList[position].profileImageUrl == ""
            || itemList[position].profileImageUrl == "null"
        )
            holder.profile.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.profile_base_image)
        else Glide.with(holder.itemView.context)
            .load(itemList[position].profileImageUrl)
            .into(holder.profile)



        holder.name.text = itemList[position].name

        holder.delete.setOnClickListener {
            onFriendClicked.onDeleteClick(itemList[position])
        }

        holder.accept.setOnClickListener {
            onFriendClicked.onAcceptClick(itemList[position])
            hideButtons(holder)
            showButtons(holder, 2)
        }

        holder.reject.setOnClickListener {
            onFriendClicked.onRejectClick(itemList[position])
            hideButtons(holder)
            showButtons(holder, 0)
        }

        holder.profile.setOnClickListener {
            onFriendClicked.onProfileClick(itemList[position])
        }

        holder.name.setOnClickListener {
            holder.profile.performClick()
        }

        holder.cancelReq.setOnClickListener {
            onFriendClicked.onCancelClick(itemList[position])
            hideButtons(holder)
            showButtons(holder, 0)
        }

        holder.request.setOnClickListener {
            onFriendClicked.onRequestClick(itemList[position])
            hideButtons(holder)
            showButtons(holder, 1)
        }

        holder.setup.setOnClickListener {
            onFriendClicked.onSetupClick(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun showButtons(holder: FriendsListViewHolder, status: Int){
        when(status){
            0 ->{ //아무사이 아님
                holder.request.visibility = View.VISIBLE
            }
            1 -> { // 내가 친구 신청중
                holder.cancelReq.visibility = View.VISIBLE
            }
            2 -> { //친구
                if (User.id == listOwner) {
                    holder.delete.visibility = View.VISIBLE
                    holder.setup.visibility = View.VISIBLE
                } else {
                    holder.myFriend.visibility = View.VISIBLE
                }
            }
            3 -> { //나한테 요청을 건사람
                holder.accept.visibility = View.VISIBLE
                holder.reject.visibility = View.VISIBLE
            }
        }
    }

    fun hideButtons(holder: FriendsListViewHolder) {
        holder.delete.visibility = View.GONE
        holder.accept.visibility = View.GONE
        holder.reject.visibility = View.GONE
        holder.setup.visibility = View.GONE
        holder.request.visibility = View.GONE
        holder.myFriend.visibility = View.GONE
        holder.cancelReq.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(items: ArrayList<FriendInfo>) {
        itemList.addAll(items)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFirstList(items: ArrayList<FriendInfo>) {
        if (items.size > 0)
            itemList = items
        else {
            itemList.clear()
        }
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteFriend(targetUser: FriendInfo) {
        itemList.remove(targetUser)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun patchInfo(item: FriendInfo) {
        val index = itemList.indexOf(item)
        itemList[index] = item
        notifyDataSetChanged()
    }

    inner class FriendsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profile = itemView.findViewById<CircleImageView>(R.id.search_profile)
        var name = itemView.findViewById<TextView>(R.id.search_id)

        var delete = itemView.findViewById<TextView>(R.id.friend_delete)
        var setup = itemView.findViewById<ImageView>(R.id.search_setup)

        var accept = itemView.findViewById<TextView>(R.id.friend_accept)
        var reject = itemView.findViewById<TextView>(R.id.friend_reject)

        var request = itemView.findViewById<TextView>(R.id.friend_send_request)
        var myFriend = itemView.findViewById<TextView>(R.id.friend_myfriend)
        var cancelReq = itemView.findViewById<TextView>(R.id.friend_cancel_request)
    }
}