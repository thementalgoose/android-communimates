package studio.roboto.communimate.util

import android.content.Context
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

        val EASTER_EGG: String = "easter"
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
                                    ChatModel.me("ID_1", "INFO", DateUtil.plusSecond(1))
                            ),
                            "Open the pod bay doors, Hal",
                            R.drawable.abc_btn_check_material
                    )
            )
        }
    }

    class EasterEgg(
            var messages: List<ChatModel>,
            var recognitionPhrase: String,
            @DrawableRes var imageRes: Int
    )
}