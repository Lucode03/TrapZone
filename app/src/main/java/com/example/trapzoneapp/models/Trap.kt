package com.example.trapzoneapp.models

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

sealed class Trap(val losingPoints:Int,val winningPoints:Int,val type:String,val minPoints:Int)
{
    object Hard : Trap(-50,80,"Hard",30000)
    object Medium : Trap(-40,60,"Medium",15000)
    object Easy : Trap(-30,40,"Easy",5000)
    object VeryEasy : Trap(-20,30,"Very Easy",0)

    fun getTrapColor(type: Trap): Color {
        return when (type) {
            is Hard -> Color(0xFFFF0000)
            is Medium -> Color(0xFFFF3300)
            is Easy -> Color(0xFF0079E3)
            is VeryEasy -> Color(0xFF36E5FF)
        }
    }
}
data class TrapInstance(
    val trap: Trap,
    val location: LatLng,
    val op1: Int,
    val op2: Int,
    val result: Int,
    val user: String,
    val time: Long,
    val firebaseKey:String
)