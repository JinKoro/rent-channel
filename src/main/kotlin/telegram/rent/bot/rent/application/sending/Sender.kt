package telegram.rent.bot.rent.application.sending

import telegram.rent.bot.rent.infrastructure.Apartment

interface Sender {

    suspend fun send(apartment: Apartment)
}