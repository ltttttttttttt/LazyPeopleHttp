import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("convention.publication")
}

group = "io.github.ltttttttttttt"
//上传到mavenCentral命令: ./gradlew publishAllPublicationsToSonatypeRepository
//mavenCentral后台: https://s01.oss.sonatype.org/#stagingRepositories
version = mVersion

kotlin {
    androidTarget {
        publishLibraryVariants("debug", "release")
    }

    jvm {
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
        compilations.all {
            defaultSourceSet.resources.srcDir("/resources")
        }
    }

    wasmJs {
        moduleName = "common_app"
        browser {
            commonWebpackConfig {
                outputFileName = "common_app.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(project.projectDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }

    cocoapods {
        summary = "LazyPeopleHttp"
        homepage = "https://github.com/ltttttttttttt/LazyPeopleHttp"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "LazyPeopleHttp"
            isStatic = true
        }
        //extraSpecAttributes["resources"] =
        //    "['resources/**']"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //协程
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                //跨平台网络请求
                api("io.ktor:ktor-client-core:$ktorVersion")
                //kt的跨平台json解析
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationJsonVersion")
                //compose runtime
                implementation("org.jetbrains.compose.runtime:runtime:1.7.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
                //网络请求引擎
                api("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val androidUnitTest by getting

        val jvmMain by getting {
            dependencies {
                //网络请求引擎
                api("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val jvmTest by getting

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
                //网络请求引擎
                api("io.ktor:ktor-client-js:$ktorVersion")
            }
        }

        val wasmJsMain by getting {
            dependencies {
                //网络请求引擎
                api("io.ktor:ktor-client-js:$ktorVersion")
            }
        }
    }
}

android {
    namespace = "com.lt.lazy_people_http"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
        sourceSets["main"].res.srcDir("resources")
        consumerProguardFiles("consumer-rules.pro")//配置库的混淆文件,会带到app中
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        targetSdk = 35
    }
}

afterEvaluate {
    try {
        tasks.findByName("signAndroidReleasePublication")!!
            .mustRunAfter(tasks.findByName("publishAndroidDebugPublicationToSonatypeRepository"))
        tasks.findByName("signIosArm64Publication")!!
            .mustRunAfter(tasks.findByName("publishAndroidDebugPublicationToSonatypeRepository"))
        tasks.findByName("signIosSimulatorArm64Publication")!!
            .mustRunAfter(tasks.findByName("publishAndroidDebugPublicationToSonatypeRepository"))
        tasks.findByName("signIosX64Publication")!!
            .mustRunAfter(tasks.findByName("publishAndroidDebugPublicationToSonatypeRepository"))
        tasks.findByName("signJsPublication")!!
            .mustRunAfter(tasks.findByName("publishAndroidDebugPublicationToSonatypeRepository"))
        tasks.findByName("signJvmPublication")!!
            .mustRunAfter(tasks.findByName("publishAndroidDebugPublicationToSonatypeRepository"))
        tasks.findByName("signKotlinMultiplatformPublication")!!
            .mustRunAfter(tasks.findByName("publishAndroidDebugPublicationToSonatypeRepository"))
        tasks.findByName("signIosArm64Publication")!!
            .mustRunAfter(tasks.findByName("publishAndroidReleasePublicationToSonatypeRepository"))
        tasks.findByName("signIosSimulatorArm64Publication")!!
            .mustRunAfter(tasks.findByName("publishIosArm64PublicationToSonatypeRepository"))
        tasks.findByName("signIosSimulatorArm64Publication")!!
            .mustRunAfter(tasks.findByName("publishAndroidReleasePublicationToSonatypeRepository"))
        tasks.findByName("signIosX64Publication")!!
            .mustRunAfter(tasks.findByName("publishIosArm64PublicationToSonatypeRepository"))
        tasks.findByName("signIosX64Publication")!!
            .mustRunAfter(tasks.findByName("publishAndroidReleasePublicationToSonatypeRepository"))
        tasks.findByName("signJsPublication")!!
            .mustRunAfter(tasks.findByName("publishIosArm64PublicationToSonatypeRepository"))
        tasks.findByName("signJsPublication")!!
            .mustRunAfter(tasks.findByName("publishAndroidReleasePublicationToSonatypeRepository"))
        tasks.findByName("signJvmPublication")!!
            .mustRunAfter(tasks.findByName("publishIosArm64PublicationToSonatypeRepository"))
        tasks.findByName("signJvmPublication")!!
            .mustRunAfter(tasks.findByName("publishAndroidReleasePublicationToSonatypeRepository"))
        tasks.findByName("signKotlinMultiplatformPublication")!!
            .mustRunAfter(tasks.findByName("publishIosArm64PublicationToSonatypeRepository"))
        tasks.findByName("signKotlinMultiplatformPublication")!!
            .mustRunAfter(tasks.findByName("publishAndroidReleasePublicationToSonatypeRepository"))
        tasks.findByName("signIosX64Publication")!!
            .mustRunAfter(tasks.findByName("publishIosSimulatorArm64PublicationToSonatypeRepository"))
        tasks.findByName("signJsPublication")!!
            .mustRunAfter(tasks.findByName("publishIosSimulatorArm64PublicationToSonatypeRepository"))
        tasks.findByName("signJvmPublication")!!
            .mustRunAfter(tasks.findByName("publishIosSimulatorArm64PublicationToSonatypeRepository"))
        tasks.findByName("signKotlinMultiplatformPublication")!!
            .mustRunAfter(tasks.findByName("publishIosSimulatorArm64PublicationToSonatypeRepository"))
        tasks.findByName("signJsPublication")!!
            .mustRunAfter(tasks.findByName("publishIosX64PublicationToSonatypeRepository"))
        tasks.findByName("signJvmPublication")!!
            .mustRunAfter(tasks.findByName("publishIosX64PublicationToSonatypeRepository"))
        tasks.findByName("signKotlinMultiplatformPublication")!!
            .mustRunAfter(tasks.findByName("publishIosX64PublicationToSonatypeRepository"))
        tasks.findByName("signJvmPublication")!!
            .mustRunAfter(tasks.findByName("publishJsPublicationToSonatypeRepository"))
        tasks.findByName("signKotlinMultiplatformPublication")!!
            .mustRunAfter(tasks.findByName("publishJsPublicationToSonatypeRepository"))
        tasks.findByName("signKotlinMultiplatformPublication")!!
            .mustRunAfter(tasks.findByName("publishJvmPublicationToSonatypeRepository"))
        tasks.findByName("signWasmJsPublication")!!
            .mustRunAfter(tasks.findByName("publishKotlinMultiplatformPublicationToSonatypeRepository"))
        tasks.findByName("signWasmJsPublication")!!
            .mustRunAfter(tasks.findByName("publishJvmPublicationToSonatypeRepository"))
        tasks.findByName("signWasmJsPublication")!!
            .mustRunAfter(tasks.findByName("publishJsPublicationToSonatypeRepository"))
        tasks.findByName("signWasmJsPublication")!!
            .mustRunAfter(tasks.findByName("publishIosX64PublicationToSonatypeRepository"))
        tasks.findByName("signWasmJsPublication")!!
            .mustRunAfter(tasks.findByName("publishIosSimulatorArm64PublicationToSonatypeRepository"))
    } catch (ignore: Exception) {
    }
}