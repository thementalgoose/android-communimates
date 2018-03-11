package studio.roboto.communimate.azure

import android.os.AsyncTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import studio.roboto.communimate.azure.retrofit_models.requests.KeyPhrasesRequestModel
import studio.roboto.communimate.azure.retrofit_models.responses.KeyPhrasesResponseModel
import studio.roboto.communimate.azure.retrofit_models.responses.SearchResponseModel
import studio.roboto.communimate.util.HardCodedData

class FindMatchForSeekerTask : AsyncTask<Unit, Unit, Unit> {

    val seekerPhrase: String
    val listener: FindMatchForSeekerTaskListener

    var helperUserId: String? = null

    constructor(seekerPhrase: String, listener: FindMatchForSeekerTaskListener) : super() {
        this.seekerPhrase = seekerPhrase
        this.listener = listener
    }

    override fun doInBackground(vararg p0: Unit?) {
        if (HardCodedData.USE_HARD_CODED_VALUES) {
            helperUserId = HardCodedData.HARD_CODED_HELPER_ID
        }
        else {
            getSeekerPhraseKeywords(seekerPhrase)
        }
    }

    override fun onPostExecute(result: Unit?) {
        if (helperUserId != null) {
            listener.success(helperUserId!!)
        } else {
            listener.failure()
        }
    }

    fun getSeekerPhraseKeywords(seekerPhrase: String) {
        val response = WebAPI.getKeyPhrasesAPI()
                .keyPhrases(KeyPhrasesRequestModel.fromPhrase(seekerPhrase))
                .execute()

        val keywordsArray = response!!.body()!!.documents!!.first().keyPhrases
        val keywordsString = keywordsArray!!.reduce { s1, s2 -> "$s1,$s2" }
        searchForKeywordsFromHelpers(keywordsString)
    }

    fun searchForKeywordsFromHelpers(seekerKeywordsString: String) {
        val response = WebAPI
                .getSearchAPI()
                .search(seekerKeywordsString)
                .execute()
        val results = response?.body()?.results
        if (results != null && results.size > 0) {
            helperUserId = results.first().userId
        }
    }

    //region FindMatchForSeekerTaskListener
    interface FindMatchForSeekerTaskListener {
        fun success(helperUserId: String?)

        fun failure()
    }
    //endregion
}