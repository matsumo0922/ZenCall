
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.ktor.server.application.Application
import io.ktor.server.application.install
import me.matsumo.zencall.core.common.formatter
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.initKoin() {
    logger.info("Supabase client initializing")

    val supabaseClient = createSupabaseClient(
        supabaseUrl = environment.config.propertyOrNull("ktor.security.supabaseUrl")?.getString()!!,
        supabaseKey = environment.config.propertyOrNull("ktor.security.supabaseKey")?.getString()!!,
    ) {
        defaultSerializer = KotlinXSerializer(formatter)
        install(Postgrest)
    }

    logger.info("Supabase client initialized. url:${supabaseClient.supabaseUrl}, key:${supabaseClient.supabaseKey}")

    install(Koin) {
        slf4jLogger()

        modules(
            module {
                single<SupabaseClient> { supabaseClient }
            },
        )
    }
}
