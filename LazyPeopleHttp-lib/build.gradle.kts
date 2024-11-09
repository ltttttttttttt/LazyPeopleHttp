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
    android {
        publishLibraryVariants("debug", "release")
    }

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    ios()
    iosSimulatorArm64()

    js(IR) {
        browser()
        compilations.all {
            defaultSourceSet.resources.srcDir("/resources")
        }
    }

    // TODO by lt ktor暂不支持wasm
    //@OptIn(ExperimentalWasmDsl::class)
    //wasmJs {
    //    moduleName = "common_app"
    //    browser {
    //        commonWebpackConfig {
    //            outputFileName = "common_app.js"
    //            devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
    //                static = (static ?: mutableListOf()).apply {
    //                    // Serve sources to debug inside browser
    //                    add(project.projectDir.path)
    //                }
    //            }
    //        }
    //    }
    //    binaries.executable()
    //}

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
                compileOnly("org.jetbrains.compose.runtime:runtime:1.4.0")
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

        val iosMain by getting {
            dependencies {
                //网络请求引擎
                api("io.ktor:ktor-client-darwin:$ktorVersion")
            }
        }
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }

        val jsMain by getting {
            dependencies {
                //网络请求引擎
                api("io.ktor:ktor-client-js:$ktorVersion")
            }
        }

        // TODO by lt ktor暂不支持wasm
        //val wasmJsMain by getting {
        //    dependencies {
        //        //网络请求引擎 todo 暂不支持wasm
        //        api("io.ktor:ktor-client-js:$ktorVersion")
        //    }
        //}
    }
}

android {
    namespace = "com.lt.lazy_people_http"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
        sourceSets["main"].res.srcDir("resources")
        consumerProguardFiles("consumer-rules.pro")//配置库的混淆文件,会带到app中
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

afterEvaluate {
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
}