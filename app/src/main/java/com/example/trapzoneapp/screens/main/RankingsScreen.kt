package com.example.trapzoneapp.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trapzoneapp.helpfunctions.firebase.loadRankings

@Composable
fun RankingsScreen(modifier: Modifier=Modifier)
{
    var rankings by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    LaunchedEffect(Unit) {
        loadRankings { list ->
            rankings = list
        }
    }

    LazyColumn(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(rankings) { index, user ->
            val backgroundColor = when (index) {
                0 -> Color(0xFFFFD700) // zlatna
                1 -> Color(0xFFC0C0C0) // srebrna
                2 -> Color(0xFFCD7F32) // bronzana
                else -> Color.Transparent
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(backgroundColor, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${index + 1}. ${user.first}", fontWeight = FontWeight.Bold)
                Text("${user.second}", fontWeight = FontWeight.Bold)
            }
        }
    }
}
    //vise tipova rangiranja:
//    val tabs = listOf("Ukupni poeni", "Rešene zamke", "Uhvaćeni protivnici", "Postavljeni objekti")
//    var selectedTab by remember { mutableIntStateOf(0) }
//    var rankings by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }
//
//    LaunchedEffect(selectedTab) {
//        val field = when(selectedTab) {
//            0 -> "totalPoints"
//            1 -> "trapsSolved"
//            2 -> "trapsSetByOthers"
//            3 -> "rewardsObjectsPlaced"
//            else -> "totalPoints"
//        }
//        loadRankingByField(field) { list ->
//            rankings = list
//        }
//    }
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        TabRow(selectedTabIndex = selectedTab) {
//            tabs.forEachIndexed { index, title ->
//                Tab(selected = selectedTab == index, onClick = { selectedTab = index }) {
//                    Text(title, modifier = Modifier.padding(16.dp))
//                }
//            }
//        }
//
//        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//            itemsIndexed(rankings) { index, user ->
//                Row(
//                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    Text("${index + 1}. ${user.first}")
//                    Text("${user.second}")
//                }
//            }
//        }
//    }