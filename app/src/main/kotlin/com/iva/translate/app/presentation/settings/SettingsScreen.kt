package com.iva.translate.app.presentation.settings

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iva.translate.app.R
import com.iva.translate.app.data.repository.UserPreferencesRepository
import com.iva.translate.app.presentation.main.SleekVolumeSlider
import com.iva.translate.app.presentation.main.supportedLanguages
import com.iva.translate.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBack: () -> Unit = {}) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val apiKey by viewModel.userApiKey.collectAsState()
    val selectedMode by viewModel.dubbingMode.collectAsState()
    val showDualSubtitles by viewModel.showDualSubtitles.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val selectedVoice by viewModel.voicePersona.collectAsState()
    val dubbingVol by viewModel.dubbingVolume.collectAsState()
    val isDubbingMuted by viewModel.isDubbingMuted.collectAsState()
    val originalVol by viewModel.originalVolumeRatio.collectAsState()
    val isOriginalMuted by viewModel.isOriginalMuted.collectAsState()

    val haptic = LocalHapticFeedback.current
    var langExpanded by remember { mutableStateOf(false) }
    val displayLang = supportedLanguages.find { it.first == targetLang }?.second ?: targetLang

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var apiKeyVisible by remember { mutableStateOf(false) }

    AmbientBackground {
        Scaffold(
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        onClick = onBack,
                        tint = TextPrimary,
                        size = 34.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 17.sp
                        )
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Target Language Selection Card
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
                                    imageVector = if (langExpanded) AppIcons.ArrowDropUp else Icons.Default.ArrowDropDown,
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
                    }
                }

                // 2. AI Voice Persona Selection Card
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
                                        imageVector = AppIcons.RecordVoiceOver,
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

                // 3. Dual Audio Mixer Card
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
                                imageVector = AppIcons.Tune,
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
                                        imageVector = if (isDubbingMuted) AppIcons.VolumeOff else AppIcons.VolumeUp,
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
                                        imageVector = if (isOriginalMuted) AppIcons.VolumeOff else AppIcons.VolumeUp,
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

                // 4. API Key Configuration Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BrandPrimarySubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = AppIcons.Key,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = stringResource(R.string.gemini_api_key),
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary,
                                    fontSize = 13.5.sp
                                )
                                Text(
                                    text = "Google AI Studio Live API",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Compact Sleek Text Field
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(CardBackgroundElevated)
                                .border(BorderStroke(1.dp, CardBorderSubtle), RoundedCornerShape(7.dp))
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (apiKey.isEmpty()) {
                                        Text(
                                            text = "AIzaSy...",
                                            color = TextMuted,
                                            fontSize = 13.sp
                                        )
                                    }
                                    BasicTextField(
                                        value = apiKey,
                                        onValueChange = viewModel::updateApiKey,
                                        singleLine = true,
                                        visualTransformation = if (apiKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        textStyle = LocalTextStyle.current.copy(
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Normal
                                        ),
                                        cursorBrush = SolidColor(BrandPrimary),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                IconButton(
                                    onClick = { apiKeyVisible = !apiKeyVisible },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (apiKeyVisible) AppIcons.VisibilityOff else AppIcons.Visibility,
                                        contentDescription = if (apiKeyVisible) stringResource(R.string.hide) else stringResource(R.string.show),
                                        tint = if (apiKeyVisible) BrandPrimary else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Compact Link Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(CardBackgroundElevated)
                                .border(BorderStroke(1.dp, CardBorderSubtle), RoundedCornerShape(7.dp))
                                .clickable {
                                    uriHandler.openUri("https://aistudio.google.com/app/apikey")
                                }
                                .padding(horizontal = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.get_api_key),
                                    color = BrandPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.5.sp
                                )

                                Icon(
                                    imageVector = AppIcons.OpenInNew,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // 2. Dual Subtitles Option Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { viewModel.updateShowDualSubtitles(!showDualSubtitles) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.dual_subtitles),
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            fontSize = 13.5.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Switch(
                            checked = showDualSubtitles,
                            onCheckedChange = { viewModel.updateShowDualSubtitles(it) },
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

                // 3. Dubbing Mode Card (Compact Options)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(BrandPrimarySubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = AppIcons.Tune,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = stringResource(R.string.dubbing_mode),
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontSize = 13.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val modes = listOf(
                            Triple(UserPreferencesRepository.MODE_HYBRID, stringResource(R.string.mode_hybrid), AppIcons.GraphicEq),
                            Triple(UserPreferencesRepository.MODE_AUDIO_ONLY, stringResource(R.string.mode_audio_only), AppIcons.VolumeUp),
                            Triple(UserPreferencesRepository.MODE_SUBTITLE_ONLY, stringResource(R.string.mode_subtitle_only), AppIcons.Subtitles)
                        )

                        modes.forEach { (modeId, modeLabel, icon) ->
                            val isSelected = selectedMode == modeId
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .height(36.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) BrandPrimarySubtle else CardBackgroundElevated)
                                    .border(
                                        BorderStroke(
                                            1.dp,
                                            if (isSelected) BrandPrimary else CardBorderSubtle
                                        ),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable { viewModel.updateDubbingMode(modeId) }
                                    .padding(horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) BrandPrimary else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = modeLabel,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 12.5.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = BrandPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // 4. Save Settings Button (Compact 42dp)
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val scale by animateFloatAsState(
                    targetValue = if (isPressed) 0.98f else 1.0f,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "save_btn_scale"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .scale(scale)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandPrimary)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                viewModel.saveSettings()
                                val savedMessage = context.getString(R.string.settings_saved)
                                scope.launch {
                                    try {
                                        snackbarHostState.showSnackbar(savedMessage)
                                    } catch (e: Exception) {
                                        android.util.Log.e("SettingsScreen", "Failed to display snackbar", e)
                                    }
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.Save,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.save_settings),
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
