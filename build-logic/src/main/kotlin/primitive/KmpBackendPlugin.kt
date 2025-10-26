package primitive

import me.matsumo.zencall.bundle
import me.matsumo.zencall.library
import me.matsumo.zencall.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

class KmpBackendPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("org.jetbrains.kotlin.jvm")
                apply("io.ktor.plugin")
                apply("kotlinx-serialization")
            }

            dependencies {
                val supabaseBom = libs.library("supabase-bom")
                "implementation"(libs.bundle("ktor-server"))
                "implementation"(project.dependencies.platform(supabaseBom))
                "implementation"(libs.bundle("supabase"))
            }

            extensions.configure<KotlinJvmProjectExtension> {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_21)
                }
            }

            tasks.withType<KotlinJvmCompile>().configureEach {
                compilerOptions {
                    freeCompilerArgs.add("-Xskip-prerelease-check")
                }
            }
        }
    }
}
