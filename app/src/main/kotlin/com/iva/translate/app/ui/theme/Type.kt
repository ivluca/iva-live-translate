package com.iva.translate.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.iva.translate.app.R

val SNProFontFamily = FontFamily(
    Font(R.font.snpro_regular, FontWeight.Normal),
    Font(R.font.snpro_medium, FontWeight.Medium),
    Font(R.font.snpro_semibold, FontWeight.SemiBold),
    Font(R.font.snpro_bold, FontWeight.Bold)
)

private val Base = Typography()

val AppTypography = Typography(
    displayLarge = Base.displayLarge.copy(fontFamily = SNProFontFamily),
    displayMedium = Base.displayMedium.copy(fontFamily = SNProFontFamily),
    displaySmall = Base.displaySmall.copy(fontFamily = SNProFontFamily),
    headlineLarge = Base.headlineLarge.copy(fontFamily = SNProFontFamily),
    headlineMedium = Base.headlineMedium.copy(fontFamily = SNProFontFamily),
    headlineSmall = Base.headlineSmall.copy(fontFamily = SNProFontFamily),
    titleLarge = Base.titleLarge.copy(fontFamily = SNProFontFamily),
    titleMedium = Base.titleMedium.copy(fontFamily = SNProFontFamily),
    titleSmall = Base.titleSmall.copy(fontFamily = SNProFontFamily),
    bodyLarge = Base.bodyLarge.copy(fontFamily = SNProFontFamily),
    bodyMedium = Base.bodyMedium.copy(fontFamily = SNProFontFamily),
    bodySmall = Base.bodySmall.copy(fontFamily = SNProFontFamily),
    labelLarge = Base.labelLarge.copy(fontFamily = SNProFontFamily),
    labelMedium = Base.labelMedium.copy(fontFamily = SNProFontFamily),
    labelSmall = Base.labelSmall.copy(fontFamily = SNProFontFamily)
)
