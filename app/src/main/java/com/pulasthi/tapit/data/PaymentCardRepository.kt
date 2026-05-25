package com.pulasthi.tapit.data

import com.pulasthi.tapit.viewmodel.SavedCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * In-memory store for wallet cards shared between Wallet and Pay Bills flows.
 */
object PaymentCardRepository {

    private val defaultCards = listOf(
        SavedCard(
            id = "1",
            holderName = "W.M.P.A.Bandara",
            maskedNumber = "1536 1154 **** ****",
        ),
        SavedCard(
            id = "2",
            holderName = "R.M.K.T.Rathnayaka",
            maskedNumber = "9865 2343 **** ****",
        ),
    )

    private val _cards = MutableStateFlow(defaultCards)
    val cards: StateFlow<List<SavedCard>> = _cards.asStateFlow()

    fun snapshot(): List<SavedCard> = _cards.value

    fun addCard(card: SavedCard) {
        _cards.update { it + card }
    }

    fun removeCards(ids: Set<String>) {
        if (ids.isEmpty()) return
        _cards.update { list -> list.filter { it.id !in ids } }
    }
}
