package com.finite.nearbuddy.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.finite.nearbuddy.R
import com.finite.nearbuddy.model.ChatMessage
import com.finite.nearbuddy.ui.ConnectionViewModel
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(
    private val chatMessages: List<ChatMessage>,
    private val ncvm: ConnectionViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_USER1 = 1
    private val VIEW_TYPE_USER2 = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_USER1) {
            val itemView = inflater.inflate(R.layout.chat_message_user1, parent, false)
            User1ViewHolder(itemView)
        } else {
            val itemView = inflater.inflate(R.layout.chat_message_user2, parent, false)
            User2ViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = differ.currentList[position]
        when (holder) {
            is User1ViewHolder -> holder.bind(chatMessage)
            is User2ViewHolder -> holder.bind(chatMessage)
        }
    }

    inner class User1ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val user1Message: TextView = itemView.findViewById(R.id.user1_message)
        private val author1ProfilePic: CircleImageView =
            itemView.findViewById(R.id.author1ProfilePic)

        fun bind(chatMessage: ChatMessage) {
            val u1Bitmap =
                BitmapFactory.decodeByteArray(ncvm.user1.profilePic, 0, ncvm.user1.profilePic.size)

            author1ProfilePic.setImageBitmap(u1Bitmap)
            user1Message.text = chatMessage.content
        }
    }

    inner class User2ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val user2Message: TextView = itemView.findViewById(R.id.user2_message)
        private val author2ProfilePic: CircleImageView =
            itemView.findViewById(R.id.author2ProfilePic)

        fun bind(chatMessage: ChatMessage) {
            val u2Bitmap = BitmapFactory.decodeByteArray(
                ncvm.user2.value!!.profilePic,
                0,
                ncvm.user2.value!!.profilePic.size
            )


            author2ProfilePic.setImageBitmap(u2Bitmap)
            user2Message.text = chatMessage.content
        }

    }

    override fun getItemCount(): Int = differ.currentList.size


    override fun getItemViewType(position: Int): Int {
        val chatMessage = differ.currentList[position]
        return if (chatMessage.author == ncvm.user1.name) {
            VIEW_TYPE_USER1
        } else {
            VIEW_TYPE_USER2
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.content == newItem.content // later add a timestamp and content check
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(newList: List<ChatMessage>) {
        differ.submitList(newList)
    }
}
