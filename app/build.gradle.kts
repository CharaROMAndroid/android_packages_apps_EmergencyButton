/*
 * SPDX-FileCopyrightText: 2023 The Calyx Institute
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("org.jlleitschuh.gradle.ktlint")
}

android {
    namespace = "com.android.emergencybutton"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.emergencybutton"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("aosp") {
            // Generated from the AOSP testkey:
            // https://android.googlesource.com/platform/build/+/refs/tags/android-11.0.0_r29/target/product/security/testkey.pk8
            keyAlias = "testkey"
            keyPassword = "testkey"
            storeFile = file("testkey.jks")
            storePassword = "testkey"
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("aosp")
        }
        release {
            isMinifyEnabled = false
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
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    lint {
        lintConfig = file("lint.xml")
    }
}

hilt {
    enableAggregatingTask = false
}

dependencies {
    /**
     * Dependencies in AOSP
     *
     * We try to keep the dependencies in sync with what AOSP ships as Panic is meant to be
     * built with the AOSP build system and gradle builds are just for more pleasant development.
     * Using the AOSP versions in gradle builds allows us to spot issues early on.
     */
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("android.jar", "libcore.jar"))))
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("info.guardianproject.panic-1.0.jar"))))

    // AndroidX
    // https://android.googlesource.com/platform/prebuilts/sdk/+/android-15.0.0_r6/current/androidx/m2repository/androidx/core/core-ktx?autodive=0
    implementation("androidx.core:core-ktx") {
        version { strictly("1.15.0-beta01") }
    }
    // https://android.googlesource.com/platform/prebuilts/sdk/+/android-15.0.0_r6/current/androidx/m2repository/androidx/appcompat/appcompat?autodive=0
    implementation("androidx.appcompat:appcompat") {
        version { strictly("1.7.0") } // 1.8.0-alpha01 in AOSP but isn't released
    }
    // https://android.googlesource.com/platform/prebuilts/sdk/+/android-15.0.0_r6/current/androidx/m2repository/androidx/preference/preference?autodive=0
    implementation("androidx.preference:preference-ktx") {
        version { strictly("1.2.1") } // 1.3.0-alpha01 in AOSP but isn't released
    }
    // https://android.googlesource.com/platform/prebuilts/sdk/+/android-15.0.0_r6/current/androidx/m2repository/androidx/lifecycle/lifecycle-viewmodel-ktx?autodive=0
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx") {
        version { strictly("2.9.0-alpha03") }
    }
    // https://android.googlesource.com/platform/prebuilts/sdk/+/android-15.0.0_r6/current/androidx/m2repository/androidx/navigation/navigation-fragment-ktx?autodive=0
    implementation("androidx.navigation:navigation-fragment-ktx") {
        version { strictly("2.9.0-alpha03") }
    }
    // https://android.googlesource.com/platform/prebuilts/sdk/+/android-15.0.0_r6/current/androidx/m2repository/androidx/navigation/navigation-ui-ktx?autodive=0
    implementation("androidx.navigation:navigation-ui-ktx") {
        version { strictly("2.9.0-alpha01") }
    }

    // Google
    // https://android.googlesource.com/platform/prebuilts/sdk/+/refs/tags/android-15.0.0_r6/current/extras/material-design-x/Android.bp#7
    implementation("com.google.android.material:material") {
        version { strictly("1.11.0") }
    }
    // https://android.googlesource.com/platform/external/dagger2/+/refs/tags/android-15.0.0_r6
    val hiltVersion = "2.54"
    ksp("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("com.google.dagger:hilt-android:$hiltVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
