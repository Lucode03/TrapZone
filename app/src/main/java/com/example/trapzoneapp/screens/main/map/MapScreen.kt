package com.example.trapzoneapp.screens.main.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import com.example.trapzoneapp.R
import com.example.trapzoneapp.helpfunctions.firebase.checkNearbyTraps
import com.example.trapzoneapp.helpfunctions.firebase.checkNearbyUsers
import com.example.trapzoneapp.helpfunctions.firebase.isObjectInRange
import com.example.trapzoneapp.helpfunctions.firebase.loadNearbyObjects
import com.example.trapzoneapp.helpfunctions.firebase.removeObjectFromFirebase
import com.example.trapzoneapp.helpfunctions.firebase.removeTrapFromFirebase
import com.example.trapzoneapp.helpfunctions.firebase.saveObjectToFirebase
import com.example.trapzoneapp.helpfunctions.firebase.saveTrapToFirebase
import com.example.trapzoneapp.helpfunctions.firebase.sendUserLocationToFirebase
import com.example.trapzoneapp.helpfunctions.updateUserPoints
import com.example.trapzoneapp.models.RewardsObjectInstance
import com.example.trapzoneapp.models.TrapInstance
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
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.delay

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
    val markers = remember { mutableStateListOf<RewardsObjectInstance>() }
    val trapQueue = remember { mutableStateListOf<TrapInstance?>() }
    val currentTrap = remember { mutableStateOf<TrapInstance?>(null) }
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

                            sendUserLocationToFirebase(userLocation,context)
                            checkNearbyUsers(context,userLocation)
                            loadNearbyObjects(context,userLocation,markers)
                            checkNearbyTraps(context, userLocation){ traps->
                                traps.forEach { trap ->
                                    val key= trap?.firebaseKey
                                    val isAlreadyQueued = trapQueue.any { it?.firebaseKey == key }
                                    val isCurrent = currentTrap.value?.firebaseKey == key
                                    if (!isAlreadyQueued && !isCurrent) {
                                        trapQueue.add(trap)
                                    }
                                }
                            }
                        }
                    }
                }
                fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

            }
        }
    }

    if (locationPermission.status.isGranted) {
        MapScreenContent(context,modifier,cameraPositionState,
            properties,uiSettings,markerState,userLocation,markers)
        TrapHandler(trapQueue,currentTrap)

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
@Composable
fun TrapHandler(trapQueue: SnapshotStateList<TrapInstance?>, currentTrap: MutableState<TrapInstance?>) {
    LaunchedEffect(trapQueue, currentTrap.value) {
        while (true) {
            if (currentTrap.value == null && trapQueue.isNotEmpty()) {
                delay(2000) // pauza između zamki
                currentTrap.value = trapQueue.first()
                trapQueue.removeAt(0)
            } else {
                delay(100)
            }
        }
    }

    currentTrap.value?.let { trap ->
        Dialog(onDismissRequest = { currentTrap.value = null }) {
            TrapScreen(
                trap = trap,
                onResult = {
                    removeTrapFromFirebase(trap)
                    currentTrap.value = null
                }
            )
        }
    }
}
@Composable
fun MapScreenContent(context: Context, modifier: Modifier,
                     cameraPositionState:CameraPositionState,
                     properties: MapProperties, uiSettings: MapUiSettings,
                     markerState: MarkerState, userLocation: LatLng,
                     markers: SnapshotStateList<RewardsObjectInstance>)
{
    var showObjectPicker by remember { mutableStateOf(false) }
    var showTrapPicker by remember { mutableStateOf(false) }
    Scaffold {paddingValues->
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
                markers.forEach { obj ->
                    Marker(
                        state = MarkerState(position = obj.location),
                        title = obj.rewardsObject.type+" objekat",
                        icon = obj.rewardsObject.getMarkerIcon(),
                        onClick = {
                            if(isObjectInRange(context,userLocation,obj)){
                                updateUserPoints(obj.rewardsObject.exp,context,"za skupljanje ${obj.rewardsObject.type} objekta")
                                removeObjectFromFirebase(obj)
                                markers.remove(obj)
                            }
                            true
                        }
                    )

                }
            }
            FloatingActionButton(
                onClick = {
                    showObjectPicker = true
                },
                containerColor = Color.Transparent,
                modifier= Modifier.size(80.dp).align(Alignment.BottomStart),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pin),
                    contentDescription = "Dodaj objekat",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (showObjectPicker) {
                Dialog(onDismissRequest = { showObjectPicker = false }) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 8.dp
                    ) {
                        ObjectTypePicker( { selectedType ->
                            saveObjectToFirebase(selectedType, userLocation, context)
                            Toast.makeText(context, "Objekat je dodat!", Toast.LENGTH_SHORT).show()
                            showObjectPicker = false
                        },
                            onDismiss = { showObjectPicker = false }
                        )
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    showTrapPicker = true
                },
                containerColor = Color.Transparent,
                modifier= Modifier.size(80.dp).align(Alignment.BottomEnd),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.trap),
                    contentDescription = "Dodaj zamku",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
            if (showTrapPicker) {
                Dialog(onDismissRequest = { showTrapPicker = false }) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 8.dp
                    ) {
                        TrapTypePicker({ selectedType ->
                            saveTrapToFirebase(selectedType, userLocation, context)
                            Toast.makeText(context, "Zamka je postavljena!", Toast.LENGTH_SHORT).show()
                            showTrapPicker = false
                        },
                            onDismiss = { showTrapPicker = false }  )
                    }
                }
            }
        }
    }
}