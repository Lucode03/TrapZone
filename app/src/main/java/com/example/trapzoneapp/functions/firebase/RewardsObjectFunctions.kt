package com.example.trapzoneapp.functions.firebase

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.trapzoneapp.functions.updateUserPointsForObject
import com.example.trapzoneapp.models.RewardsObject
import com.example.trapzoneapp.models.RewardsObject.Companion.generateRewardsObject
import com.example.trapzoneapp.models.RewardsObjectInstance
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

fun saveObjectToFirebase(rewardsObject: RewardsObject, objectLocation: LatLng, context: Context) {

    val db : DatabaseReference = FirebaseDatabase.getInstance().reference
    val key = db.child("objects").push().key ?: return
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val uid = auth.currentUser!!.uid
    var name:String?
    var surname:String?
    db.child("users").child(uid).child("data").get()
        .addOnSuccessListener { child ->
            name = child.child("name").getValue(String::class.java)
            surname= child.child("surname").getValue(String::class.java)

            val objectData = mapOf(
                "latitude" to objectLocation.latitude,
                "longitude" to objectLocation.longitude,
                "name" to rewardsObject.name,
                "type" to rewardsObject.type,
                "time" to System.currentTimeMillis(),
                "creator" to "$name $surname"
            )
            db.child("objects").child(key).setValue(objectData)
                .addOnSuccessListener {
                    val numObjectsRef = db.child("users").child(uid).child("stats").child("numObjects")
                    numObjectsRef.runTransaction(object : Transaction.Handler {
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
                                Toast.makeText(context, "Greška pri ažuriranju broja objekata", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
                .addOnFailureListener {e->
                    Toast.makeText(context, "Greška pri čuvanju objekta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    updateUserPointsForObject(50,context,"za postavljanje novog objekta")

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


fun isObjectInRange(context: Context,userLocation: LatLng,obj:RewardsObjectInstance): Boolean {
    val distance = FloatArray(1)
    Location.distanceBetween(
        userLocation.latitude, userLocation.longitude,
        obj.location.latitude, obj.location.longitude,
        distance
    )
    if (distance[0] < 500) {
        return true
    }
    Toast.makeText(context, "Morate biti bliže objektu da biste skupili poene!", Toast.LENGTH_SHORT).show()
    return false
}


fun loadNearbyObjects(context: Context,userLocation: LatLng,markers: SnapshotStateList<RewardsObjectInstance>) {
    val db = FirebaseDatabase.getInstance().getReference("objects")

    db.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists()) {
                markers.clear()
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })

    db.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val obj = createRewardsObjectFromFirebase(snapshot, userLocation) ?: return
            markers.add(obj)
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val obj = createRewardsObjectFromFirebase(snapshot, userLocation) ?: return
            val index = markers.indexOfFirst { it.firebaseKey == obj.firebaseKey }
            if (index != -1) {
                markers[index] = obj
            }
        }
        override fun onChildRemoved(snapshot: DataSnapshot) {
            val key = snapshot.key ?: return
            markers.removeAll { it.firebaseKey == key }
        }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri čitanju objekata u blizini: ${error.message}")
        }
    })
}
private fun createRewardsObjectFromFirebase(snapshot: DataSnapshot, userLocation: LatLng): RewardsObjectInstance? {
    val key = snapshot.key ?: return null
    val lat = snapshot.child("latitude").getValue(Double::class.java) ?: return null
    val lon = snapshot.child("longitude").getValue(Double::class.java) ?: return null
    val type = snapshot.child("type").getValue(String::class.java) ?: return null
    val creator = snapshot.child("creator").getValue(String::class.java) ?: return null
    val name = snapshot.child("name").getValue(String::class.java) ?: return null
    val objectLocation = LatLng(lat, lon)

    val distance = FloatArray(1)
    Location.distanceBetween(
        userLocation.latitude, userLocation.longitude,
        objectLocation.latitude, objectLocation.longitude,
        distance
    )

    if (distance[0] > 3000)
        return null

    val rewardsObject = generateRewardsObject(type,name)
    return RewardsObjectInstance(
        rewardsObject=rewardsObject,
        location = objectLocation,
        firebaseKey = key,
        creator = creator)
}