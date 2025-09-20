import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("maven-publish")
    alias(libs.plugins.maven.publish)
}

kotlin {
    androidLibrary {
        namespace = "com.daiatech.chitralekhan"
        compileSdk = 35
        minSdk = 21

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ChitralekhanKit"
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.ui)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                implementation(libs.kotlinx.serializationJson)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.testExt.junit)
            }
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.daiatech.chitralekhan"
    generateResClass = auto
}


group = "io.github.karya-inc"
version = "0.0.4"

mavenPublishing {
    val artifactId = "chitralekhan"
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(
        groupId = group.toString(),
        artifactId = artifactId,
        version = version.toString()
    )

    pom {
        name.set(artifactId)
        description.set("An Android library for image annotation")
        url.set("https://github.com/karya-inc/ChitraLekhan.git")

        licenses {
            license {
                name.set("GNU license")
                url.set("https://opensource.org/license/gpl-3-0")
            }
        }

        developers {
            developer {
                id.set("divyansh@karya.in")
                name.set("Divyansh Kushwaha")
                email.set("divyansh@karya.in")
            }
        }

        scm {
            connection.set("scm:git:ssh://git@github.com/karya-inc/ChitraLekhan.git")
            developerConnection.set("scm:git:ssh://git@github.com/karya-inc/ChitraLekhan.git")
            url.set("https://github.com/karya-inc/ChitraLekhan.git")
        }
    }
}
