package com.example.trapzoneapp.models

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

sealed class RewardsObject(val name: String,val exp: Int,val type:String,val minPoints:Int)
{
    class Legendary(name: String) : RewardsObject(name,1000,"Legendary",20000)
    class UltraRare(name: String) : RewardsObject(name,400,"Ultra Rare",8000)
    class Rare(name: String) : RewardsObject(name,250,"Rare",5000)
    class Common(name: String) : RewardsObject(name,100,"Common",0)

    companion object {
        fun generateRewardsObject(type: String,name: String): RewardsObject {
            return when(type) {
                "Legendary" -> Legendary(name)
                "Ultra Rare" -> UltraRare(name)
                "Rare" -> Rare(name)
                else -> Common(name)
            }
        }
    }
    fun getObjectColor(): Color {
        return when (this.type) {
            "Legendary" -> Color(0xFFFF2020)
            "Ultra Rare" -> Color(0xFF8137FF)
            "Rare" -> Color(0xFFFF3C00)
            else -> Color(0xFFFFE935)
        }
    }
    fun getMarkerIcon(): BitmapDescriptor {
        return when (this.type) {
            "Legendary" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            "Ultra Rare" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            "Rare" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
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
