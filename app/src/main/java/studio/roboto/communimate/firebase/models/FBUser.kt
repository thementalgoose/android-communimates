package studio.roboto.communimate.firebase.models

class FBUser(
        var skills: List<String>,
        var messages: List<String>,
        var type: String
) {
    companion object {
        val TYPE_SEEKER = "SEEKER"
        val TYPE_HELPER = "HELPER"
    }
}