package com.talhaoz.bitcointicker.models

import com.google.gson.annotations.SerializedName

data class PriceChangePercentage1hInCurrency(
    @SerializedName("usd")
    val usd_curr: String?,
    @SerializedName("try")
    val try_curr: String?,
    @SerializedName("eur")
    val eur_curr: String?,
    @SerializedName("gbp")
    val gbp_curr: String?
)