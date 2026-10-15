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
    packageName("idv.neo.module.di.tag")

    buildConfigField("String", "APP_NAME_TAG", "\"APP_NAME\"")
    buildConfigField("String", "APP_VERSION_TAG", "\"APP_VERSION\"")
    buildConfigField("String", "APP_RUNTIME_TYPE_TAG", "\"BUILD_TYPE\"")
    buildConfigField("String", "APP_RUNTIME_ENV_TAG", "\"FLAVOR_ENV\"")

    buildConfigField("String", "APP_API_URL_TAG", "\"API_URL\"")
    buildConfigField("String", "APP_API_URLS_TAG", "\"API_URLS\"")

    buildConfigField("String", "NETWORK_DIAGNOSTIC_SLOW_REQUEST_THRESHOLD_TAG", "\"SLOW_REQUEST_THRESHOLD\"")

    buildConfigField("String", "HTTP_CLIENT_WEBSOCKET_TAG", "\"HTTP_CLIENT_WEBSOCKET\"")
    buildConfigField("String", "HTTP_CLIENT_DEFAULT_PLUGINS_TAG", "\"HTTP_CLIENT_DEFAULT_PLUGINS\"")
    buildConfigField("String", "HTTP_CLIENT_PLUGINS_TAG", "\"HTTP_CLIENT_PLUGINS\"")
    buildConfigField("String", "AUTH_TOKEN_TAG", "\"AUTH_TOKEN\"")
    buildConfigField("String", "AUTH_ERROR_CODE_TAG", "\"AUTH_ERROR_CODE\"")
    buildConfigField("String", "APP_DATASTORE_FILE_TAG", "\"APP_DATASTORE\"")

    buildConfigField("String", "APP_RUNTIME_SESSION_TAG", "\"RUNTIME_SESSION\"")

    buildConfigField("String", "GENERAL_JSON_TAG", "\"GENERAL_KOTLINX_JSON\"")

    buildConfigField("String", "PROBE_PATH_TAG", "\"PROBE_PATH\"")

    buildConfigField("String", "PROBE_TIMEOUT_TAG", "\"PROBE_TIMEOUT\"")

    buildConfigField("String", "MAX_SIZE_TAG", "\"MAX_SIZE_JSON\"")

    buildConfigField("String", "SESSION_SCOPE_ID_TAG", "\"UserSessionScope\"")

    buildConfigField("String", "SESSION_SCOPE_NAME_TAG", "\"UserSession\"")


    buildConfigField("String", "GRAFANA_LOKI_URL_TAG", "\"LOKI_URL\"")
    buildConfigField("String", "GRAFANA_LOKI_USER_ID_TAG", "\"LOKI_USER_ID\"")
    buildConfigField("String", "GRAFANA_LOKI_API_KEY_TAG", "\"LOKI_API_KEY\"")

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

    val xcfName = "GeneralTAGSKit"
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

        }

        androidMain.dependencies {

        }

        commonTest.dependencies {

        }
    }
}

android {
    namespace = "idv.neo.module.di.tag"
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
