package com.example.trapzoneapp.functions

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.trapzoneapp.models.DangerZoneInstance
import com.google.android.gms.maps.model.LatLng
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun filterObjects(
    objects: List<DangerZoneInstance>,userLocation: LatLng,
    creator: String?, type: String?, name: String?,
    startDate: LocalDate?, endDate: LocalDate?,
    minMeters: String,maxMeters:String): List<DangerZoneInstance>
{
    return objects.filter { obj ->
        val matchesType = type.isNullOrBlank() || type==obj.dangerObject.type
        val matchesCreator = creator.isNullOrBlank() || obj.creator.contains(creator, ignoreCase = true)
        val matchesName = name.isNullOrBlank() || obj.dangerObject.name.contains(name, ignoreCase = true)

        val objInstant = Instant.ofEpochMilli(obj.time)
        val objDate = objInstant.atZone(ZoneId.systemDefault()).toLocalDate()
        val matchesStartDate = startDate == null || !objDate.isBefore(startDate)
        val matchesEndDate = endDate == null || !objDate.isAfter(endDate)

        val minDistance=minMeters.toIntOrNull()?:0
        val maxDistance=maxMeters.toIntOrNull()?:Int.MAX_VALUE
        val distance = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            obj.location.latitude, obj.location.longitude,
            distance
        )
        val matchesRadius=(minDistance<= distance[0]) &&(distance[0] <= maxDistance)

        matchesRadius&& matchesCreator && matchesType && matchesName && matchesStartDate && matchesEndDate
    }
}

fun filterObjectsByRadius(objects: List<DangerZoneInstance>, userLocation: LatLng, min: String,max:String)
: List<DangerZoneInstance>
{
    val minDistance=min.toInt()
    val maxDistance=max.toInt()
    return objects.filter { obj ->
        val distance = FloatArray(1)
        Location.distanceBetween(
            userLocation.latitude, userLocation.longitude,
            obj.location.latitude, obj.location.longitude,
            distance
        )
        (minDistance<= distance[0]) &&(distance[0] <= maxDistance)
    }
}
fun filterObjectsByTime(objects: List<DangerZoneInstance>, fromTime: Long? = null, toTime: Long? = null)
: List<DangerZoneInstance>
{
    return objects.filter { obj ->
        (fromTime == null || obj.time >= fromTime) && (toTime == null || obj.time <= toTime)
    }
}
fun filterObjectsByCreator(objects: List<DangerZoneInstance>, creator: String? = null)
: List<DangerZoneInstance>
{
    return objects.filter { obj ->
        (creator == null || obj.creator == creator)
    }
}