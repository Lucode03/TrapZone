package com.example.trapzoneapp.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.trapzoneapp.functions.firebase.loadObjects
import com.example.trapzoneapp.models.DangerZoneInstance
import java.time.LocalDate

class MapViewModel : ViewModel() {
    var creatorFilter by  mutableStateOf("")
    var typeFilter by  mutableStateOf("")
    var nameFilter by  mutableStateOf("")
    var dateFromFilter by  mutableStateOf<LocalDate?>(null)
    var dateToFilter by  mutableStateOf<LocalDate?>(null)

    var allDangerZones=mutableStateListOf<DangerZoneInstance>()
        private set
    var dangerZones = mutableStateListOf<DangerZoneInstance>()
        private set

    init {
        loadObjects(allDangerZones)
        dangerZones.clear()
        loadObjects(dangerZones)
    }

    fun setDangerZones(filteredObjects: List<DangerZoneInstance>){
        dangerZones.clear()
        dangerZones.addAll(filteredObjects)
    }
    fun resetDangerZones(){
        dangerZones.clear()
        dangerZones.addAll(allDangerZones)
    }
}