import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    kotlin("plugin.compose")
}

group = "com.lt"
version = "1.0-SNAPSHOT"


kotlin {
    jvm {
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "LazyPeopleHttp"
            packageVersion = "1.0.0"
        }
        buildTypes.release.proguard {
            obfuscate.set(true)//开启混淆
            configurationFiles.from(project.file("proguard-rules.pro"))//配置混淆
        }
    }
}
