package com.example.trapzoneapp.models

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

sealed class RewardsObject(val exp: Int,val type:String,val minPoints:Int) {
    object Legendary : RewardsObject(500,"Legendary",10000)
    object UltraRare : RewardsObject(300,"Ultra Rare",6000)
    object Rare : RewardsObject(200,"Rare",3000)
    object Common : RewardsObject(100,"Common",0)

    fun getMarkerIcon(): BitmapDescriptor {
        return when (this) {
            Legendary -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            UltraRare -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            Rare -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
            Common -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        }
    }
    fun createObjectFromFirebase(type: String): RewardsObject {
        return when(type) {
            "Legendary" -> Legendary
            "UltraRare" -> UltraRare
            "Rare" -> Rare
            "Common" -> Common
            else -> Common
        }
    }
    fun getObjectColor(type: RewardsObject): Color {
        return when (type) {
            is Legendary -> Color(0xFFFF2020)
            is UltraRare -> Color(0xFF8137FF)
            is Rare -> Color(0xFFFF3C00)
            is Common -> Color(0xFFFFE935)
        }
    }
}

data class RewardsObjectInstance(
    val rewardsObject :RewardsObject,
    val location: LatLng,
    val firebaseKey: String
)
