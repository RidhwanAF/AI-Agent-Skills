package com.raf.chiikawa.standard.domain

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Pure Domain representation of 2D coordinates and deformation metrics.
 * 100% decoupled from Android framework packages for KMP compliance.
 */
data class PhysicsVector(
    val x: Float = 0f,
    val y: Float = 0f
) {
    val magnitude: Float get() = hypot(x, y)
    val angle: Float get() = atan2(y, x)

    operator fun plus(other: PhysicsVector): PhysicsVector =
        PhysicsVector(x + other.x, y + other.y)

    operator fun times(factor: Float): PhysicsVector =
        PhysicsVector(x * factor, y * factor)
}

/**
 * Calculated elastic body deformation state.
 */
data class CharacterDeformation(
    val dragOffset: PhysicsVector = PhysicsVector(),
    val stretchRatio: Float = 0f,
    val tiltXDegrees: Float = 0f,
    val tiltYDegrees: Float = 0f
)

/**
 * Pure domain interactor calculating squash-and-stretch physics.
 */
class CalculateDeformationUseCase {
    operator fun invoke(rawOffset: PhysicsVector, maxDragPx: Float): CharacterDeformation {
        val dist = rawOffset.magnitude
        val dampedDist = dist.coerceAtMost(maxDragPx)
        val angle = rawOffset.angle

        val clampedOffset = PhysicsVector(
            x = cos(angle) * dampedDist,
            y = sin(angle) * dampedDist
        )
        val stretch = (dampedDist / maxDragPx).coerceIn(0f, 1f)

        return CharacterDeformation(
            dragOffset = clampedOffset,
            stretchRatio = stretch,
            tiltXDegrees = (-clampedOffset.y / maxDragPx) * 18f,
            tiltYDegrees = (clampedOffset.x / maxDragPx) * 18f
        )
    }
}
