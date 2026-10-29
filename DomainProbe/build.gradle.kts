import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
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
    packageName("idv.neo.module.network.domain.probe")
    buildConfigField("String", "PROBE_PATH", "\"/public/connectivity-probe.png\"")
    // Infers Long from the Kotlin literal
    buildConfigField("PROBE_TIMEOUT", 3000L)
    // Explicit type declaration (Optional)
    buildConfigField<Long>("MAX_SIZE", 999999)

}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    val xcfName = "network_domain_probe"
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

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.json)
            implementation(libs.ktor.client.logging)

            implementation(project(":DI_TAGS"))
            implementation(project(":utils"))
            implementation(project(":network"))
            implementation(project(":DomainManager"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ktor.client.cio)
            }
        }

        androidMain {
            dependencies {
                implementation(libs.kotlinx.coroutines.android)
                implementation(libs.ktor.client.cio)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

android {
    namespace = "idv.neo.module.network.domain.probe"
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
