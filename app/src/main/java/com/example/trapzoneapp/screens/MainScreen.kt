package com.example.trapzoneapp.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trapzoneapp.AuthState
import com.example.trapzoneapp.AuthViewModel
import com.example.trapzoneapp.dataclasses.NavItem
import com.example.trapzoneapp.screens.main.HomeScreen
import com.example.trapzoneapp.screens.main.MapScreen
import com.example.trapzoneapp.screens.main.RankingsScreen

@Composable
fun MainScreen(modifier: Modifier=Modifier, authNavController: NavController, authViewModel: AuthViewModel)
{
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> authNavController.navigate("login")
            else -> Unit
        }
    }
    val bottomNavItems = listOf(
        NavItem.Rankings,
        NavItem.Home,
        NavItem.Map
    )

    val mainNavController = rememberNavController()
    BackHandler(enabled = true) {
        val currentRoute = mainNavController.currentBackStackEntry?.destination?.route
        if (currentRoute != NavItem.Home.route) {
            mainNavController.navigate(NavItem.Home.route) {
                launchSingleTop = true
            }
        } else {
            val activity = context as? Activity
            activity?.finish()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                val currentRoute = mainNavController
                    .currentBackStackEntryAsState().value?.destination?.route

                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            mainNavController.navigate(item.route) {
                                popUpTo(mainNavController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(text = item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
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
