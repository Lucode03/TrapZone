package com.example.trapzoneapp.models

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng

sealed class DangerZone(val name: String, val type:String)
{
    class High(name: String) : DangerZone(name,"Velika")
    class Medium(name: String) : DangerZone(name,"Srednja")
    class Low(name: String) : DangerZone(name,"Mala")

    companion object {
        fun generateObject(type: String, name: String): DangerZone {
            return when(type) {
                "Velika" -> High(name)
                "Srednja" -> Medium(name)
                else -> Low(name)
            }
        }
    }
    fun getObjectColor(): Color {
        return when (this.type) {
            "Velika" -> Color(0xFFFF2020)
            "Srednja" -> Color(0xFF8137FF)
            else -> Color(0xFFFF6436)
        }
    }
    fun getMarkerIcon(): BitmapDescriptor {
        return when (this.type) {
            "Velika" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            "Srednja" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
            else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        }
    }
}

data class DangerZoneInstance(
    val dangerObject :DangerZone,
    val location: LatLng,
    val firebaseKey: String="",
    val creator: String="",
    val time:Long
)
