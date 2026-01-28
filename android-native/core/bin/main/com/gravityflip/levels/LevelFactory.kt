package com.gravityflip.levels

import com.gravityflip.GameConfig

/**
 * Creates built-in levels for the game.
 */
object LevelFactory {
    
    /**
     * Create Level 1 - Tutorial level introducing basic mechanics.
     */
    fun createLevel1(): Level {
        val level = Level("Level 1 - Awakening")
        
        // Build floor
        for (x in 0 until GameConfig.GRID_WIDTH) {
            level.setTile(x, 0, TileType.SOLID)  // Bottom floor
            level.setTile(x, GameConfig.GRID_HEIGHT - 1, TileType.SOLID)  // Top ceiling
        }
        
        // Build walls
        for (y in 0 until GameConfig.GRID_HEIGHT) {
            level.setTile(0, y, TileType.SOLID)  // Left wall
            level.setTile(GameConfig.GRID_WIDTH - 1, y, TileType.SOLID)  // Right wall
        }
        
        // Player spawn (bottom left area)
        level.setTile(3, 1, TileType.SPAWN)
        
        // Simple platform puzzle
        // First platform - float above ground
        for (x in 8..12) {
            level.setTile(x, 4, TileType.SOLID)
        }
        
        // Second platform - higher and to the right
        for (x in 15..19) {
            level.setTile(x, 8, TileType.SOLID)
        }
        
        // Third platform - requires gravity flip up
        for (x in 22..26) {
            level.setTile(x, 12, TileType.SOLID)
        }
        
        // Add some vertical obstacles
        for (y in 1..6) {
            level.setTile(14, y, TileType.SOLID)
        }
        
        // Goal at top right
        level.setTile(28, 20, TileType.GOAL)
        
        // Platform near goal
        for (x in 26..29) {
            level.setTile(x, 19, TileType.SOLID)
        }
        
        return level
    }
    
    /**
     * Create Level 2 - Introduces spikes.
     */
    fun createLevel2(): Level {
        val level = Level("Level 2 - Danger Zone")
        
        // Build boundaries
        for (x in 0 until GameConfig.GRID_WIDTH) {
            level.setTile(x, 0, TileType.SOLID)
            level.setTile(x, GameConfig.GRID_HEIGHT - 1, TileType.SOLID)
        }
        for (y in 0 until GameConfig.GRID_HEIGHT) {
            level.setTile(0, y, TileType.SOLID)
            level.setTile(GameConfig.GRID_WIDTH - 1, y, TileType.SOLID)
        }
        
        // Spawn
        level.setTile(3, 1, TileType.SPAWN)
        
        // Platforms with spike gaps
        for (x in 5..10) {
            level.setTile(x, 3, TileType.SOLID)
        }
        
        // Spike pit
        for (x in 11..14) {
            level.setTile(x, 1, TileType.SPIKE)
        }
        
        // Continue platform
        for (x in 15..20) {
            level.setTile(x, 3, TileType.SOLID)
        }
        
        // Upper section
        for (x in 10..18) {
            level.setTile(x, 10, TileType.SOLID)
        }
        
        // Ceiling spikes
        for (x in 12..16) {
            level.setTile(x, GameConfig.GRID_HEIGHT - 2, TileType.SPIKE)
        }
        
        // Goal
        level.setTile(25, 10, TileType.GOAL)
        for (x in 23..27) {
            level.setTile(x, 9, TileType.SOLID)
        }
        
        return level
    }
    
    /**
     * Create Level 3 - More complex gravity puzzles.
     */
    fun createLevel3(): Level {
        val level = Level("Level 3 - Zero G")
        
        // Boundaries
        for (x in 0 until GameConfig.GRID_WIDTH) {
            level.setTile(x, 0, TileType.SOLID)
            level.setTile(x, GameConfig.GRID_HEIGHT - 1, TileType.SOLID)
        }
        for (y in 0 until GameConfig.GRID_HEIGHT) {
            level.setTile(0, y, TileType.SOLID)
            level.setTile(GameConfig.GRID_WIDTH - 1, y, TileType.SOLID)
        }
        
        // Spawn at bottom left
        level.setTile(2, 1, TileType.SPAWN)
        
        // Central maze-like structure
        // Vertical pillars
        for (y in 5..15) {
            level.setTile(10, y, TileType.SOLID)
            level.setTile(20, y, TileType.SOLID)
        }
        
        // Horizontal platforms at different heights
        for (x in 5..9) {
            level.setTile(x, 8, TileType.SOLID)
        }
        for (x in 11..19) {
            level.setTile(x, 12, TileType.SOLID)
        }
        for (x in 21..28) {
            level.setTile(x, 8, TileType.SOLID)
        }
        
        // Spike hazards
        for (x in 13..17) {
            level.setTile(x, 7, TileType.SPIKE)
        }
        
        // Goal at top right
        level.setTile(28, 20, TileType.GOAL)
        for (x in 26..29) {
            level.setTile(x, 19, TileType.SOLID)
        }
        
        return level
    }
    
    /**
     * Get a level by number.
     */
    fun getLevel(levelNumber: Int): Level? {
        return when (levelNumber) {
            1 -> createLevel1()
            2 -> createLevel2()
            3 -> createLevel3()
            else -> null
        }
    }
    
    /**
     * Get total number of available levels.
     */
    fun getTotalLevels(): Int = 3
}
