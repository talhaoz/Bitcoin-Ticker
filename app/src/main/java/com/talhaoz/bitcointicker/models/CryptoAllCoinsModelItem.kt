package com.talhaoz.bitcointicker.models

import com.google.gson.annotations.SerializedName

data class CryptoAllCoinsModelItem(
    @SerializedName("id")
    var id: String="",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("symbol")
    var symbol: String = ""
)