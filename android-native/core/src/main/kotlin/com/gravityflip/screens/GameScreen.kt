package com.gravityflip.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import com.gravityflip.GameConfig
import com.gravityflip.GravityFlipGame
import com.gravityflip.entities.Player
import com.gravityflip.input.SwipeInputHandler
import com.gravityflip.levels.Level
import com.gravityflip.levels.LevelFactory
import com.gravityflip.levels.TileType
import com.gravityflip.physics.GravityController

/**
 * Main gameplay screen.
 * Handles game loop, rendering, and input.
 */
class GameScreen(private val game: GravityFlipGame) : Screen {
    
    // Camera and viewport for responsive scaling
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(
        GameConfig.VIRTUAL_WIDTH,
        GameConfig.VIRTUAL_HEIGHT,
        camera
    )
    
    // Game objects
    private val gravityController = GravityController()
    private lateinit var player: Player
    private lateinit var currentLevel: Level
    
    // Input
    private lateinit var inputHandler: SwipeInputHandler
    
    // Game state
    private var currentLevelNumber = 1
    private var isLevelComplete = false
    private var levelCompleteTimer = 0f
    
    // Visual effects
    private val starfield = Starfield()
    
    override fun show() {
        // Center camera
        camera.position.set(
            GameConfig.VIRTUAL_WIDTH / 2,
            GameConfig.VIRTUAL_HEIGHT / 2,
            0f
        )
        camera.update()
        
        // Load first level
        loadLevel(currentLevelNumber)
        
        // Setup input
        inputHandler = SwipeInputHandler { direction ->
            if (!isLevelComplete && gravityController.flipGravity(direction)) {
                Gdx.app.log("GameScreen", "Gravity flipped to: $direction")
            }
        }
        Gdx.input.inputProcessor = inputHandler
        
        Gdx.app.log("GameScreen", "Game screen shown")
    }
    
    private fun loadLevel(levelNumber: Int) {
        val level = LevelFactory.getLevel(levelNumber)
        if (level == null) {
            Gdx.app.log("GameScreen", "No more levels! Game complete!")
            // TODO: Show victory screen
            return
        }
        
        currentLevel = level
        player = Player(level.spawnPoint.x, level.spawnPoint.y)
        gravityController.reset()
        isLevelComplete = false
        levelCompleteTimer = 0f
        
        Gdx.app.log("GameScreen", "Loaded: ${level.name}")
    }
    
    override fun render(delta: Float) {
        // Limit delta to prevent physics issues
        val clampedDelta = minOf(delta, 0.016f)
        
        // Update
        update(clampedDelta)
        
        // Clear screen with deep space color
        ScreenUtils.clear(0.04f, 0.04f, 0.12f, 1f)
        
        // Render
        viewport.apply()
        game.shapeRenderer.projectionMatrix = camera.combined
        
        // Draw background effects
        renderBackground()
        
        // Draw level
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        currentLevel.render(game.shapeRenderer)
        game.shapeRenderer.end()
        
        // Draw player
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        player.render(game.shapeRenderer)
        game.shapeRenderer.end()
        
        // Draw HUD
        renderHUD()
    }
    
    private fun update(delta: Float) {
        if (isLevelComplete) {
            levelCompleteTimer += delta
            if (levelCompleteTimer > 1.5f) {
                // Load next level
                currentLevelNumber++
                loadLevel(currentLevelNumber)
            }
            return
        }
        
        // Update gravity controller
        gravityController.update(delta)
        
        // Update player physics
        val gravity = gravityController.getGravityForce()
        player.update(delta, gravity, gravityController.currentDirection)
        
        // Handle collisions
        handleCollisions()
        
        // Check if player is out of bounds
        if (player.position.y < -100 || player.position.y > GameConfig.VIRTUAL_HEIGHT + 100 ||
            player.position.x < -100 || player.position.x > GameConfig.VIRTUAL_WIDTH + 100) {
            player.respawn()
            gravityController.reset()
        }
    }
    
    private fun handleCollisions() {
        val collisions = currentLevel.getCollidingTiles(player.bounds)
        
        for (collision in collisions) {
            when (collision.type) {
                TileType.SPIKE -> {
                    // Player dies
                    player.respawn()
                    gravityController.reset()
                    return
                }
                TileType.GOAL -> {
                    // Level complete!
                    isLevelComplete = true
                    Gdx.app.log("GameScreen", "Level complete!")
                    return
                }
                TileType.SOLID -> {
                    // Resolve collision
                    resolveCollision(player, collision.bounds)
                }
                else -> {}
            }
        }
    }
    
    private fun resolveCollision(player: Player, tileBounds: com.badlogic.gdx.math.Rectangle) {
        val playerBounds = player.bounds
        
        // Calculate overlap on each axis
        val overlapX = minOf(
            playerBounds.x + playerBounds.width - tileBounds.x,
            tileBounds.x + tileBounds.width - playerBounds.x
        )
        val overlapY = minOf(
            playerBounds.y + playerBounds.height - tileBounds.y,
            tileBounds.y + tileBounds.height - playerBounds.y
        )
        
        // Resolve on the axis with smallest overlap
        if (overlapX < overlapY) {
            // Horizontal collision
            val normal = if (player.position.x < tileBounds.x) {
                Vector2(-1f, 0f)
            } else {
                Vector2(1f, 0f)
            }
            player.handleCollision(normal, overlapX)
        } else {
            // Vertical collision
            val normal = if (player.position.y < tileBounds.y) {
                Vector2(0f, -1f)
            } else {
                Vector2(0f, 1f)
            }
            player.handleCollision(normal, overlapY)
        }
    }
    
    private fun renderBackground() {
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Simple starfield effect
        game.shapeRenderer.color = Color.WHITE
        starfield.render(game.shapeRenderer)
        
        // Grid overlay (subtle)
        game.shapeRenderer.end()
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        game.shapeRenderer.color = Color(0f, 1f, 1f, 0.05f)
        
        val gridSize = 64f
        for (x in 0..(GameConfig.VIRTUAL_WIDTH / gridSize).toInt()) {
            game.shapeRenderer.line(x * gridSize, 0f, x * gridSize, GameConfig.VIRTUAL_HEIGHT)
        }
        for (y in 0..(GameConfig.VIRTUAL_HEIGHT / gridSize).toInt()) {
            game.shapeRenderer.line(0f, y * gridSize, GameConfig.VIRTUAL_WIDTH, y * gridSize)
        }
        
        game.shapeRenderer.end()
    }
    
    private fun renderHUD() {
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        
        // Gravity direction indicator (top-left)
        val indicatorSize = 40f
        val margin = 20f
        
        // Background circle
        game.shapeRenderer.color = Color(0f, 0f, 0f, 0.5f)
        game.shapeRenderer.circle(margin + indicatorSize, GameConfig.VIRTUAL_HEIGHT - margin - indicatorSize, indicatorSize)
        
        // Direction arrow color
        val dirColor = when (gravityController.currentDirection) {
            com.gravityflip.physics.GravityDirection.DOWN -> Color.CYAN
            com.gravityflip.physics.GravityDirection.UP -> Color.MAGENTA
            com.gravityflip.physics.GravityDirection.LEFT -> Color.YELLOW
            com.gravityflip.physics.GravityDirection.RIGHT -> Color.GREEN
        }
        game.shapeRenderer.color = dirColor
        
        // Draw arrow pointing in gravity direction
        val centerX = margin + indicatorSize
        val centerY = GameConfig.VIRTUAL_HEIGHT - margin - indicatorSize
        val arrowSize = 25f
        
        when (gravityController.currentDirection) {
            com.gravityflip.physics.GravityDirection.DOWN -> {
                game.shapeRenderer.triangle(
                    centerX, centerY - arrowSize,
                    centerX - arrowSize / 2, centerY + arrowSize / 2,
                    centerX + arrowSize / 2, centerY + arrowSize / 2
                )
            }
            com.gravityflip.physics.GravityDirection.UP -> {
                game.shapeRenderer.triangle(
                    centerX, centerY + arrowSize,
                    centerX - arrowSize / 2, centerY - arrowSize / 2,
                    centerX + arrowSize / 2, centerY - arrowSize / 2
                )
            }
            com.gravityflip.physics.GravityDirection.LEFT -> {
                game.shapeRenderer.triangle(
                    centerX - arrowSize, centerY,
                    centerX + arrowSize / 2, centerY - arrowSize / 2,
                    centerX + arrowSize / 2, centerY + arrowSize / 2
                )
            }
            com.gravityflip.physics.GravityDirection.RIGHT -> {
                game.shapeRenderer.triangle(
                    centerX + arrowSize, centerY,
                    centerX - arrowSize / 2, centerY - arrowSize / 2,
                    centerX - arrowSize / 2, centerY + arrowSize / 2
                )
            }
        }
        
        // Level complete message
        if (isLevelComplete) {
            game.shapeRenderer.color = Color(0f, 1f, 0.5f, 0.8f)
            game.shapeRenderer.rect(
                GameConfig.VIRTUAL_WIDTH / 2 - 150,
                GameConfig.VIRTUAL_HEIGHT / 2 - 30,
                300f, 60f
            )
        }
        
        game.shapeRenderer.end()
    }
    
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
        camera.position.set(
            GameConfig.VIRTUAL_WIDTH / 2,
            GameConfig.VIRTUAL_HEIGHT / 2,
            0f
        )
    }
    
    override fun pause() {}
    
    override fun resume() {}
    
    override fun hide() {
        Gdx.input.inputProcessor = null
    }
    
    override fun dispose() {
        Gdx.app.log("GameScreen", "Game screen disposed")
    }
}

/**
 * Simple starfield background effect.
 */
private class Starfield {
    private val stars = List(100) {
        Star(
            x = (Math.random() * GameConfig.VIRTUAL_WIDTH).toFloat(),
            y = (Math.random() * GameConfig.VIRTUAL_HEIGHT).toFloat(),
            size = (Math.random() * 2 + 0.5).toFloat(),
            alpha = (Math.random() * 0.5 + 0.2).toFloat()
        )
    }
    
    fun render(shapeRenderer: ShapeRenderer) {
        for (star in stars) {
            val twinkle = 0.8f + 0.2f * kotlin.math.sin(
                System.currentTimeMillis() / 1000.0 * star.twinkleSpeed + star.twinkleOffset
            ).toFloat()
            shapeRenderer.color = Color(1f, 1f, 1f, star.alpha * twinkle)
            shapeRenderer.circle(star.x, star.y, star.size)
        }
    }
    
    private data class Star(
        val x: Float,
        val y: Float,
        val size: Float,
        val alpha: Float,
        val twinkleSpeed: Float = (Math.random() * 2 + 1).toFloat(),
        val twinkleOffset: Float = (Math.random() * Math.PI * 2).toFloat()
    )
}
