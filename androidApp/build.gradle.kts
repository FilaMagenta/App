import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.arnyminerz.filamagenta.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.arnyminerz.filamagenta.android"
        minSdk = 24
        targetSdk = 34

        val versionPropsFile = project.rootProject.file("version.properties")
        if (!versionPropsFile.canRead()) {
            throw GradleException("Cannot read version.properties")
        }
        val versionProps = Properties().apply {
            versionPropsFile.inputStream().use {
                load(versionPropsFile.inputStream())
            }
        }
        val sharedVersionName = versionProps.getProperty("shared.versionName")
        val androidVersionCode = versionProps.getProperty("android.versionCode").toInt()
        val androidVersionName = versionProps.getProperty("android.versionName")

        versionCode = androidVersionCode
        versionName = "$sharedVersionName-$androidVersionName.$versionCode"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
        }
    }

    signingConfigs {
        create("release") {
            val properties = Properties()
            project.rootProject.file("local.properties").inputStream().use(properties::load)

            val signingKeystorePassword: String? = properties.getProperty("signing.keystore.password")
            val signingKeyAlias: String? = properties.getProperty("signing.key.alias")
            val signingKeyPassword: String? = properties.getProperty("signing.key.password")

            storeFile = File(project.rootDir, "keystore.jks")
            storePassword = signingKeystorePassword
            keyAlias = signingKeyAlias
            keyPassword = signingKeyPassword
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isDebuggable = false

            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Force inclusion of all translation files.
    // See: https://github.com/icerockdev/moko-resources#example-6---select-localization-in-runtime
    @Suppress("UnstableApiUsage")
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.android)

    coreLibraryDesugaring(libs.desugar.jdkLibs)
}
