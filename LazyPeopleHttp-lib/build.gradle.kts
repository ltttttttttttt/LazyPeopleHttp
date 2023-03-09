plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

group = "io.github.ltttttttttttt"
version = "1.0.0"

kotlin {
    jvm("desktop") {
        compilations.all {
            defaultSourceSet.resources.srcDir("/resources")
            kotlinOptions {
                jvmTarget = "11"
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

    cocoapods {
        summary = "Jatpack(JetBrains) Compose views"
        homepage = "https://github.com/ltttttttttttt/ComposeViews"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "LazyPeopleHttp"
            isStatic = true
        }
        extraSpecAttributes["resources"] =
            "['resources/**']"
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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val desktopMain by getting
        val desktopTest by getting

        val iosMain by getting
        val iosTest by getting
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }

        val jsMain by getting
    }
}