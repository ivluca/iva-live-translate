package com.alad.app.presentation.main

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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.alad.app.R
import com.alad.app.ui.theme.*
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
    val targetLang by viewModel.targetLang.collectAsState()
    val targetDialect by viewModel.targetDialect.collectAsState()

    val dubbingVol by viewModel.dubbingVolume.collectAsState()
    val isDubbingMuted by viewModel.isDubbingMuted.collectAsState()
    val originalVol by viewModel.originalVolumeRatio.collectAsState()
    val isOriginalMuted by viewModel.isOriginalMuted.collectAsState()

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
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBackgroundElevated)
                                .border(BorderStroke(1.dp, CardBorderSubtle), RoundedCornerShape(8.dp))
                                .padding(5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "IVA Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "IVA",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Live Translate Audio",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        GlassIconButton(
                            icon = Icons.Default.Settings,
                            contentDescription = "Settings",
                            onClick = onNavigateToSettings,
                            tint = TextSecondary
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
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                // 1. Status Indicator
                val statusText = if (isConnected) {
                    stringResource(R.string.status_connected)
                } else {
                    stringResource(R.string.status_disconnected)
                }

                StatusBadge(
                    isConnected = isConnected,
                    statusText = statusText,
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Audio Spectrum Visualizer
                AnimatedAudioBarsVisualizer(
                    isConnected = isConnected,
                    amplitude = amplitude,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                // 3. Live Dual Subtitles Banner
                AnimatedVisibility(
                    visible = isConnected && (liveSubtitleText.isNotEmpty() || (showDualSubtitles && liveSourceSubtitleText.isNotEmpty())),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        borderColor = CardBorderSubtle
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                            .background(StatusActive)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.live_subtitles).uppercase(),
                                        color = StatusActive,
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

                            Spacer(modifier = Modifier.height(10.dp))

                            if (showDualSubtitles && liveSourceSubtitleText.isNotEmpty()) {
                                Text(
                                    text = liveSourceSubtitleText,
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            if (liveSubtitleText.isNotEmpty()) {
                                Text(
                                    text = liveSubtitleText,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // 4. Target Language & Dialect Selection Card
                var langExpanded by remember { mutableStateOf(false) }
                var dialectExpanded by remember { mutableStateOf(false) }
                val displayLang = supportedLanguages.find { it.first == targetLang }?.second ?: targetLang

                val availableDialects = supportedDialects[targetLang.split("-")[0]] ?: emptyList()
                val displayDialect = availableDialects.find { it.code.equals(targetDialect, ignoreCase = true) }?.displayName
                    ?: stringResource(R.string.standard_dialect)

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.target_dubbing_language),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BrandPrimarySubtle)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = targetLang.uppercase(),
                                    color = BrandPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Language Select Box (No keyboard, clean selector)
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val menuWidth = maxWidth
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CardBackgroundElevated)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (langExpanded) BrandPrimary else CardBorderSubtle
                                        ),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { langExpanded = !langExpanded }
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = displayLang,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = if (langExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = if (langExpanded) BrandPrimary else TextMuted
                                )
                            }

                            DropdownMenu(
                                expanded = langExpanded,
                                onDismissRequest = { langExpanded = false },
                                modifier = Modifier
                                    .width(menuWidth)
                                    .background(CardBackgroundElevated)
                                    .border(1.dp, CardBorderSubtle, RoundedCornerShape(8.dp))
                            ) {
                                supportedLanguages.forEach { (code, name) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                name,
                                                color = if (code == targetLang) BrandPrimary else TextPrimary,
                                                fontWeight = if (code == targetLang) FontWeight.Bold else FontWeight.Normal,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        },
                                        onClick = {
                                            viewModel.updateTargetLang(code)
                                            langExpanded = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }

                        // Dialect / Accent Selector (if dialects available for this language)
                        if (availableDialects.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = stringResource(R.string.target_dialect),
                                color = TextSecondary,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                val menuWidth = maxWidth
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CardBackgroundElevated)
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                if (dialectExpanded) BrandPrimary else CardBorderSubtle
                                            ),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { dialectExpanded = !dialectExpanded }
                                        .padding(horizontal = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = displayDialect,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = if (dialectExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = if (dialectExpanded) BrandPrimary else TextMuted
                                    )
                                }

                                DropdownMenu(
                                    expanded = dialectExpanded,
                                    onDismissRequest = { dialectExpanded = false },
                                    modifier = Modifier
                                        .width(menuWidth)
                                        .background(CardBackgroundElevated)
                                        .border(1.dp, CardBorderSubtle, RoundedCornerShape(8.dp))
                                ) {
                                    availableDialects.forEach { dialect ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    dialect.displayName,
                                                    color = if (dialect.code == targetDialect) BrandPrimary else TextPrimary,
                                                    fontWeight = if (dialect.code == targetDialect) FontWeight.Bold else FontWeight.Normal,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            },
                                            onClick = {
                                                viewModel.updateTargetDialect(dialect.code)
                                                dialectExpanded = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Dual Audio Mixer Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.audio_mixer),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // A. Dubbed AI Voice Volume Slider + Mute
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dubbed_voice_volume),
                                color = if (isDubbingMuted) TextMuted else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isDubbingMuted) "MUTED" else "${(dubbingVol * 100).toInt()}%",
                                    color = if (isDubbingMuted) DangerRed else BrandPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isDubbingMuted) DangerRedSubtle else CardBackgroundElevated)
                                        .clickable {
                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                            viewModel.toggleDubbingMute()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDubbingMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Mute AI Voice",
                                        tint = if (isDubbingMuted) DangerRed else TextPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Slider(
                            value = dubbingVol,
                            onValueChange = { viewModel.setDubbingVolume(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = BrandPrimary,
                                activeTrackColor = BrandPrimary,
                                inactiveTrackColor = CardBorderSubtle
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // B. Original Media Audio Volume Slider + Mute
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.original_media_volume),
                                color = if (isOriginalMuted) TextMuted else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isOriginalMuted) "MUTED" else "${(originalVol * 100).toInt()}%",
                                    color = if (isOriginalMuted) DangerRed else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isOriginalMuted) DangerRedSubtle else CardBackgroundElevated)
                                        .clickable {
                                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                            viewModel.toggleOriginalMute()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isOriginalMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Mute Original Audio",
                                        tint = if (isOriginalMuted) DangerRed else TextPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Slider(
                            value = originalVol,
                            onValueChange = { viewModel.setOriginalVolumeRatio(it) },
                            valueRange = 0f..1f,
                            colors = SliderDefaults.colors(
                                thumbColor = TextSecondary,
                                activeTrackColor = TextSecondary,
                                inactiveTrackColor = CardBorderSubtle
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // 6. Floating Overlay Widget Switch Card
                val isWidgetActive by com.alad.app.core.service.OverlayWidgetService.isWidgetActive.collectAsState()
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        if (!Settings.canDrawOverlays(context)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, com.alad.app.core.service.OverlayWidgetService::class.java)
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
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.floating_widget),
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (Settings.canDrawOverlays(context)) {
                                    stringResource(R.string.enable_overlay)
                                } else {
                                    stringResource(R.string.grant_permission_widget)
                                },
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Switch(
                            checked = isWidgetActive,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BrandPrimary,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = CardBackgroundElevated
                            )
                        )
                    }
                }

                // 7. Start/Stop Translate Action Button
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

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/**
 * Clean Studio Audio Bars Visualizer.
 * Displays 32 discrete rounded bars that smoothly animate with audio amplitude when connected.
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
            .height(64.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackground)
            .border(BorderStroke(1.dp, CardBorderSubtle), RoundedCornerShape(10.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalBars = 32
            val spacing = 3.dp.toPx()
            val availableWidth = size.width - (spacing * (totalBars - 1))
            val barWidth = availableWidth / totalBars
            val maxHeight = size.height - 4.dp.toPx()
            val minHeight = 4.dp.toPx()

            val barColor = if (isConnected) StatusActive else CardBorderSubtle

            for (i in 0 until totalBars) {
                val normalizedIndex = i.toFloat() / (totalBars - 1)
                // Curve weighting: center bars are taller
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
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
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
            .height(50.dp)
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
                imageVector = if (isConnected) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isConnected) stringResource(R.string.btn_stop) else stringResource(R.string.btn_start),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 0.5.sp,
                    fontSize = 15.sp
                )
            )
        }
    }
}
