package com.iva.translate.app.core.service

import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.iva.translate.app.R
import com.iva.translate.app.TransparentCaptureActivity
import com.iva.translate.app.data.repository.DubbingSessionRepository
import com.iva.translate.app.ui.theme.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class OverlayWidgetService : LifecycleService() {

    companion object {
        val isWidgetActive = MutableStateFlow(false)
    }

    private lateinit var windowManager: WindowManager
    private lateinit var controllerComposeView: ComposeView
    private lateinit var subtitleComposeView: ComposeView

    private var controllerParams: WindowManager.LayoutParams? = null
    private var subtitleParams: WindowManager.LayoutParams? = null
    private var isControllerViewAttached = false
    private var isSubtitleViewAttached = false

    private val savedStateRegistryOwner by lazy { ServiceSavedStateRegistryOwner(this) }
    private val viewModelStoreOwner by lazy { ServiceViewModelStoreOwner() }

    override fun onCreate() {
        super.onCreate()
        isWidgetActive.value = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        // 1. Controller Capsule Window Params (Top-left draggable, ultra-compact)
        controllerParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 30
            y = 160
        }

        // Setup Controller ComposeView
        controllerComposeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ALADTheme {
                    FloatingControllerCapsule(
                        onDrag = { dx, dy ->
                            controllerParams?.let { p ->
                                p.x = (p.x + dx.toInt()).coerceAtLeast(0)
                                p.y = (p.y + dy.toInt()).coerceAtLeast(0)
                                windowManager.updateViewLayout(controllerComposeView, p)
                            }
                        },
                        onClose = {
                            stopSelf()
                        },
                        onToggle = { isRunning ->
                            if (isRunning) {
                                val intent = Intent(this@OverlayWidgetService, AudioDubbingForegroundService::class.java).apply {
                                    action = AudioDubbingForegroundService.ACTION_STOP
                                }
                                startService(intent)
                            } else {
                                val captureIntent = Intent(this@OverlayWidgetService, TransparentCaptureActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                startActivity(captureIntent)
                            }
                        }
                    )
                }
            }
        }
        controllerComposeView.setViewTreeLifecycleOwner(this)
        controllerComposeView.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        controllerComposeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        try {
            windowManager.addView(controllerComposeView, controllerParams)
            isControllerViewAttached = true
        } catch (e: Exception) {
            android.util.Log.e("OverlayWidgetService", "Error initially attaching controller view", e)
        }

        // 2. Subtitle Window Params (Bottom-center draggable)
        subtitleParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            x = 0
            y = 100
        }

        // Setup Subtitle ComposeView
        subtitleComposeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            visibility = View.GONE
            setContent {
                ALADTheme {
                    FloatingCinemaSubtitles(
                        onDrag = { dx, dy ->
                            subtitleParams?.let { p ->
                                p.x = (p.x + dx.toInt())
                                p.y = (p.y - dy.toInt()).coerceAtLeast(20)
                                windowManager.updateViewLayout(subtitleComposeView, p)
                            }
                        },
                        onDismiss = {
                            DubbingSessionRepository.setSubtitleOverlayVisibility(false)
                        }
                    )
                }
            }
        }
        subtitleComposeView.setViewTreeLifecycleOwner(this)
        subtitleComposeView.setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)
        subtitleComposeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)

        // Attach subtitle view to WindowManager
        try {
            windowManager.addView(subtitleComposeView, subtitleParams)
            isSubtitleViewAttached = true
        } catch (e: Exception) {
            android.util.Log.e("OverlayWidgetService", "Error initially attaching subtitle view", e)
        }

        // Dynamically toggle subtitle visibility and touchability based on session state
        lifecycleScope.launch {
            combine(
                DubbingSessionRepository.isDubbingRunning,
                DubbingSessionRepository.isSubtitleOverlayVisible
            ) { isRunning, isVisible ->
                isRunning && isVisible
            }.collect { shouldShowSubtitles ->
                if (!isSubtitleViewAttached) return@collect
                subtitleParams?.let { params ->
                    if (shouldShowSubtitles) {
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        subtitleComposeView.visibility = View.VISIBLE
                    } else {
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        subtitleComposeView.visibility = View.GONE
                    }
                    try {
                        windowManager.updateViewLayout(subtitleComposeView, params)
                    } catch (e: Exception) {
                        android.util.Log.e("OverlayWidgetService", "Error updating subtitle layout", e)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isWidgetActive.value = false
        if (isControllerViewAttached && ::controllerComposeView.isInitialized) {
            try { windowManager.removeView(controllerComposeView) } catch (_: Exception) {}
            isControllerViewAttached = false
        }
        if (isSubtitleViewAttached && ::subtitleComposeView.isInitialized) {
            try { windowManager.removeView(subtitleComposeView) } catch (_: Exception) {}
            isSubtitleViewAttached = false
        }
    }
}

@Composable
fun FloatingControllerCapsule(
    onDrag: (Float, Float) -> Unit,
    onClose: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    val isRunning by DubbingSessionRepository.isDubbingRunning.collectAsState()
    val isSubtitleVisible by DubbingSessionRepository.isSubtitleOverlayVisible.collectAsState()
    val isDubbingMuted by DubbingSessionRepository.isDubbingMuted.collectAsState()
    val haptic = LocalHapticFeedback.current

    Row(
        modifier = Modifier
            .wrapContentSize()
            .clip(CircleShape)
            .background(Color(0xE60F141E))
            .border(
                BorderStroke(0.75.dp, Color.White.copy(alpha = 0.15f)),
                shape = CircleShape
            )
            .padding(horizontal = 5.dp, vertical = 3.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // 1. Play / Stop Button (Consistent Red)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(DangerRed)
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggle(isRunning)
                }
        ) {
            if (isRunning) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.White, shape = RoundedCornerShape(1.5.dp))
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        // 2. Mute Toggle Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isDubbingMuted) DangerRed.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f)
                )
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    DubbingSessionRepository.toggleDubbingMute()
                }
        ) {
            Icon(
                imageVector = if (isDubbingMuted) AppIcons.VolumeOff else AppIcons.VolumeUp,
                contentDescription = "Toggle Mute",
                tint = if (isDubbingMuted) DangerRed else Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(13.dp)
            )
        }

        // 3. Subtitle Visibility Toggle Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    if (isSubtitleVisible) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.08f)
                )
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    DubbingSessionRepository.toggleSubtitleOverlayVisibility()
                }
        ) {
            Icon(
                imageVector = if (isSubtitleVisible) AppIcons.Subtitles else AppIcons.VisibilityOff,
                contentDescription = "Toggle Subtitles",
                tint = if (isSubtitleVisible) Color.White else Color.White.copy(alpha = 0.45f),
                modifier = Modifier.size(13.dp)
            )
        }

        // 4. Close '✕' Button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .clickable {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClose()
                }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Widget",
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun FloatingCinemaSubtitles(
    onDrag: (Float, Float) -> Unit,
    onDismiss: () -> Unit
) {
    val liveSubtitleText by DubbingSessionRepository.liveSubtitleText.collectAsState()
    val liveSourceSubtitleText by DubbingSessionRepository.liveSourceSubtitleText.collectAsState()
    val showDualSubtitles by DubbingSessionRepository.showDualSubtitles.collectAsState()
    val connectionStatus by DubbingSessionRepository.connectionStatus.collectAsState()
    val subtitleHistory by DubbingSessionRepository.subtitleHistory.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "listening")
    val listeningAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "listeningAlpha"
    )

    val scrollState = rememberScrollState()
    LaunchedEffect(liveSubtitleText, subtitleHistory.size) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthDp = configuration.screenWidthDp.dp
    val minWidth = 200.dp
    val maxWidth = screenWidthDp // Có thể kéo dài hết toàn bộ chiều ngang màn hình
    // Chiều dài mặc định ban đầu
    val defaultWidth = 360.dp.coerceIn(minWidth, maxWidth)

    val defaultHeight = 88.dp
    val minHeight = 72.dp
    val maxHeight = defaultHeight * 2 // Tối đa 2 lần chiều cao mặc định = 176dp

    var currentWidth by remember { mutableStateOf(defaultWidth) }
    var currentHeight by remember { mutableStateOf(defaultHeight) }

    val cornerRadius = if (currentWidth >= screenWidthDp - 8.dp) 0.dp else 8.dp

    Box(
        modifier = Modifier
            .width(currentWidth)
            .height(currentHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xF50D111C),
                        Color(0xFA070A12)
                    )
                )
            )
            .border(
                BorderStroke(0.75.dp, Color.White.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(cornerRadius)
            )
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar with Status Dot + Title + Drag Handle + Close
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (connectionStatus == "Gemini Ready" || liveSubtitleText.isNotEmpty()) StatusActive else StatusIdle)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live Subtitles",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    )
                }

                // Minimize icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hide Subtitles",
                        tint = TextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            // Fixed-height scrollable content area (Target language only, matching title font size 10sp)
            val isWaiting = subtitleHistory.isEmpty() && liveSubtitleText.isEmpty()
            if (isWaiting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    Text(
                        text = stringResource(R.string.subtitles_waiting),
                        color = TextSecondary.copy(alpha = listeningAlpha),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 4.dp, bottom = 4.dp, end = 18.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Top
                ) {
                    // Completed sentences (showing most recent translated lines dimmed)
                    val recentHistory = subtitleHistory.takeLast(4)
                    recentHistory.forEach { line ->
                        if (line.translated.isNotEmpty()) {
                            Text(
                                text = line.translated,
                                color = Color.White.copy(alpha = 0.60f),
                                fontSize = 9.sp,
                                lineHeight = 13.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 2.dp)
                            )
                        }
                    }

                    // Live Subtitle Content (Target language only, un-bolded, font matching title at 10sp)
                    if (liveSubtitleText.isNotEmpty()) {
                        Text(
                            text = liveSubtitleText,
                            color = Color.White,
                            fontSize = 10.sp,
                            lineHeight = 14.5.sp,
                            fontWeight = FontWeight.Normal,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.85f),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 3f
                                )
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Corner Resize Grip Handle (Bottom-Right)
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(26.dp)
                .pointerInput(density) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        with(density) {
                            currentWidth = (currentWidth + (dragAmount.x * 2.0f).toDp()).coerceIn(minWidth, maxWidth)
                            currentHeight = (currentHeight + dragAmount.y.toDp()).coerceIn(minHeight, maxHeight)
                        }
                    }
                }
                .padding(1.dp)
        ) {
            Canvas(modifier = Modifier.size(10.dp)) {
                val stroke = 1.5.dp.toPx()
                val gripColor = Color.White.copy(alpha = 0.35f)
                drawLine(gripColor, Offset(size.width, size.height * 0.35f), Offset(size.width * 0.35f, size.height), stroke)
                drawLine(gripColor, Offset(size.width, size.height * 0.70f), Offset(size.width * 0.70f, size.height), stroke)
            }
        }
    }
}
