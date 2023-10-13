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
        val androidVersionCode = versionProps.getProperty("VERSION_CODE").toInt()

        val sharedVersionName = project.extra["shared.versionName"] as String
        val androidVersionName = project.extra["android.versionName"] as String

        versionCode = androidVersionCode
        versionName = "$sharedVersionName-$androidVersionName~$versionCode"
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
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
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
}

dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlinx.coroutines.android)

    coreLibraryDesugaring(libs.desugar.jdkLibs)
}
