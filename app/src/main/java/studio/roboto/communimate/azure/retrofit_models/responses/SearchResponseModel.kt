package studio.roboto.communimate.azure.retrofit_models.responses

import com.google.gson.annotations.SerializedName

class SearchResponseModel {

    @SerializedName("value")
    var results: Array<SearchResponseResultModel>? = null
}

class SearchResponseResultModel {

    @SerializedName("PartitionKey")
    val userId: String? = null

    @SerializedName("RowKey")
    var userKeywords: String? = null
}