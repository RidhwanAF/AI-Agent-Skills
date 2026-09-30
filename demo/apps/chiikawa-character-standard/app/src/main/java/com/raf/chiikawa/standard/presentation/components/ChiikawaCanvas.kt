package com.raf.chiikawa.standard.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.raf.chiikawa.standard.domain.CalculateDeformationUseCase
import com.raf.chiikawa.standard.domain.PhysicsVector
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.coroutines.launch

@Composable
fun StretchyChiikawaView(
    modifier: Modifier = Modifier,
    characterSize: Dp = 260.dp,
    maxDragDistance: Dp = 120.dp
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val maxDragPx = with(density) { maxDragDistance.toPx() }
    val calculateDeformation = remember { CalculateDeformationUseCase() }

    val dragOffsetAnim = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var lastHapticThreshold by remember { mutableFloatStateOf(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "breath_anim")
    val breathScaleY by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    val currentOffset = dragOffsetAnim.value
    val deformation = calculateDeformation(
        rawOffset = PhysicsVector(currentOffset.x, currentOffset.y),
        maxDragPx = maxDragPx
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .size(characterSize)
                .graphicsLayer {
                    rotationX = deformation.tiltXDegrees
                    rotationY = deformation.tiltYDegrees
                    cameraDistance = 14f * density.density
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { lastHapticThreshold = 0f },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val nextOffset = PhysicsVector(
                                x = dragOffsetAnim.value.x + dragAmount.x,
                                y = dragOffsetAnim.value.y + dragAmount.y
                            )
                            val def = calculateDeformation(nextOffset, maxDragPx)

                            if (def.stretchRatio - lastHapticThreshold >= 0.25f) {
                                lastHapticThreshold = def.stretchRatio
                                if (def.stretchRatio >= 0.95f) {
                                    haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
                                } else {
                                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                                }
                            }

                            coroutineScope.launch {
                                dragOffsetAnim.snapTo(Offset(def.dragOffset.x, def.dragOffset.y))
                            }
                        },
                        onDragEnd = {
                            haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            coroutineScope.launch {
                                dragOffsetAnim.animateTo(
                                    targetValue = Offset.Zero,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        },
                        onDragCancel = {
                            coroutineScope.launch {
                                dragOffsetAnim.animateTo(
                                    targetValue = Offset.Zero,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                )
                            }
                        }
                    )
                }
        ) {
            val baseCenter = Offset(size.width / 2f, size.height / 2f + 20.dp.toPx())
            val headCenter = baseCenter + currentOffset * 0.75f

            drawShadow(baseCenter, deformation.stretchRatio)
            drawElasticBody(baseCenter, headCenter, deformation.stretchRatio, breathScaleY)
            drawHeadAndFace(headCenter, deformation.stretchRatio, currentOffset)
        }
    }
}

private fun DrawScope.drawShadow(baseCenter: Offset, stretchRatio: Float) {
    val shadowWidth = (140.dp.toPx()) * (1f - stretchRatio * 0.35f)
    val shadowHeight = (30.dp.toPx()) * (1f - stretchRatio * 0.5f)
    val shadowAlpha = (0.22f * (1f - stretchRatio * 0.45f)).coerceAtLeast(0.05f)

    drawOval(
        color = Color(0xFF6B584E).copy(alpha = shadowAlpha),
        topLeft = Offset(baseCenter.x - shadowWidth / 2f, baseCenter.y + 60.dp.toPx()),
        size = Size(shadowWidth, shadowHeight)
    )
}

private fun DrawScope.drawElasticBody(
    baseCenter: Offset,
    headCenter: Offset,
    stretchRatio: Float,
    breathScaleY: Float
) {
    val outlineColor = Color(0xFF382218)
    val bodyColor = Color(0xFFFFFDF8)
    val strokeWidth = 3.5.dp.toPx()

    val baseRadiusX = (65.dp.toPx()) * (1f - stretchRatio * 0.25f)
    val baseRadiusY = (35.dp.toPx()) * breathScaleY

    val angle = atan2(headCenter.y - baseCenter.y, headCenter.x - baseCenter.x)
    val perpAngle = angle + (Math.PI / 2.0).toFloat()
    val neckWidth = (60.dp.toPx()) * (1f - stretchRatio * 0.45f)

    val leftBase = baseCenter + Offset(cos(perpAngle) * baseRadiusX, sin(perpAngle) * baseRadiusX)
    val rightBase = baseCenter - Offset(cos(perpAngle) * baseRadiusX, sin(perpAngle) * baseRadiusX)
    val leftHead = headCenter + Offset(cos(perpAngle) * neckWidth, sin(perpAngle) * neckWidth)
    val rightHead = headCenter - Offset(cos(perpAngle) * neckWidth, sin(perpAngle) * neckWidth)

    val midAnchor = Offset((baseCenter.x + headCenter.x) / 2f, (baseCenter.y + headCenter.y) / 2f)

    val bodyPath = Path().apply {
        moveTo(leftBase.x, leftBase.y)
        quadraticTo(midAnchor.x, midAnchor.y, leftHead.x, leftHead.y)
        lineTo(rightHead.x, rightHead.y)
        quadraticTo(midAnchor.x, midAnchor.y, rightBase.x, rightBase.y)
        close()
    }

    drawPath(path = bodyPath, color = bodyColor)
    drawPath(path = bodyPath, color = outlineColor, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))

    drawOval(color = bodyColor, topLeft = Offset(baseCenter.x - baseRadiusX, baseCenter.y - baseRadiusY), size = Size(baseRadiusX * 2, baseRadiusY * 2))
    drawOval(color = outlineColor, topLeft = Offset(baseCenter.x - baseRadiusX, baseCenter.y - baseRadiusY), size = Size(baseRadiusX * 2, baseRadiusY * 2), style = Stroke(strokeWidth))

    drawTinyFoot(Offset(baseCenter.x - 28.dp.toPx(), baseCenter.y + 22.dp.toPx()))
    drawTinyFoot(Offset(baseCenter.x + 28.dp.toPx(), baseCenter.y + 22.dp.toPx()))
}

private fun DrawScope.drawHeadAndFace(headCenter: Offset, stretchRatio: Float, dragOffset: Offset) {
    val outlineColor = Color(0xFF382218)
    val bodyColor = Color(0xFFFFFDF8)
    val blushColor = Color(0xFFFFB2BF)
    val earInnerColor = Color(0xFFFFD4DC)
    val strokeWidth = 3.5.dp.toPx()

    val headRadiusX = (72.dp.toPx()) * (1f - stretchRatio * 0.12f)
    val headRadiusY = (66.dp.toPx()) * (1f + stretchRatio * 0.18f)

    val earOffsetY = -52.dp.toPx()
    val earSpacingX = 46.dp.toPx()
    drawCuteEar(headCenter + Offset(-earSpacingX, earOffsetY), 17.dp.toPx(), bodyColor, earInnerColor, outlineColor, strokeWidth)
    drawCuteEar(headCenter + Offset(earSpacingX, earOffsetY), 17.dp.toPx(), bodyColor, earInnerColor, outlineColor, strokeWidth)

    drawOval(color = bodyColor, topLeft = Offset(headCenter.x - headRadiusX, headCenter.y - headRadiusY), size = Size(headRadiusX * 2, headRadiusY * 2))
    drawOval(color = outlineColor, topLeft = Offset(headCenter.x - headRadiusX, headCenter.y - headRadiusY), size = Size(headRadiusX * 2, headRadiusY * 2), style = Stroke(strokeWidth))

    val faceCenter = headCenter + dragOffset * 0.12f

    val cheekDistanceX = 44.dp.toPx()
    val cheekY = faceCenter.y + 8.dp.toPx()
    drawCircle(color = blushColor.copy(alpha = 0.75f), radius = 13.dp.toPx(), center = Offset(faceCenter.x - cheekDistanceX, cheekY))
    drawCircle(color = blushColor.copy(alpha = 0.75f), radius = 13.dp.toPx(), center = Offset(faceCenter.x + cheekDistanceX, cheekY))

    val eyeDistanceX = 26.dp.toPx()
    val eyeY = faceCenter.y - 4.dp.toPx()
    drawChiikawaEye(Offset(faceCenter.x - eyeDistanceX, eyeY), 7.5.dp.toPx())
    drawChiikawaEye(Offset(faceCenter.x + eyeDistanceX, eyeY), 7.5.dp.toPx())

    val mouthY = faceCenter.y + 7.dp.toPx()
    val mouthPath = Path().apply {
        moveTo(faceCenter.x - 9.dp.toPx(), mouthY)
        quadraticTo(faceCenter.x - 4.5.dp.toPx(), mouthY + 5.5.dp.toPx(), faceCenter.x, mouthY)
        quadraticTo(faceCenter.x + 4.5.dp.toPx(), mouthY + 5.5.dp.toPx(), faceCenter.x + 9.dp.toPx(), mouthY)
    }
    drawPath(path = mouthPath, color = outlineColor, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round))
}

private fun DrawScope.drawCuteEar(center: Offset, radius: Float, bodyColor: Color, innerColor: Color, outlineColor: Color, strokeWidth: Float) {
    drawCircle(color = bodyColor, radius = radius, center = center)
    drawCircle(color = innerColor, radius = radius * 0.58f, center = center)
    drawCircle(color = outlineColor, radius = radius, center = center, style = Stroke(strokeWidth))
}

private fun DrawScope.drawChiikawaEye(center: Offset, radius: Float) {
    drawCircle(color = Color(0xFF281810), radius = radius, center = center)
    drawCircle(color = Color.White, radius = radius * 0.42f, center = Offset(center.x - radius * 0.28f, center.y - radius * 0.28f))
    drawCircle(color = Color.White.copy(alpha = 0.85f), radius = radius * 0.22f, center = Offset(center.x + radius * 0.32f, center.y + radius * 0.25f))
}

private fun DrawScope.drawTinyFoot(center: Offset) {
    val footWidth = 14.dp.toPx()
    val footHeight = 9.dp.toPx()
    drawOval(color = Color(0xFFFFFDF8), topLeft = Offset(center.x - footWidth / 2f, center.y - footHeight / 2f), size = Size(footWidth, footHeight))
    drawOval(color = Color(0xFF382218), topLeft = Offset(center.x - footWidth / 2f, center.y - footHeight / 2f), size = Size(footWidth, footHeight), style = Stroke(2.5.dp.toPx()))
}
