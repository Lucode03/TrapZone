package com.example.trapzoneapp.clickables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.trapzoneapp.functions.firebase.getUserPointsFromFirebase
import com.example.trapzoneapp.models.RewardsObject
import com.example.trapzoneapp.screens.auth.fields.CustomTextField

val objectTypes = listOf(
    RewardsObject.Legendary(""),
    RewardsObject.UltraRare(""),
    RewardsObject.Rare(""),
    RewardsObject.Common("")
)

@Composable
fun ObjectTypePicker(
    onTypeSelected: (RewardsObject) -> Unit,
    onDismiss: () -> Unit
) {
    var userPoints by remember { mutableIntStateOf(0) }
    var objectName by remember { mutableStateOf("") }

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
                            val namedObj: RewardsObject = when (obj) {
                                is RewardsObject.Legendary -> RewardsObject.Legendary(objectName)
                                is RewardsObject.UltraRare -> RewardsObject.UltraRare(objectName)
                                is RewardsObject.Rare -> RewardsObject.Rare(objectName)
                                is RewardsObject.Common -> RewardsObject.Common(objectName)
                            }
                            onTypeSelected(namedObj)
                            onDismiss()
                        },
                        enabled = objectName.isNotBlank() && (userPoints >= obj.minPoints),
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