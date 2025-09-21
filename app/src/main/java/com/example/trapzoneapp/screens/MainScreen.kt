package com.example.trapzoneapp.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trapzoneapp.AuthState
import com.example.trapzoneapp.AuthViewModel
import com.example.trapzoneapp.dataclasses.NavItem
import com.example.trapzoneapp.screens.main.HomeScreen
import com.example.trapzoneapp.screens.main.MapScreen
import com.example.trapzoneapp.screens.main.RankingsScreen

@Composable
fun MainScreen(modifier: Modifier =Modifier, authNavController: NavController, authViewModel: AuthViewModel)
{
    val authState = authViewModel.authState.observeAsState()
    val mainNavController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> authNavController.navigate("login")
            else -> Unit
        }
    }
    BackHandler {
        if (!mainNavController.popBackStack()) {
            // Ako nema gde da se vrati u bottom nav
            activity?.finish()
        }
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(mainNavController)
        }
    )
        { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) { HomeScreen() }
            composable(NavItem.Rankings.route) { RankingsScreen() }
            composable(NavItem.Map.route) { MapScreen() }
        }
    }
}
