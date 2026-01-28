plugins {
    id("com.android.application")
    kotlin("android")
}

val gdxVersion = "1.12.1"

// Create natives configuration BEFORE dependencies
val natives by configurations.creating

android {
    namespace = "com.gravityflip.game"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gravityflip.game"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("../assets")
            jniLibs.srcDirs("libs")
        }
    }
}

dependencies {
    implementation(project(":core"))
    
    implementation("com.badlogicgames.gdx:gdx-backend-android:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    
    // Native libraries for all Android architectures
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86")
    natives("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64")
    
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

// Copy native libraries to jniLibs folder
tasks.register("copyAndroidNatives") {
    doFirst {
        val nativesDir = file("libs")
        nativesDir.mkdirs()
        
        natives.files.forEach { jar ->
            val outputDir = when {
                jar.name.contains("armeabi-v7a") -> file("libs/armeabi-v7a")
                jar.name.contains("arm64-v8a") -> file("libs/arm64-v8a")
                jar.name.contains("x86_64") -> file("libs/x86_64")
                jar.name.contains("x86") -> file("libs/x86")
                else -> return@forEach
            }
            outputDir.mkdirs()
            
            copy {
                from(zipTree(jar))
                into(outputDir)
                include("*.so")
            }
        }
    }
}

tasks.matching { it.name.contains("merge") && it.name.contains("JniLibFolders") }.configureEach {
    dependsOn("copyAndroidNatives")
}
