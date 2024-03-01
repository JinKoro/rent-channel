package telegram.rent.bot.rent.application.sending

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import telegram.rent.bot.infrastructure.configuration.Config
import telegram.rent.bot.rent.infrastructure.Apartment
import telegram.rent.bot.rent.infrastructure.HttpClient

class TelegramSender(
    private val config: Config
) : Sender, HttpClient() {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val chat = config.rent.channels.first()

    override suspend fun send(apartment: Apartment) {
        client.use { client ->
            val response = client.post("https://api.telegram.org/bot${config.rent.bot.token}/sendMessage") {
                contentType(ContentType.Application.Json)
                setBody(Request(chat, apartment.toString(), ParseMode.MARKDOWN.modeName))
            }.body<Response>()

            if (!response.ok) {
                logger.error("Bad Sending:" + response.description)
            }
        }
    }
}

@Serializable
data class Response(
    val ok: Boolean,
    val description: String?
)

@Serializable
data class Request(
    @SerialName("chat_id")
    val chatId: String,
    val text: String,
    @SerialName("parse_mode")
    val parseMode: String
)

enum class ParseMode(val modeName: String) {
    MARKDOWN("Markdown"),
    HTML("HTML"),
    MARKDOWN_V2("MarkdownV2")
}