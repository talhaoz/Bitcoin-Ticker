package com.talhaoz.bitcointicker.viewmodel



import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.talhaoz.bitcointicker.api.RetrofitClient
import com.talhaoz.bitcointicker.db.DBHelper
import com.talhaoz.bitcointicker.models.CryptoAllCoinsModel
import com.talhaoz.bitcointicker.models.CryptoAllCoinsModelItem
import com.talhaoz.bitcointicker.models.CryptoSingleModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class CryptoViewModel : ViewModel() {

    var compositeDisposable: CompositeDisposable

    var coinsList: MutableLiveData<ArrayList<CryptoAllCoinsModelItem>>
    var singleCoin: MutableLiveData<CryptoSingleModel>



    init {
        compositeDisposable = CompositeDisposable()
        coinsList = MutableLiveData()
        singleCoin= MutableLiveData()

    }

    fun getData(idP : String?) : Boolean {


        val retrofitRes = RetrofitClient.loadData()

        lateinit var singleObj : Single<CryptoAllCoinsModel>
        lateinit var singleObj2 : Single<CryptoSingleModel>


        if(idP==null) {
            singleObj = retrofitRes.fetchAllCoins()
            getDataFromApi(singleObj)
        }
        else {
            singleObj2 = retrofitRes.getSingleCoinDetails(id = idP)
            getDataFromApi2(singleObj2)
        }



        return true
    }


    private fun getDataFromApi(singleObject : Single<CryptoAllCoinsModel>){

        compositeDisposable.add(
            singleObject
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<CryptoAllCoinsModel>(){
                    override fun onSuccess( it : CryptoAllCoinsModel) {

                        coinsList.value=it

                    }

                    override fun onError(e: Throwable) {

                        println("ERROR while fetching data!")
                        e.printStackTrace()

                    }

                })
        )
    }


    private fun getDataFromApi2(singleObject : Single<CryptoSingleModel>){

        compositeDisposable.add(
            singleObject
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<CryptoSingleModel>(){
                    override fun onSuccess( it : CryptoSingleModel) {

                        singleCoin.value=it

                    }

                    override fun onError(e: Throwable) {

                        println("ERROR while fetching data!")
                        e.printStackTrace()

                    }

                })
        )

    }

}