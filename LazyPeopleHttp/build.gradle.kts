plugins {
    kotlin("multiplatform")
    //https://github.com/vanniktech/gradle-maven-publish-plugin
    //https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html#publish-to-maven-central-using-continuous-integration
    //https://central.sonatype.com/publishing/deployments
    id("com.vanniktech.maven.publish")
    kotlin("plugin.serialization")
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
    jvm {
        compilerOptions {
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
                implementation("com.github.ltttttttttttt:KSPUtil:1.0.1")
            }
        }
        val jvmTest by getting
    }
}