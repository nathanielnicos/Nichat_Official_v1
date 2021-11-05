package id.nns.nichat.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.nns.nichat.databinding.ListImageFromMeBinding
import id.nns.nichat.databinding.ListImageFromYouBinding
import id.nns.nichat.databinding.ListMessageFromMeBinding
import id.nns.nichat.databinding.ListMessageFromYouBinding
import id.nns.nichat.domain.model.Message
import id.nns.nichat.ui.image.ImageActivity
import id.nns.nichat.utils.Constants.FIREBASE_STORAGE_URL
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(private val fromId: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val CHAT_FROM_ME = 0
        private const val CHAT_FROM_YOU = 1
        private const val IMAGE_FROM_ME = 2
        private const val IMAGE_FROM_YOU = 3
    }

    private lateinit var messageFromMeBinding: ListMessageFromMeBinding
    private lateinit var messageFromYouBinding: ListMessageFromYouBinding
    private lateinit var imageFromMeBinding: ListImageFromMeBinding
    private lateinit var imageFromYouBinding: ListImageFromYouBinding

    var chats = ArrayList<Message>()
        @SuppressLint("NotifyDataSetChanged")
        set(data) {
            if (data.size > 0) {
                this.chats.clear()
            }
            this.chats.addAll(data)

            notifyDataSetChanged()
        }

    fun clear() {
        val size = chats.size
        chats.clear()
        notifyItemRangeRemoved(0, size)
    }

    inner class ChatFromMeViewHolder(private val cfmBinding: ListMessageFromMeBinding) : RecyclerView.ViewHolder(cfmBinding.root) {
        fun bind(message: Message) {
            if (message.timeStamp != null) {
                val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                val timeStamp = Date(message.timeStamp * 1000)
                val time = timeFormat.format(timeStamp)

                cfmBinding.tvFromMe.text = message.text
                cfmBinding.tvTimeFromMe.text = time
            }
        }
    }

    inner class ImageFromMeViewHolder(private val ifmBinding: ListImageFromMeBinding) : RecyclerView.ViewHolder(ifmBinding.root) {
        fun bind(message: Message) {
            if (message.timeStamp != null) {
                val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                val timeStamp = Date(message.timeStamp * 1000)
                val time = timeFormat.format(timeStamp)

                Glide.with(itemView.context)
                    .load(message.text)
                    .into(ifmBinding.ivFromMe)

                ifmBinding.tvTimeFromMe.text = time

                ifmBinding.ivFromMe.setOnClickListener {
                    Intent(itemView.context, ImageActivity::class.java).apply {
                        putExtra(ImageActivity.OPEN_IMAGE, message.text)
                        itemView.context.startActivity(this)
                    }
                }
            }
        }
    }

    inner class ChatFromYouViewHolder(private val cfyBinding: ListMessageFromYouBinding) : RecyclerView.ViewHolder(cfyBinding.root) {
        fun bind(message: Message) {
            if (message.timeStamp != null) {
                val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                val timeStamp = Date(message.timeStamp * 1000)
                val time = timeFormat.format(timeStamp)

                cfyBinding.tvFromYou.text = message.text
                cfyBinding.tvTimeFromYou.text = time
            }
        }
    }

    inner class ImageFromYouViewHolder(private val ifyBinding: ListImageFromYouBinding) : RecyclerView.ViewHolder(ifyBinding.root) {
        fun bind(message: Message) {
            if (message.timeStamp != null) {
                val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                val timeStamp = Date(message.timeStamp * 1000)
                val time = timeFormat.format(timeStamp)

                Glide.with(itemView.context)
                    .load(message.text)
                    .into(ifyBinding.ivFromYou)

                ifyBinding.tvTimeFromYou.text = time

                ifyBinding.ivFromYou.setOnClickListener {
                    Intent(itemView.context, ImageActivity::class.java).apply {
                        putExtra(ImageActivity.OPEN_IMAGE, message.text)
                        itemView.context.startActivity(this)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return when (viewType) {
            CHAT_FROM_ME -> {
                messageFromMeBinding = ListMessageFromMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChatFromMeViewHolder(messageFromMeBinding)
            }
            CHAT_FROM_YOU -> {
                messageFromYouBinding = ListMessageFromYouBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChatFromYouViewHolder(messageFromYouBinding)
            }
            IMAGE_FROM_ME -> {
                imageFromMeBinding = ListImageFromMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageFromMeViewHolder(imageFromMeBinding)
            }
            else -> {
                imageFromYouBinding = ListImageFromYouBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ImageFromYouViewHolder(imageFromYouBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            CHAT_FROM_ME -> {
                val chatFromMeViewHolder = holder as ChatFromMeViewHolder
                chatFromMeViewHolder.bind(chats[position])
            }
            CHAT_FROM_YOU -> {
                val chatFromYouViewHolder = holder as ChatFromYouViewHolder
                chatFromYouViewHolder.bind(chats[position])
            }
            IMAGE_FROM_ME -> {
                val imageFromMeViewHolder = holder as ImageFromMeViewHolder
                imageFromMeViewHolder.bind(chats[position])
            }
            else -> {
                val imageFromYouViewHolder = holder as ImageFromYouViewHolder
                imageFromYouViewHolder.bind(chats[position])
            }
        }
    }

    override fun getItemCount(): Int = chats.size

    override fun getItemViewType(position: Int): Int {
        return if (chats[position].fromId == fromId) {
            if (chats[position].text.toString().contains(FIREBASE_STORAGE_URL)) {
                IMAGE_FROM_ME
            } else {
                CHAT_FROM_ME
            }
        } else {
            if (chats[position].text.toString().contains(FIREBASE_STORAGE_URL)) {
                IMAGE_FROM_YOU
            } else {
                CHAT_FROM_YOU
            }
        }
    }

}