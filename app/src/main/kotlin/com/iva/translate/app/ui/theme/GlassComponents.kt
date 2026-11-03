package com.iva.translate.app.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iva.translate.app.R

/**
 * Modern Clean Canvas Container.
 * Provides a deep neutral dark background without excessive glowing gradients.
 */
@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasBackground)
    ) {
        content()
    }
}

/**
 * Modern Clean Card surface with subtle border and crisp elevation.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    backgroundColor: Color = CardBackground,
    borderColor: Color = CardBorderSubtle,
    borderAlpha: Float = 1.0f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor.copy(alpha = borderAlpha)), shape = shape)
            .then(clickableModifier)
    ) {
        content()
    }
}

/**
 * Modern Pill Chip.
 */
@Composable
fun LiquidGlassPill(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val pillBg = if (isSelected) BrandPrimarySubtle else CardBackgroundElevated
    val borderColor = if (isSelected) BrandPrimary else CardBorderSubtle

    val clickableModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier

    Row(
        modifier = modifier
            .clip(shape)
            .background(pillBg)
            .border(BorderStroke(1.dp, borderColor), shape = shape)
            .then(clickableModifier)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        content()
    }
}

/**
 * Clean circular or rounded icon button with subtle surface tone.
 */
@Composable
fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary,
    size: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackgroundElevated)
            .border(BorderStroke(1.dp, CardBorderSubtle), shape = RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Sleek, professional Status Card with clean live indicator.
 */
@Composable
fun StatusBadge(
    isConnected: Boolean,
    statusText: String,
    modifier: Modifier = Modifier
) {
    val dotColor = if (isConnected) StatusActive else StatusIdle

    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = CardBackground,
        borderColor = if (isConnected) StatusActive.copy(alpha = 0.35f) else CardBorderSubtle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Indicator Dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.system_status).uppercase(),
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            // Clean State Pill Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isConnected) StatusActiveSubtle else CardBackgroundElevated)
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isConnected) StatusActive.copy(alpha = 0.4f) else CardBorderSubtle
                        ),
                        RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (isConnected) "LIVE" else "IDLE",
                    color = if (isConnected) StatusActive else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
