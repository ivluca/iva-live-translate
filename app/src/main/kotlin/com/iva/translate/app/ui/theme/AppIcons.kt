package com.iva.translate.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lightweight custom Material Vector Icons so we don't need the 80MB+ `material-icons-extended` dependency.
 */
object AppIcons {

    val ArrowDropUp: ImageVector by lazy {
        materialIcon("ArrowDropUp") {
            moveTo(7f, 14f)
            lineToRelative(5f, -5f)
            lineToRelative(5f, 5f)
            close()
        }
    }

    val RecordVoiceOver: ImageVector by lazy {
        materialIcon("RecordVoiceOver") {
            moveTo(9f, 9f)
            moveToRelative(-4f, 0f)
            arcToRelative(4f, 4f, 0f, true, true, 8f, 0f)
            arcToRelative(4f, 4f, 0f, true, true, -8f, 0f)
            moveTo(9f, 15f)
            curveToRelative(-2.67f, 0f, -8f, 1.34f, -8f, 4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(16f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -2.66f, -5.33f, -4f, -8f, -4f)
            close()
            moveTo(16.76f, 5.36f)
            lineToRelative(-1.68f, 1.69f)
            curveToRelative(0.84f, 1.18f, 0.84f, 2.71f, 0f, 3.89f)
            lineToRelative(1.68f, 1.69f)
            curveToRelative(2.02f, -2.02f, 2.02f, -5.07f, 0f, -7.27f)
            close()
            moveTo(20.07f, 2f)
            lineToRelative(-1.63f, 1.63f)
            curveToRelative(2.77f, 3.02f, 2.77f, 7.56f, 0f, 10.74f)
            lineTo(20.07f, 16f)
            curveToRelative(3.9f, -3.89f, 3.91f, -9.95f, 0f, -14f)
            close()
        }
    }

    val Tune: ImageVector by lazy {
        materialIcon("Tune") {
            moveTo(3f, 17f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(6f)
            verticalLineToRelative(-2f)
            lineTo(3f, 17f)
            close()
            moveTo(3f, 5f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(10f)
            lineTo(13f, 5f)
            lineTo(3f, 5f)
            close()
            moveTo(13f, 21f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(8f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-8f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(2f)
            close()
            moveTo(7f, 9f)
            verticalLineToRelative(2f)
            lineTo(3f, 11f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(2f)
            lineTo(9f, 9f)
            lineTo(7f, 9f)
            close()
            moveTo(21f, 13f)
            verticalLineToRelative(-2f)
            lineTo(11f, 11f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(10f)
            close()
            moveTo(15f, 9f)
            horizontalLineToRelative(2f)
            lineTo(17f, 7f)
            horizontalLineToRelative(4f)
            lineTo(21f, 5f)
            horizontalLineToRelative(-4f)
            lineTo(17f, 3f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(6f)
            close()
        }
    }

    val VolumeUp: ImageVector by lazy {
        materialIcon("VolumeUp", autoMirror = true) {
            moveTo(3f, 9f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(4f)
            lineToRelative(5f, 5f)
            lineTo(12f, 4f)
            lineTo(7f, 9f)
            lineTo(3f, 9f)
            close()
            moveTo(16.5f, 12f)
            curveToRelative(0f, -1.77f, -1.02f, -3.29f, -2.5f, -4.03f)
            verticalLineToRelative(8.05f)
            curveToRelative(1.48f, -0.73f, 2.5f, -2.25f, 2.5f, -4.02f)
            close()
            moveTo(14f, 3.23f)
            verticalLineToRelative(2.06f)
            curveToRelative(2.89f, 0.86f, 5f, 3.54f, 5f, 6.71f)
            reflectiveCurveToRelative(-2.11f, 5.85f, -5f, 6.71f)
            verticalLineToRelative(2.06f)
            curveToRelative(4.01f, -0.91f, 7f, -4.49f, 7f, -8.77f)
            reflectiveCurveToRelative(-2.99f, -7.86f, -7f, -8.77f)
            close()
        }
    }

    val VolumeOff: ImageVector by lazy {
        materialIcon("VolumeOff", autoMirror = true) {
            moveTo(16.5f, 12f)
            curveToRelative(0f, -1.77f, -1.02f, -3.29f, -2.5f, -4.03f)
            verticalLineToRelative(2.21f)
            lineToRelative(2.45f, 2.45f)
            curveToRelative(0.03f, -0.2f, 0.05f, -0.41f, 0.05f, -0.63f)
            close()
            moveTo(19f, 12f)
            curveToRelative(0f, 0.94f, -0.2f, 1.82f, -0.54f, 2.64f)
            lineToRelative(1.51f, 1.51f)
            curveTo(20.63f, 14.91f, 21f, 13.5f, 21f, 12f)
            curveToRelative(0f, -4.28f, -2.99f, -7.86f, -7f, -8.77f)
            verticalLineToRelative(2.06f)
            curveToRelative(2.89f, 0.86f, 5f, 3.54f, 5f, 6.71f)
            close()
            moveTo(4.27f, 3f)
            lineTo(3f, 4.27f)
            lineTo(7.73f, 9f)
            lineTo(3f, 9f)
            verticalLineToRelative(6f)
            horizontalLineToRelative(4f)
            lineToRelative(5f, 5f)
            verticalLineToRelative(-6.73f)
            lineToRelative(4.25f, 4.25f)
            curveToRelative(-0.67f, 0.52f, -1.42f, 0.93f, -2.25f, 1.18f)
            verticalLineToRelative(2.06f)
            curveToRelative(1.38f, -0.31f, 2.63f, -0.95f, 3.69f, -1.81f)
            lineTo(19.73f, 21f)
            lineTo(21f, 19.73f)
            lineToRelative(-9f, -9f)
            lineTo(4.27f, 3f)
            close()
            moveTo(12f, 4f)
            lineTo(9.91f, 6.09f)
            lineTo(12f, 8.18f)
            lineTo(12f, 4f)
            close()
        }
    }

    val Stop: ImageVector by lazy {
        materialIcon("Stop") {
            moveTo(6f, 6f)
            horizontalLineToRelative(12f)
            verticalLineToRelative(12f)
            horizontalLineTo(6f)
            close()
        }
    }

    val Key: ImageVector by lazy {
        materialIcon("Key") {
            moveTo(12.65f, 10f)
            curveTo(11.83f, 7.67f, 9.61f, 6f, 7f, 6f)
            curveToRelative(-3.31f, 0f, -6f, 2.69f, -6f, 6f)
            reflectiveCurveToRelative(2.69f, 6f, 6f, 6f)
            curveToRelative(2.61f, 0f, 4.83f, -1.67f, 5.65f, -4f)
            horizontalLineTo(17f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-4f)
            horizontalLineTo(12.65f)
            close()
            moveTo(7f, 14f)
            curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
            reflectiveCurveToRelative(0.9f, -2f, 2f, -2f)
            reflectiveCurveToRelative(2f, 0.9f, 2f, 2f)
            reflectiveCurveToRelative(-0.9f, 2f, -2f, 2f)
            close()
        }
    }

    val Visibility: ImageVector by lazy {
        materialIcon("Visibility") {
            moveTo(12f, 4.5f)
            curveTo(7f, 4.5f, 2.73f, 7.61f, 1f, 12f)
            curveToRelative(1.73f, 4.39f, 6f, 7.5f, 11f, 7.5f)
            reflectiveCurveToRelative(9.27f, -3.11f, 11f, -7.5f)
            curveToRelative(-1.73f, -4.39f, -6f, -7.5f, -11f, -7.5f)
            close()
            moveTo(12f, 17f)
            curveToRelative(-2.76f, 0f, -5f, -2.24f, -5f, -5f)
            reflectiveCurveToRelative(2.24f, -5f, 5f, -5f)
            reflectiveCurveToRelative(5f, 2.24f, 5f, 5f)
            reflectiveCurveToRelative(-2.24f, 5f, -5f, 5f)
            close()
            moveTo(12f, 9f)
            curveToRelative(-1.66f, 0f, -3f, 1.34f, -3f, 3f)
            reflectiveCurveToRelative(1.34f, 3f, 3f, 3f)
            reflectiveCurveToRelative(3f, -1.34f, 3f, -3f)
            reflectiveCurveToRelative(-1.34f, -3f, -3f, -3f)
            close()
        }
    }

    val VisibilityOff: ImageVector by lazy {
        materialIcon("VisibilityOff") {
            moveTo(12f, 7f)
            curveToRelative(2.76f, 0f, 5f, 2.24f, 5f, 5f)
            curveToRelative(0f, 0.65f, -0.13f, 1.26f, -0.36f, 1.83f)
            lineToRelative(2.92f, 2.92f)
            curveToRelative(1.51f, -1.26f, 2.7f, -2.89f, 3.43f, -4.75f)
            curveToRelative(-1.73f, -4.39f, -6f, -7.5f, -11f, -7.5f)
            curveToRelative(-1.4f, 0f, -2.74f, 0.25f, -3.98f, 0.7f)
            lineToRelative(2.16f, 2.16f)
            curveTo(10.74f, 7.13f, 11.35f, 7f, 12f, 7f)
            close()
            moveTo(2f, 4.27f)
            lineToRelative(2.28f, 2.28f)
            lineToRelative(0.46f, 0.46f)
            curveTo(3.08f, 8.3f, 1.78f, 10.02f, 1f, 12f)
            curveToRelative(1.73f, 4.39f, 6f, 7.5f, 11f, 7.5f)
            curveToRelative(1.55f, 0f, 3.03f, -0.3f, 4.38f, -0.84f)
            lineToRelative(0.42f, 0.42f)
            lineTo(19.73f, 22f)
            lineTo(21f, 20.73f)
            lineTo(3.27f, 3f)
            lineTo(2f, 4.27f)
            close()
            moveTo(7.53f, 9.8f)
            lineToRelative(1.55f, 1.55f)
            curveToRelative(-0.05f, 0.21f, -0.08f, 0.43f, -0.08f, 0.65f)
            curveToRelative(0f, 1.66f, 1.34f, 3f, 3f, 3f)
            curveToRelative(0.22f, 0f, 0.44f, -0.03f, 0.65f, -0.08f)
            lineToRelative(1.55f, 1.55f)
            curveToRelative(-0.67f, 0.33f, -1.41f, 0.53f, -2.2f, 0.53f)
            curveToRelative(-2.76f, 0f, -5f, -2.24f, -5f, -5f)
            curveToRelative(0f, -0.79f, 0.2f, -1.53f, 0.53f, -2.2f)
            close()
            moveTo(11.84f, 9.02f)
            lineToRelative(3.15f, 3.15f)
            lineToRelative(0.02f, -0.16f)
            curveToRelative(0f, -1.66f, -1.34f, -3f, -3f, -3f)
            lineToRelative(-0.17f, 0.01f)
            close()
        }
    }

    val OpenInNew: ImageVector by lazy {
        materialIcon("OpenInNew", autoMirror = true) {
            moveTo(19f, 19f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(7f)
            verticalLineTo(3f)
            horizontalLineTo(5f)
            curveToRelative(-1.11f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.1f, 0.89f, 2f, 2f, 2f)
            horizontalLineToRelative(14f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineToRelative(-7f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(7f)
            close()
            moveTo(14f, 3f)
            verticalLineToRelative(2f)
            horizontalLineToRelative(3.59f)
            lineToRelative(-9.83f, 9.83f)
            lineToRelative(1.41f, 1.41f)
            lineTo(19f, 6.41f)
            verticalLineTo(10f)
            horizontalLineToRelative(2f)
            verticalLineTo(3f)
            horizontalLineToRelative(-7f)
            close()
        }
    }

    val GraphicEq: ImageVector by lazy {
        materialIcon("GraphicEq") {
            moveTo(7f, 18f)
            horizontalLineToRelative(2f)
            verticalLineTo(6f)
            horizontalLineTo(7f)
            verticalLineToRelative(12f)
            close()
            moveTo(11f, 22f)
            horizontalLineToRelative(2f)
            verticalLineTo(2f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(20f)
            close()
            moveTo(3f, 14f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-4f)
            horizontalLineTo(3f)
            verticalLineToRelative(4f)
            close()
            moveTo(15f, 18f)
            horizontalLineToRelative(2f)
            verticalLineTo(6f)
            horizontalLineToRelative(-2f)
            verticalLineToRelative(12f)
            close()
            moveTo(19f, 10f)
            verticalLineToRelative(4f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-4f)
            horizontalLineToRelative(-2f)
            close()
        }
    }

    val Subtitles: ImageVector by lazy {
        materialIcon("Subtitles") {
            moveTo(20f, 4f)
            horizontalLineTo(4f)
            curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(12f)
            curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
            horizontalLineToRelative(16f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(6f)
            curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
            close()
            moveTo(4f, 12f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(2f)
            horizontalLineTo(4f)
            verticalLineToRelative(-2f)
            close()
            moveTo(14f, 18f)
            horizontalLineTo(4f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(2f)
            close()
            moveTo(20f, 18f)
            horizontalLineToRelative(-4f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(4f)
            verticalLineToRelative(2f)
            close()
            moveTo(20f, 14f)
            horizontalLineTo(10f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(2f)
            close()
        }
    }

    val Save: ImageVector by lazy {
        materialIcon("Save") {
            moveTo(17f, 3f)
            horizontalLineTo(5f)
            curveToRelative(-1.11f, 0f, -2f, 0.9f, -2f, 2f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 1.1f, 0.89f, 2f, 2f, 2f)
            horizontalLineToRelative(14f)
            curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
            verticalLineTo(7f)
            lineToRelative(-4f, -4f)
            close()
            moveTo(12f, 19f)
            curveToRelative(-1.66f, 0f, -3f, -1.34f, -3f, -3f)
            reflectiveCurveToRelative(1.34f, -3f, 3f, -3f)
            reflectiveCurveToRelative(3f, 1.34f, 3f, 3f)
            reflectiveCurveToRelative(-1.34f, 3f, -3f, 3f)
            close()
            moveTo(15f, 9f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(4f)
            close()
        }
    }

    private inline fun materialIcon(
        name: String,
        autoMirror: Boolean = false,
        pathBuilder: androidx.compose.ui.graphics.vector.PathBuilder.() -> Unit
    ): ImageVector {
        return ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
            autoMirror = autoMirror
        ).path(
            fill = SolidColor(Color.Black),
            fillAlpha = 1f,
            stroke = null,
            strokeAlpha = 1f,
            strokeLineWidth = 1f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Bevel,
            strokeLineMiter = 1f,
            pathFillType = PathFillType.NonZero,
            pathBuilder = pathBuilder
        ).build()
    }
}
