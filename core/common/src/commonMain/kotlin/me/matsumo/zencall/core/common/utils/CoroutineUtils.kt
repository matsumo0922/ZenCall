package me.matsumo.zencall.core.common.utils

import io.github.aakira.napier.Napier
import kotlin.coroutines.cancellation.CancellationException

suspend fun <T> suspendRunCatching(block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (cancellationException: CancellationException) {
    throw cancellationException
} catch (exception: Throwable) {
    Napier.i { "Failed to evaluate a suspendRunCatchingBlock. Returning failure Result.\n${exception.stackTraceToString().take(1000)}" }
    Result.failure(exception)
}
