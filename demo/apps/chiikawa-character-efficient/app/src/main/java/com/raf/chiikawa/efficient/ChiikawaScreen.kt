package com.raf.chiikawa.efficient

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlinx.coroutines.launch

@Composable
fun StretchyChiikawaScreen(modifier: Modifier = Modifier, characterSize: Dp = 260.dp, maxDragDistance: Dp = 120.dp) {
    val haptic = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val maxDragPx = with(density) { maxDragDistance.toPx() }

    val dragAnim = remember { Animatable(Offset.Zero, Offset.VectorConverter) }
    var lastTick by remember { mutableFloatStateOf(0f) }

    val breathY by rememberInfiniteTransition(label = "breath").animateFloat(
        initialValue = 0.98f, targetValue = 1.02f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breath"
    )

    val offset = dragAnim.value
    val dist = hypot(offset.x, offset.y)
    val stretch = (dist / maxDragPx).coerceIn(0f, 1f)

    Scaffold(modifier = modifier.fillMaxSize()) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium, color = Color(0xFF382218))
                Text(stringResource(R.string.desc_character_hint), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF8C7A70), modifier = Modifier.padding(bottom = 24.dp))
                Canvas(
                    modifier = Modifier.size(characterSize)
                        .graphicsLayer {
                            rotationX = (-offset.y / maxDragPx) * 18f
                            rotationY = (offset.x / maxDragPx) * 18f
                            cameraDistance = 14f * density.density
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { lastTick = 0f },
                                onDrag = { change, drag ->
                                    change.consume()
                                    val nx = dragAnim.value.x + drag.x
                                    val ny = dragAnim.value.y + drag.y
                                    val d = hypot(nx, ny).coerceAtMost(maxDragPx)
                                    val a = atan2(ny, nx)
                                    val target = Offset(cos(a) * d, sin(a) * d)

                                    val s = d / maxDragPx
                                    if (s - lastTick >= 0.25f) {
                                        lastTick = s
                                        haptic.performHapticFeedback(if (s >= 0.95f) HapticFeedbackType.GestureThresholdActivate else HapticFeedbackType.SegmentTick)
                                    }
                                    scope.launch { dragAnim.snapTo(target) }
                                },
                                onDragEnd = {
                                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                                    scope.launch { dragAnim.animateTo(Offset.Zero, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
                                },
                                onDragCancel = {
                                    scope.launch { dragAnim.animateTo(Offset.Zero, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)) }
                                }
                            )
                        }
                ) {
                    val base = Offset(size.width / 2f, size.height / 2f + 20.dp.toPx())
                    val head = base + offset * 0.75f
                    drawShadow(base, stretch)
                    drawBody(base, head, stretch, breathY)
                    drawHead(head, stretch, offset)
                }
            }
        }
    }
}

private fun DrawScope.drawShadow(base: Offset, stretch: Float) {
    val w = 140.dp.toPx() * (1f - stretch * 0.35f)
    val h = 30.dp.toPx() * (1f - stretch * 0.5f)
    val alpha = (0.22f * (1f - stretch * 0.45f)).coerceAtLeast(0.05f)
    drawOval(Color(0xFF6B584E).copy(alpha = alpha), Offset(base.x - w / 2f, base.y + 60.dp.toPx()), Size(w, h))
}

private fun DrawScope.drawBody(base: Offset, head: Offset, stretch: Float, breathY: Float) {
    val outline = Color(0xFF382218)
    val body = Color(0xFFFFFDF8)
    val sw = 3.5.dp.toPx()
    val bx = 65.dp.toPx() * (1f - stretch * 0.25f)
    val by = 35.dp.toPx() * breathY
    val a = atan2(head.y - base.y, head.x - base.x)
    val pa = a + (Math.PI / 2.0).toFloat()
    val nw = 60.dp.toPx() * (1f - stretch * 0.45f)

    val p = Path().apply {
        moveTo(base.x + cos(pa) * bx, base.y + sin(pa) * bx)
        quadraticTo((base.x + head.x) / 2f, (base.y + head.y) / 2f, head.x + cos(pa) * nw, head.y + sin(pa) * nw)
        lineTo(head.x - cos(pa) * nw, head.y - sin(pa) * nw)
        quadraticTo((base.x + head.x) / 2f, (base.y + head.y) / 2f, base.x - cos(pa) * bx, base.y - sin(pa) * bx)
        close()
    }
    drawPath(p, body)
    drawPath(p, outline, style = Stroke(sw, cap = StrokeCap.Round))
    drawOval(body, Offset(base.x - bx, base.y - by), Size(bx * 2, by * 2))
    drawOval(outline, Offset(base.x - bx, base.y - by), Size(bx * 2, by * 2), style = Stroke(sw))
    drawFoot(Offset(base.x - 28.dp.toPx(), base.y + 22.dp.toPx()))
    drawFoot(Offset(base.x + 28.dp.toPx(), base.y + 22.dp.toPx()))
}

private fun DrawScope.drawHead(head: Offset, stretch: Float, drag: Offset) {
    val outline = Color(0xFF382218)
    val body = Color(0xFFFFFDF8)
    val sw = 3.5.dp.toPx()
    val rx = 72.dp.toPx() * (1f - stretch * 0.12f)
    val ry = 66.dp.toPx() * (1f + stretch * 0.18f)

    drawEar(head + Offset(-46.dp.toPx(), -52.dp.toPx()), 17.dp.toPx(), body, outline, sw)
    drawEar(head + Offset(46.dp.toPx(), -52.dp.toPx()), 17.dp.toPx(), body, outline, sw)
    drawOval(body, Offset(head.x - rx, head.y - ry), Size(rx * 2, ry * 2))
    drawOval(outline, Offset(head.x - rx, head.y - ry), Size(rx * 2, ry * 2), style = Stroke(sw))

    val fc = head + drag * 0.12f
    drawCircle(Color(0xFFFFB2BF).copy(alpha = 0.75f), 13.dp.toPx(), Offset(fc.x - 44.dp.toPx(), fc.y + 8.dp.toPx()))
    drawCircle(Color(0xFFFFB2BF).copy(alpha = 0.75f), 13.dp.toPx(), Offset(fc.x + 44.dp.toPx(), fc.y + 8.dp.toPx()))

    drawEye(Offset(fc.x - 26.dp.toPx(), fc.y - 4.dp.toPx()), 7.5.dp.toPx())
    drawEye(Offset(fc.x + 26.dp.toPx(), fc.y - 4.dp.toPx()), 7.5.dp.toPx())

    val mp = Path().apply {
        moveTo(fc.x - 9.dp.toPx(), fc.y + 7.dp.toPx())
        quadraticTo(fc.x - 4.5.dp.toPx(), fc.y + 12.5.dp.toPx(), fc.x, fc.y + 7.dp.toPx())
        quadraticTo(fc.x + 4.5.dp.toPx(), fc.y + 12.5.dp.toPx(), fc.x + 9.dp.toPx(), fc.y + 7.dp.toPx())
    }
    drawPath(mp, outline, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
}

private fun DrawScope.drawEar(c: Offset, r: Float, b: Color, o: Color, sw: Float) {
    drawCircle(b, r, c)
    drawCircle(Color(0xFFFFD4DC), r * 0.58f, c)
    drawCircle(o, r, c, style = Stroke(sw))
}

private fun DrawScope.drawEye(c: Offset, r: Float) {
    drawCircle(Color(0xFF281810), r, c)
    drawCircle(Color.White, r * 0.42f, Offset(c.x - r * 0.28f, c.y - r * 0.28f))
    drawCircle(Color.White.copy(alpha = 0.85f), r * 0.22f, Offset(c.x + r * 0.32f, c.y + r * 0.25f))
}

private fun DrawScope.drawFoot(c: Offset) {
    val w = 14.dp.toPx()
    val h = 9.dp.toPx()
    drawOval(Color(0xFFFFFDF8), Offset(c.x - w / 2f, c.y - h / 2f), Size(w, h))
    drawOval(Color(0xFF382218), Offset(c.x - w / 2f, c.y - h / 2f), Size(w, h), style = Stroke(2.5.dp.toPx()))
}
