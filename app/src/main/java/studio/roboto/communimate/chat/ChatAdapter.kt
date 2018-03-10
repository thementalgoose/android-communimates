package studio.roboto.communimate.chat

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import studio.roboto.communimate.R
import studio.roboto.communimate.util.SortedListAdapter
import studio.roboto.communimate.util.SortedListComparator

class ChatAdapter(val rv: RecyclerView): SortedListAdapter<ChatModel, ChatViewHolder>(ChatModel::class.java, ChatComparator()) {

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int, item: ChatModel) {
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val itemView: View? = LayoutInflater.from(parent.context).inflate(R.layout.element_chat, parent, false)
        return ChatViewHolder(itemView)
    }

    //region Class Declaration:- Chat Comparator

    class ChatComparator : SortedListComparator<ChatModel> {
        override fun compare(o1: ChatModel?, o2: ChatModel?): Int {
            return o1!!.Time.compareTo(o2!!.Time)
        }

        override fun equal(obj1: ChatModel, obj2: ChatModel): Boolean {
            return obj1.Id == obj2.Id || obj1.Id.equals(obj2.Id)
        }
    }

    //endregion

    override fun add(model: ChatModel) {
        super.add(model)
        rv.smoothScrollToPosition(itemCount)
    }
}