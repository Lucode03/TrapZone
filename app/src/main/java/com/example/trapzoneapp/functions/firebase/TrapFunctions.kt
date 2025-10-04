package com.example.trapzoneapp.functions.firebase

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.trapzoneapp.functions.showNearbyTrapNotification
import com.example.trapzoneapp.models.Question
import com.example.trapzoneapp.models.QuestionData
import com.example.trapzoneapp.models.QuestionData.Companion.generateQuestionFromData
import com.example.trapzoneapp.models.Trap
import com.example.trapzoneapp.models.Trap.Companion.generateTrap
import com.example.trapzoneapp.models.TrapInstance
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

fun saveTrapToFirebase(trap : Trap, trapLocation: LatLng, context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().reference
    val uid = auth.currentUser!!.uid
    val key = db.child("traps").push().key ?: return

    val question= Question.generateQuestion(trap)
    val questionData= QuestionData.generateQuestionData(question)
    val trapData = mapOf(
        "latitude" to trapLocation.latitude,
        "longitude" to trapLocation.longitude,
        "creatorId" to uid,
        "type" to trap.type,
        "question" to questionData
    )
    db.child("traps").child(key).setValue(trapData)
        .addOnSuccessListener {
            val numTrapsRef = db.child("users").child(uid).child("stats").child("numTraps")
            numTrapsRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    val currentValue = currentData.getValue(Int::class.java) ?: 0
                    currentData.value = currentValue + 1
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (error != null) {
                        Toast.makeText(context, "Greška pri ažuriranju broja zamki", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju zamke: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun removeTrapFromFirebase(trap: TrapInstance,onComplete: (Boolean) -> Unit = {}){
    val db = FirebaseDatabase.getInstance().getReference("traps")
    val key = trap.firebaseKey
    if (key.isEmpty()) {
        onComplete(false)
        return
    }
    db.child(key).removeValue()
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Neuspešno brisanje zamke: ${e.message}")
            onComplete(false)
        }
}

fun checkNearbyTraps(context: Context,userLocation: LatLng, nearbyTraps: SnapshotStateList<TrapInstance>
)
{
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val uid = auth.currentUser!!.uid
    val db = FirebaseDatabase.getInstance().getReference("traps")

    db.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists()) {
                nearbyTraps.clear()
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })

    db.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val obj = createTrapFromFirebase(snapshot, userLocation,uid) ?: return
            nearbyTraps.add(obj)
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val obj = createTrapFromFirebase(snapshot, userLocation,uid) ?: return
            val index = nearbyTraps.indexOfFirst { it.firebaseKey == obj.firebaseKey }
            if (index != -1) {
                nearbyTraps[index] = obj
            }
        }
        override fun onChildRemoved(snapshot: DataSnapshot) {
            val key = snapshot.key ?: return
            nearbyTraps.removeAll { it.firebaseKey == key }
        }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri čitanju zamki: ${error.message}")
        }
    })
    if (nearbyTraps.isNotEmpty())
    {
        showNearbyTrapNotification(
            context,
            "ZAMKE u blizini!",
            "Rešite ih da biste se izbavili."
        )
    }
}
private fun createTrapFromFirebase(child: DataSnapshot,userLocation: LatLng,uid:String): TrapInstance?{
    val key = child.key ?: return null
    val lat = child.child("latitude").getValue(Double::class.java) ?: return null
    val lon = child.child("longitude").getValue(Double::class.java) ?: return null
    val creatorId=child.child("creatorId").getValue(String::class.java) ?: return null
    val type = child.child("type").getValue(String::class.java) ?: return null
    val questionData = child.child("question").getValue(QuestionData::class.java) ?:return null

    val distance = FloatArray(1)
    Location.distanceBetween(
        userLocation.latitude, userLocation.longitude,
        lat, lon,
        distance
    )
    if (distance[0] > 50 || creatorId==uid)
        return null

    val trapType = generateTrap(type)
    val question = generateQuestionFromData(questionData)
    return TrapInstance(
        trap = trapType,
        location = LatLng(lat, lon),
        question = question,
        firebaseKey = key,
        creatorId = creatorId
    )
}