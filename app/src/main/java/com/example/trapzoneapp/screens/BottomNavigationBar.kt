package com.example.trapzoneapp.screens

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.trapzoneapp.dataclasses.NavItem

@Composable
fun BottomNavigationBar(mainNavController: NavController)
{
    val backStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val bottomNavItems = listOf(
        NavItem.Rankings,
        NavItem.Home,
        NavItem.Map
    )
    NavigationBar{
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                modifier = Modifier.size(40.dp),
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
                icon = {
                    Icon(painter = painterResource(id = item.icon),
                    contentDescription = item.label) },
                label = { Text(text = item.label) }
            )
        }
    }
}