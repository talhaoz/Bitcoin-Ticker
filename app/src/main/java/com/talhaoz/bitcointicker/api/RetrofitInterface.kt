package com.talhaoz.bitcointicker.api

import com.talhaoz.bitcointicker.models.CryptoAllCoinsModel
import com.talhaoz.bitcointicker.models.CryptoSingleModel
import com.talhaoz.bitcointicker.models.FavoriteCoinsModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {


    @GET("coins/list")
    fun fetchAllCoins(

    ): Single<CryptoAllCoinsModel>


    @GET("coins/{id}")
    fun getSingleCoinDetails(
        @Path("id")
        id : String
    ): Single<CryptoSingleModel>

    @GET("coins/markets")
    fun getFavoriteCoinsDetails(
        @Query("vs_currency")
        vs_currency : String,
        @Query("ids")
        ids : String
    ): Single<FavoriteCoinsModel>



}