package com.gravityflip.game

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.gravityflip.GravityFlipGame

/**
 * Android launcher activity for Gravity Flip game.
 * Initializes LibGDX with optimal settings for mobile gaming.
 */
class AndroidLauncher : AndroidApplication() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val config = AndroidApplicationConfiguration().apply {
            // Use immersive full-screen mode
            useImmersiveMode = true
            
            // Enable multi-touch for potential future gestures
            numSamples = 2
            
            // Use OpenGL ES 2.0 for wide compatibility (Android 7+)
            // LibGDX handles this automatically
        }
        
        initialize(GravityFlipGame(), config)
    }
}
