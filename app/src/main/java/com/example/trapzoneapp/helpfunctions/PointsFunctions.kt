package com.example.trapzoneapp.helpfunctions

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import kotlin.math.abs

fun updateUserPoints(points: Int, context: Context,msg:String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseDatabase.getInstance().getReference("users")
    val uid = auth.currentUser?.uid ?: return

    val userRef = db.child(uid).child("points")

    userRef.runTransaction(object : Transaction.Handler {
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            val currentPoints = currentData.getValue(Int::class.java) ?: 0
            if((currentPoints+points)<0)
                currentData.value=0
            else
                currentData.value = currentPoints + points
            return Transaction.success(currentData)
        }

        override fun onComplete(
            error: DatabaseError?,
            committed: Boolean,
            currentData: DataSnapshot?
        ) {
            if (error != null) {
                Toast.makeText(context, "Greška: ${error.message}", Toast.LENGTH_SHORT).show()
            } else if (committed) {
                if(points>0)
                    Toast.makeText(context, "Dobili ste +${points} poena ${msg}!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context, "Izgublili ste ${abs(points)} poena ${msg}.", Toast.LENGTH_SHORT).show()
            }
        }
    })
}