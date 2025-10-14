package com.example.trapzoneapp.screens.main.map

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.trapzoneapp.R
import com.example.trapzoneapp.ViewModels.MapViewModel
import com.example.trapzoneapp.clickables.FilterDialog
import com.example.trapzoneapp.clickables.ObjectTypePicker
import com.example.trapzoneapp.clickables.TrapTypePicker
import com.example.trapzoneapp.functions.filterObjects
import com.example.trapzoneapp.functions.firebase.checkNearbyUsers
import com.example.trapzoneapp.functions.firebase.isObjectInRange
import com.example.trapzoneapp.functions.firebase.loadTraps
import com.example.trapzoneapp.functions.firebase.saveObjectToFirebase
import com.example.trapzoneapp.functions.firebase.saveTrapToFirebase
import com.example.trapzoneapp.functions.firebase.sendUserLocationToFirebase
import com.example.trapzoneapp.models.DangerZoneInstance
import com.example.trapzoneapp.models.TrapInstance
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(viewModel: MapViewModel, modifier: Modifier=Modifier)
{
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermission = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    var userLocation  by remember { mutableStateOf(LatLng(43.321473, 21.896174)) }
    val markerState = remember{ MarkerState(position = userLocation)}
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(userLocation, 15f) }
    val currentZoom by remember {derivedStateOf{cameraPositionState.position.zoom} }

    val uiSettings by remember{ mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    val properties by remember { mutableStateOf(MapProperties(mapType = MapType.NORMAL)) }

    HandleLocationUpdates(
        context = context,
        permissionGranted = locationPermission.status.isGranted,
        fusedLocationClient = fusedLocationClient,
        onLocationUpdate = { location ->
            userLocation = location
            markerState.position = location
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, currentZoom)

            sendUserLocationToFirebase(location, context)
            checkNearbyUsers(context, location)
        }
    )
    if (locationPermission.status.isGranted) {
        MapScreenContent(context,modifier,cameraPositionState,
            properties,uiSettings,markerState,userLocation,viewModel)
        TrapHandler(context/*,allTraps*/,userLocation)

    } else {
        PermissionRequestScreen(locationPermission)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreenContent(context: Context, modifier: Modifier,
                     cameraPositionState:CameraPositionState,
                     properties: MapProperties, uiSettings: MapUiSettings,
                     markerState: MarkerState, userLocation: LatLng,
                     viewModel: MapViewModel)
{
    var showObjectPicker by remember { mutableStateOf(false) }
    var showTrapPicker by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedObject by remember { mutableStateOf<DangerZoneInstance?>(null) }

    val creatorFilter by viewModel::creatorFilter
    val typeFilter by viewModel::typeFilter
    val nameFilter by viewModel::nameFilter
    val dateFromFilter by viewModel::dateFromFilter
    val dateToFilter by viewModel::dateToFilter

    val minDistanceFilter by viewModel::minDistanceFilter
    val maxDistanceFilter by viewModel::maxDistanceFilter

    var filteredDangerZones by remember { mutableStateOf<List<DangerZoneInstance>>(viewModel.dangerZones) }

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
            filteredDangerZones.forEach { obj ->
                Marker(
                    state = MarkerState(position = obj.location),
                    title = obj.dangerObject.type+" opasnost!",
                    icon = obj.dangerObject.getMarkerIcon(),
                    onClick = {
                        if(isObjectInRange(userLocation,obj))
                            selectedObject=obj
                        else
                            Toast.makeText(context, "Morate prići bliže objektu", Toast.LENGTH_SHORT).show()
                        true
                    }
                )
            }
            selectedObject?.let { reward ->
                DangerZoneObjectDialog(
                    selectedObj = reward,
                    onDismiss = { selectedObject = null }
                )
            }
        }
        TextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .clip(RoundedCornerShape(24.dp))
                .clickable { showFilterDialog = true },
            placeholder = { Text("Filtriraj objekte...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Pretraga")
            },
            enabled = false,
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = Color.Black,
                disabledContainerColor = Color.White.copy(alpha = 0.9f)
            )
        )
        if (showFilterDialog) {
            FilterDialog(
                creatorFilter = creatorFilter,
                typeFilter = typeFilter,
                nameFilter = nameFilter,
                dateFrom = dateFromFilter,
                dateTo =dateToFilter,
                minDistance = minDistanceFilter,
                maxDistance =maxDistanceFilter,
                onCreatorChange = { viewModel.creatorFilter = it },
                onTypeChange = { viewModel.typeFilter = it },
                onNameChange = { viewModel.nameFilter = it },
                onDateFromChange = { viewModel.dateFromFilter = it },
                onDateToChange = { viewModel.dateToFilter = it },
                onDistanceFromChange ={ viewModel.minDistanceFilter=it ?:"0"},
                onDistanceToChange ={ viewModel.maxDistanceFilter = it?:"${Int.MAX_VALUE}"},
                onApply = {
                    filteredDangerZones= filterObjects(
                        objects = viewModel.allDangerZones,
                        userLocation=userLocation,
                        creator=creatorFilter,
                        type = typeFilter,
                        name = nameFilter,
                        startDate = dateFromFilter,
                        endDate = dateToFilter,
                        minMeters = minDistanceFilter,
                        maxMeters = maxDistanceFilter
                    )
                    viewModel.setDangerZones(filteredDangerZones)
                    showFilterDialog = false
                },
                onDismiss = { showFilterDialog = false },
                onReset = {
                    viewModel.resetDangerZones()
                    viewModel.resetFilters()
                    filteredDangerZones=viewModel.dangerZones
                    showFilterDialog = false
                }
            )
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
            ObjectTypePicker( { selectedType ->
                saveObjectToFirebase(selectedType, userLocation, context)
                Toast.makeText(context, "Objekat je dodat!", Toast.LENGTH_SHORT).show()
                showObjectPicker = false
                },
                onDismiss = { showObjectPicker = false }
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
            TrapTypePicker({ selectedType ->
                saveTrapToFirebase(selectedType, userLocation, context)
                Toast.makeText(context, "Zamka je postavljena!", Toast.LENGTH_SHORT).show()
                showTrapPicker = false
            },
                onDismiss = { showTrapPicker = false }  )
        }
    }
}