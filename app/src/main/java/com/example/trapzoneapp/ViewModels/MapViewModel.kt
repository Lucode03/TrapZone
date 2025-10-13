package com.example.trapzoneapp.ViewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.trapzoneapp.functions.firebase.loadObjects
import com.example.trapzoneapp.models.DangerZoneInstance

class MapViewModel : ViewModel() {

    var dangerZones = mutableStateListOf<DangerZoneInstance>()
        private set

    init {
        loadDangerZones()
    }
    fun loadDangerZones(){
        dangerZones.clear()
        loadObjects(dangerZones)
    }
    fun setDangerZones(filteredObjects: List<DangerZoneInstance>){
        dangerZones.clear()
        dangerZones.addAll(filteredObjects)
    }
}