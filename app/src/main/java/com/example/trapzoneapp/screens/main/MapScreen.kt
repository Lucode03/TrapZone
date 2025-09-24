package com.example.trapzoneapp.screens.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.trapzoneapp.R
import com.example.trapzoneapp.helpfunctions.saveObjectLocationToFirebase
import com.example.trapzoneapp.helpfunctions.saveTrapLocationToFirebase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(modifier: Modifier=Modifier)
{
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var userLocation  by remember { mutableStateOf(LatLng(43.321473, 21.896174)) }
    val markerState = remember{ MarkerState(position = userLocation)}
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 15f)
    }
    val uiSettings by remember{ mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    val properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }
    val locationPermission = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )
    LaunchedEffect(locationPermission.status.isGranted) {
        if (locationPermission.status.isGranted) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        userLocation = LatLng(it.latitude, it.longitude)
                        markerState.position = userLocation
                        cameraPositionState.position=CameraPosition.fromLatLngZoom(
                            userLocation, 15f)
                    }
                }
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        result.lastLocation?.let { location ->
                            userLocation = LatLng(location.latitude, location.longitude)
                            markerState.position=userLocation
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                userLocation, 15f)
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

            }
        }
    }
    val markers = remember { mutableStateListOf<LatLng>() }
    if (locationPermission.status.isGranted) {
        Scaffold() {paddingValues->
            Box(modifier = modifier.fillMaxSize().padding(paddingValues)) {
                GoogleMap(
                    modifier = modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    uiSettings = uiSettings
                ) {
                    Marker(
                        state = markerState,
                        title = "Vaša lokacija",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                    markers.forEach { latLng ->
                        Marker(
                            state = MarkerState(position = latLng),
                            title = "Objekat",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        )

                    }
                }
                FloatingActionButton(
                    onClick = {
                        userLocation.let {
                            saveObjectLocationToFirebase(userLocation,context)
                            Toast.makeText(context, "Objekat je dodat!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = Color.Transparent,
                    modifier= Modifier.size(80.dp).align(Alignment.BottomStart),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
//                    Image(
//                        painter = painterResource(id = R.drawable.trap), // tvoja slika
//                        contentDescription = "Dodaj zamku",
//                        contentScale = ContentScale.FillBounds, // popunjava ceo FAB
//                        modifier = Modifier.fillMaxSize()
//                    )
                }
                FloatingActionButton(
                    onClick = {
                        userLocation.let {
                            saveTrapLocationToFirebase(userLocation,context)
                            Toast.makeText(context, "Zamka je dodata!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    containerColor = Color.Transparent,
                    modifier= Modifier.size(80.dp).align(Alignment.BottomEnd),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.trap), // tvoja slika
                        contentDescription = "Dodaj zamku",
                        contentScale = ContentScale.FillBounds, // popunjava ceo FAB
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

    } else {
        Column(
            modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Potrebna je dozvola za lokaciju")
            Button(onClick = { locationPermission.launchPermissionRequest() }) {
                Text("Dozvoli")
            }
        }
    }
}
