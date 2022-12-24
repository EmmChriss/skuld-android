package com.lab.skuld.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShowTasksScreen(text: String = "DefaultTaskString") {
    LazyColumn {
        items(count = 100) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
            {
                Text(
                    text = "$text $it",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

        }
    }
}