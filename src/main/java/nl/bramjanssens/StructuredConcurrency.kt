package nl.bramjanssens

import kotlinx.coroutines.*

suspend fun handle() = coroutineScope {
    awaitAll(
        async { t1() },
        async { t2() }
    )
}

fun t1() = 42

fun t2() = 1337

suspend fun <T> awaitAll(vararg results: Deferred<T>) = results.asList().awaitAll()

fun main() {
    runBlocking {
        handle()
    }
}

