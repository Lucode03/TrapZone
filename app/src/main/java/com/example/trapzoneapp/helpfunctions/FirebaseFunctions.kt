package com.example.trapzoneapp.helpfunctions

import android.content.Context
import android.location.Location
import android.widget.Toast
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
    db.child("locations").child(uid).setValue(userLocation)
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
    db.child("objects").push().setValue(objectLocation)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju objekta: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun saveTrapLocationToFirebase(trapLocation: LatLng,context: Context)
{
    val db : DatabaseReference = FirebaseDatabase.getInstance().reference
//    val trapData = mapOf(
//        "latitude" to trapLocation.latitude,
//        "longitude" to trapLocation.longitude,
//        "timestamp" to System.currentTimeMillis()
//    )
    db.child("traps").push().setValue(trapLocation)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju zamke: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
fun getNearUsers(context: Context,userLocation: LatLng)
{
    val db = FirebaseDatabase.getInstance().getReference("locations")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { child ->
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach

                val otherLocation = LatLng(lat, lon)

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    otherLocation.latitude, otherLocation.longitude,
                    distance
                )

                if (distance[0] < 100) { // bliže od 100m
                    showNotification(context,
                        "Korisnik u blizini!",
                        "Drugi korisnik je na ${distance[0].toInt()}m od vas.")
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}
fun getNearObjects(context: Context,userLocation: LatLng)
{
    val db = FirebaseDatabase.getInstance().getReference("objects")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { child ->
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach

                val otherLocation = LatLng(lat, lon)

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    otherLocation.latitude, otherLocation.longitude,
                    distance
                )

                if (distance[0] < 1000) { // bliže od 100m
                    showNotification(context,
                        "Objekat u blizini!",
                        "Objekat za dobijanje novih zamki je na ${distance[0].toInt()}m od vas.")
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}
fun getNearTraps(context: Context,userLocation: LatLng)
{
    val db = FirebaseDatabase.getInstance().getReference("traps")

    db.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach { child ->
                val lat = child.child("latitude").getValue(Double::class.java) ?: return@forEach
                val lon = child.child("longitude").getValue(Double::class.java) ?: return@forEach

                val otherLocation = LatLng(lat, lon)

                val distance = FloatArray(1)
                Location.distanceBetween(
                    userLocation.latitude, userLocation.longitude,
                    otherLocation.latitude, otherLocation.longitude,
                    distance
                )

                if (distance[0] < 100) { // bliže od 100m
                    showNotification(context ,
                        "ZAMKA!",
                        "Upali ste u zamku! Morate rešiti zadatak da izađete!")
                }
            }
        }
        override fun onCancelled(error: DatabaseError) {}
    })
}