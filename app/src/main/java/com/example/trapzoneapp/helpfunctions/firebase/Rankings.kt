package com.example.trapzoneapp.helpfunctions.firebase

import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun loadRankings(onResult: (List<Pair<String, Int>>) -> Unit)
{
    val db = FirebaseDatabase.getInstance().getReference("users")
    db.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val rankings = snapshot.children.mapNotNull { child ->
                val name = child.child("data").child("name").getValue(String::class.java)
                val surname = child.child("data").child("surname").getValue(String::class.java)
                val fullName="$name $surname"
                val points = child.child("points").getValue(Int::class.java)
                if (points != null) fullName to points else null
            }.sortedByDescending { it.second }

            onResult(rankings)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri učitavanju rang liste: ${error.message}")
            onResult(emptyList())
        }
    })
}

//fun loadRankingsByCategory(category:String,onResult: (List<Pair<String, Int>>) -> Unit)
//{
//    val db = FirebaseDatabase.getInstance().getReference("users")
//    db.addListenerForSingleValueEvent(object : ValueEventListener {
//        override fun onDataChange(snapshot: DataSnapshot) {
//            val rankings = snapshot.children.mapNotNull { child ->
//                val name = child.child("data").child("name").getValue(String::class.java)
//                val surname = child.child("data").child("surname").getValue(String::class.java)
//                val fullName="$name $surname"
//                val value = child.child("achievements").child(category).getValue(Int::class.java)
//                if (value != null) fullName to value else null
//            }.sortedByDescending { it.second }
//
//            onResult(rankings)
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            Log.e("Firebase", "Greška pri učitavanju rang liste: ${error.message}")
//            onResult(emptyList())
//        }
//    })
//}