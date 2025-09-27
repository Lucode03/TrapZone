package com.example.trapzoneapp.screens.main.map

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.trapzoneapp.helpfunctions.updateUserPoints
import com.example.trapzoneapp.models.TrapInstance
import kotlinx.coroutines.delay

@Composable
fun TrapScreen(
    trap: TrapInstance,
    onResult: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var answer by remember { mutableStateOf("") }
    val context= LocalContext.current
    val taskText = "${trap.op1} + ${trap.op2} = ?"
    var timeLeft by remember { mutableLongStateOf(trap.time) }
    LaunchedEffect(trap) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
        updateUserPoints(trap.trap.losingPoints, context,"za neuspešno rešenu zamku")
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
                val expected = trap.result
                val userAnswer = answer.toIntOrNull()
                if (userAnswer == expected) {
                    updateUserPoints(trap.trap.winningPoints, context,"za uspešno rešenu zamku")
                    onResult(true)
                } else {
                    updateUserPoints(trap.trap.losingPoints, context,"za neuspešno rešenu zamku")
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
