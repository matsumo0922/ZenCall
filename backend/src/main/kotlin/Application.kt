import io.ktor.resources.Resource
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.serialization.Serializable
import me.matsumo.zencall.core.common.formatter
import route.revisionRoute

val logger = KtorSimpleLogger("ZenCall")

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(formatter)
    }
    install(Resources)
    install(CallLogging)
    initKoin()
    routes()
}

fun Application.routes() {
    revisionRoute()
}

@Serializable
sealed interface Route {
    @Serializable
    @Resource("/revision")
    data object Revision : Route
}
