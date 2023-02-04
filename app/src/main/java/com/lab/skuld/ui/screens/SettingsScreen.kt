package com.lab.skuld.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lab.skuld.ui.UiContextViewModel

@Composable
fun ShowSettingsScreen() {

    Column {
        Text("Settings", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.padding(16.dp))
        Text("Theme", style = MaterialTheme.typography.h6)
        RadioButtonsThemes()
    }
}




@Composable
fun RadioButtonsThemes() {
    val uiContextViewModel: UiContextViewModel = viewModel()
    val radioOptions = listOf("Light", "Dark", "C for 'Coming soon'")
    //val (selectedOption) = remember { mutableStateOf(uiContextViewModel.theme ) }

    fun onOptionSelected(text: String) {
        when (text) {
            "Light" -> {
                //colors = LightColorPalette
                uiContextViewModel.theme = "Light"
            }
            "Dark" -> {
                //colors = DarkColorPalette
                uiContextViewModel.theme = "Dark"
            }
            "C for 'Coming soon'" -> {
                //colors = LightColorPalette
                uiContextViewModel.theme = "C for 'Coming soon'"
            }
        }
    }
    Column {
        radioOptions.forEach { text ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == uiContextViewModel.theme),
                        onClick = {
                            onOptionSelected(text)
                        }
                    )
                    .padding(horizontal = 16.dp)
            ) {
                RadioButton(
                    selected = (text == uiContextViewModel.theme),
                    onClick = { onOptionSelected(text)}
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 0.dp)
                )
            }
        }
    }
}


/*
private val LightColorPalette = lightColors(
    primary = Color(0xFF212121),
    primaryVariant = Color(0xFFAAAAAA),
    secondary = Color(0xFFb71c1c),
    secondaryVariant = Color(0xFFb71c1c),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color(0xFFffffff),
    onSecondary = Color(0xFFffffff),
    onBackground = Color.Black,
    onSurface = Color.Black,
)
*/
