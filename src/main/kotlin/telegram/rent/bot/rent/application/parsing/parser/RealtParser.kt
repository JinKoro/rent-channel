package telegram.rent.bot.rent.application.parsing.parser

import io.ktor.client.call.body
import io.ktor.client.request.get
import java.time.LocalDateTime
import java.time.ZoneId
import org.json.XML
import telegram.rent.bot.rent.application.parsing.Parser
import telegram.rent.bot.rent.application.parsing.data.Realt
import telegram.rent.bot.rent.infrastructure.Apartment
import telegram.rent.bot.rent.infrastructure.HttpClient
import telegram.rent.bot.rent.infrastructure.Link

class RealtParser : Parser, HttpClient() {
    private var lastUpdated: LocalDateTime = LocalDateTime.now(ZoneId.of("Europe/Minsk"))

    override suspend fun parse(): List<Apartment> {
        return client.use { client ->
            client.get(Link.REALT_MINSK).body<String>()
                .let { json.decodeFromString(Realt.serializer(), XML.toJSONObject(it).toString()) }.apartments
                .mapNotNull { apartment ->
                    apartment.transform().takeIf { it.announcement.updatedAt > lastUpdated }
                }
                .also { apartments ->
                    if (apartments.isNotEmpty()) {
                        lastUpdated = apartments.maxOf { it.announcement.updatedAt }
                    }
                }
        }
    }
}
