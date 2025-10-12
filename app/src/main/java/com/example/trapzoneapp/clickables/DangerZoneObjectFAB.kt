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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trapzoneapp.models.DangerZone
import com.example.trapzoneapp.screens.auth.fields.CustomTextField

val objectTypes = listOf(
    DangerZone.High(""),
    DangerZone.Medium(""),
    DangerZone.Low("")
)

@Composable
fun ObjectTypePicker(
    onTypeSelected: (DangerZone) -> Unit,
    onDismiss: () -> Unit
) {
    var objectName by remember { mutableStateOf("") }

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
                    text = "Unesite naziv objekta",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                CustomTextField(
                    value = objectName,
                    onValueChange = { objectName = it },
                    label = "Naziv"
                )

                Spacer(modifier = Modifier.height((30.dp)))
                Text(
                    text = "Izaberite tip objekta",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                objectTypes.forEach { obj ->
                    Button(
                        onClick = {
                            val namedObj: DangerZone = when (obj) {
                                is DangerZone.High -> DangerZone.High(objectName)
                                is DangerZone.Medium -> DangerZone.Medium(objectName)
                                is DangerZone.Low -> DangerZone.Low(objectName)
                            }
                            onTypeSelected(namedObj)
                            onDismiss()
                        },
                        enabled = objectName.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = obj.getObjectColor().copy(alpha = 0.8f),
                            disabledContainerColor = obj.getObjectColor().copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(obj.type)
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