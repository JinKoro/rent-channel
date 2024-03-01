package telegram.rent.bot.rent.api

import kotlinx.coroutines.delay
import telegram.rent.bot.infrastructure.configuration.Config
import telegram.rent.bot.rent.application.parsing.Parser
import telegram.rent.bot.rent.application.sending.Sender
import telegram.rent.bot.rent.infrastructure.Apartment
import telegram.rent.bot.rent.infrastructure.Apartment.Price.Companion.priceDiaposon

private const val RESEND_DELAY = 3100L

class Worker(
    private val parsers: List<Parser>,
    private val sender: Sender,
    config: Config
): AbstractWorker(config.rent.delay) {

    private val rooms = listOf(Apartment.Type.STUDIO, Apartment.Type.ONE_ROOMS)

    override suspend fun process() {
        parsers
            .map { parser ->
                try { parser.parse()} catch (ignore: Exception) {
                    println(
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
