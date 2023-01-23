package com.lab.skuld.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
/*
private val DarkColorPalette = darkColors(
    primary = Purple200,
    primaryVariant = Purple700,
    secondary = Teal200
)*/

private val DarkColorPalette = darkColors(
    primary = Color(0xFF212121),
    primaryVariant = Color(0xFF000000),
    secondary = Color(0xFF7f0000)
)
/*
private val LightColorPalette = lightColors(
    primary = Purple500,
    primaryVariant = Purple700,
    secondary = Teal200,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)*/


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
    val colors = if (darkTheme) {
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
