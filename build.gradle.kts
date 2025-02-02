import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "1.9.21"
    id("org.jetbrains.compose") version "1.5.11"
    id("app.cash.sqldelight") version "2.0.1"
}

group = "org.zen.doggo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Compose Desktop
    implementation(compose.desktop.currentOs)
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
    
    // Material Icons
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    
    // SQLDelight
    implementation("app.cash.sqldelight:runtime-jvm:2.0.1")
    implementation("app.cash.sqldelight:sqlite-driver:2.0.1")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ZenDoggo"
            packageVersion = "1.0.0"
        }
    }
}

sqldelight {
    databases {
        create("ZenDoggoDatabase") {
            packageName.set("org.zendoggo.db")
        }
    }
}
