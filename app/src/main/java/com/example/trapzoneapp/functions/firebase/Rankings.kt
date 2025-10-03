package com.example.trapzoneapp.functions.firebase

import android.util.Log
import com.example.trapzoneapp.classes.UserStats
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun loadRankingsByCategory(category:String,onResult: (List<UserStats>) -> Unit)
{
    val db = FirebaseDatabase.getInstance().getReference("users")
    db.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val rankings = snapshot.children.mapNotNull { child ->
                val uid = child.key ?: return@mapNotNull null
                val name = child.child("data").child("name").getValue(String::class.java)
                val surname = child.child("data").child("surname").getValue(String::class.java)
                val fullName="$name $surname"
                val stat = child.child("stats").child(category).getValue(Int::class.java)?:0
                UserStats(uid,fullName,stat)
            }.sortedByDescending { it.stat }

            onResult(rankings)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri učitavanju rang liste: ${error.message}")
            onResult(emptyList())
        }
    })
}