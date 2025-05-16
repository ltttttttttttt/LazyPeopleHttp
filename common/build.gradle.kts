import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    kotlin("native.cocoapods")
    kotlin("plugin.serialization") version kotlinVersion
    id("com.google.devtools.ksp") version kspVersion
    kotlin("plugin.compose")
}

group = "com.lt"
version = "1.0-SNAPSHOT"

kotlin {
    androidTarget()
    jvm("desktop") {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    js(IR) {
        browser()
    }
    cocoapods {
        summary = "LazyPeopleHttp"
        homepage = "https://github.com/ltttttttttttt/LazyPeopleHttp"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "common"
            isStatic = true
        }
        //extraSpecAttributes["resources"] =
        //    "['resources/**']"
    }
    sourceSets {
        val commonMain by getting {
            //配置ksp生成目录
            kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            dependencies {
                api(project(":LazyPeopleHttp-lib"))
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                //跨平台网络请求
                api("io.ktor:ktor-client-core:$ktorVersion")
                //kt的跨平台json解析
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationJsonVersion")
                //跨平台网络请求-日志打印
                api("io.ktor:ktor-client-logging:$ktorVersion")
                //kotlin
                api("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
                //协程
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
                api("io.ktor:ktor-client-okhttp:$ktorVersion")
                //协程
                api("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                api("io.ktor:ktor-client-okhttp:$ktorVersion")
                //协程
                api("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
            }
        }
        val desktopTest by getting


        fun KotlinSourceSet.iosDependencies() {
            dependencies {
                //网络请求引擎
                api("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }

        val iosX64Main by getting {
            iosDependencies()
        }
        val iosX64Test by getting
        val iosArm64Main by getting {
            iosDependencies()
        }
        val iosArm64Test by getting
        val iosSimulatorArm64Main by getting {
            iosDependencies()
        }
        val iosSimulatorArm64Test by getting

        val jsMain by getting {
            dependencies {
                api("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
    }
    ksp {
        arg("getFunAnnotationsWithLazyPeopleHttp", "true")
        arg("functionReplaceFromWithLazyPeopleHttp", "_")
        arg("functionReplaceToWithLazyPeopleHttp", "/")
        arg("customizeOutputFileWithLazyPeopleHttp", "${project.projectDir.absoluteFile}/customizeOutputFile.json")
    }
}

android {
    namespace = "com.lt.lazy_people_http.common"
    compileSdkVersion(35)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(33)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    add("kspCommonMainMetadata", project(":LazyPeopleHttp"))
}

tasks.register<Delete>("clearBuild") {
    doLast {
        delete(buildDir)
    }
}

tasks.register<Delete>("clearKsp") {
    doFirst {
        delete(File(buildDir, "generated/ksp").absolutePath)
    }
}

tasks.register<Task>("runKsp") {
    dependsOn("clearKsp")
        .dependsOn("kspCommonMainKotlinMetadata")
}