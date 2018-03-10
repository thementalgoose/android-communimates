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
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import studio.roboto.communimate.util.DateUtil


class ChatActivity: BaseActivity(), View.OnClickListener, TextView.OnEditorActionListener {

    private lateinit var mAdapter: ChatAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private var mHandler: Handler = Handler()
    private var mQuestionInputMode = true

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
                if (x == questions.size - 1) {
                    llButtons.visibility = View.VISIBLE
                }
            }, ((x + 1) * 1000).toLong())
        }
    }

    private fun transitionToTextInput() {
        llButtons.visibility = View.GONE
        rlTextInput.visibility = View.VISIBLE
        etInput.requestFocus()
    }

    private fun processInput(input: String) {
        val egg: HardCodedData.EasterEgg? = HardCodedData.getEasterEggByPhrase(input)
        if (egg != null) {
            launchEasterEgg(egg)
        }
        else {
            if (mQuestionInputMode) {
                val input: String = etInput.text.toString()
                Toast.makeText(this, "QUESTION INPUT", Toast.LENGTH_LONG).show()
            }
            else {
                // Add to Firebase
                mAdapter.add(ChatModel.me(UUID.randomUUID().toString(), input, Date()))
                etInput.setText("")
            }
        }
    }

    //region Easter Egg Functionality

    private fun launchEasterEgg(egg: HardCodedData.EasterEgg) {
        HardCodedData.IS_EASTER_EGG_ENABLED = true
        mAdapter.notifyDataSetChanged()
        animateBackgroundAndToolbar(egg.backgroundColor)
        launchScript(egg.messages)
        hideKeyboard(this)
        etInput.isEnabled = false
        imgIcon.setBackgroundResource(egg.imageRes)
        etInput.setText("")
    }

    private fun animateBackgroundAndToolbar(colour: Int) {
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
                    val backgroundC: Int = colorAnim.evaluate(x.toFloat() / 100f, Color.WHITE, colour) as Int
                    val toolbarC: Int = colorAnim.evaluate(x.toFloat() / 100f, ContextCompat.getColor(applicationContext, R.color.colorPrimary), colour) as Int
                    toolbarChat.setBackgroundColor(toolbarC)
                    rlBackground.setBackgroundColor(backgroundC)
                }
                runOnUiThread {
                    setStatusBarColor(Color.BLACK)
                }
            }
        }.start()
    }

    private fun launchScript(msg: List<ChatModel>) {
        object : Thread() {
            override fun run() {
                for (x in msg) {
                    runOnUiThread {
                        mAdapter.add(x)
                    }
                    try {
                        Thread.sleep(2000)
                    }
                    catch (e: TimeoutException) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    //endregion

    //region Interface :- OnClickListener

    override fun onClick(v: View?) {
        when (v) {
            btnHelp -> {
                transitionToTextInput()
                mAdapter.add(ChatModel.me("ID_HELP", getString(R.string.here_to_help), Date()))
                mHandler.postDelayed({
                    runOnUiThread {
                        mAdapter.add(ChatModel.other("ID_HELP_1", getString(R.string.help_question), DateUtil.plusSecond(1)))
                        mQuestionInputMode = true
                    }
                }, 500)
            }
            btnSeek -> {
                transitionToTextInput()
                mAdapter.add(ChatModel.me("ID_SEEK", getString(R.string.here_to_chat), Date()))
                mHandler.postDelayed({
                    runOnUiThread {
                        mAdapter.add(ChatModel.other("ID_SEEK_1", getString(R.string.seek_question), DateUtil.plusSecond(1)))
                        mQuestionInputMode = true
                    }
                }, 500)
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

    companion object {
        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            var view = activity.currentFocus
            if (view == null) {
                view = View(activity)
            }
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}