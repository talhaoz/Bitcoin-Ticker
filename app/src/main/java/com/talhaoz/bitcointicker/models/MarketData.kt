package com.talhaoz.bitcointicker.models

data class MarketData(
    val current_price: CurrentPrice,
    val price_change_percentage_1h_in_currency: PriceChangePercentage1hInCurrency,
    val price_change_percentage_24h: String?,
    val price_change_percentage_7d: String?
)