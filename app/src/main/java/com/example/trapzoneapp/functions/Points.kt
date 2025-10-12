package com.example.trapzoneapp.functions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
fun updateUserPointsForObject(points: Int, context: Context,msg:String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseDatabase.getInstance().getReference("users")
    val uid = auth.currentUser?.uid ?: return
    val userRef = db.child(uid).child("stats").child("points")
    userRef.runTransaction(object : Transaction.Handler {
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val currentPoints = currentData.getValue(Int::class.java) ?: 0
            if((currentPoints+points)<0)
                currentData.value=0
            else
                currentData.value = currentPoints + points
            return Transaction.success(currentData)
        }
        override fun onComplete( error: DatabaseError?,
                                 committed: Boolean,
                                 currentData: DataSnapshot? ) {
            if (error != null) {
                Toast.makeText(context, "Greška: ${error.message}", Toast.LENGTH_SHORT).show()
            } else {
                    Toast.makeText(context, "Dobili ste +$points poena ${msg}!",
                        Toast.LENGTH_SHORT).show()
            }
        }
    })
}

fun updateUserPointsForTrap(context: Context,creatorId:String,userPoints: Int,creatorPoints: Int){
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: return
    updateUserPointsById(uid,userPoints){ error, success, _ ->
        if (success) {
            if(userPoints>0)
                Toast.makeText(context, "Dobili ste +$userPoints poena za uspesno resenu zamku!",
                    Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, "Izgublili ste $userPoints poena za neuspesno resenu zamku.",
                Toast.LENGTH_SHORT).show()
        }
        else {
            Log.e("Poeni za zamku", "Greška pri ažuriranju korisnika: ${error?.message}")
        }
    }
    updateUserPointsById(creatorId,creatorPoints){ error,success, newPoints ->
        if (success) {
            Log.d("Poeni za zamku", "Kreatoru  ažurirani poeni: $newPoints")
        } else {
            Log.e("Poeni za zamku", "Greška pri ažuriranju kreatora : ${error?.message}")
        }
    }
}

fun updateUserPointsById(userId:String,points: Int,
                         onComplete: ( DatabaseError?,Boolean, Int?) -> Unit) {
    val db = FirebaseDatabase.getInstance().getReference("users")
    val userRef = db.child(userId).child("stats").child("points")
    userRef.runTransaction(object : Transaction.Handler {
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val currentPoints = currentData.getValue(Int::class.java) ?: 0
            if((currentPoints+points)<0)
                currentData.value=0
            else
                currentData.value = currentPoints + points
            return Transaction.success(currentData)
        }
        override fun onComplete(error: DatabaseError?,
                                committed: Boolean,
                                currentData: DataSnapshot?) {
            val updatedPoints = currentData?.getValue(Int::class.java)
            onComplete( error,committed, updatedPoints)
        }
    })
}