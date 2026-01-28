plugins {
    kotlin("jvm")
}

val gdxVersion = "1.12.1"

dependencies {
    implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

kotlin {
    jvmToolchain(17)
}

