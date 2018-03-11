package studio.roboto.communimate.azure

import android.os.AsyncTask
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import studio.roboto.communimate.azure.azure_entities.HelperEntity
import studio.roboto.communimate.azure.retrofit_models.requests.KeyPhrasesRequestModel
import studio.roboto.communimate.azure.retrofit_models.responses.KeyPhrasesResponseModel
import studio.roboto.communimate.util.HardCodedData

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
        if (!HardCodedData.USE_HARD_CODED_VALUES)  {
            saveHelperState(helperUserId, helperUserPhrase)
        }
        else {
            isSuccess = true
        }
    }

    override fun onPostExecute(result: Unit?) {
        if (isSuccess) {
            listener.success()
        } else {
            listener.failure()
        }
    }

    fun saveHelperState(helperUserId: String, phrase: String) {

        val response = WebAPI
                .getKeyPhrasesAPI()
                .keyPhrases(KeyPhrasesRequestModel.fromPhrase(phrase))
                .execute()
        val keywordsArray = response!!.body()!!.documents!!.first().keyPhrases
        val keywordsString = keywordsArray!!.reduce { s1, s2 -> "$s1,$s2" }
        saveKeywordsToAzureTables(helperUserId, keywordsString)
    }

    private fun saveKeywordsToAzureTables(helperUserId: String, keywordsString: String) {

        InsertHelperEntityTask(object : InsertHelperEntityTask.InsertHelperEntityTaskListener {

            override fun success() {
                isSuccess = true
            }

            override fun failure() {
                isSuccess = false
            }
        }).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, HelperEntity(helperUserId, keywordsString))

//        InsertHelperEntityTask()

        //execute(HelperEntity(helperUserId, keywordsString))
    }

    //region SaveHelperStateTaskListener
    interface SaveHelperStateTaskListener {

        fun success()

        fun failure()
    }
    //endregion
}
