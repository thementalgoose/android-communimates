package studio.roboto.communimate.chat

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_chat.*
import studio.roboto.communimate.BaseActivity
import studio.roboto.communimate.R
import studio.roboto.communimate.util.HardCodedData
import java.util.*
import java.util.concurrent.TimeoutException

class ChatActivity: BaseActivity(), View.OnClickListener, TextView.OnEditorActionListener {

    private lateinit var mAdapter: ChatAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private var mHandler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
    }

    private fun initViews() {
        mAdapter = ChatAdapter(rvMain)
        rvMain.adapter = mAdapter
        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.stackFromEnd = true
        rvMain.layoutManager = mLayoutManager

        // First time initialise
        firstTimeQuestions()
        rlTextInput.visibility = View.GONE

        btnHelp.setOnClickListener(this)
        btnSeek.setOnClickListener(this)
        btnSend.setOnClickListener(this)
        etInput.setOnEditorActionListener(this)
    }

    // Load stock questions
    private fun firstTimeQuestions() {
        val questions = HardCodedData.getQuestions(this)
        for (x in 0 until questions.size) {
            mHandler.postDelayed( {
                mAdapter.add(questions[x])
            }, ((x + 1) * 1000).toLong())
        }
    }

    private fun transitionToTextInput() {
        llButtons.visibility = View.GONE
        rlTextInput.visibility = View.VISIBLE
        etInput.requestFocus()
    }

    private fun processInput(input: String) {
        if (input == HardCodedData.EASTER_EGG || input.equals(HardCodedData.EASTER_EGG)) {
            launchEasterEgg()
        }
        else {
            mAdapter.add(ChatModel.me(UUID.randomUUID().toString(), input, Date()))
            etInput.setText("")
        }
    }

    private fun launchEasterEgg() {
        Toast.makeText(this, "Easter Egg", Toast.LENGTH_LONG).show()
        HardCodedData.IS_EASTER_EGG_ENABLED = true
        mAdapter.notifyDataSetChanged()
        animateBackgroundAndToolbar()
    }

    private fun animateBackgroundAndToolbar() {
        object : Thread() {
            override fun run() {
                val colorAnim = ArgbEvaluator()
                for (x in 0..100) {
                    try {
                        Thread.sleep(10)
                    }
                    catch (e: TimeoutException) {
                        e.printStackTrace()
                    }
                    val backgroundC: Int = colorAnim.evaluate(x.toFloat() / 100f, Color.WHITE, Color.BLACK) as Int
                    val toolbarC: Int = colorAnim.evaluate(x.toFloat() / 100f, ContextCompat.getColor(applicationContext, R.color.colorPrimary), Color.BLACK) as Int
                    toolbarChat.setBackgroundColor(toolbarC)
                    rlBackground.setBackgroundColor(backgroundC)
                }
                runOnUiThread {
                    setStatusBarColor(Color.BLACK)
                }
            }
        }.start()
    }

    //region Interface :- OnClickListener

    override fun onClick(v: View?) {
        when (v) {
            btnHelp -> {
                Toast.makeText(this, "HELP CLICKED", Toast.LENGTH_LONG).show()
                transitionToTextInput()
            }
            btnSeek -> {
                Toast.makeText(this, "SEEK CLICKED", Toast.LENGTH_LONG).show()
                transitionToTextInput()
            }
            btnSend -> {
                processInput(etInput.text.toString())
            }
        }
    }

    //endregion

    //region Interface :- TextView.OnEditorActionListener

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            processInput(etInput.text.toString())
            return true
        }
        return false
    }

    //endregion
}