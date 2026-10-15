import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
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
    packageName("idv.neo.module.storage")
    buildConfigField("String", "APP_DATASTORE_FILE_TAG", "\"APP_DATASTORE\"")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcfName = "StorageKit"
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
            implementation(libs.androidx.datastore.preferences.core)
            implementation(project(":DI_TAGS"))
            implementation(libs.koin.core)
        }

        androidMain.dependencies {
            implementation(libs.koin.android)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "idv.neo.module.storage"
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
