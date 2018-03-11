package studio.roboto.communimate.azure.retrofit_models.requests

import java.util.*

class KeyPhrasesRequestModel {

    public val documents: Array<KeyPhrasesDocumentRequestModel>

    constructor(documents: Array<KeyPhrasesDocumentRequestModel>) {
        this.documents = documents
    }

    companion object {
        fun fromPhrase(phrase: String) : KeyPhrasesRequestModel {

            val documentModel =
                    KeyPhrasesDocumentRequestModel(
                            "en",
                            UUID.randomUUID().toString(),
                            phrase)

            val array = Array<KeyPhrasesDocumentRequestModel>(1, { documentModel })

            return KeyPhrasesRequestModel(array)
        }
    }
}

class KeyPhrasesDocumentRequestModel {

    public val language: String
    public val id: String
    public val text: String

    constructor(language: String, id: String, text: String) {
        this.language = language
        this.id = id
        this.text = text
    }
}
