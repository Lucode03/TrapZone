package com.example.trapzoneapp.clickables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trapzoneapp.functions.firebase.getUserPointsFromFirebase
import com.example.trapzoneapp.models.Trap
import com.example.trapzoneapp.models.Trap.Easy.getTrapColor

val trapTypes = listOf(
    Trap.Hard,
    Trap.Medium,
    Trap.Easy,
    Trap.VeryEasy
)
@Composable
fun TrapTypePicker(
    onTypeSelected: (Trap) -> Unit,
    onDismiss: () -> Unit
) {
    var userPoints by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        getUserPointsFromFirebase { points->
            userPoints =points}
    }
    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Izaberi tip zamke",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                trapTypes.forEach { type ->
                    Button(
                        onClick = {
                            onTypeSelected(type)
                            onDismiss()
                        },
                        enabled = userPoints >= type.minPoints,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getTrapColor(type).copy(alpha = 0.8f),
                            disabledContainerColor = getTrapColor(type).copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(type.type)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(onClick = { onDismiss() }) {
                    Text("Otkaži")
                }
            }
        }
    }
}
