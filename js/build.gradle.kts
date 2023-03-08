plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        browser()
        binaries.executable()
        compilations.all {
            defaultSourceSet.resources.srcDirs("../ComposeViews/resources", "../desktop_app/src/desktopMain/resources")
        }
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":common"))
            }
        }
    }
}

compose.experimental {
    web.application {}
}