package com.example.trapzoneapp.helpfunctions.firebase

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.example.trapzoneapp.helpfunctions.showNotification
import com.example.trapzoneapp.models.Question
import com.example.trapzoneapp.models.QuestionData
import com.example.trapzoneapp.models.QuestionData.Companion.generateQuestionFromData
import com.example.trapzoneapp.models.Trap
import com.example.trapzoneapp.models.Trap.Companion.generateTrap
import com.example.trapzoneapp.models.TrapInstance
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun saveTrapToFirebase(trap : Trap, trapLocation: LatLng, context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().getReference("traps")
    val uid = auth.currentUser!!.uid
    val key = db.push().key ?: return

    val question= Question.generateQuestion(trap)
    val questionData= QuestionData.generateQuestionData(question)
    val trapData = mapOf(
        "latitude" to trapLocation.latitude,
        "longitude" to trapLocation.longitude,
        "creatorId" to uid,
        "type" to trap.type,
        "question" to questionData
    )
    db.child(key).setValue(trapData)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju zamke: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

fun createTrapFromFirebase(child: DataSnapshot): TrapInstance?{
    val key = child.key ?: return null
    val lat = child.child("latitude").getValue(Double::class.java) ?: return null
    val lon = child.child("longitude").getValue(Double::class.java) ?: return null
    val creatorId=child.child("creatorId").getValue(String::class.java) ?: return null
    val type = child.child("type").getValue(String::class.java) ?: return null
    val questionData = child.child("question").getValue(QuestionData::class.java) ?:return null

    val trapType = generateTrap(type)
    val question=generateQuestionFromData(questionData)
    return TrapInstance(
        trap = trapType,
        location = LatLng(lat, lon),
        question = question,
        firebaseKey = key,
        creatorId = creatorId
    )
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

fun checkNearbyTraps(context: Context,userLocation: LatLng,
                     onTrapsFound: (List<TrapInstance?>) -> Unit)
{
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val uid = auth.currentUser!!.uid
    val db = FirebaseDatabase.getInstance().getReference("traps")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val nearbyTraps=  mutableListOf<TrapInstance?>()
            snapshot.children.forEach { child ->
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach
                val creatorId = child.child("creatorId").value as? String

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    lat, lon,
                    distance
                )

                if (distance[0] < 50 /*&& creatorId!=uid*/) {
                    val trap = createTrapFromFirebase(child)
                    nearbyTraps.add(trap)
                }
            }
            if (nearbyTraps.isNotEmpty()) {
                showNotification(
                    context,
                    "ZAMKE u blizini!",
                    "Rešite ih da biste se izbavili."
                )
                onTrapsFound(nearbyTraps)
            } else {
                onTrapsFound(emptyList())
            }

        }
        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri čitanju zamki: ${error.message}")
        }
    })
}