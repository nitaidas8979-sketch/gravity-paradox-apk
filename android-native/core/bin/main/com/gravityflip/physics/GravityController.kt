package com.gravityflip.physics

import com.badlogic.gdx.math.Vector2

/**
 * Gravity direction enum with corresponding vector values.
 */
enum class GravityDirection(val vector: Vector2, val rotation: Float) {
    DOWN(Vector2(0f, -1f), 0f),
    UP(Vector2(0f, 1f), 180f),
    LEFT(Vector2(-1f, 0f), 90f),
    RIGHT(Vector2(1f, 0f), -90f);
    
    /**
     * Get the color associated with this gravity direction (for visual feedback).
     */
    fun getColor(): Int {
        return when (this) {
            DOWN -> com.gravityflip.GameConfig.Colors.GRAVITY_DOWN
            UP -> com.gravityflip.GameConfig.Colors.GRAVITY_UP
            LEFT -> com.gravityflip.GameConfig.Colors.GRAVITY_LEFT
            RIGHT -> com.gravityflip.GameConfig.Colors.GRAVITY_RIGHT
        }
    }
}

/**
 * Controls gravity in the game world.
 * Handles gravity flipping and provides gravity force calculations.
 */
class GravityController {
    
    var currentDirection: GravityDirection = GravityDirection.DOWN
        private set
    
    private var isFlipping = false
    private var flipTimer = 0f
    
    companion object {
        const val FLIP_COOLDOWN = 0.2f // Minimum time between flips
    }
    
    /**
     * Attempt to flip gravity in the specified direction.
     * @return true if flip was successful, false if on cooldown
     */
    fun flipGravity(direction: GravityDirection): Boolean {
        if (isFlipping || direction == currentDirection) {
            return false
        }
        
        currentDirection = direction
        isFlipping = true
        flipTimer = FLIP_COOLDOWN
        
        return true
    }
    
    /**
     * Update the controller (handles cooldown).
     */
    fun update(deltaTime: Float) {
        if (isFlipping) {
            flipTimer -= deltaTime
            if (flipTimer <= 0f) {
                isFlipping = false
            }
        }
    }
    
    /**
     * Get the current gravity vector scaled by gravity force.
     */
    fun getGravityForce(): Vector2 {
        return Vector2(currentDirection.vector).scl(com.gravityflip.GameConfig.GRAVITY)
    }
    
    /**
     * Check if gravity is currently flipping (for animation).
     */
    fun isCurrentlyFlipping(): Boolean = isFlipping
    
    /**
     * Reset to default state.
     */
    fun reset() {
        currentDirection = GravityDirection.DOWN
        isFlipping = false
        flipTimer = 0f
    }
}
