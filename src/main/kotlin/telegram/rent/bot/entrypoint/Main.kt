package telegram.rent.bot.entrypoint

import kotlinx.coroutines.runBlocking
import telegram.rent.bot.infrastructure.configuration.Configuration
import telegram.rent.bot.rent.api.Worker
import telegram.rent.bot.rent.application.parsing.Parser
import telegram.rent.bot.rent.application.parsing.parser.KufarParser
import telegram.rent.bot.rent.application.parsing.parser.OnlinerParser
import telegram.rent.bot.rent.application.parsing.parser.RealtParser
import telegram.rent.bot.rent.application.sending.TelegramSender

fun main() {
    val configuration = Configuration.load()
    val parsers = listOf<Parser>(
        KufarParser(),
        RealtParser(),
        OnlinerParser()
    )
    val sender = TelegramSender(configuration)

    Worker(parsers, sender).start()

    runBlocking {
        while (true) {
            // Nothing
        }
    }
}
