plugins {
    id("matsumo.primitive.kmp.common")
    id("matsumo.primitive.android.library")
    id("matsumo.primitive.kmp.android")
    id("matsumo.primitive.kmp.ios")
    id("matsumo.primitive.detekt")
}

android {
    namespace = "me.matsumo.zencall.core.datasource"
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.proto)
        }

        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:model"))

            api(project.dependencies.platform(libs.supabase.bom))
            api(libs.bundles.supabase)
            api(libs.bundles.filekit)
            api(libs.bundles.ktor.client)
            api(libs.bundles.ksoup)
            api(libs.androidx.datastore.preferences)
        }
    }
}
