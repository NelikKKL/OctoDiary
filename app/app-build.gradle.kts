import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

val gitLatestCommit: String = ByteArrayOutputStream().use { outputStream ->
    project.exec {
        executable("git")
        args("log", "--oneline", "-1", "--format=format:%h", ".")
        standardOutput = outputStream
    }
    outputStream.toString()
}

android {
    namespace = "org.bxkr.octodiary"
    compileSdk = 36

    defaultConfig {
        applicationId = "org.bxkr.octodiary"
        minSdk = 26
        targetSdk = 36
        versionCode = 35
        versionName = "2.1.9"
        archivesName = gitLatestCommit

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        // Signs release builds so the resulting APK is actually installable
        // (unsigned release APKs fail with INSTALL_PARSE_FAILED_NO_CERTIFICATES).
        // Uses a dedicated, committed self-signed keystore (keystore/release-debug.keystore)
        // by default, so every CI build is signed with the *same* key and installs
        // cleanly as an update over the previous build. This is NOT a secret/production
        // key - it exists purely so personal/CI builds are installable and updatable.
        //
        // IMPORTANT: this deliberately does NOT reuse signingConfigs.getByName("debug"),
        // because AGP assigns that config's default storeFile during DSL finalization,
        // which happens *after* this block runs - reading it here returns null and
        // silently produces an unsigned APK again.
        //
        // If RELEASE_STORE_FILE / RELEASE_STORE_PASSWORD / RELEASE_KEY_ALIAS /
        // RELEASE_KEY_PASSWORD env vars are set (e.g. real release-signing secrets
        // in CI), those take priority over the committed keystore.
        create("release") {
            val storeFilePath = System.getenv("RELEASE_STORE_FILE")
            if (storeFilePath != null) {
                storeFile = file(storeFilePath)
                storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            } else {
                storeFile = rootProject.file("keystore/release-debug.keystore")
                storePassword = "octodiary-debug"
                keyAlias = "octodiary-debug"
                keyPassword = "octodiary-debug"
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.browser)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.glide)
    implementation(libs.converter.scalars)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    implementation(libs.androidx.biometric.ktx)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.compose)
    implementation(libs.zoomable)
    implementation(libs.dotsindicator)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}