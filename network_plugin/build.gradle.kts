import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.de.ktorfit )
    alias(libs.plugins.buildConfig)
    `maven-publish`
}

group = "com.github.cybernhl.GeneralModule"
version = "1.0-SNAPSHOT"

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

buildConfig {
    useKotlinOutput()
    packageName("idv.neo.module.network.plugin")
    buildConfigField("String", "HTTP_CLIENT_DEFAULT_PLUGINS_TAG", "\"HTTP_CLIENT_DEFAULT_PLUGINS\"")
    buildConfigField("String", "HTTP_CLIENT_PLUGINS_TAG", "\"HTTP_CLIENT_PLUGINS\"")
    buildConfigField("String", "AUTH_ERROR_CODE_TAG", "\"AUTH_ERROR_CODE\"")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcfName = "network_plugin"
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = xcfName
            isStatic = true
        }
    }

    jvm() {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilations.all {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)

            implementation (libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.de.ktorfit)
            implementation(libs.de.ktorfit.converters.call)
            implementation(libs.de.ktorfit.converters.flow)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)

            implementation(libs.koin.core)
            implementation(project(":DI_TAGS"))
            implementation(project(":utils"))
            implementation("com.github.cybernhl.Entity:general:0.0.4")
            implementation(project(":DomainManager"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "idv.neo.module.network.plugin"
    compileSdk = 36
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = 21
    }
}
dependencies {

}

tasks.matching { it.name.contains("sourcesJar") || it.name.contains("Sources") }.configureEach {
    dependsOn(tasks.matching { it.name.startsWith("ksp") })
}
