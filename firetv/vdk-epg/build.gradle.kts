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
    implementation(project(":vdk-core"))

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")

//    androidTestImplementation(Libs.AndroidX.Test.runner)
//    androidTestImplementation(Libs.AndroidX.Test.extJunit)
}
