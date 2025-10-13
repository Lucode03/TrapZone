package com.example.trapzoneapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import com.example.trapzoneapp.ViewModels.AuthViewModel
import com.example.trapzoneapp.functions.firebase.setUserActive
import com.example.trapzoneapp.functions.firebase.setUserInactive
import com.example.trapzoneapp.ui.theme.TrapZoneAppTheme

class MainActivity : ComponentActivity() {
    override fun onStart() {
        super.onStart()
        setUserActive()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        setContent {
            TrapZoneAppTheme {
                AuthNavigation(modifier = Modifier,authViewModel = authViewModel)
            }
        }
    }
    override fun onStop() {
        super.onStop()
        setUserInactive()
    }
}