package studio.roboto.communimate.chat

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import studio.roboto.communimate.R

class ChatViewHolder: RecyclerView.ViewHolder {

    private val mLlFromMe: LinearLayout
    private val mTvFromMe: TextView
    private val mLlFromOther: LinearLayout
    private val mTvFromOther: TextView

    constructor(itemView: View?) : super(itemView) {
        mLlFromMe = itemView!!.findViewById(R.id.llFromMe)
        mLlFromOther = itemView.findViewById(R.id.llFromOther)
        mTvFromMe = itemView.findViewById(R.id.tvFromMe)
        mTvFromOther = itemView.findViewById(R.id.tvFromOther)
    }

    fun bind(msg: ChatModel) {
        mLlFromMe.visibility = if (msg.Type == ChatModel.TYPE_MESSAGE_FROM_ME) View.VISIBLE else View.GONE
        mLlFromOther.visibility = if (msg.Type == ChatModel.TYPE_MESSAGE_FROM_OTHER) View.VISIBLE else View.GONE

        when (msg.Type) {
            ChatModel.TYPE_MESSAGE_FROM_ME -> {
                mTvFromMe.text = msg.Message
            }
            ChatModel.TYPE_MESSAGE_FROM_OTHER -> {
                mTvFromOther.text = msg.Message
            }
        }
    }
}