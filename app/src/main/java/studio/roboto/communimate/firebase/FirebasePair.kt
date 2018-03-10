package studio.roboto.communimate.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FirebasePairChild(
        val reference: DatabaseReference,
        val listener: ChildEventListener
)

class FirebasePairValue(
        val reference: DatabaseReference,
        val listener: ValueEventListener
)