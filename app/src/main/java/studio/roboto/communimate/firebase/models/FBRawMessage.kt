package studio.roboto.communimate.firebase.models

import android.content.Context
import studio.roboto.communimate.chat.ChatModel
import studio.roboto.communimate.firebase.FirebaseHandler
import java.util.*

class FBRawMessage() {

    private var date: Long = 0L
    private lateinit var content: String
    private lateinit var id: String

    constructor(date: Long, content: String, id: String) : this() {
        this.date = date
        this.content = content
        this.id = id
    }

    fun getDate(): Long {
        return date
    }
    fun setDate(date: Long) {
        this.date = date
    }

    fun getContent(): String {
        return content
    }
    fun setContent(content: String) {
        this.content = content
    }

    fun getId(): String {
        return id
    }
    fun setId(id: String) {
        this.id = id
    }

    fun convertToMsgObject(msgId: String, context: Context): ChatModel {
        val uid: String = FirebaseHandler.getMyUserId(context)
        if (uid == id) {
            return ChatModel.me(msgId, content, Date(date))
        }
        else {
            return ChatModel.other(msgId, content, Date(date))
        }
    }

    override fun toString(): String {
        return "ID[$id] Content[$content]"
    }
}