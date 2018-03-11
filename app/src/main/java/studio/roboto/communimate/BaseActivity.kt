package studio.roboto.communimate

import android.support.annotation.ColorInt
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.support.v4.content.ContextCompat
import android.view.WindowManager



abstract class BaseActivity: AppCompatActivity() {

    open var toolbar: Toolbar? = null

    fun initToolbar(showBack: Boolean?) {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        showBack?.let { sB ->
            if (sB) {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                supportActionBar?.setHomeButtonEnabled(true)
            }
        }
    }

    override fun onBackPressed() {

    }

    fun setStatusBarColor(@ColorInt colour: Int) {
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        // finally change the color
        window.statusBarColor = colour
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}