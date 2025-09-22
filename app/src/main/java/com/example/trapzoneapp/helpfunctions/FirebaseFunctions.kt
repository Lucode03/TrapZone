package com.example.trapzoneapp.helpfunctions

import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

fun sendLocationToFirebase(userLocation: LatLng,context: Context) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val db : DatabaseReference = FirebaseDatabase.getInstance().reference
    val uid = auth.currentUser!!.uid
    db.child("locations").child(uid).setValue(userLocation)
        .addOnFailureListener {e->
            Toast.makeText(context, "Greška pri čuvanju podataka: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}
