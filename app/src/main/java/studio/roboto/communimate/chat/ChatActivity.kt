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
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.internal.FirebaseAppHelper
import studio.roboto.communimate.azure.FindMatchForSeekerTask
import studio.roboto.communimate.azure.SaveHelperStateTask
import studio.roboto.communimate.firebase.FirebaseHandler
import studio.roboto.communimate.firebase.FirebasePairChild
import studio.roboto.communimate.firebase.FirebasePairValue
import studio.roboto.communimate.firebase.models.FBRawMessage
import studio.roboto.communimate.util.DateUtil
import studio.roboto.communimate.util.GenUtils


class ChatActivity: BaseActivity(), View.OnClickListener, TextView.OnEditorActionListener {

    private lateinit var mAdapter: ChatAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private var mHandler: Handler = Handler()
    private var mQuestionInputMode = true
    private var mIsSeeker: Boolean? = null

    private var mPair: MutableList<FirebasePairChild> = mutableListOf()

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
                if (isHelper() != null && isHelper()!!) {
                    val userId: String = FirebaseHandler.getMyUserId(this)
                    val phrase: String = etInput.text.toString()
                    SaveHelperStateTask(userId, phrase, object : SaveHelperStateTask.SaveHelperStateTaskListener {
                        override fun success() {
                            addLoadingChatMessage()
                        }

                        override fun failure() {

                        }
                    }).execute()
                }
                else if (isSeeker() != null && isSeeker()!!) {
                    addLoadingChatMessage()
                }
            }
            else {
                // Add to Firebase
                val input = etInput.text.toString()
                btnSend.isEnabled = false
                etInput.isEnabled = false
                etInput.setText("")
                mConversationId?.let {
                    val mUUID: String = FirebaseHandler.getMyUserId(applicationContext)
                    val msg: FBRawMessage = FBRawMessage(System.currentTimeMillis(), input, mUUID)
                    FirebaseHandler.getDBConvos { ref ->
                        ref.child(it)
                                .child("messages")
                                .push().setValue(msg).addOnCompleteListener {
                            if (it.isSuccessful) {
                                etInput.isEnabled = true
                                btnSend.isEnabled = true
                            } else {
                                etInput.isEnabled = true
                                btnSend.isEnabled = true
                                etInput.setText(input)
                                Toast.makeText(applicationContext, "Error sending your message at this time", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addLoadingChatMessage() {
        val yourMsg: ChatModel = ChatModel.me("LOADING_DESC_ME", etInput.text.toString(), Date())
        mAdapter.add(yourMsg)
        val msg: ChatModel = ChatModel.other("LOADING_DESC", getString(R.string.please_wait_finding_chat_partner), DateUtil.plusSecond(1))
        mAdapter.add(msg)
        val model: ChatModel = ChatModel.other("LOADING_DIALOG_" + GenUtils.getUniqueId(this), "MSG", DateUtil.plusSecond(2))
        mAdapter.add(model)
        mQuestionInputMode = false
        if (isHelper()!!) {
            etInput.setText("")
            FirebaseHandler.waitForConversationPairing(this, object : FirebaseHandler.Companion.MessageListener {
                override fun messageAdded(msgKey: String, msg: FBRawMessage) {
                    mAdapter.add(msg.convertToMsgObject(msgKey, applicationContext))
                }

                override fun messageDeleted(msgKey: String, msg: FBRawMessage) {
                    mAdapter.remove(msg.convertToMsgObject(msgKey, applicationContext))
                }

                override fun refs(ref: DatabaseReference, listChild: ChildEventListener?, listEvent: ValueEventListener?) {
                    /* SHOULD NEVER BE CALLED IN THIS CONTEXT */
                }
            })
        }
        else if (isSeeker()!!) {
            FindMatchForSeekerTask(etInput.text.toString(), object : FindMatchForSeekerTask.FindMatchForSeekerTaskListener {
                override fun success(helperUserId: String?) {
                    if (helperUserId == null) {
                        mAdapter.add(ChatModel.other("COULDN'T_FIND_" + GenUtils.getUniqueId(applicationContext), "We couldn't match you to anyone at this time", Date()))
                    }
                    else {
                        configureChat(helperUserId)
                    }
                }

                override fun failure() {

                }
            }).execute()
        }
    }

    private fun isSeeker(): Boolean? {
        return mIsSeeker
    }
    private fun isHelper(): Boolean? {
        if (mIsSeeker == null) {
            return mIsSeeker
        }
        else {
            return !mIsSeeker!!
        }
    }

    //region Chat Functionality

    /**
     * Method called when you're a seeker in the app
     * - HelperId will be the user ID of the person you need to talk too
     * - Push a generated conversation ID underneath my UID node, and their UID node
     * - Helper will be listening for changes to that node, will get the firebase auto push
     * - Seeker and Helper will then both have conversation IDs and can listen underneath the node
     *
     * Again. Called as a SEEKER
     */
    private var mConversationId: String? = null
    private fun configureChat(helpersId: String) {
        // Generate a conversation UUID and get my ID
        mConversationId = UUID.randomUUID().toString()
        val myUserId: String = FirebaseHandler.getMyUserId(this)

        // Generate Conversation node
        FirebaseHandler.getDBConvos {
            it.child(mConversationId).child("users").child(helpersId).setValue(true)
            it.child(mConversationId).child("users").child(myUserId).setValue(true)
        }

        // Push this under both our user IDs
        FirebaseHandler.getDBUsers {
            it.child(myUserId).child("conversations").child(mConversationId).setValue(System.currentTimeMillis())
            it.child(helpersId).child("conversations").child(mConversationId).setValue(System.currentTimeMillis())
        }

        // Listen for all the messages
        FirebaseHandler.listenForMessages(mConversationId!!, object : FirebaseHandler.Companion.MessageListener {
            override fun refs(ref: DatabaseReference, listChild: ChildEventListener?, listEvent: ValueEventListener?) {
                listChild?.let {
                    mPair.add(FirebasePairChild(ref, it))
                }
            }

            override fun messageAdded(msgKey: String, msg: FBRawMessage) {
                mAdapter.add(msg.convertToMsgObject(msgKey, applicationContext))
            }

            override fun messageDeleted(msgKey: String, msg: FBRawMessage) {
                mAdapter.remove(msg.convertToMsgObject(msgKey, applicationContext))
            }
        })

        // Push my message to Firebase
        etInput.isEnabled = true
        btnSend.isEnabled = true
        etInput.setText("")
    }

    override fun onDestroy() {
        super.onDestroy()
        for (x in mPair) {
            FirebaseHandler.cleanUp(x)
        }
    }

    //endregion

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
                mIsSeeker = false
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
                mIsSeeker = true
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