package telegram.rent.bot.rent.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

abstract class AbstractWorker(
    private val interval: Long,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineScope by CoroutineScope(dispatcher) {

    fun start() = launch {
        println("Start parsing...")
        while (isActive) {
            try {
                process()
            } catch (logging: Throwable) {
                println("Worker exception: ${logging.cause?.message}")
            }

            delay(interval)
        }
    }

    protected abstract suspend fun process()
}
