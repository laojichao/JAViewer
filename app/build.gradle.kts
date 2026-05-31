plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    alias(libs.plugins.hilt.android)
}

configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}

android {
    compileSdk = 34
    namespace = "io.github.javiewer"

    defaultConfig {
        applicationId = "io.github.javiewer"
        minSdk = 24
        targetSdk = 34
        versionCode = 27
        versionName = "3.0.0-kotlin"
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
    kapt(libs.roomCompiler)

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
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    debugImplementation(libs.composeUiTooling)

    // Hilt
    implementation(libs.hiltAndroid)
    kapt(libs.hiltCompiler)

    // View (legacy - kept for XML screens still in use)
    implementation(libs.materialdrawer)
    implementation(libs.ahbottomnavigation)
    implementation(libs.flowlayout)

    // Glide
    implementation(libs.glide)
    kapt(libs.glideCompiler)

    // Network
    implementation(libs.retrofit)
    implementation(libs.converterGson)
    implementation(libs.retrofitKotlinxSerialization)
    implementation(libs.gson)
    implementation(libs.kotlinxSerializationJson)
    implementation(libs.jsoup)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Player
    implementation(libs.media3Exoplayer)
    implementation(libs.media3Ui)
    implementation(libs.media3Hls)

    // Crash reporting
    implementation(libs.customactivityoncrash)

    // Test
    testImplementation(libs.junit)
}
