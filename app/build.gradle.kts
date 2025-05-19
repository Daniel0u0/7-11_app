plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.sevenelevenapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sevenelevenapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // 推荐：使用 Java Toolchain 指定 Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // Toolchain 配置（AGP 7.0+ 支持）
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("com.google.android.material:material:1.12.0")

    // Use Picasso for image loading (removing Glide since you have both)
    implementation("com.squareup.picasso:picasso:2.8")

    // Location services
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.squareup.picasso:picasso:2.71828")

}