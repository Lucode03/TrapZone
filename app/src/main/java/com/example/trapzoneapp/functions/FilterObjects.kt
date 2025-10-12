//package com.example.trapzoneapp.functions
//
//import android.location.Location
//import com.example.trapzoneapp.models.DangerZoneInstance
//import com.google.android.gms.maps.model.LatLng
//
//fun filterObjectsByRadius(objects: List<DangerZoneInstance>, userLocation: LatLng,
//                          radiusMeters: Float): List<DangerZoneInstance> {
//    return objects.filter { obj ->
//        val distance = FloatArray(1)
//        Location.distanceBetween(
//            userLocation.latitude, userLocation.longitude,
//            obj.location.latitude, obj.location.longitude,
//            distance
//        )
//        distance[0] <= radiusMeters
//    }
//}
//fun filterObjectsByTime(objects: List<DangerZoneInstance>,
//    fromTime: Long? = null,
//    toTime: Long? = null
//): List<DangerZoneInstance> {
//    return objects.filter { obj ->
//        (fromTime == null || obj.rewardsObject.time >= fromTime) && (toTime == null || obj.time <= toTime)
//    }
//}
//fun filterObjectsByCreator(objects: List<DangerZoneInstance>, creator: String? = null): List<DangerZoneInstance> {
//    return objects.filter { obj ->
//        (author == null || obj.creator == author) &&
//                (type == null || obj.type == type) &&
//                (fromTime == null || obj.time >= fromTime) &&
//                (toTime == null || obj.time <= toTime)
//    }
//}