package com.example.trapzoneapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trapzoneapp.ViewModels.AuthViewModel
import com.example.trapzoneapp.screens.MainScreen
import com.example.trapzoneapp.screens.auth.LoginScreen
import com.example.trapzoneapp.screens.auth.SignUpScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AuthNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel)
{
    val navController = rememberNavController()
    NavHost(navController=navController, startDestination ="login", builder ={
        composable("login") {
            LoginScreen(modifier,navController,authViewModel)
        }
        composable("signup") {
            SignUpScreen(modifier,navController,authViewModel)
        }
        composable("main") {
            MainScreen(modifier,navController,authViewModel)
        }
    })
}