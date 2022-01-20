plugins {
    id("com.android.library")
}

android {
    compileSdk = 31
    buildToolsVersion = "30.0.3"

    defaultConfig {
        minSdk = 22
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        isAbortOnError = false
    }
}


dependencies {
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.annotation:annotation:1.2.0")

    compileOnly("com.google.code.gson:gson:2.8.5")
    compileOnly("com.squareup.okhttp3:okhttp:3.14.2")

    androidTestImplementation("commons-io:commons-io:2.6")
    androidTestImplementation("com.google.code.gson:gson:2.8.5")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
}
