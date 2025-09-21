package com.example.trapzoneapp.dataclasses

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(val route: String, val label: String, val icon: ImageVector)
{
    object Rankings : NavItem("rankings", "Rankings", Icons.Default.Star)
    object Home : NavItem("home", "Home", Icons.Default.Home)
    object Map : NavItem("map", "Map", Icons.Default.LocationOn)
}