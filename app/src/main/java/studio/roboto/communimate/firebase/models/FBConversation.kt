package studio.roboto.communimate.firebase.models

class FBConversation(
        val users: Array<String>,
        val messages: Array<FBConversation>
)