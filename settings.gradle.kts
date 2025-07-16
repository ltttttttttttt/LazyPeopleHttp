pluginManagement {
    repositories {
        maven("https://jitpack.io")
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("android").version(extra["kotlin.version"] as String)
        kotlin("plugin.compose").version(extra["kotlin.version"] as String)
        id("com.android.application").version(extra["agp.version"] as String)
        id("com.android.library").version(extra["agp.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        id("com.vanniktech.maven.publish").version(extra["publish.version"] as String)
    }
}

rootProject.name = "LazyPeopleHttp"

include(
    ":android",
    ":desktop",
    ":common",
    ":js",
    ":ios",
    ":LazyPeopleHttp-lib",
    ":LazyPeopleHttp"
)