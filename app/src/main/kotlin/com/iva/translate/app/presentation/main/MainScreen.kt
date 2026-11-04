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
                                contentDescription = "iva Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "iva",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Live Translate Audio",
                                color = TextMuted,
                                fontSize = 10.5.sp,
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
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
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

                // 2. Audio Spectrum Visualizer (Compact & Clean)
                AnimatedAudioBarsVisualizer(
                    isConnected = isConnected,
                    amplitude = amplitude,
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Live Dual Subtitles Banner
                AnimatedVisibility(
                    visible = isConnected && (liveSubtitleText.isNotEmpty() || (showDualSubtitles && liveSourceSubtitleText.isNotEmpty())),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        borderColor = CardBorderSubtle
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .clip(CircleShape)
                                            .background(StatusActive)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = stringResource(R.string.live_subtitles).uppercase(),
                                        color = StatusActive,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                Text(
                                    text = "$selectedVoice • $selectedMode",
                                    color = TextMuted,
                                    fontSize = 10.5.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (showDualSubtitles && liveSourceSubtitleText.isNotEmpty()) {
                                Text(
                                    text = liveSourceSubtitleText,
                                    color = TextSecondary,
                                    fontSize = 12.5.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            if (liveSubtitleText.isNotEmpty()) {
                                Text(
                                    text = liveSubtitleText,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    lineHeight = 20.sp,
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
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.target_dubbing_language),
                                color = TextPrimary,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(BrandPrimarySubtle)
                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = targetLang.uppercase(),
                                    color = BrandPrimary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Compact Language Select Box
                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                            val menuWidth = maxWidth
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(7.dp))
                                    .background(CardBackgroundElevated)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (langExpanded) BrandPrimary else CardBorderSubtle
                                        ),
                                        RoundedCornerShape(7.dp)
                                    )
                                    .clickable { langExpanded = !langExpanded }
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = displayLang,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Icon(
                                    imageVector = if (langExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = if (langExpanded) BrandPrimary else TextMuted,
                                    modifier = Modifier.size(20.dp)
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
                                                fontSize = 13.sp,
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

                        // Compact Dialect / Accent Selector
                        if (availableDialects.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.target_dialect),
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                val menuWidth = maxWidth
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(CardBackgroundElevated)
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                if (dialectExpanded) BrandPrimary else CardBorderSubtle
                                            ),
                                            RoundedCornerShape(7.dp)
                                        )
                                        .clickable { dialectExpanded = !dialectExpanded }
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = displayDialect,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Icon(
                                        imageVector = if (dialectExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = if (dialectExpanded) BrandPrimary else TextMuted,
                                        modifier = Modifier.size(20.dp)
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
                                                    fontSize = 13.sp,
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

                // 5. AI Voice Persona Selection Card (Sleek Compact Grid)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(BrandPrimarySubtle),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = BrandPrimary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = stringResource(R.string.voice_persona),
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 13.5.sp
                                )
                            }

                            Text(
                                text = "Active: $selectedVoice",
                                color = BrandPrimary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val voiceRows = listOf(
                            listOf("Aoede", "Kore", "Zephyr"),
                            listOf("Puck", "Charon", "Fenrir")
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            voiceRows.forEach { rowVoices ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    rowVoices.forEach { voiceId ->
                                        val isSelected = selectedVoice == voiceId
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(34.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSelected) BrandPrimarySubtle else CardBackgroundElevated)
                                                .border(
                                                    BorderStroke(
                                                        1.dp,
                                                        if (isSelected) BrandPrimary else CardBorderSubtle
                                                    ),
                                                    shape = RoundedCornerShape(6.dp)
                                                )
                                                .clickable {
                                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                                    viewModel.updateVoicePersona(voiceId)
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = voiceId,
                                                color = if (isSelected) BrandPrimary else TextSecondary,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6. Dual Audio Mixer Card (Compact Layout)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.audio_mixer),
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // A. Dubbed AI Voice Volume Slider + Mute
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dubbed_voice_volume),
                                color = if (isDubbingMuted) TextMuted else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isDubbingMuted) "MUTED" else "${(dubbingVol * 100).toInt()}%",
                                    color = if (isDubbingMuted) DangerRed else BrandPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(4.dp))
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
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        SleekVolumeSlider(
                            value = dubbingVol,
                            onValueChange = { viewModel.setDubbingVolume(it) },
                            activeColor = BrandPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // B. Original Media Audio Volume Slider + Mute
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.original_media_volume),
                                color = if (isOriginalMuted) TextMuted else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isOriginalMuted) "MUTED" else "${(originalVol * 100).toInt()}%",
                                    color = if (isOriginalMuted) DangerRed else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(RoundedCornerShape(4.dp))
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
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        SleekVolumeSlider(
                            value = originalVol,
                            onValueChange = { viewModel.setOriginalVolumeRatio(it) },
                            activeColor = TextSecondary
                        )
                    }
                }

                // 7. Floating Overlay Widget Switch Card (Compact)
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
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (Settings.canDrawOverlays(context)) {
                                    stringResource(R.string.enable_overlay)
                                } else {
                                    stringResource(R.string.grant_permission_widget)
                                },
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
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
                imageVector = if (isConnected) Icons.Default.Stop else Icons.Default.PlayArrow,
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

