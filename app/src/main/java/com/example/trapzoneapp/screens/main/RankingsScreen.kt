package com.example.trapzoneapp.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trapzoneapp.R
import com.example.trapzoneapp.classes.UserStats
import com.example.trapzoneapp.functions.firebase.loadRankingsByCategory
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RankingsScreen(modifier: Modifier=Modifier) {
    val tabs = listOf("Ukupni poeni", "Postavljeni objekti", "Postavljene zamke")
    var selectedTab by remember { mutableIntStateOf(0) }
    val rankings = remember { mutableStateListOf<UserStats>() }

    LaunchedEffect(selectedTab) {
        val statShown = when(selectedTab) {
            0 -> "points"
            1 -> "numObjects"
            2 -> "numTraps"
            else -> "points"
        }
        rankings.clear()
        loadRankingsByCategory(statShown,rankings)
    }

    Box(modifier = modifier.fillMaxSize())
    {
        Image(
            painter = painterResource(id = R.drawable.rankings_background),
            contentDescription = "Rankings background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E1E1E),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    SecondaryIndicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTab])
                            .height(4.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                        color = Color(0xFFFFD700)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index, onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) Color.White else Color.LightGray
                            )
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )

                }
            }

            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                itemsIndexed(rankings) { index, user ->
                    val backgroundColor = when (index) {
                        0 -> Color(0xFFFFD700)
                        1 -> Color(0xFFC0C0C0)
                        2 -> Color(0xFFCD7F32)
                        else -> Color.White
                    }
                    Row(
                        modifier = modifier
                            .fillMaxWidth(0.95f)
                            .padding(vertical = 4.dp)
                            .background(backgroundColor, shape = RoundedCornerShape(10.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "${index + 1}. ${user.fullName}",
                            fontWeight = FontWeight.Bold,
                            color = if (user.uid == FirebaseAuth.getInstance().currentUser!!.uid) Color.Blue else Color.Black
                        )
                        Text(
                            "${user.stat}",
                            fontWeight = FontWeight.Bold,
                            color = if (user.uid == FirebaseAuth.getInstance().currentUser!!.uid) Color.Blue else Color.Black
                        )
                    }
                }
            }
        }
    }
}