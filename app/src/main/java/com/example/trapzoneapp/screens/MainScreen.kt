package com.example.trapzoneapp.screens

import android.app.Activity
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trapzoneapp.ViewModels.AuthViewModel
import com.example.trapzoneapp.ViewModels.MapViewModel
import com.example.trapzoneapp.classes.AuthState
import com.example.trapzoneapp.classes.NavItem
import com.example.trapzoneapp.clickables.BottomNavigationBar
import com.example.trapzoneapp.screens.main.HomeScreen
import com.example.trapzoneapp.screens.main.ListScreen
import com.example.trapzoneapp.screens.main.RankingsScreen
import com.example.trapzoneapp.screens.main.map.MapScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(modifier: Modifier =Modifier, authNavController: NavController, authViewModel: AuthViewModel)
{
    val authState = authViewModel.authState.observeAsState()
    val mainNavController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity

    val mapViewModel: MapViewModel = viewModel()

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> authNavController.navigate("login")
            else -> Unit
        }
    }
    BackHandler {
        if (!mainNavController.popBackStack()) {
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
            modifier = modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) {
                HomeScreen(onSignOut = { authViewModel.signout() })
            }
            composable(NavItem.Rankings.route) { RankingsScreen() }
            composable(NavItem.Map.route) { MapScreen(mapViewModel) }
            composable(NavItem.List.route) { ListScreen(mapViewModel) }
        }
    }
}
