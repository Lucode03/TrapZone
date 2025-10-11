package com.example.trapzoneapp.screens.main.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth


@Composable
fun HandleLocationUpdates(
    context: Context,
    permissionGranted: Boolean,
    fusedLocationClient: FusedLocationProviderClient,
    onLocationUpdate: (LatLng) -> Unit
) {
    DisposableEffect(permissionGranted) {
        if (!permissionGranted) return@DisposableEffect onDispose {}

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                FirebaseAuth.getInstance().currentUser ?: return
                result.lastLocation?.let { location ->
                    onLocationUpdate(LatLng(location.latitude, location.longitude))
                }
            }
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        }

        onDispose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }
}
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionRequestScreen(
    locationPermission: PermissionState
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Potrebna je dozvola za lokaciju")
        Button(onClick = {
            locationPermission.launchPermissionRequest()
        }) {
            Text("Dozvoli pristup lokaciji")
        }
    }
}
