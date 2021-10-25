package com.talhaoz.bitcointicker.models

import com.google.gson.annotations.SerializedName

data class CurrentPrice(
    @SerializedName("usd")
    val usd_curr: String?,
    @SerializedName("try")
    val try_curr: String?,
    @SerializedName("eur")
    val eur_curr: String?,
    @SerializedName("gbp")
    val gbp_curr: String?

)