//package com.example.trapzoneapp.helpfunctions
//
//import android.location.Location
//import com.example.trapzoneapp.models.RewardsObjectInstance
//import com.google.android.gms.maps.model.LatLng
//
//fun filterObjectsByRadius(objects: List<RewardsObjectInstance>, userLocation: LatLng,
//                          radiusMeters: Float): List<RewardsObjectInstance> {
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
//fun filterObjectsByTime(objects: List<RewardsObjectInstance>,
//    fromTime: Long? = null,
//    toTime: Long? = null
//): List<RewardsObjectInstance> {
//    return objects.filter { obj ->
//        (fromTime == null || obj.rewardsObject.time >= fromTime) && (toTime == null || obj.time <= toTime)
//    }
//}
//fun filterObjectsByCreator(objects: List<RewardsObjectInstance>, creator: String? = null): List<RewardsObjectInstance> {
//    return objects.filter { obj ->
//        (author == null || obj.creator == author) &&
//                (type == null || obj.type == type) &&
//                (fromTime == null || obj.time >= fromTime) &&
//                (toTime == null || obj.time <= toTime)
//    }
//}