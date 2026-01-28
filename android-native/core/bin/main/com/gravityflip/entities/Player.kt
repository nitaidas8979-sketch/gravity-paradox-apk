package com.gravityflip.entities

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.gravityflip.GameConfig
import com.gravityflip.physics.GravityDirection

/**
 * Player entity - the cube that the player controls.
 * Responds to gravity changes and handles physics.
 */
class Player(
    startX: Float,
    startY: Float
) {
    // Position and size
    val position = Vector2(startX, startY)
    val size = GameConfig.TILE_SIZE - 4f  // Slightly smaller than tile for visual clarity
    
    // Physics
    val velocity = Vector2(0f, 0f)
    private val maxVelocity = GameConfig.MAX_VELOCITY
    
    // State
    var isGrounded = false
        private set
    var isAlive = true
        private set
    
    // Visual
    private var targetRotation = 0f
    private var currentRotation = 0f
    private val rotationSpeed = 720f // degrees per second
    
    // Collision bounds
    val bounds: Rectangle
        get() = Rectangle(position.x, position.y, size, size)
    
    // Spawn point for respawning
    private val spawnPoint = Vector2(startX, startY)
    
    /**
     * Update player physics and state.
     */
    fun update(deltaTime: Float, gravity: Vector2, gravityDirection: GravityDirection) {
        if (!isAlive) return
        
        // Apply gravity to velocity
        velocity.x += gravity.x * deltaTime
        velocity.y += gravity.y * deltaTime
        
        // Clamp velocity
        velocity.x = velocity.x.coerceIn(-maxVelocity, maxVelocity)
        velocity.y = velocity.y.coerceIn(-maxVelocity, maxVelocity)
        
        // Apply velocity to position
        position.x += velocity.x * deltaTime
        position.y += velocity.y * deltaTime
        
        // Update rotation based on gravity direction
        targetRotation = gravityDirection.rotation
        
        // Smooth rotation
        val rotationDiff = targetRotation - currentRotation
        if (kotlin.math.abs(rotationDiff) > 1f) {
            val direction = if (rotationDiff > 0) 1f else -1f
            // Handle wrapping around 360 degrees
            val adjustedDiff = if (kotlin.math.abs(rotationDiff) > 180f) {
                -direction
            } else {
                direction
            }
            currentRotation += adjustedDiff * rotationSpeed * deltaTime
            
            // Normalize rotation
            if (currentRotation > 180f) currentRotation -= 360f
            if (currentRotation < -180f) currentRotation += 360f
        } else {
            currentRotation = targetRotation
        }
    }
    
    /**
     * Handle collision with a solid surface.
     */
    fun handleCollision(
        collisionNormal: Vector2,
        penetration: Float
    ) {
        // Push player out of collision
        position.x += collisionNormal.x * penetration
        position.y += collisionNormal.y * penetration
        
        // Stop velocity in collision direction
        if (collisionNormal.x != 0f) {
            velocity.x = 0f
        }
        if (collisionNormal.y != 0f) {
            velocity.y = 0f
        }
        
        // Check if grounded (collision from below relative to gravity)
        isGrounded = collisionNormal.y > 0f || collisionNormal.y < 0f
    }
    
    /**
     * Kill the player.
     */
    fun die() {
        isAlive = false
        velocity.set(0f, 0f)
    }
    
    /**
     * Respawn at the spawn point.
     */
    fun respawn() {
        position.set(spawnPoint)
        velocity.set(0f, 0f)
        isAlive = true
        isGrounded = false
        currentRotation = 0f
        targetRotation = 0f
    }
    
    /**
     * Set a new spawn point.
     */
    fun setSpawnPoint(x: Float, y: Float) {
        spawnPoint.set(x, y)
    }
    
    /**
     * Render the player.
     */
    fun render(shapeRenderer: ShapeRenderer) {
        if (!isAlive) return
        
        shapeRenderer.identity()
        
        // Translate to center, rotate, then draw
        val centerX = position.x + size / 2
        val centerY = position.y + size / 2
        
        shapeRenderer.translate(centerX, centerY, 0f)
        shapeRenderer.rotate(0f, 0f, 1f, currentRotation)
        shapeRenderer.translate(-centerX, -centerY, 0f)
        
        // Draw player cube with glow effect
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        
        // Outer glow
        shapeRenderer.color = Color(0f, 1f, 1f, 0.3f)
        shapeRenderer.rect(position.x - 2, position.y - 2, size + 4, size + 4)
        
        // Main cube
        shapeRenderer.color = Color.CYAN
        shapeRenderer.rect(position.x, position.y, size, size)
        
        // Inner highlight
        shapeRenderer.color = Color(0.5f, 1f, 1f, 1f)
        shapeRenderer.rect(position.x + 4, position.y + 4, size - 8, size - 8)
        
        shapeRenderer.identity()
    }
    
    /**
     * Get the center position of the player.
     */
    fun getCenter(): Vector2 = Vector2(position.x + size / 2, position.y + size / 2)
}
