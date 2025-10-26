package primitive

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpJvmPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            kotlin {
                jvm()
            }
        }
    }
}
