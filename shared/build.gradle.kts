import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.io.FileFilter
import java.time.LocalDateTime
import java.util.Properties

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.buildKonfig)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
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
            isStatic = true

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
                implementation(compose.animation)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)

                // Kotlin libraries
                api(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.kotlinx.serialization)

                // Multiplatform libraries
                api(libs.multiplatform.settings)
                api(libs.multiplatform.viewmodel)
                api(libs.napier)
                api(libs.sentry.multiplatform)
                api(libs.libsodium)

                // Moko Resources
                api(libs.moko.resources)
                api(libs.moko.resourcescompose)

                // Ktor
                implementation(libs.ktor.core)
                implementation(libs.ktor.client.auth)
                implementation(libs.ktor.client.contentNegotiation)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.json)

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
                api(libs.androidx.activity.compose)
                implementation(libs.androidx.camera.camera2)
                implementation(libs.androidx.camera.lifecycle)
                implementation(libs.androidx.camera.view)
                implementation(libs.androidx.lifecycle.viewmodel)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.ktor.okhttp)
                implementation(libs.mlkit.barcode)
                implementation(libs.qrcode.android)
                api(libs.sentry.android)
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

    val properties = Properties().apply {
        load(rootProject.file("local.properties").inputStream())
    }

    val versionProps = Properties().apply {
        project.rootProject.file("version.properties").inputStream().use(this::load)
    }
    val sharedVersionName = versionProps["shared.versionName"] as String

    defaultConfigs {
        buildConfigField(STRING, "ServerHostname", properties.getProperty("server.hostname"))

        buildConfigField(STRING, "OAuthClientId", properties.getProperty("oauth.clientId"))
        buildConfigField(STRING, "OAuthClientSecret", properties.getProperty("oauth.clientSecret"))

        buildConfigField(STRING, "WooClientId", properties.getProperty("woo.clientId"))
        buildConfigField(STRING, "WooClientSecret", properties.getProperty("woo.clientSecret"))

        buildConfigField(STRING, "SqlTunnelHost", properties.getProperty("sql.tunner.host"))

        buildConfigField(STRING, "SqlHost", properties.getProperty("sql.host"))
        buildConfigField(STRING, "SqlPort", properties.getProperty("sql.port"))
        buildConfigField(STRING, "SqlUsername", properties.getProperty("sql.username"))
        buildConfigField(STRING, "SqlPassword", properties.getProperty("sql.password"))
        buildConfigField(STRING, "SqlDatabase", properties.getProperty("sql.database"))

        val mokoSpecialDirectories = setOf("fonts", "images", "base")
        val languages = mutableListOf("en")
        project.rootProject.file("shared/src/commonMain/resources/MR")
            // Include only directories
            .listFiles(FileFilter { it.isDirectory })!!
            // Filter Moko Directories
            .filterNot { mokoSpecialDirectories.contains(it.name) }
            // Include only the names
            .map { it.name }
            // Add all languages to list
            .forEach { languages.add(it) }
        buildConfigField(STRING, "Languages", languages.joinToString(","))

        buildConfigField(STRING, "ReleaseName", sharedVersionName)

        buildConfigField(STRING, "QrCodeKey", properties.getProperty("encryption.qrcode.key"))
        buildConfigField(STRING, "QrCodeNonce", properties.getProperty("encryption.qrcode.nonce"))
    }

    targetConfigs {
        create("android") {
            val versionName = versionProps["android.versionName"] as String
            val androidVersionCode = versionProps["android.versionCode"] as String

            buildConfigField(STRING, "SentryDsn", properties.getProperty("sentry.dsn.android"))
            buildConfigField(STRING, "ReleaseName", "$sharedVersionName-$versionName.$androidVersionCode")
        }
        create("ios") {
            val versionName = versionProps["ios.versionName"] as String

            buildConfigField(STRING, "SentryDsn", properties.getProperty("sentry.dsn.ios"))
            buildConfigField(STRING, "ReleaseName", "$sharedVersionName-$versionName")
        }
    }
    targetConfigs("dev") {
        create("android") {
            buildConfigField(BOOLEAN, "IsProduction", "false")
        }
        create("ios") {
            buildConfigField(BOOLEAN, "IsProduction", "false")
        }
    }
    targetConfigs("production") {
        create("android") {
            buildConfigField(BOOLEAN, "IsProduction", "true")
        }
        create("ios") {
            buildConfigField(BOOLEAN, "IsProduction", "true")
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("com.arnyminerz.filamagenta.cache")
        }
    }
}

task("increaseVersionCode") {
    doFirst {
        val versionPropsFile = project.rootProject.file("version.properties")
        if (!versionPropsFile.canRead()) {
            throw GradleException("Cannot read version.properties")
        }
        val versionProps = Properties().apply {
            versionPropsFile.inputStream().use {
                load(versionPropsFile.inputStream())
            }
        }
        val code = versionProps.getProperty("android.versionCode").toInt() + 1
        versionProps["android.versionCode"] = code.toString()
        versionPropsFile.outputStream().use {
            val date = LocalDateTime.now()
            versionProps.store(it, "Updated at $date")
        }
        println("Increased version code to $code")
    }
}
