package com.example.trapzoneapp.helpfunctions.firebase

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import com.example.trapzoneapp.helpfunctions.showNotification
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun sendUserLocationToFirebase(userLocation: LatLng, context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().getReference("users")
    val uid = auth.currentUser!!.uid
    val locationData = mapOf(
        "latitude" to userLocation.latitude,
        "longitude" to userLocation.longitude,
        //"time" to System.currentTimeMillis()
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
                val lat = child.child("location").child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("location").child("longitude").getValue(Double::class.java) ?: return@forEach
                val user = child.key

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    lat, lon,
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