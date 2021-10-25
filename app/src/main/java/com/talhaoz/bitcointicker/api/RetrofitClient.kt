package com.talhaoz.bitcointicker.api


import android.widget.Toast
import com.talhaoz.martianstalker.util.Constants.Companion.BASE_URL
import com.talhaoz.martianstalker.util.Constants.Companion.CONNECTION_TIMEOUT
import dagger.Module
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitClient {

    companion object {

        fun loadData() : RetrofitInterface {

            RxJavaPlugins.setErrorHandler {
                if( it is UndeliverableException){
                    //Toast.makeText(view?.getContext(), "UndeliverableException: " + it.message, Toast.LENGTH_LONG).show()
                    println(it.message)
                    return@setErrorHandler
                }
            }

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(RetrofitInterface::class.java)

        }


        private fun getHttpClient() : OkHttpClient
        {
            val httpClient = OkHttpClient.Builder()

            httpClient.readTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            httpClient.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
            httpClient.writeTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)

            return httpClient.build()
        }


    }
}