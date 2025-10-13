package com.example.trapzoneapp.clickables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.trapzoneapp.classes.NavItem

@Composable
fun BottomNavigationBar(mainNavController: NavController)
{
    val backStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val bottomNavItems = listOf(
        NavItem.Rankings,
        NavItem.Home,
        NavItem.Map,
        NavItem.List
    )
    NavigationBar(
        containerColor = Color(0xFF1E1E1E),
        contentColor = Color.White
    ){
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                modifier = Modifier.size(40.dp),
                selected = isSelected,
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
                        contentDescription = item.label,
                        tint = if (isSelected) Color.White else Color.Gray) },
                label = {
                    Text(text = item.label,
                        color = if (isSelected) Color.White else Color.Gray)
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}