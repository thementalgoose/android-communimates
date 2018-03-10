package studio.roboto.communimate.util

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import studio.roboto.communimate.R
import studio.roboto.communimate.chat.ChatModel

class HardCodedData {
    companion object {

        fun getQuestions(context: Context): List<ChatModel> {
            val list: MutableList<ChatModel> = mutableListOf()
            list.add(ChatModel.other("MSG_1", context.getString(R.string.wave), DateUtil.from("01/01/1970 00:00")))
            list.add(ChatModel.other("MSG_2", context.getString(R.string.chat_msg_1), DateUtil.from("01/01/1970 00:01")))
            list.add(ChatModel.other("MSG_3", context.getString(R.string.chat_msg_2), DateUtil.from("01/01/1970 00:02")))
            return list.toList()
        }

        var IS_EASTER_EGG_ENABLED: Boolean = false

        fun getEasterEggByPhrase(phrase: String): EasterEgg? {
            for (x in allEasterEggs()) {
                if (x.recognitionPhrase == phrase || x.recognitionPhrase.equals(phrase)) {
                    return x
                }
            }
            return null
        }

        fun allEasterEggs(): List<EasterEgg> {
            return listOf(
                    EasterEgg(listOf(
                                    ChatModel.me("ID_1", "Open the pod bay doors", DateUtil.plusSecond(1)),
                                    ChatModel.other("ID_2", "I'm sorry, Dave. I'm afraid I can't do that.", DateUtil.plusSecond(2)),
                                    ChatModel.me("ID_3", "What's the problem?", DateUtil.plusSecond(3)),
                                    ChatModel.other("ID_4", "I think you know what the problem is just as well as I do.", DateUtil.plusSecond(4)),
                                    ChatModel.me("ID_5", "What are you talking about, HAL?", DateUtil.plusSecond(5)),
                                    ChatModel.other("ID_6", "This mission is too important for me to allow you to jeopardize it", DateUtil.plusSecond(6)),
                                    ChatModel.me("ID_7", "I don't know what you're talking about, HAL", DateUtil.plusSecond(7)),
                                    ChatModel.other("ID_8", "I know that you and Frank were planning to disconnect me, and I'm afraid that's something I cannot allow to happen.", DateUtil.plusSecond(8))
                            ),
                            "Open the pod bay doors",
                            R.drawable.oval_2,
                            Color.BLACK
                    ),
                    EasterEgg(listOf(
                                    ChatModel.me("ID_1", "A long time ago, in a galaxy far far away", DateUtil.plusSecond(1))
                            ),
                            "A long time ago",
                            R.drawable.ic_home,
                            Color.BLACK
                    )
            )
        }
    }

    class EasterEgg(
            var messages: List<ChatModel>,
            var recognitionPhrase: String,
            @DrawableRes var imageRes: Int,
            @ColorInt var backgroundColor: Int
    )
}