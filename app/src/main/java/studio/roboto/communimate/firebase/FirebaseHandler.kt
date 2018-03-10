package studio.roboto.communimate.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import studio.roboto.communimate.firebase.models.FBConversation
import studio.roboto.communimate.firebase.models.FBRawMessage

class FirebaseHandler {
    companion object {

        //region Utility Method

        fun getAuth(): FirebaseUser {
            val auth: FirebaseAuth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously()
            }
            return auth.currentUser!!
        }

        fun getDB(): FirebaseDatabase {
            val user: FirebaseUser = getAuth()
            return FirebaseDatabase.getInstance()
        }

        //endregion

        fun listenForMessages(conversationId: String, listener: MessageListener): FirebasePairChild {
            val eventListener: ChildEventListener = object : ChildEventListener {
                override fun onCancelled(p0: DatabaseError?) { /* DO NOTHING */ }

                override fun onChildMoved(p0: DataSnapshot?, p1: String?) { /* DO NOTHING */ }

                override fun onChildChanged(p0: DataSnapshot?, p1: String?) { /* DO NOTHING */ }

                override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                    p0?.let {
                        val msg: FBRawMessage? = it.getValue(FBRawMessage::class.java)
                        if (msg != null) {
                            listener.messageAdded(msg)
                        }
                    }
                }

                override fun onChildRemoved(p0: DataSnapshot?) {
                    p0?.let {
                        val msg: FBRawMessage? = it.getValue(FBRawMessage::class.java)
                        if (msg != null) {
                            listener.messageDeleted(msg)
                        }
                    }
                }

            }
            val ref = getDB().getReference("conversations")
                    .child(conversationId)
                    .child("messages")
            ref.addChildEventListener(eventListener)
            return FirebasePairChild(ref, eventListener)
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
            fun messageAdded(msg: FBRawMessage)
            fun messageDeleted(msg: FBRawMessage)
        }

        //endregion
    }
}