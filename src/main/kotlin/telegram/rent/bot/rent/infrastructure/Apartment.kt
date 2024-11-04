package telegram.rent.bot.rent.infrastructure

import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import telegram.rent.bot.infrastructure.date.LocalDateTimeSerializer
import telegram.rent.bot.rent.infrastructure.extensions.datetime.toTimeFormat

@Serializable
data class Apartment(
    val photoLink: String,
    val location: Location,
    val price: Price,
    val type: Type,
    val announcement: Announcement
) {
    override fun toString(): String {
        return """
        🐳 *${type.typeName} за $price 💸*
        
        🏠 Место: *${location.address}*        
        ⌚ Время: *${announcement.updatedAt.toTimeFormat()}*        
  
        ${if(announcement.info != null) { " \uD83D\uDCC4 Описание: _${announcement.info.trimIndent()}_" } else ""}
        🌧🌧🌧🌧🌧🌧🌧🌧🌧🌧🌧🌧🌧
        
        📸 [Фото]($photoLink)
        
        🗺 [Карта](${location.mapLink})
        
        🔍 [Источник](${announcement.url})     _${announcement.site.name.lowercase().replaceFirstChar { it.uppercase() }}_
        
        """.trimIndent()
    }

    @Serializable
    data class Location(
        val city: String,
        val address: String,
        val coordinate: Coordinate,
        val mapLink: String
    ) {
        @Serializable
        data class Coordinate(
            val latitude: Double,
            val longitude: Double
        )
    }

    @Serializable
    data class Price(
        val amount: Double,
        val currency: Currency
    ): Comparable<Price> {
        override fun toString(): String {
            if (amount == 0.0) return "0$. Цена не указана"
            return "${amount.toInt()}${currency.symbols.first()}"
        }

        enum class Currency(val symbols: List<String>) {
            USD(listOf("$")),
            BYN(listOf("Br", "руб")),
            EUR(listOf("€")),
            RUB(listOf("₽"))
        }

        override fun compareTo(other: Price): Int {
            val otherAmount = when (other.currency) {
                Currency.USD -> other.amount
                Currency.BYN -> other.amount / BYN_USD
                Currency.RUB -> other.amount / RUB_USD
                Currency.EUR -> other.amount
            }
            return when (currency) {
                Currency.USD -> compareValues(amount, otherAmount)
                Currency.BYN -> compareValues((amount / BYN_USD), otherAmount)
                Currency.EUR -> compareValues(amount, otherAmount)
                Currency.RUB -> compareValues(amount / RUB_USD, otherAmount)
            }
        }

        companion object {
            private const val BYN_USD = 3.31
            private const val RUB_USD = 98.7
            private val minPrice = Price(270.0, Currency.USD)
            private val maxPrice = Price(410.0, Currency.USD)

            val priceDiaposon = minPrice..maxPrice
        }
    }

    enum class Type(val typeName: String) {
        ROOM("Комната"),
        STUDIO("Студия"),
        ONE_ROOMS("1-Комнатная"),
        TWO_ROOMS("2-Комнатная"),
        THREE_ROOMS("3-Комнатная"),
        FOUR_ROOMS("4-Комнатная"),
        FIVE_ROOMS("5-Комнатная"),
        SIX_ROOMS("6-Комнатная")
    }

    @Serializable
    data class Announcement(
        val url: String,
        @Serializable(LocalDateTimeSerializer::class)
        val updatedAt: LocalDateTime,
        val site: Site,
        val info: String? = null
    )
}
