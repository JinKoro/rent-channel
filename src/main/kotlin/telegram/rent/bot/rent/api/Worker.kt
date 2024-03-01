package telegram.rent.bot.rent.api

import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import telegram.rent.bot.rent.application.parsing.Parser
import telegram.rent.bot.rent.application.sending.Sender
import telegram.rent.bot.rent.infrastructure.Apartment
import telegram.rent.bot.rent.infrastructure.Apartment.Price.Companion.priceDiaposon

private const val RESTART_DELAY = 60000L
private const val RESEND_DELAY = 3100L

class Worker(
    private val parsers: List<Parser>,
    private val sender: Sender
): AbstractWorker(RESTART_DELAY) {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val rooms = listOf(Apartment.Type.STUDIO, Apartment.Type.ONE_ROOMS)

    override suspend fun process() {
        parsers
            .map { parser ->
                try { parser.parse()} catch (ignore: Exception) {
                    logger.error(
                        "Apartment parsing error with parser (${parser::class.simpleName}): " + ignore.message
                    ); emptyList()
                }
            }
            .flatten()
            .filter { it.type in rooms && it.price in priceDiaposon }
            .sortedBy { it.announcement.updatedAt }
            .forEach {
                sender.send(it)
                delay(RESEND_DELAY)
            }
    }
}