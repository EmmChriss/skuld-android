package com.lab.skuld.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lab.skuld.ui.UiContextViewModel


private val DarkColorPalette = darkColors(
    primary = Color(0xFF111111),
    primaryVariant = Color(0xFF777777),
    /*secondary = Color(0xFFb71c1c),
    secondaryVariant = Color(0xFFb71c1c),*/
    secondary = Color(0xFF111111),
    secondaryVariant = Color(0xFF111111),
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color(0xFFffffff),
    onSecondary = Color(0xFFffffff),
    onBackground = Color.White,
    onSurface = Color.White,
)


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

@Composable
fun SkuldFrontendTheme(

    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val uiContextViewModel: UiContextViewModel = viewModel()
    var colors = if (uiContextViewModel.theme == "Dark") {
        DarkColorPalette
    } else {
        LightColorPalette
    }


    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
