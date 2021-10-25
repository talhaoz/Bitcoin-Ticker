package com.talhaoz.bitcointicker.models

data class CryptoSingleModel(
    val description: Description,
    val hashing_algorithm: String,
    val id: String,
    val image: Image,
    val market_data: MarketData,
    val name: String,
    val symbol: String
)