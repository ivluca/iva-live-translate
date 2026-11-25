package com.iva.translate.app.presentation.main

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.iva.translate.app.R
import com.iva.translate.app.ui.theme.*
import kotlin.math.abs
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit,
    onConnectClicked: () -> Unit,
    onDisconnectClicked: () -> Unit
) {
    val isConnected by viewModel.isConnected.collectAsState()
    val amplitude by viewModel.audioAmplitude.collectAsState()
    val liveSubtitleText by viewModel.liveSubtitleText.collectAsState()
    val liveSourceSubtitleText by viewModel.liveSourceSubtitleText.collectAsState()
    val showDualSubtitles by viewModel.showDualSubtitles.collectAsState()
    val selectedVoice by viewModel.voicePersona.collectAsState()
    val selectedMode by viewModel.dubbingMode.collectAsState()

    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    AmbientBackground {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(CardBackgroundElevated)
                                .border(BorderStroke(1.dp, CardBorderSubtle), RoundedCornerShape(7.dp))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Live Translate Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Live Translate",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlassIconButton(
                            icon = Icons.Default.Settings,
                            contentDescription = "Settings",
                            onClick = onNavigateToSettings,
                            tint = TextSecondary,
                            size = 34.dp
                        )
                    }
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                // 1. Unified Status & Audio Spectrum Card (Combined Status & Process Bars)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    backgroundColor = CardBackground,
                    borderColor = if (isConnected) StatusActive.copy(alpha = 0.35f) else CardBorderSubtle
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (isConnected) StatusActive else StatusIdle)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = stringResource(R.string.system_status).uppercase(),
                                    color = if (isConnected) StatusActive else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isConnected) StatusActive.copy(alpha = 0.15f) else CardBorderSubtle.copy(alpha = 0.4f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (isConnected) "LIVE" else "OFF",
                                    color = if (isConnected) StatusActive else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Audio Process Waveform Bars inside the same card
                        AudioBarsCanvas(
                            isConnected = isConnected,
                            amplitude = amplitude,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(26.dp)
                        )
                    }
                }

                // 2. Dual Subtitles Live Screen (Default visible, split 50/50: Top = Source, Bottom = Translated)
                val sourceScrollState = rememberScrollState()
                val targetScrollState = rememberScrollState()

                var autoScrollSource by remember { mutableStateOf(true) }
                var autoScrollTarget by remember { mutableStateOf(true) }

                // Auto-detect when user scrolls up vs scrolls back down to bottom
                LaunchedEffect(sourceScrollState) {
                    snapshotFlow { sourceScrollState.maxValue - sourceScrollState.value }
                        .collect { distanceToBottom ->
                            if (sourceScrollState.isScrollInProgress) {
                                autoScrollSource = distanceToBottom <= 60
                            }
                        }
                }

                LaunchedEffect(liveSourceSubtitleText, sourceScrollState.maxValue, autoScrollSource) {
                    if (autoScrollSource && sourceScrollState.maxValue > 0) {
                        sourceScrollState.animateScrollTo(sourceScrollState.maxValue)
                    }
                }

                LaunchedEffect(targetScrollState) {
                    snapshotFlow { targetScrollState.maxValue - targetScrollState.value }
                        .collect { distanceToBottom ->
                            if (targetScrollState.isScrollInProgress) {
                                autoScrollTarget = distanceToBottom <= 60
                            }
                        }
                }

                LaunchedEffect(liveSubtitleText, targetScrollState.maxValue, autoScrollTarget) {
                    if (autoScrollTarget && targetScrollState.maxValue > 0) {
                        targetScrollState.animateScrollTo(targetScrollState.maxValue)
                    }
                }

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    borderColor = if (isConnected) StatusActive.copy(alpha = 0.25f) else CardBorderSubtle
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isConnected) StatusActive else StatusIdle)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.live_subtitles).uppercase(),
                                    color = if (isConnected) StatusActive else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Text(
                                text = "$selectedVoice • $selectedMode",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Nửa trên: Tiếng nguồn (Ví dụ tiếng Nhật / Anh)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(sourceScrollState)
                        ) {
                            if (liveSourceSubtitleText.isNotEmpty()) {
                                Text(
                                    text = liveSourceSubtitleText,
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = if (isConnected) "Listening for speech..." else "Source speech will appear here...",
                                    color = TextMuted.copy(alpha = 0.6f),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Phân cách giữa 2 nửa
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = CardBorderSubtle.copy(alpha = 0.6f),
                            thickness = 1.dp
                        )

                        // Nửa dưới: Tiếng dịch (Tiếng Việt)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(targetScrollState)
                        ) {
                            if (liveSubtitleText.isNotEmpty()) {
                                Text(
                                    text = liveSubtitleText,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    lineHeight = 21.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text(
                                    text = if (isConnected) "Translating..." else "Translation will appear here...",
                                    color = TextMuted.copy(alpha = 0.6f),
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // 4. Floating Overlay Widget Switch Card (Compact)
                val isWidgetActive by com.iva.translate.app.core.service.OverlayWidgetService.isWidgetActive.collectAsState()
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        if (!Settings.canDrawOverlays(context)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, com.iva.translate.app.core.service.OverlayWidgetService::class.java)
                            if (isWidgetActive) {
                                context.stopService(intent)
                            } else {
                                context.startService(intent)
                            }
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.floating_widget),
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontSize = 13.5.sp
                            )
                            if (!Settings.canDrawOverlays(context)) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(R.string.grant_permission_widget),
                                    fontSize = 11.5.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Switch(
                            checked = isWidgetActive,
                            onCheckedChange = null,
                            modifier = Modifier.scale(0.85f),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BrandPrimary,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CardBackgroundElevated
                            )
                        )
                    }
                }

                // 8. Start/Stop Translate Action Button (Sleek 42dp)
                StartStopTranslateButton(
                    isConnected = isConnected,
                    onClick = {
                        if (isConnected) {
                            onDisconnectClicked()
                        } else {
                            onConnectClicked()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun AudioBarsCanvas(
    isConnected: Boolean,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val animatedAmplitude by animateFloatAsState(
        targetValue = if (isConnected) amplitude.coerceIn(0.05f, 1f) else 0.03f,
        animationSpec = tween(80, easing = LinearEasing),
        label = "canvas_amplitude"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars_canvas")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "canvas_phase"
    )

    Canvas(modifier = modifier) {
        val totalBars = 32
        val spacing = 2.5.dp.toPx()
        val availableWidth = size.width - (spacing * (totalBars - 1))
        val barWidth = availableWidth / totalBars
        val maxHeight = size.height - 2.dp.toPx()
        val minHeight = 2.5.dp.toPx()

        val barColor = if (isConnected) StatusActive else CardBorderSubtle

        for (i in 0 until totalBars) {
            val normalizedIndex = i.toFloat() / (totalBars - 1)
            val centerWeight = sin(normalizedIndex * Math.PI).toFloat()

            val wave = if (isConnected) {
                val w1 = sin(phase + normalizedIndex * 4f).toFloat() * 0.5f
                val w2 = sin((phase * 1.5f) + normalizedIndex * 6f).toFloat() * 0.5f
                (w1 + w2) * 0.5f
            } else {
                0f
            }

            val currentHeight = if (isConnected) {
                val h = minHeight + (maxHeight - minHeight) * centerWeight * (animatedAmplitude * 0.7f + abs(wave) * 0.3f)
                h.coerceIn(minHeight, maxHeight)
            } else {
                minHeight
            }

            val x = i * (barWidth + spacing)
            val y = (size.height - currentHeight) / 2f

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, currentHeight),
                cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
            )
        }
    }
}

/**
 * Clean Studio Audio Bars Visualizer.
 * Sleek 44dp height with 32 discrete rounded bars that smoothly animate with audio amplitude.
 */
@Composable
fun AnimatedAudioBarsVisualizer(
    isConnected: Boolean,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val animatedAmplitude by animateFloatAsState(
        targetValue = if (isConnected) amplitude.coerceIn(0.05f, 1f) else 0.03f,
        animationSpec = tween(80, easing = LinearEasing),
        label = "amplitude"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackground)
            .border(BorderStroke(1.dp, CardBorderSubtle), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalBars = 32
            val spacing = 2.5.dp.toPx()
            val availableWidth = size.width - (spacing * (totalBars - 1))
            val barWidth = availableWidth / totalBars
            val maxHeight = size.height - 2.dp.toPx()
            val minHeight = 3.dp.toPx()

            val barColor = if (isConnected) StatusActive else CardBorderSubtle

            for (i in 0 until totalBars) {
                val normalizedIndex = i.toFloat() / (totalBars - 1)
                val centerWeight = sin(normalizedIndex * Math.PI).toFloat()

                val wave = if (isConnected) {
                    val w1 = sin(phase + normalizedIndex * 4f).toFloat() * 0.5f
                    val w2 = sin((phase * 1.5f) + normalizedIndex * 6f).toFloat() * 0.5f
                    (w1 + w2) * 0.5f
                } else {
                    0f
                }

                val currentHeight = if (isConnected) {
                    val h = minHeight + (maxHeight - minHeight) * centerWeight * (animatedAmplitude * 0.7f + abs(wave) * 0.3f)
                    h.coerceIn(minHeight, maxHeight)
                } else {
                    minHeight
                }

                val x = i * (barWidth + spacing)
                val y = (size.height - currentHeight) / 2f

                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, currentHeight),
                    cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
                )
            }
        }
    }
}

/**
 * Modern tactile action button for Start/Stop Translate.
 */
@Composable
fun StartStopTranslateButton(
    isConnected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val haptic = LocalHapticFeedback.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    val buttonColor = if (isConnected) DangerRed else BrandPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(buttonColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isConnected) AppIcons.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = if (isConnected) stringResource(R.string.btn_stop) else stringResource(R.string.btn_start),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    fontSize = 14.sp
                )
            )
        }
    }
}

/**
 * Sleek, professional audio slider with 100% pixel-perfect concentric track (3dp) and thumb (12dp).
 */
@Composable
fun SleekVolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val thumbRadiusPx = with(density) { 6.dp.toPx() }
    val trackHeightPx = with(density) { 3.dp.toPx() }
    val borderWidthPx = with(density) { 1.2.dp.toPx() }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(26.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val availableWidth = (widthPx - thumbRadiusPx * 2).coerceAtLeast(1f)

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(widthPx) {
                    detectTapGestures { offset ->
                        val newValue = ((offset.x - thumbRadiusPx) / availableWidth).coerceIn(0f, 1f)
                        onValueChange(newValue)
                    }
                }
                .pointerInput(widthPx) {
                    detectHorizontalDragGestures { change, _ ->
                        change.consume()
                        val newValue = ((change.position.x - thumbRadiusPx) / availableWidth).coerceIn(0f, 1f)
                        onValueChange(newValue)
                    }
                }
        ) {
            val centerY = size.height / 2f
            val startX = thumbRadiusPx
            val endX = size.width - thumbRadiusPx
            val currentThumbX = (startX + availableWidth * value.coerceIn(0f, 1f)).coerceIn(startX, endX)

            // Inactive track (background track)
            drawLine(
                color = CardBorderSubtle,
                start = Offset(startX, centerY),
                end = Offset(endX, centerY),
                strokeWidth = trackHeightPx,
                cap = StrokeCap.Round
            )

            // Active track (progress)
            if (currentThumbX > startX) {
                drawLine(
                    color = activeColor,
                    start = Offset(startX, centerY),
                    end = Offset(currentThumbX, centerY),
                    strokeWidth = trackHeightPx,
                    cap = StrokeCap.Round
                )
            }

            // Thumb Fill
            drawCircle(
                color = activeColor,
                radius = thumbRadiusPx,
                center = Offset(currentThumbX, centerY)
            )

            // Thumb Border
            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = thumbRadiusPx,
                center = Offset(currentThumbX, centerY),
                style = Stroke(width = borderWidthPx)
            )
        }
    }
}

