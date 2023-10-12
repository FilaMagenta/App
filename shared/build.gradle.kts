import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktorfit)
    alias(libs.plugins.mokoResources)
    alias(libs.plugins.sqldelight)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"

            export(libs.moko.resources)
            export(libs.moko.graphics) // toUIColor here

            export(libs.multiplatform.viewmodel)
        }
    }

    @Suppress("UnusedPrivateProperty")
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose Dependencies
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                api(libs.compose.webview)

                // Kotlin libraries
                api(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)

                // Multiplatform libraries
                api(libs.multiplatform.settings)
                api(libs.multiplatform.viewmodel)
                api(libs.napier)

                // Moko Resources
                api(libs.moko.resources)
                api(libs.moko.resourcescompose)

                // Ktor
                implementation(libs.ktor.core)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktorfit.lib)

                // SQlDelight
                implementation(libs.sqldelight.coroutines)
                implementation(libs.sqldelight.primitiveAdapters)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)

                implementation(libs.moko.resourcestest)
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)

            dependencies {
                implementation(libs.compose.material3.windowSizeClass)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.ktor.okhttp)
                implementation(libs.sqldelight.android)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by getting {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                implementation(libs.ktor.darwin)
                implementation(libs.sqldelight.native)
            }
        }
    }
}

dependencies {
    val ktorfitVersion = libs.versions.ktorfit.get()
    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    add("kspAndroid", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
}

android {
    namespace = "com.arnyminerz.filamagenta"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.arnyminerz.filamagenta" // required
}

buildkonfig {
    packageName = "com.arnyminerz.filamagenta"

    defaultConfigs {
        val properties = Properties().apply {
            load(rootProject.file("local.properties").inputStream())
        }

        buildConfigField(STRING, "ServerHostname", properties.getProperty("server.hostname"))

        buildConfigField(STRING, "OAuthClientId", properties.getProperty("oauth.clientId"))
        buildConfigField(STRING, "OAuthClientSecret", properties.getProperty("oauth.clientSecret"))

        buildConfigField(STRING, "WooClientId", properties.getProperty("woo.clientId"))
        buildConfigField(STRING, "WooClientSecret", properties.getProperty("woo.clientSecret"))
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.arnyminerz.filamagenta.cache")
        }
    }
}
