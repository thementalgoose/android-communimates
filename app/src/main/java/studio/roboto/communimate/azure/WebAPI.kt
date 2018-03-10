package studio.roboto.communimate.azure

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class WebAPI {
    companion object {

        var sWebAPI: IWebAPI? = null

        val AZURE_WEB_API: String = ""

        fun getAPI(): IWebAPI {
            if (sWebAPI == null) {
                initWebAPI()
            }
            return sWebAPI!!
        }

        private fun initWebAPI() {
            val interceptor: Interceptor = Interceptor { chain ->
                var request: Request = chain.request()!!
                request = request.newBuilder()
                        .addHeader("Content-type", "application/json")
                        .build()
                chain.proceed(request)
            }
            sWebAPI = buildAPIAdapter(interceptor).create(IWebAPI::class.java)
        }

        private fun buildAPIAdapter(interceptor: Interceptor): Retrofit {
            val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder().addInterceptor(interceptor)

            val loggingIntercept: HttpLoggingInterceptor = HttpLoggingInterceptor()
            loggingIntercept.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingIntercept)

            val client: OkHttpClient = clientBuilder
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()

            return Retrofit.Builder()
                    .client(client)
                    .baseUrl(AZURE_WEB_API)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
    }
}