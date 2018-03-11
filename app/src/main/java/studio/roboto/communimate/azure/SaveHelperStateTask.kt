package studio.roboto.communimate.azure

import android.os.AsyncTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import studio.roboto.communimate.azure.retrofit_models.requests.KeyPhrasesRequestModel
import studio.roboto.communimate.azure.retrofit_models.responses.KeyPhrasesResponseModel

class SaveHelperStateTask : AsyncTask<Unit, Unit, Unit> {

    val helperUserId: String
    val helperUserPhrase: String
    val listener: SaveHelperStateTaskListener

    var isSuccess = true

    constructor(helperUserId: String, helperUserPhrase: String, listener: SaveHelperStateTaskListener) : super() {
        this.helperUserId = helperUserId
        this.helperUserPhrase = helperUserPhrase
        this.listener = listener
    }

    override fun doInBackground(vararg p0: Unit?) {
        saveHelperState(helperUserId, helperUserPhrase)
    }

    override fun onPostExecute(result: Unit?) {
        if (isSuccess) {
            listener.success()
        } else {
            listener.failure()
        }
    }

    fun saveHelperState(helperUserId: String, phrase: String) {

        WebAPI
                .getKeyPhrasesAPI()
                .keyPhrases(KeyPhrasesRequestModel.fromPhrase(phrase))
                .enqueue(object : Callback<KeyPhrasesResponseModel> {

                    override fun onFailure(call: Call<KeyPhrasesResponseModel>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<KeyPhrasesResponseModel>?,
                                            response: Response<KeyPhrasesResponseModel>?) {

                        val keywordsArray = response!!.body()!!.documents!!.first().keyPhrases

                        val keywordsString = keywordsArray!!.reduce { s1, s2 -> "$s1,$s2" }

                        saveKeywordsToAzureTables(helperUserId, keywordsString)
                    }
                })
    }

    private fun saveKeywordsToAzureTables(helperUserId: String, keywordsString: String) {
        InsertHelperEntityTask(object : InsertHelperEntityTask.InsertHelperEntityTaskListener {

            override fun success() {
                isSuccess = true
            }

            override fun failure() {
                isSuccess = false
            }
        })
    }

    //region SaveHelperStateTaskListener
    interface SaveHelperStateTaskListener {

        fun success()

        fun failure()
    }
    //endregion
}