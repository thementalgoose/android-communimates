package studio.roboto.donny

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}