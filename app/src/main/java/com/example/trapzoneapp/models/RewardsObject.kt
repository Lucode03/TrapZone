package com.example.trapzoneapp.models

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

sealed class RewardsObject(val exp: Int,val type:String,val minPoints:Int)
{
    object Legendary : RewardsObject(1000,"Legendary",20000)
    object UltraRare : RewardsObject(400,"Ultra Rare",8000)
    object Rare : RewardsObject(250,"Rare",5000)
    object Common : RewardsObject(100,"Common",0)

    companion object {
        fun generateRewardsObject(type: String): RewardsObject {
            return when(type) {
                "Legendary" -> Legendary
                "Ultra Rare" -> UltraRare
                "Rare" -> Rare
                else -> Common
            }
        }
    }
    fun getObjectColor(obj: RewardsObject): Color {
        return when (obj) {
            is Legendary -> Color(0xFFFF2020)
            is UltraRare -> Color(0xFF8137FF)
            is Rare -> Color(0xFFFF3C00)
            is Common -> Color(0xFFFFE935)
        }
    }
    fun getMarkerIcon(): BitmapDescriptor {
        return when (this) {
            Legendary -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            UltraRare -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            Rare -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        }
    }
}

data class RewardsObjectInstance(
    val rewardsObject :RewardsObject,
    val location: LatLng,
    val firebaseKey: String="",
    val creator: String="",  //user.name + user.surname
    val time:Long = System.currentTimeMillis()
)
