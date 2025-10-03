package com.example.trapzoneapp.classes

import com.example.trapzoneapp.R

sealed class NavItem(val route: String, val label: String, val icon: Int)
{
    object Rankings : NavItem("rankings", "Rankings",R.drawable.rankings_icon)
    object Home : NavItem("home", "Home", R.drawable.home_icon)
    object Map : NavItem("map", "Map", R.drawable.map_icon)
}