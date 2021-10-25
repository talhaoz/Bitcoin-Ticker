package com.talhaoz.bitcointicker.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.talhaoz.bitcointicker.models.CryptoAllCoinsModelItem
import com.talhaoz.bitcointicker.models.FavoriteCoinsModelItem

class DBHelper(val context: Context?) : SQLiteOpenHelper(context,DBHelper.DATABASE_NAME,null,DBHelper.DATABASE_VERSION)
{
    private val TABLE_NAME="Coins"
    private val TABLE_NAME_FAVORITE="Favorites"
    private val TABLE_NAME_VALUES="CoinValues"
    private val COL_ID = "id"

    private val COIN_ID = "coinId"
    private val COIN_NAME = "coinName"
    private val COIN_SYMBOL = "coinSymbol"
    private val COIN_VALUE = "coinSymbol"

    val dataLoading = MutableLiveData<Boolean>()
    /*private val COIN_PRICE = "coinPrice"
    private val COIN_Image = "coinImage"
    private val COIN_PRICE_CHANGE_1h = "coinPriceChange1h"
    private val COIN_PRICE_CHANGE_24h = "coinPriceChange24h" */


    companion object {
        private val DATABASE_NAME = "SQLITE_DATABASE"
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE $TABLE_NAME ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COIN_ID VARCHAR(256),$COIN_NAME  VARCHAR(256),$COIN_SYMBOL  VARCHAR(256))"
        val createTable2 = "CREATE TABLE $TABLE_NAME_FAVORITE ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COIN_ID VARCHAR(256))"
        val createTable3 = "CREATE TABLE $TABLE_NAME_VALUES ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COIN_ID VARCHAR(256),$COIN_NAME VARCHAR(256),$COIN_VALUE  VARCHAR(256))"
        db?.execSQL(createTable)
        db?.execSQL(createTable2)
        db?.execSQL(createTable3)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insertData(coinList : ArrayList<CryptoAllCoinsModelItem>){
        val sqliteDB = this.writableDatabase

        dataLoading.postValue(true)
        for(coin : CryptoAllCoinsModelItem in coinList)
        {
            println(coin.id)
            val contentValues = ContentValues()
            contentValues.put(COIN_ID , coin.id)
            contentValues.put(COIN_NAME, coin.name)
            contentValues.put(COIN_SYMBOL, coin.symbol)

            val result = sqliteDB.insert(TABLE_NAME,null,contentValues)
        }

        sqliteDB.close()

        dataLoading.postValue(false)
        println("${coinList.size} data successfully inserted!!!!")
        //Toast.makeText(context,if(result != -1L) "Kayıt Başarılı" else "Kayıt yapılamadı.", Toast.LENGTH_SHORT).show()
    }

    fun insertFavoriteCoins(coinList : ArrayList<String>)
    {
        val sqliteDB = this.writableDatabase
        sqliteDB.delete(TABLE_NAME_FAVORITE,null,null)

        for(coin : String in coinList)
        {
            println(coin)
            val contentValues = ContentValues()
            contentValues.put(COIN_ID , coin)

            val result = sqliteDB.insert(TABLE_NAME_FAVORITE,null,contentValues)
        }

        sqliteDB.close()
    }

    fun insertFavoriteCoinsValues(coinList : ArrayList<FavoriteCoinsModelItem>)
    {
        val sqliteDB = this.writableDatabase
        sqliteDB.delete(TABLE_NAME_VALUES,null,null)

        for(coin : FavoriteCoinsModelItem in coinList)
        {

            val contentValues = ContentValues()
            contentValues.put(COIN_ID , coin.id)
            contentValues.put(COIN_NAME, coin.name)
            contentValues.put(COIN_VALUE, coin.current_price)

            val result = sqliteDB.insert(TABLE_NAME_VALUES,null,contentValues)
        }

        sqliteDB.close()
    }

    fun readFavoriteCoinsValues() : ArrayList<FavoriteCoinsModelItem>
    {
        val coinList = arrayListOf<FavoriteCoinsModelItem>()
        val sqliteDB = this.readableDatabase


        val result = sqliteDB.rawQuery("SELECT * FROM $TABLE_NAME_VALUES",null)
        if(result.moveToFirst()){
            do {
                val coin = FavoriteCoinsModelItem()
                coin.id = result.getString(result.getColumnIndex(COIN_ID))
                coin.name = result.getString(result.getColumnIndex(COIN_NAME))
                coin.current_price = result.getString(result.getColumnIndex(COIN_VALUE))

                coinList.add(coin)
            }while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()
        return coinList
    }

    fun deleteFavoriteCoinValues()
    {
        val sqliteDB = this.writableDatabase
        sqliteDB.delete(TABLE_NAME_VALUES,null,null)
        sqliteDB.close()
    }

    fun readFavoriteCoins() : String
    {
        var coinList = ""
        val sqliteDB = this.readableDatabase

        var query = "SELECT * FROM $TABLE_NAME_FAVORITE"
        val result = sqliteDB.rawQuery(query,null)
        if(result.moveToFirst()){
            do {
                val coin = result.getString(result.getColumnIndex(COIN_ID))
                coinList += coin + ","
            }while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()

        return coinList
    }

    fun readData(queryStr : String?): ArrayList<CryptoAllCoinsModelItem>{
        val coinList = arrayListOf<CryptoAllCoinsModelItem>()
        val sqliteDB = this.readableDatabase

        var query : String
        if(queryStr==null)
            query = "SELECT * FROM $TABLE_NAME"
        else
            query = "SELECT * FROM $TABLE_NAME WHERE $COIN_NAME LIKE '%$queryStr%' OR $COIN_SYMBOL LIKE '%$queryStr%'"

        val result = sqliteDB.rawQuery(query,null)
        if(result.moveToFirst()){
            do {
                val coin = CryptoAllCoinsModelItem()
                coin.id = result.getString(result.getColumnIndex(COIN_ID))
                coin.name = result.getString(result.getColumnIndex(COIN_NAME))
                coin.symbol = result.getString(result.getColumnIndex(COIN_SYMBOL))

                coinList.add(coin)
            }while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()
        return coinList
    }

    fun getCoinByID(coinId : String) : CryptoAllCoinsModelItem
    {
        var currCoin = CryptoAllCoinsModelItem()
        val sqliteDB = this.readableDatabase
        var query = "SELECT * FROM $TABLE_NAME WHERE $COIN_ID = '$coinId'"
        val result = sqliteDB.rawQuery(query,null)

        if(result.moveToFirst()){
            do {
                currCoin.id = result.getString(result.getColumnIndex(COIN_ID))
                currCoin.name = result.getString(result.getColumnIndex(COIN_NAME))
                currCoin.symbol = result.getString(result.getColumnIndex(COIN_SYMBOL))

            }while (result.moveToNext())
        }
        result.close()
        sqliteDB.close()
        return currCoin
    }

    fun getNumberOfCoins() : Int
    {
        val sqliteDB = this.readableDatabase
        val count = sqliteDB.rawQuery("SELECT count(*) FROM $TABLE_NAME",null).count

        return count
    }

    fun deleteAllData(){
        val sqliteDB = this.writableDatabase
        sqliteDB.delete(TABLE_NAME,null,null)
        sqliteDB.close()

    }


}