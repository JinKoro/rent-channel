package telegram.rent.bot.rent.api

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

abstract class AbstractWorker(
    private val interval: Long,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
) : CoroutineScope by CoroutineScope(dispatcher) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun start() = launch {
        while (isActive) {
            try {
                process()
            } catch (logging: Throwable) {
                logger.error("Worker exception: $logging", logging)
            }

            delay(interval)
        }
    }

    protected abstract suspend fun process()
}
