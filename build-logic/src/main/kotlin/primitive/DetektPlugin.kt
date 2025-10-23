package primitive

import me.matsumo.zencall.configureDetekt
import me.matsumo.zencall.library
import me.matsumo.zencall.libs
import me.matsumo.zencall.plugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class DetektPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(libs.plugin("detekt").pluginId)

            configureDetekt()

            dependencies {
                "detektPlugins"(libs.library("detekt-formatting"))
                "detektPlugins"(libs.library("twitter-compose-rule"))
            }
        }
    }
}
