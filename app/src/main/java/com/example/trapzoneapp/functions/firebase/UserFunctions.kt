package com.example.trapzoneapp.functions.firebase

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.example.trapzoneapp.functions.showNearbyUserNotification
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun getUserPointsFromFirebase( onResult: (Int) -> Unit){
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    val uid = auth.currentUser!!.uid
    db.child(uid).child("stats").child("points")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val points = snapshot.getValue(Int::class.java) ?: 0
                onResult(points)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Greška pri čitanju poena korisnika: ${error.message}")
                onResult(0)
            }
        })
}
fun sendUserLocationToFirebase(userLocation: LatLng, context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    val uid = auth.currentUser!!.uid
    val locationData = mapOf(
        "latitude" to userLocation.latitude,
        "longitude" to userLocation.longitude
    )
    db.child(uid).child("location").setValue(locationData)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju lokacije korisnika: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

fun checkNearbyUsers(context: Context,userLocation: LatLng) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val uid = auth.currentUser!!.uid
    val db = FirebaseDatabase.getInstance().getReference("users")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { child ->
                val user = child.key
                if (uid==user)
                    return@forEach
                val lat = child.child("location").child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("location").child("longitude").getValue(Double::class.java) ?: return@forEach
                val active = child.child("active").getValue(Boolean::class.java) ?: return@forEach

                if(!active)
                    return@forEach

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    lat, lon,
                    distance
                )

                if (distance[0] < 100) {
                    showNearbyUserNotification(context,
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
fun setUserActive(){
    val user= FirebaseAuth.getInstance().currentUser?:return
    val db : DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    db.child(user.uid).child("active").setValue(true)
}
fun setUserInactive(){
    val user= FirebaseAuth.getInstance().currentUser?:return
    val db : DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    db.child(user.uid).child("active").setValue(false)
}