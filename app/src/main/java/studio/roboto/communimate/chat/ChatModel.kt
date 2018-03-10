package studio.roboto.communimate.chat

import java.util.*

class ChatModel(
        val Type: Int,
        val Id: String,
        val Message: String,
        val Time: Date
) {
    companion object {
        val TYPE_MESSAGE_FROM_ME: Int = 0
        val TYPE_MESSAGE_FROM_OTHER: Int = 1

        fun me(Id: String, Message: String, Time: Date): ChatModel {
            return ChatModel(TYPE_MESSAGE_FROM_ME, Id, Message, Time)
        }
        fun other(Id: String, Message: String, Time: Date): ChatModel {
            return ChatModel(TYPE_MESSAGE_FROM_OTHER, Id, Message, Time)
        }
    }
}