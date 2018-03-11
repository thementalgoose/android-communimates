package studio.roboto.communimate.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import studio.roboto.communimate.firebase.models.FBConversation
import studio.roboto.communimate.firebase.models.FBRawMessage
import studio.roboto.communimate.util.GenUtils
import java.util.*

class FirebaseHandler {
    companion object {

        //region Utility Method

        fun getAuth(done: () -> Unit) {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().addOnCompleteListener { task ->
                    println(task.exception)
                    println("Task is successful: " + task.isSuccessful)
                    println("Task::: BOOM " + task.isComplete)
                    if (task.isSuccessful) {
                        done()
                    }
                    else {
                        println("ERROR!")
                    }
                }
            }
            else {
                done()
            }
        }

        fun getDB(done: (db: FirebaseDatabase) -> Unit) {
            getAuth {
                done(FirebaseDatabase.getInstance())
            }
        }

        fun getDBUsers(done: (db: DatabaseReference) -> Unit) {
            getAuth {
                done(FirebaseDatabase.getInstance().getReference("users"))
            }
        }

        fun getDBConvos(done: (db: DatabaseReference) -> Unit) {
            getAuth {
                done(FirebaseDatabase.getInstance().getReference("conversations"))
            }
        }

        fun getMyUserId(context: Context): String {
            var uid: String? = GenUtils.getSP(context).getString("USERNAME", null)
            if (uid == null) {
                uid = UUID.randomUUID().toString()
                GenUtils.getSP(context).edit().putString("USERNAME", uid).apply()
                getDBUsers {
                    it.child(uid).child("registered").setValue(true)
                }
            }
            return uid
        }

        //endregion

        fun listenForMessages(conversationId: String, listener: MessageListener) {
            val eventListener: ChildEventListener = object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError?) { /* DO NOTHING */ }

                override fun onChildMoved(p0: DataSnapshot?, p1: String?) { /* DO NOTHING */ }

                override fun onChildChanged(p0: DataSnapshot?, p1: String?) { /* DO NOTHING */ }

                override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                    p0?.let {
                        val msg: FBRawMessage? = it.getValue(FBRawMessage::class.java)
                        if (msg != null) {
                            listener.messageAdded(it.key, msg)
                        }
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot?) {
                    p0?.let {
                        val msg: FBRawMessage? = it.getValue(FBRawMessage::class.java)
                        if (msg != null) {
                            listener.messageDeleted(it.key, msg)
                        }
                    }
                }

            }
            getDBConvos { it ->
                val ref = it.child(conversationId)
                        .child("messages")
                ref.addChildEventListener(eventListener)
                listener.refs(ref, eventListener, null)
            }
        }

        fun waitForConversationPairing(context: Context, listener: MessageListener) {
            getDBUsers {
                var firstSync: Boolean = false
                val now: Long = System.currentTimeMillis()
                val mUserId: String = getMyUserId(context)
                it.child(mUserId).child("conversations").addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {

                    }

                    override fun onDataChange(p0: DataSnapshot?) {
//                        if (!firstSync) {
//                            firstSync = true
//                        }
//                        else {
                            p0?.let {
                                for (x in it.children) {
                                    if (x.getValue(Long::class.java)!! > now) {
                                        // This one!
                                        println("Conversation ID found: " + x.key)
                                        listenForMessages(x.key, listener)
                                    }
                                }
                            }
//                        }
                    }
                })
            }
        }

        //region Clean Up for Firebase Listeners

        fun cleanUp(pair: FirebasePairChild) {
            pair.reference.removeEventListener(pair.listener)
        }

        fun cleanUp(pair: FirebasePairValue) {
            pair.reference.removeEventListener(pair.listener)
        }

        //endregion

        //region Interface Declaration :- Message listener

        interface MessageListener {
            fun messageAdded(msgKey: String, msg: FBRawMessage)
            fun messageDeleted(msgKey: String, msg: FBRawMessage)
            fun refs(ref: DatabaseReference, listChild: ChildEventListener?, listEvent: ValueEventListener?)
        }

        //endregion
    }
}