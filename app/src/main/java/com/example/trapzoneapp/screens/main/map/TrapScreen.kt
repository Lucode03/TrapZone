package com.example.trapzoneapp.screens.main.map

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trapzoneapp.functions.firebase.removeTrapFromFirebase
import com.example.trapzoneapp.functions.updateUserPointsForTrap
import com.example.trapzoneapp.models.TrapInstance
import kotlinx.coroutines.delay

@Composable
fun TrapHandler(
    context: Context,
    trapQueue: SnapshotStateList<TrapInstance>,
    currentTrap: MutableState<TrapInstance?>
) {
    LaunchedEffect(trapQueue.size) {
        if (currentTrap.value == null && trapQueue.isNotEmpty()) {
            delay(2000)
            currentTrap.value = trapQueue.removeFirstOrNull()
        }
    }

    currentTrap.value?.let { trap ->
        Dialog(onDismissRequest = { currentTrap.value = null }) {
            TrapDialog(
                context = context,
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
fun TrapDialog(context: Context,
               trap: TrapInstance,
               onResult: (Boolean) -> Unit,
               modifier: Modifier = Modifier
) {
    var answer by remember { mutableStateOf("") }
    val taskText = "${trap.question.op1} + ${trap.question.op2} = ?"
    var timeLeft by remember { mutableIntStateOf(trap.question.time) }
    LaunchedEffect(trap) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
        updateUserPointsForTrap(context,trap.creatorId,
            userPoints = trap.trap.losingPoints,
            creatorPoints = trap.trap.winningPoints)
        onResult(false)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            color = Color.White,
            textAlign = TextAlign.Center,
            text = "Rešite zadatak da biste se izbavili!",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(color = Color.White,
            textAlign = TextAlign.Center,
            text = "Vreme preostalo: $timeLeft s",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            color = Color.White,
            textAlign = TextAlign.Center,
            text = taskText,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White,
                                            focusedContainerColor = Color.White,
                                            unfocusedTextColor = Color.Black,
                                            focusedTextColor = Color.Black),
            value = answer,
            onValueChange = {
                if (it.all { ch -> ch.isDigit() }) {
                    answer = it
                }
            },
            label = { Text("Unesite rezultat") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val expected = trap.question.result
                val userAnswer = answer.toIntOrNull()
                if (userAnswer == expected) {
                    updateUserPointsForTrap(context,trap.creatorId,
                        userPoints = trap.trap.winningPoints,
                        creatorPoints = trap.trap.losingPoints)
                    onResult(true)
                } else {
                    updateUserPointsForTrap(context,trap.creatorId,
                        userPoints = trap.trap.losingPoints,
                        creatorPoints = trap.trap.winningPoints)
                    onResult(false)
                }
                answer = ""
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("Potvrdi")
        }
    }
}
