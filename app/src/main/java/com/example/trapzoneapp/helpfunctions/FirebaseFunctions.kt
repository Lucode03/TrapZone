package com.example.trapzoneapp.helpfunctions

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun sendUserLocationToFirebase(userLocation: LatLng,context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().reference
    val uid = auth.currentUser!!.uid
    val locationData = mapOf(
        "latitude" to userLocation.latitude,
        "longitude" to userLocation.longitude
    )
    db.child("locations").child(uid).setValue(locationData)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju lokacije korisnika: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun saveObjectLocationToFirebase(objectLocation: LatLng,context: Context)
{
    val db : DatabaseReference = FirebaseDatabase.getInstance().reference
//    val objectData = mapOf(
//        "latitude" to objectLocation.latitude,
//        "longitude" to objectLocation.longitude,
//        "timestamp" to System.currentTimeMillis()
//    )
    //dodavanje poena
    db.child("objects").push().setValue(objectLocation)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju objekta: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun saveTrapLocationToFirebase(trapLocation: LatLng,context: Context)
{
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().reference
    val uid = auth.currentUser!!.uid
    val trapData = mapOf(
        "latitude" to trapLocation.latitude,
        "longitude" to trapLocation.longitude,
        "user" to uid
    )
    db.child("traps").push().setValue(trapData)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju zamke: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun checkNearbyUsers(context: Context,userLocation: LatLng)
{
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

                if (distance[0] < 100 && uid!=user) { // bliže od 100m
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
fun loadNearbyObjects(context: Context,userLocation: LatLng,markers: SnapshotStateList<LatLng>)
{
    val db = FirebaseDatabase.getInstance().getReference("objects")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            markers.clear()
            snapshot.children.forEach { child ->
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach

                val objectLocation = LatLng(lat, lon)
                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    objectLocation.latitude, objectLocation.longitude,
                    distance
                )

                if (distance[0] < 1000) { // bliže od 100m
                    markers.add(objectLocation)
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri čitanju objekata u blizini: ${error.message}")
        }
    })
}
fun checkNearbyTraps(context: Context,userLocation: LatLng)
{
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val uid = auth.currentUser!!.uid
    val db = FirebaseDatabase.getInstance().getReference("traps")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { child ->
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach
                val user = child.child("user").value as? String
                val otherLocation = LatLng(lat, lon)

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    otherLocation.latitude, otherLocation.longitude,
                    distance
                )

                if (distance[0] < 100/* && user!=uid*/) { // bliže od 100m
                    showNotification(context ,
                        "ZAMKA!",
                        "Upali ste u zamku! Morate rešiti zadatak da izađete!")
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}