plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.companyvihva.vihva"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.companyvihva.vihva"
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
    buildFeatures{

        viewBinding = true

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }


    // Habilita o recurso de View Binding no projeto Android.
    // O View Binding gera classes de ligação para cada arquivo de layout XML,
    // fornecendo acesso direto aos elementos de interface do usuário definidos nesses layouts.
    // Ao definir 'enable' como 'true', estamos instruindo o Gradle a ativar o View Binding.
    viewBinding{
        enable = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("com.google.android.gms:play-services-auth:20.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("androidx.datastore:datastore-core-android:1.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.android.support.constraint:constraint-layout:1.1.3")
    implementation ("com.google.android.material:material:<version>")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.google.android.material:material:1.5.0")
    implementation ("androidx.work:work-runtime-ktx:2.7.1")
    implementation ("com.google.android.material:material:1.9.0")



    //dependencias do Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-common-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx:23.0.3")
    implementation ("com.google.firebase:firebase-database:20.0.5")
    implementation ("com.google.firebase:firebase-analytics:21.0.0")


    //Picasso
    implementation ("com.squareup.picasso:picasso:2.71828")


    //dependencia da biblioteca de mascaras
    implementation ("com.github.santalu:maskara:1.0.0")

    //Dependencias GPS
    implementation("androidx.activity:activity-ktx:1.9.0")
    implementation("androidx.fragment:fragment-ktx:1.7.1")

    //Firebase Storage
    implementation ("com.google.firebase:firebase-storage:20.0.0")




}