package studio.roboto.communimate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import studio.roboto.communimate.chat.ChatActivity

class SplashActivity : AppCompatActivity(), Runnable {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Careful when you uncomment this
        //        for (i in 1..100) {
        //            InsertHelperEntityTask(object : InsertHelperEntityTask.InsertHelperEntityTaskListener {
        //                override fun success() {
        //                    Timber.d("Success inserting a helper entity")
        //                }
        //
        //                override fun failure() {
        //                    Timber.e("Failure inserting a helper entity")
        //                }
        //            }).execute(HelperEntity(
        //                    "seb-partition-" + UUID.randomUUID().toString(),
        //                    "jordan-keys-" + UUID.randomUUID().toString()))
        //        }

        //        ListHelperEntitiesTask(object : ListHelperEntitiesTask.ListHelperEntitiesTaskListener {
        //            override fun success(list: Iterable<HelperEntity>) {
        //                Timber.d("Success")
        //            }
        //
        //            override fun failure() {
        //                Timber.d("Failure")
        //            }
        //        }).execute()

        //        WebAPI
        //                .getKeyPhrasesAPI()
        //                .keyPhrases(KeyPhrasesRequestModel.fromPhrase("How do I fix a flat tyre"))
        //                .enqueue(object : Callback<KeyPhrasesResponseModel>{
        //
        //                    override fun onFailure(call: Call<KeyPhrasesResponseModel>?, t: Throwable?) {
        //
        //                    }
        //
        //                    override fun onResponse(call: Call<KeyPhrasesResponseModel>?, response: Response<KeyPhrasesResponseModel>?) {
        //                        Timber.d(response!!.body()!!.documents!!.first().keyPhrases!!.first())
        //                    }
        //                })

        //        WebAPI
        //                .getSearchAPI()
        //                .search("seb")
        //                .enqueue(object : Callback<SearchResponseModel> {
        //                    override fun onResponse(call: Call<SearchResponseModel>?, response: Response<SearchResponseModel>?) {
        //                        Timber.d("${response!!.body()!!.results!!.first().userKeywords}")
        //                    }
        //
        //                    override fun onFailure(call: Call<SearchResponseModel>?, t: Throwable?) {
        //                    }
        //                })
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(this, 1000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(this)
    }

    override fun run() {
        finish()
        startActivity(Intent(this, ChatActivity::class.java))
    }
}