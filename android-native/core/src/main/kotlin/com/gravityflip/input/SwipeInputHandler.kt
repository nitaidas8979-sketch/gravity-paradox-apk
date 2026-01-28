package com.gravityflip.input

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2
import com.gravityflip.physics.GravityDirection

/**
 * Handles touch/swipe input for gravity control.
 */
class SwipeInputHandler(
    private val onGravityFlip: (GravityDirection) -> Unit
) : InputAdapter() {
    
    private val touchStart = Vector2()
    private var isTouching = false
    
    companion object {
        // Minimum swipe distance in pixels (scaled by screen density)
        const val MIN_SWIPE_DISTANCE = 50f
        
        // Angle thresholds for direction detection
        const val DIAGONAL_THRESHOLD = 45f
    }
    
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (pointer == 0) {  // Only handle first touch
            touchStart.set(screenX.toFloat(), screenY.toFloat())
            isTouching = true
        }
        return true
    }
    
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (pointer == 0 && isTouching) {
            isTouching = false
            
            val touchEnd = Vector2(screenX.toFloat(), screenY.toFloat())
            val swipe = touchEnd.sub(touchStart)
            
            // Check if swipe distance is sufficient
            if (swipe.len() >= MIN_SWIPE_DISTANCE) {
                val direction = detectSwipeDirection(swipe)
                direction?.let { onGravityFlip(it) }
            }
        }
        return true
    }
    
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return true
    }
    
    /**
     * Detect the swipe direction from the swipe vector.
     */
    private fun detectSwipeDirection(swipe: Vector2): GravityDirection? {
        val angle = swipe.angleDeg()
        
        // Normalize angle to 0-360
        val normalizedAngle = if (angle < 0) angle + 360 else angle
        
        // Determine direction based on angle
        // Note: Screen Y is inverted (0 at top), so we need to adjust
        return when {
            // Right: -45 to 45 degrees
            normalizedAngle < DIAGONAL_THRESHOLD || normalizedAngle >= 360 - DIAGONAL_THRESHOLD -> {
                GravityDirection.RIGHT
            }
            // Up: 45 to 135 degrees (screen Y inverted, so this is swipe up on screen)
            normalizedAngle in DIAGONAL_THRESHOLD..(180 - DIAGONAL_THRESHOLD) -> {
                // Swipe up on screen = gravity UP (screen coords are inverted)
                GravityDirection.DOWN  // LibGDX Y is bottom-up, screen Y is top-down
            }
            // Left: 135 to 225 degrees
            normalizedAngle in (180 - DIAGONAL_THRESHOLD)..(180 + DIAGONAL_THRESHOLD) -> {
                GravityDirection.LEFT
            }
            // Down: 225 to 315 degrees
            else -> {
                GravityDirection.UP
            }
        }
    }
    
    /**
     * Alternative: Get direction from velocity-like swipe for more intuitive control.
     * Swipe in direction you want to "throw" gravity.
     */
    fun detectSwipeDirectionIntuitive(swipe: Vector2): GravityDirection {
        val absX = kotlin.math.abs(swipe.x)
        val absY = kotlin.math.abs(swipe.y)
        
        return if (absX > absY) {
            // Horizontal swipe
            if (swipe.x > 0) GravityDirection.RIGHT else GravityDirection.LEFT
        } else {
            // Vertical swipe (note: screen Y is inverted in touch coords)
            // Swipe down on screen (positive Y in screen coords) = gravity DOWN
            if (swipe.y > 0) GravityDirection.DOWN else GravityDirection.UP
        }
    }
}
