package com.gravityflip

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.gravityflip.screens.GameScreen

/**
 * Main game class for Gravity Flip.
 * Manages screen transitions and shared rendering resources.
 */
class GravityFlipGame : Game() {
    
    // Shared renderers (created once, used across screens)
    lateinit var batch: SpriteBatch
        private set
    lateinit var shapeRenderer: ShapeRenderer
        private set
    
    override fun create() {
        // Initialize shared rendering resources
        batch = SpriteBatch()
        shapeRenderer = ShapeRenderer()
        
        // Log game start
        Gdx.app.log("GravityFlip", "Game initialized - Version 1.0.0")
        
        // Start with the game screen (will add menu later)
        setScreen(GameScreen(this))
    }
    
    override fun render() {
        super.render()
    }
    
    override fun dispose() {
        batch.dispose()
        shapeRenderer.dispose()
        screen?.dispose()
        
        Gdx.app.log("GravityFlip", "Game disposed")
    }
}
