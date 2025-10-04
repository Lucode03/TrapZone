package com.example.trapzoneapp.functions.firebase

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.trapzoneapp.classes.UserStats
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

fun loadRankingsByCategory(category:String, rankings:SnapshotStateList<UserStats>)
{
    val db = FirebaseDatabase.getInstance().getReference("users")
    val userStatsMap = mutableMapOf<String, UserStats>()

    db.addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val obj = createRankingFromFirebase(snapshot, category) ?: return
            if (!userStatsMap.containsKey(obj.uid)) {
                userStatsMap[obj.uid] = obj
                rankings.add(obj)
                sortRankings(rankings)
            }
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val obj = createRankingFromFirebase(snapshot, category) ?: return
            val index = rankings.indexOfFirst { it.uid == obj.uid }
            if (index != -1) {
                rankings[index] = obj
                sortRankings(rankings)
            }
        }
        override fun onChildRemoved(snapshot: DataSnapshot) {
            val key = snapshot.key ?: return
            userStatsMap.remove(key)
            rankings.removeAll { it.uid == key }
        }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
//        override fun onDataChange(snapshot: DataSnapshot) {
//            val rankings = snapshot.children.mapNotNull { child ->
//                val uid = child.key ?: return@mapNotNull null
//                val name = child.child("data").child("name").getValue(String::class.java)
//                val surname = child.child("data").child("surname").getValue(String::class.java)
//                val fullName="$name $surname"
//                val stat = child.child("stats").child(category).getValue(Int::class.java)?:0
//                UserStats(uid,fullName,stat)
//            }.sortedByDescending { it.stat }
//
//            onResult(rankings)
//        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Greška pri učitavanju rang liste: ${error.message}")
        }
    })
}
private fun createRankingFromFirebase(child: DataSnapshot,category:String):UserStats?{
    val uid = child.key ?: return null
    val name = child.child("data").child("name").getValue(String::class.java)?:return null
    val surname = child.child("data").child("surname").getValue(String::class.java)
    val fullName="$name $surname"
    val stat = child.child("stats").child(category).getValue(Int::class.java)?:0
    return UserStats(uid,fullName,stat)
}
private fun sortRankings(rankings: SnapshotStateList<UserStats>) {
    val sorted = rankings.sortedByDescending { it.stat }
    rankings.clear()
    rankings.addAll(sorted)
}