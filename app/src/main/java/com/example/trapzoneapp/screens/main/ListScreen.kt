package com.example.trapzoneapp.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.trapzoneapp.ViewModels.MapViewModel
import com.example.trapzoneapp.models.DangerZoneInstance
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListScreen(viewModel: MapViewModel){

    val dangerZones = viewModel.dangerZones
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss")
        .withZone(ZoneId.systemDefault())
    LazyColumn {
        items(dangerZones) { obj ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${obj.dangerObject.type} opasnost!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                        color = obj.dangerObject.getObjectColor(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    HorizontalDivider(thickness = 1.dp, color = Color.Gray, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("Naziv: ${obj.dangerObject.name}")
                    Text("Kreator: ${obj.creator}")
                    Text("Vreme kreiranja: ${formatter.format(Instant.ofEpochMilli(obj.time))}")
                }
            }
        }
    }

}