package id.nns.nichat.utils

import androidx.recyclerview.widget.DiffUtil
import id.nns.nichat.domain.model.Channel

class ChannelDiffCallback(
    private val oldList: ArrayList<Channel>,
    private val newList: ArrayList<Channel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].partnerId == newList[newItemPosition].partnerId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].latestMessage?.text ==
            newList[newItemPosition].latestMessage?.text &&
                oldList[oldItemPosition].latestMessage?.timeStamp ==
                    newList[newItemPosition].latestMessage?.timeStamp

}