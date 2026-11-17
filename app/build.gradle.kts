plugins { id("com.android.application") }

android {
    namespace = "com.sorteosgraficos.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sorteosgraficos.app"
        minSdk = 19
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
