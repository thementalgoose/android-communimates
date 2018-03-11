package studio.roboto.communimate.azure

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import studio.roboto.communimate.azure.retrofit_models.requests.KeyPhrasesRequestModel
import studio.roboto.communimate.azure.retrofit_models.responses.KeyPhrasesResponseModel


interface IKeyPhrasesAPI {

    @POST("text/analytics/v2.0/keyPhrases")
    fun keyPhrases(@Body requestModel: KeyPhrasesRequestModel): Call<KeyPhrasesResponseModel>
}