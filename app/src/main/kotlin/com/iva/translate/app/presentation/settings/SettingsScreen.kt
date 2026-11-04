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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iva.translate.app.R
import com.iva.translate.app.data.repository.UserPreferencesRepository
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
                // 1. API Key Configuration Card
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
                                    imageVector = Icons.Default.Key,
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
                                        imageVector = if (apiKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
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
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
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
                                    imageVector = Icons.Default.Tune,
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
                            Triple(UserPreferencesRepository.MODE_HYBRID, stringResource(R.string.mode_hybrid), Icons.Default.GraphicEq),
                            Triple(UserPreferencesRepository.MODE_AUDIO_ONLY, stringResource(R.string.mode_audio_only), Icons.AutoMirrored.Filled.VolumeUp),
                            Triple(UserPreferencesRepository.MODE_SUBTITLE_ONLY, stringResource(R.string.mode_subtitle_only), Icons.Default.Subtitles)
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
                            imageVector = Icons.Default.Save,
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
