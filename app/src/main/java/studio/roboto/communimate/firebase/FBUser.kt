package studio.roboto.communimate.firebase

class FBUser(
        var Skills: Array<String>,
        var Messages: Array<String>,
        var Type: String
) {
    companion object {
        val TYPE_SEEKER = "SEEKER"
        val TYPE_HELPER = "HELPER"
    }
}