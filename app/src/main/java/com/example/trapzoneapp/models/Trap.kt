package com.example.trapzoneapp.models

import androidx.compose.ui.graphics.Color
import com.example.trapzoneapp.models.RewardsObject.Common
import com.example.trapzoneapp.models.RewardsObject.Legendary
import com.example.trapzoneapp.models.RewardsObject.Rare
import com.example.trapzoneapp.models.RewardsObject.UltraRare
import com.google.android.gms.maps.model.LatLng

sealed class Trap(val losingPoints:Int,val winningPoints:Int,val type:String,val minPoints:Int)
{
    object Hard : Trap(-100,150,"Hard",30000)
    object Medium : Trap(-80,100,"Medium",15000)
    object Easy : Trap(-50,70,"Easy",5000)
    object VeryEasy : Trap(-20,30,"Very Easy",0)

    companion object {
        fun generateTrap(type: String): Trap {
            return when (type) {
                "Hard" -> Hard
                "Medium" -> Medium
                "Easy" -> Easy
                else -> VeryEasy
            }
        }
    }
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
    val question: QuestionData,
    val creator: String="",//user id
    val firebaseKey:String
)