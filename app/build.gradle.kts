plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

android {
    compileSdk = 35
    namespace = "io.github.javiewer"

    defaultConfig {
        applicationId = "io.github.javiewer"
        minSdk = 24
        targetSdk = 35
        versionCode = 28
        versionName = "3.1.0-kotlin"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    // AndroidX
    implementation(libs.appcompat)
    implementation(libs.cardview)
    implementation(libs.material)
    implementation(libs.palette)
    implementation(libs.constraintlayout)
    implementation(libs.swiperefreshlayout)
    implementation(libs.viewpager2)
    implementation(libs.recyclerview)
    implementation(libs.coreKtx)
    implementation(libs.activityKtx)
    implementation(libs.fragmentKtx)

    // Lifecycle + ViewModel
    implementation(libs.lifecycleViewmodelKtx)
    implementation(libs.lifecycleLivedataKtx)
    implementation(libs.lifecycleRuntimeKtx)

    // Coroutines
    implementation(libs.coroutinesCore)
    implementation(libs.coroutinesAndroid)

    // Room
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    ksp(libs.roomCompiler)

    // DataStore
    implementation(libs.datastorePreferences)

    // Compose
    implementation(platform(libs.composeBom))
    implementation(libs.composeUi)
    implementation(libs.composeUiToolingPreview)
    implementation(libs.composeMaterial3)
    implementation(libs.composeMaterialIconsExtended)
    implementation(libs.activityCompose)
    implementation(libs.navigationCompose)
    implementation(libs.lifecycleRuntimeCompose)
    implementation(libs.lifecycleViewmodelCompose)
    implementation(libs.hiltNavigationCompose)
    debugImplementation(libs.composeUiTooling)

    // Hilt
    implementation(libs.hiltAndroid)
    ksp(libs.hiltCompiler)

    // View (legacy - kept for XML screens still in use)
    implementation(libs.materialdrawer)

    // Glide
    implementation(libs.glide)
    ksp(libs.glideCompiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.converterGson)
    implementation(libs.retrofitKotlinxSerialization)
    implementation(libs.gson)
    implementation(libs.kotlinxSerializationJson)
    implementation(libs.jsoup)
    implementation(libs.okhttp)

    // Player
    implementation(libs.media3Exoplayer)
    implementation(libs.media3Ui)
    implementation(libs.media3Hls)

    // Crash reporting (exclude old Support Library, uses AndroidX via Jetifier-free path)
    implementation(libs.customactivityoncrash) {
        exclude(group = "com.android.support")
    }

    // Test
    testImplementation(libs.junit)
}
