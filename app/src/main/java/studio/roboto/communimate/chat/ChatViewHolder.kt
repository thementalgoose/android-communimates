package studio.roboto.communimate.chat

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import studio.roboto.communimate.R
import studio.roboto.communimate.util.HardCodedData

class ChatViewHolder: RecyclerView.ViewHolder {

    private val mLlFromMe: LinearLayout
    private val mTvFromMe: TextView
    private val mLlFromOther: LinearLayout
    private val mTvFromOther: TextView
    private val mLottieView: LottieAnimationView

    constructor(itemView: View?) : super(itemView) {
        mLlFromMe = itemView!!.findViewById(R.id.llFromMe)
        mLlFromOther = itemView.findViewById(R.id.llFromOther)
        mTvFromMe = itemView.findViewById(R.id.tvFromMe)
        mTvFromOther = itemView.findViewById(R.id.tvFromOther)
        mLottieView = itemView.findViewById(R.id.lottieView)
    }

    fun bind(msg: ChatModel) {
        mLlFromMe.visibility = if (msg.Type == ChatModel.TYPE_MESSAGE_FROM_ME) View.VISIBLE else View.GONE
        mLlFromOther.visibility = if (msg.Type == ChatModel.TYPE_MESSAGE_FROM_OTHER) View.VISIBLE else View.GONE

        when (msg.Type) {
            ChatModel.TYPE_MESSAGE_FROM_ME -> {
                mTvFromMe.text = msg.Message
            }
            ChatModel.TYPE_MESSAGE_FROM_OTHER -> {
                if (msg.Id.startsWith("LOADING_DIALOG_")) {
                    mLottieView.visibility = View.VISIBLE
                    mTvFromOther.visibility = View.GONE
                }
                else {
                    mLottieView.visibility = View.GONE
                    mTvFromOther.visibility = View.VISIBLE
                    mTvFromOther.text = msg.Message
                    if (msg.Message == itemView?.context?.getString(R.string.wave)) {
                        mTvFromOther.textSize = 64f
                    } else {
                        mTvFromOther.textSize = 16f
                    }

                    if (HardCodedData.IS_EASTER_EGG_ENABLED) {
                        mLlFromOther.setBackgroundResource(R.drawable.chat_other_easter)
                    } else {
                        mLlFromOther.setBackgroundResource(R.drawable.chat_other)
                    }
                }
            }
        }
    }
}