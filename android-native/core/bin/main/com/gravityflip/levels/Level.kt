package com.gravityflip.levels

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.gravityflip.GameConfig

/**
 * Tile types in the game.
 */
enum class TileType {
    EMPTY,      // No collision
    SOLID,      // Wall/platform
    SPIKE,      // Kills player
    GOAL,       // Level complete
    SPAWN       // Player spawn point
}

/**
 * Represents a single level in the game.
 */
class Level(
    val name: String,
    val width: Int = GameConfig.GRID_WIDTH,
    val height: Int = GameConfig.GRID_HEIGHT
) {
    // Tile grid
    private val tiles = Array(height) { Array(width) { TileType.EMPTY } }
    
    // Player spawn position (in world coordinates)
    var spawnPoint = Vector2(GameConfig.TILE_SIZE * 2, GameConfig.TILE_SIZE * 2)
        private set
    
    // Goal position (in world coordinates)
    var goalPosition = Vector2(GameConfig.TILE_SIZE * (width - 3), GameConfig.TILE_SIZE * (height - 3))
        private set
    
    /**
     * Set a tile at the specified grid position.
     */
    fun setTile(gridX: Int, gridY: Int, type: TileType) {
        if (gridX in 0 until width && gridY in 0 until height) {
            tiles[gridY][gridX] = type
            
            // Update spawn/goal positions if needed
            when (type) {
                TileType.SPAWN -> spawnPoint.set(
                    gridX * GameConfig.TILE_SIZE,
                    gridY * GameConfig.TILE_SIZE
                )
                TileType.GOAL -> goalPosition.set(
                    gridX * GameConfig.TILE_SIZE,
                    gridY * GameConfig.TILE_SIZE
                )
                else -> {}
            }
        }
    }
    
    /**
     * Get the tile at the specified grid position.
     */
    fun getTile(gridX: Int, gridY: Int): TileType {
        return if (gridX in 0 until width && gridY in 0 until height) {
            tiles[gridY][gridX]
        } else {
            TileType.SOLID // Treat out-of-bounds as solid
        }
    }
    
    /**
     * Get the tile at the specified world position.
     */
    fun getTileAtWorldPos(worldX: Float, worldY: Float): TileType {
        val gridX = (worldX / GameConfig.TILE_SIZE).toInt()
        val gridY = (worldY / GameConfig.TILE_SIZE).toInt()
        return getTile(gridX, gridY)
    }
    
    /**
     * Get all solid tiles overlapping the given bounds.
     * Returns list of collision rectangles.
     */
    fun getCollidingTiles(bounds: Rectangle): List<TileCollision> {
        val collisions = mutableListOf<TileCollision>()
        
        val startX = (bounds.x / GameConfig.TILE_SIZE).toInt() - 1
        val endX = ((bounds.x + bounds.width) / GameConfig.TILE_SIZE).toInt() + 1
        val startY = (bounds.y / GameConfig.TILE_SIZE).toInt() - 1
        val endY = ((bounds.y + bounds.height) / GameConfig.TILE_SIZE).toInt() + 1
        
        for (gridY in startY..endY) {
            for (gridX in startX..endX) {
                val tile = getTile(gridX, gridY)
                if (tile != TileType.EMPTY) {
                    val tileRect = Rectangle(
                        gridX * GameConfig.TILE_SIZE,
                        gridY * GameConfig.TILE_SIZE,
                        GameConfig.TILE_SIZE,
                        GameConfig.TILE_SIZE
                    )
                    if (bounds.overlaps(tileRect)) {
                        collisions.add(TileCollision(tile, tileRect, gridX, gridY))
                    }
                }
            }
        }
        
        return collisions
    }
    
    /**
     * Render the level.
     */
    fun render(shapeRenderer: ShapeRenderer) {
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled)
        
        for (gridY in 0 until height) {
            for (gridX in 0 until width) {
                val tile = tiles[gridY][gridX]
                if (tile == TileType.EMPTY || tile == TileType.SPAWN) continue
                
                val x = gridX * GameConfig.TILE_SIZE
                val y = gridY * GameConfig.TILE_SIZE
                
                when (tile) {
                    TileType.SOLID -> {
                        // Dark blue platform with subtle border
                        shapeRenderer.color = Color(0.1f, 0.1f, 0.24f, 1f)
                        shapeRenderer.rect(x, y, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE)
                        
                        // Border
                        shapeRenderer.color = Color(0.15f, 0.15f, 0.35f, 1f)
                        shapeRenderer.rect(x + 1, y + 1, GameConfig.TILE_SIZE - 2, GameConfig.TILE_SIZE - 2)
                    }
                    TileType.SPIKE -> {
                        // Red danger tile
                        shapeRenderer.color = Color(0.8f, 0.2f, 0.2f, 1f)
                        shapeRenderer.rect(x, y, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE)
                        
                        // Draw spike triangle pattern
                        shapeRenderer.color = Color(1f, 0.3f, 0.3f, 1f)
                        shapeRenderer.triangle(
                            x, y,
                            x + GameConfig.TILE_SIZE / 2, y + GameConfig.TILE_SIZE,
                            x + GameConfig.TILE_SIZE, y
                        )
                    }
                    TileType.GOAL -> {
                        // Pulsing green goal
                        val pulse = 0.7f + 0.3f * kotlin.math.sin(System.currentTimeMillis() / 200.0).toFloat()
                        shapeRenderer.color = Color(0f, pulse, 0.5f * pulse, 1f)
                        shapeRenderer.rect(x, y, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE)
                        
                        // Inner glow
                        shapeRenderer.color = Color(0.3f, 1f, 0.6f, 0.8f)
                        shapeRenderer.rect(x + 4, y + 4, GameConfig.TILE_SIZE - 8, GameConfig.TILE_SIZE - 8)
                    }
                    else -> {}
                }
            }
        }
    }
}

/**
 * Represents a collision with a tile.
 */
data class TileCollision(
    val type: TileType,
    val bounds: Rectangle,
    val gridX: Int,
    val gridY: Int
)
