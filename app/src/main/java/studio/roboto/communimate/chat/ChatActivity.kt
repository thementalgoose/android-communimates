package studio.roboto.communimate.chat

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chat.*
import studio.roboto.communimate.BaseActivity
import studio.roboto.communimate.R
import studio.roboto.communimate.util.DateUtil
import java.util.*

class ChatActivity: BaseActivity() {

    private lateinit var mAdapter: ChatAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()

        initToolbar(false)
    }

    private fun initViews() {
        mAdapter = ChatAdapter()
        rvMain.adapter = mAdapter
        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.stackFromEnd = true
        rvMain.layoutManager = mLayoutManager

        mAdapter.add(ChatModel.me("ID1", "Jono is a massive twat", DateUtil.from("10/03/2018 13:36")))
        mAdapter.add(ChatModel.other("ID2", "Msg", DateUtil.from("10/03/2018 13:37")))
        mAdapter.add(ChatModel.me("ID3", "Msg", DateUtil.from("10/03/2018 13:38")))
        mAdapter.add(ChatModel.other("ID4", "Msg", DateUtil.from("10/03/2018 13:39")))
    }
}