package id.nns.nichat.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.nns.nichat.R
import id.nns.nichat.databinding.ListChatBinding
import id.nns.nichat.domain.model.Channel
import id.nns.nichat.ui.chat.ChatActivity
import id.nns.nichat.ui.image.ImageActivity
import id.nns.nichat.utils.ChannelDiffCallback
import id.nns.nichat.utils.Constants.FIREBASE_STORAGE_URL
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeAdapter(private val currentUserId: String?) : RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {

    private lateinit var binding: ListChatBinding

    var channels = ArrayList<Channel>()
        set(data) {
            val diffCallback = ChannelDiffCallback(channels, data)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            this.channels.clear()
            this.channels.addAll(data)
            diffResult.dispatchUpdatesTo(this)
        }

    inner class HomeViewHolder(private val vhBinding: ListChatBinding) : RecyclerView.ViewHolder(vhBinding.root) {

        fun bind(channel: Channel) {
            Glide.with(itemView.context)
                .load(channel.partnerUser?.imgUrl)
                .placeholder(R.drawable.profile)
                .into(vhBinding.civListChat)

            if (channel.latestMessage?.timeStamp != null) {
                val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
                val timeStamp = Date(channel.latestMessage.timeStamp * 1000)
                val time = timeFormat.format(timeStamp)

                vhBinding.tvTimeChat.text = time
            }

            vhBinding.tvNameListChat.text = channel.partnerUser?.name
            vhBinding.tvLatestMessageListChat.text =
                if (channel.latestMessage?.fromId == currentUserId) {
                    if (channel.latestMessage?.text.toString().contains(FIREBASE_STORAGE_URL)) {
                        itemView.context.resources.getString(R.string.you_sent_an_image)
                    } else {
                        itemView.context.resources.getString(R.string.you) + " " + channel.latestMessage?.text
                    }
                } else {
                    if (channel.latestMessage?.text.toString().contains(FIREBASE_STORAGE_URL)) {
                        itemView.context.resources.getString(R.string.image)
                    } else {
                        channel.latestMessage?.text
                    }
                }

            vhBinding.civListChat.setOnClickListener {
                Intent(itemView.context, ImageActivity::class.java).apply {
                    putExtra(ImageActivity.OPEN_IMAGE, channel.partnerUser?.imgUrl)
                    itemView.context.startActivity(this)
                }
            }

            vhBinding.cvListChat.setOnClickListener {
                Intent(itemView.context, ChatActivity::class.java).apply {
                    putExtra(ChatActivity.KEY_USER, channel.partnerUser)
                    itemView.context.startActivity(this)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        binding = ListChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(channels[position])
    }

    override fun getItemCount(): Int = channels.size

}