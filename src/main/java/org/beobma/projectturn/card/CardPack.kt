package org.beobma.projectturn.card

data class CardPack(
    val name: String,
    val description: List<String>,
    val cardList: MutableList<Card> = mutableListOf()
    ) {
    
}