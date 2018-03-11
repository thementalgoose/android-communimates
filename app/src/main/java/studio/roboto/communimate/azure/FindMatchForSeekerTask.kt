package studio.roboto.communimate.azure

import android.os.AsyncTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import studio.roboto.communimate.azure.retrofit_models.requests.KeyPhrasesRequestModel
import studio.roboto.communimate.azure.retrofit_models.responses.KeyPhrasesResponseModel
import studio.roboto.communimate.azure.retrofit_models.responses.SearchResponseModel

class FindMatchForSeekerTask : AsyncTask<Unit, Unit, Unit> {

    val seekerPhrase: String
    val listener: FindMatchForSeekerTaskListener

    var helperUserId: String? = null

    constructor(seekerPhrase: String, listener: FindMatchForSeekerTaskListener) : super() {
        this.seekerPhrase = seekerPhrase
        this.listener = listener
    }

    override fun doInBackground(vararg p0: Unit?) {

        getSeekerPhraseKeywords(seekerPhrase)

        WebAPI.getSearchAPI()
    }

    override fun onPostExecute(result: Unit?) {
        if (helperUserId != null) {
            listener.success(helperUserId!!)
        } else {
            listener.failure()
        }
    }

    fun getSeekerPhraseKeywords(seekerPhrase: String) {
        WebAPI
                .getKeyPhrasesAPI()
                .keyPhrases(KeyPhrasesRequestModel.fromPhrase(seekerPhrase))
                .enqueue(object : Callback<KeyPhrasesResponseModel> {

                    override fun onFailure(call: Call<KeyPhrasesResponseModel>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<KeyPhrasesResponseModel>?,
                                            response: Response<KeyPhrasesResponseModel>?) {

                        val keywordsArray = response!!.body()!!.documents!!.first().keyPhrases

                        val keywordsString = keywordsArray!!.reduce { s1, s2 -> "$s1,$s2" }

                        searchForKeywordsFromHelpers(keywordsString)
                    }
                })
    }

    fun searchForKeywordsFromHelpers(seekerKeywordsString: String) {
        WebAPI
                .getSearchAPI()
                .search(seekerKeywordsString)
                .enqueue(object : Callback<SearchResponseModel> {
                    override fun onResponse(call: Call<SearchResponseModel>?, response: Response<SearchResponseModel>?) {
                        helperUserId = response!!.body()!!.results!!.first().userId!!
                    }

                    override fun onFailure(call: Call<SearchResponseModel>?, t: Throwable?) {
                    }
                })
    }

    //region FindMatchForSeekerTaskListener
    interface FindMatchForSeekerTaskListener {
        fun success(helperUserId: String)

        fun failure()
    }
    //endregion
}