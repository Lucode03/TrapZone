package com.example.trapzoneapp.helpfunctions

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.trapzoneapp.models.Question
import com.example.trapzoneapp.models.RewardsObject
import com.example.trapzoneapp.models.RewardsObject.Common.createObjectFromFirebase
import com.example.trapzoneapp.models.RewardsObjectInstance
import com.example.trapzoneapp.models.Trap
import com.example.trapzoneapp.models.TrapInstance
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun sendUserLocationToFirebase(userLocation: LatLng,context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().getReference("locations")
    val uid = auth.currentUser!!.uid
    val locationData = mapOf(
        "latitude" to userLocation.latitude,
        "longitude" to userLocation.longitude,
        //"time" to System.currentTimeMillis()
    )
    db.child(uid).setValue(locationData)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju lokacije korisnika: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun saveObjectToFirebase(rewardsObject: RewardsObject,objectLocation: LatLng,context: Context) {

    val db : DatabaseReference = FirebaseDatabase.getInstance().getReference("objects")
    val key = db.push().key ?: return
    val type= when(rewardsObject)
    {
        is RewardsObject.Legendary->"Legendary"
        is RewardsObject.UltraRare->"UltraRare"
        is RewardsObject.Rare->"Rare"
        is RewardsObject.Common->"Common"
    }
    val objectData = mapOf(
        "latitude" to objectLocation.latitude,
        "longitude" to objectLocation.longitude,
        "type" to type,
        "time" to System.currentTimeMillis()
    )
    updateUserPoints(50,context,"za postavljanje novog objekta")
    db.child(key).setValue(objectData)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju objekta: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun removeObjectFromFirebase(obj: RewardsObjectInstance, onComplete: (Boolean) -> Unit = {}) {
    val db = FirebaseDatabase.getInstance().getReference("objects")
    val key= obj.firebaseKey
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
fun saveTrapToFirebase(trap : Trap, trapLocation: LatLng, context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().reference
    val uid = auth.currentUser!!.uid
    val key = db.push().key ?: return
    val type= when(trap)
    {
        is Trap.Hard->"Hard"
        is Trap.Medium->"Medium"
        is Trap.Easy->"Easy"
        is Trap.VeryEasy->"Very Easy"
    }
    val question=Question.generate(trap)
    val trapData = mapOf(
        "latitude" to trapLocation.latitude,
        "longitude" to trapLocation.longitude,
        "user" to uid,
        "type" to type,
        "op1" to question.op1,
        "op2" to question.op2,
        "result" to question.result,
        "time" to question.time
    )
    db.child("traps").child(key).setValue(trapData)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju zamke: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun createTrapFromFirebase(child: DataSnapshot): TrapInstance?{
    val key = child.key ?: return null
    val lat = child.child("latitude").getValue(Double::class.java) ?: return null
    val lon = child.child("longitude").getValue(Double::class.java) ?: return null
    val type = child.child("type").getValue(String::class.java) ?: return null
    val op1 = child.child("op1").getValue(Int::class.java) ?: 0
    val op2 = child.child("op2").getValue(Int::class.java) ?: 0
    val result = child.child("result").getValue(Int::class.java) ?: 0
    val user = child.child("user").getValue(String::class.java) ?: ""
    val time = child.child("time").getValue(Long::class.java) ?: 0L

    val trapType = when (type) {
        "Hard" -> Trap.Hard
        "Medium" -> Trap.Medium
        "Easy" -> Trap.Easy
        "Very Easy" -> Trap.VeryEasy
        else -> return null
    }
    return TrapInstance(
        trap = trapType,
        location = LatLng(lat, lon),
        op1 = op1,
        op2 = op2,
        result = result,
        user = user,
        time = time,
        firebaseKey = key
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
fun checkNearbyUsers(context: Context,userLocation: LatLng) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val uid = auth.currentUser!!.uid
    val db = FirebaseDatabase.getInstance().getReference("locations")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { child ->
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach
                val user = child.key
                val otherLocation = LatLng(lat, lon)

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    otherLocation.latitude, otherLocation.longitude,
                    distance
                )

                if (distance[0] < 100 && uid!=user) {
                    showNotification(context,
                        "Korisnik u blizini!",
                        "Drugi korisnik je na ${distance[0].toInt()}m od vas.")
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri čitanju korisnika u blizini: ${error.message}")
        }
    })
}
fun loadNearbyObjects(context: Context,userLocation: LatLng,markers: SnapshotStateList<RewardsObjectInstance>) {
    val db = FirebaseDatabase.getInstance().getReference("objects")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            markers.clear()
            snapshot.children.forEach { child ->
                val key = child.key ?: return@forEach
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach
                val type = child.child("type").getValue(String::class.java) ?: return@forEach
                val objectLocation = LatLng(lat, lon)

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    objectLocation.latitude, objectLocation.longitude,
                    distance
                )

                if (distance[0] < 3000) {
                    val rewardsObject = createObjectFromFirebase(type)
                    markers.add(RewardsObjectInstance(rewardsObject,objectLocation,key))
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri čitanju objekata u blizini: ${error.message}")
        }
    })
}
fun checkNearbyTraps(context: Context,userLocation: LatLng,
                     onTrapsFound: (List<TrapInstance?>) -> Unit)
{
    //val auth: FirebaseAuth = FirebaseAuth.getInstance()
    //val uid = auth.currentUser!!.uid
    val db = FirebaseDatabase.getInstance().getReference("traps")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val nearbyTraps=  mutableListOf<TrapInstance?>()
            snapshot.children.forEach { child ->
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach
                //val user = child.child("user").value as? String
                val trapLocation = LatLng(lat, lon)

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    trapLocation.latitude, trapLocation.longitude,
                    distance
                )

                if (distance[0] < 50/* && user!=uid*/) {
                    val trap = createTrapFromFirebase(child)
                    nearbyTraps.add(trap)
                }
            }
            if (nearbyTraps.isNotEmpty()) {
                showNotification(
                    context,
                    "ZAMKE u blizini!",
                    "Imate ${nearbyTraps.size} zamki u blizini! Rešite ih jednu po jednu."
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