package primitive

import me.matsumo.zencall.android
import me.matsumo.zencall.androidTestImplementation
import me.matsumo.zencall.debugImplementation
import me.matsumo.zencall.implementation
import me.matsumo.zencall.library
import me.matsumo.zencall.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class KmpComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            android {
                buildFeatures.compose = true
            }

            dependencies {
                val bom = libs.library("compose-bom")

                implementation(project.dependencies.platform(bom))
                implementation(libs.library("compose-ui-tooling-preview"))
                debugImplementation(libs.library("compose-ui-tooling"))
                androidTestImplementation(project.dependencies.platform(bom))
            }
        }
    }
}
