package studio.roboto.communimate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import studio.roboto.communimate.chat.ChatActivity

class SplashActivity: AppCompatActivity(), Runnable {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
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