package com.gravityflip

/**
 * Game constants matching the original web game specifications.
 */
object GameConfig {
    // Screen dimensions (virtual, will be scaled to fit device)
    const val VIRTUAL_WIDTH = 1024f
    const val VIRTUAL_HEIGHT = 768f
    
    // Tile-based level grid
    const val TILE_SIZE = 32f
    const val GRID_WIDTH = 32  // 1024 / 32
    const val GRID_HEIGHT = 24 // 768 / 32
    
    // Physics constants (from original game)
    const val GRAVITY = 800f
    const val PLAYER_SPEED = 200f
    const val JUMP_FORCE = 350f
    const val MAX_VELOCITY = 600f
    
    // Colors (from original game's theme)
    object Colors {
        // Background
        const val DEEP_SPACE = 0x0A0A1EFF.toInt()
        
        // Primary accent (cyan)
        const val PRIMARY = 0x00FFFFFF.toInt()
        
        // Gravity direction colors
        const val GRAVITY_DOWN = 0x00FFFFFF.toInt()  // Cyan
        const val GRAVITY_UP = 0xFF00FFFF.toInt()    // Magenta
        const val GRAVITY_LEFT = 0xFFFF00FF.toInt()  // Yellow
        const val GRAVITY_RIGHT = 0x00FF00FF.toInt() // Green
        
        // Game elements
        const val PLAYER = 0x00FFFFFF.toInt()        // Cyan
        const val PLATFORM = 0x1A1A3EFF.toInt()      // Dark blue
        const val GOAL = 0x00FF88FF.toInt()          // Bright green
        const val SPIKE = 0xFF4444FF.toInt()         // Red
    }
    
    // Animation timings
    const val GRAVITY_FLIP_DURATION = 0.3f
    const val DEATH_ANIMATION_DURATION = 0.5f
}
