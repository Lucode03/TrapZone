package com.example.trapzoneapp.clickables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilterDialog(
    creatorFilter: String, typeFilter: String, nameFilter: String,
    dateFrom: LocalDate?, dateTo: LocalDate?,
    minDistance: String, maxDistance: String,
    onCreatorChange: (String) -> Unit, onTypeChange: (String) -> Unit, onNameChange: (String) -> Unit,
    onDateFromChange: (LocalDate?) -> Unit, onDateToChange: (LocalDate?) -> Unit,
    onDistanceFromChange: (String?) -> Unit, onDistanceToChange: (String?) -> Unit,
    onApply: () -> Unit, onDismiss: () -> Unit,
    onReset:()->Unit)
{
    var expanded by remember { mutableStateOf(false) }
    val types = listOf("Velika", "Srednja", "Mala")
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Filtriraj objekte",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        OutlinedTextField(
                            value = typeFilter,
                            onValueChange = {},
                            label = { Text("Nivo opasnosti") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            types.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        onTypeChange(type)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    ResetButton { onTypeChange("") }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){

                    OutlinedTextField(
                        value = nameFilter,
                        onValueChange = onNameChange,
                        label = { Text("Naziv") },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    ResetButton { onNameChange("") }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    OutlinedTextField(
                        value = creatorFilter,
                        onValueChange = onCreatorChange,
                        label = { Text("Kreator") },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    ResetButton { onCreatorChange("") }
                }


                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Opseg datuma",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium)
                    ResetButton {
                        onDateToChange(null)
                        onDateFromChange(null)
                    }
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DatePickerButton(
                        label="Od",
                        date = dateFrom,
                        onDateSelected = onDateFromChange
                    )
                    Spacer(Modifier.width(2.dp))
                    DatePickerButton(
                        label="Do",
                        date = dateTo,
                        onDateSelected = onDateToChange
                    )
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "Udaljenost(u metrima)",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium)
                    ResetButton {
                        onDistanceToChange("")
                        onDistanceFromChange("")
                    }
                }
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = minDistance,
                        onValueChange = { digit->
                            val filtered = digit.filter { it.isDigit() }
                            onDistanceFromChange(filtered)
                                        },
                        label = { Text("Od") },
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = maxDistance,
                        onValueChange = { digit->
                            val filtered = digit.filter { it.isDigit() }
                            onDistanceToChange(filtered)
                        },
                        label = { Text("Do") },
                        modifier = Modifier.weight(1f).padding(start = 4.dp),
                        singleLine = true
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onReset) {
                        Text("Poništi filtere")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = onApply) {
                        Text("Filtriraj")
                    }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onDismiss) {
                        Text("Otkaži")
                    }
                }
            }
        }
    }
}