package com.example.trapzoneapp.screens.main.map

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.example.trapzoneapp.R
import com.example.trapzoneapp.clickables.ObjectTypePicker
import com.example.trapzoneapp.clickables.TrapTypePicker
import com.example.trapzoneapp.functions.firebase.checkNearbyTraps
import com.example.trapzoneapp.functions.firebase.checkNearbyUsers
import com.example.trapzoneapp.functions.firebase.isObjectInRange
import com.example.trapzoneapp.functions.firebase.loadNearbyObjects
import com.example.trapzoneapp.functions.firebase.removeObjectFromFirebase
import com.example.trapzoneapp.functions.firebase.saveObjectToFirebase
import com.example.trapzoneapp.functions.firebase.saveTrapToFirebase
import com.example.trapzoneapp.functions.firebase.sendUserLocationToFirebase
import com.example.trapzoneapp.functions.updateUserPointsForObject
import com.example.trapzoneapp.models.RewardsObjectInstance
import com.example.trapzoneapp.models.TrapInstance
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(modifier: Modifier=Modifier)
{
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermission = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    var userLocation  by remember { mutableStateOf(LatLng(43.321473, 21.896174)) }

    val markerState = remember{ MarkerState(position = userLocation)}
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(userLocation, 15f) }

    val uiSettings by remember{ mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    val properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }

    val markers = remember { mutableStateListOf<RewardsObjectInstance>() }
    val trapQueue = remember { mutableStateListOf<TrapInstance>() }
    val currentTrap = remember { mutableStateOf<TrapInstance?>(null) }
    HandleLocationUpdates(
        context = context,
        permissionGranted = locationPermission.status.isGranted,
        fusedLocationClient = fusedLocationClient,
        onLocationUpdate = { location ->
            userLocation = location
            markerState.position = location
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 15f)

            sendUserLocationToFirebase(location, context)
            checkNearbyUsers(context, location)
            loadNearbyObjects(context, location, markers)
            checkNearbyTraps(context, location, trapQueue)
        }
    )
    if (locationPermission.status.isGranted) {
        MapScreenContent(context,modifier,cameraPositionState,
            properties,uiSettings,markerState,userLocation,markers)
        TrapHandler(context,trapQueue,currentTrap)

    } else {
        PermissionRequestScreen(locationPermission)
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
        Box(modifier = modifier.fillMaxSize()) {
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
                                updateUserPointsForObject(obj.rewardsObject.exp,context,
                                    "za skupljanje ${obj.rewardsObject.type} objekta")
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
                    val currentZoom = cameraPositionState.position.zoom
                    cameraPositionState.move(
                        CameraUpdateFactory.newLatLngZoom(userLocation, currentZoom)
                    )
                },
                containerColor = Color.Transparent,
                modifier= Modifier
                    .size(60.dp)
                    .align(Alignment.BottomCenter),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.center_point),
                    contentDescription = "Centriraj lokaciju",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
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