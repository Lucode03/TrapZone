package com.example.trapzoneapp.clickables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerButton(
    label:String,
    date: LocalDate?,
    onDateSelected: (LocalDate?) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Button(
        onClick = {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }) {
        Text(("$label ${date?.toString()?:""}"))
    }
}
@Composable
fun ResetButton(onChange:()->Unit){
    IconButton(
        onClick = { onChange()},
        modifier = Modifier.size(50.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = "Resetuj tip",

            )
    }
}