import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    //https://github.com/vanniktech/gradle-maven-publish-plugin
    //https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html#publish-to-maven-central-using-continuous-integration
    //https://central.sonatype.com/publishing/deployments
    id("com.vanniktech.maven.publish")
}

group = PublishConfig.group
version = mVersion

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), project.name, version.toString())

    pom {
        name = project.name
        description = PublishConfig.description
        inceptionYear = PublishConfig.inceptionYear
        url = PublishConfig.projectUrl
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "ltttttttttttt"
                name = "lt"
                email = "lt.dygzs@qq.com"
                url = "https://github.com/ltttttttttttt"
            }
        }
        scm {
            url = PublishConfig.projectUrl
        }
    }
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
    }

    jvm {
        compilerOptions {
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
        outputModuleName = "common_app"
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

        val iosMain by creating {
            dependencies {
                dependsOn(commonMain)//网络请求引擎
                api("io.ktor:ktor-client-darwin:${ktorVersion}")
            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosX64Main by getting {
            dependsOn(iosMain)
        }

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