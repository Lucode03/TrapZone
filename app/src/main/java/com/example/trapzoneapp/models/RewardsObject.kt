package com.example.trapzoneapp.models

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

sealed class RewardsObject(val exp: Int, val markerIcon: BitmapDescriptor)
{
    object Legendary : RewardsObject(500,
        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
    object UltraRare : RewardsObject(300,
        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
    object Rare : RewardsObject(200,
        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
    object Common : RewardsObject(100,
        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
}

