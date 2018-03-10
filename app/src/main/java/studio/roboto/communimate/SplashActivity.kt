package studio.roboto.communimate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import studio.roboto.communimate.chat.ChatActivity

class SplashActivity: BaseActivity(), Runnable {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setStatusBarColor(ContextCompat.getColor(this, R.color.chat_bubble_other))
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(this, 2000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(this)
    }

    override fun run() {
        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in)
        startActivity(Intent(this, ChatActivity::class.java))
        finish()
    }
}