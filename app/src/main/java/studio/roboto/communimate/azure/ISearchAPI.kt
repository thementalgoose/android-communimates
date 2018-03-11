package studio.roboto.communimate.azure

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import studio.roboto.communimate.azure.retrofit_models.responses.SearchResponseModel

interface ISearchAPI {

    @GET("/indexes/azuretable-index/docs?api-version=2016-09-01")
    fun search(@Query("search") searchString: String): Call<SearchResponseModel>
}