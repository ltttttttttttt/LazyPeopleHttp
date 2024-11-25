plugins {
    kotlin("multiplatform")
    id("convention.publication")
    kotlin("plugin.serialization") version kotlinVersion
}

group = "io.github.ltttttttttttt"
//上传到mavenCentral命令: ./gradlew publishAllPublicationsToSonatypeRepository
//mavenCentral后台: https://s01.oss.sonatype.org/#stagingRepositories
version = mVersion

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting

        val jvmMain by getting {
            dependencies {
                implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
                implementation(project(":LazyPeopleHttp-lib"))
                implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationJsonVersion")
            }
        }
        val jvmTest by getting
    }
}

try {
    tasks.findByName("signKotlinMultiplatformPublication")!!
        .dependsOn(tasks.findByName("publishJvmPublicationToSonatypeRepository"))
} catch (e: Exception) {
    e.printStackTrace()
}