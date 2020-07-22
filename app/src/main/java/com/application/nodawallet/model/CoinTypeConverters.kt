package com.application.nodawallet.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CoinTypeConverters {


    @TypeConverter
    fun toCoinList(value: String): List<CoinList> {
        val gson = Gson()
        val type = object : TypeToken<List<CoinList>>() {}.type
        return gson.fromJson(value, type)
    }


    @TypeConverter
    fun toJson(value: List<CoinList>): String {
        val gson = Gson()
        val type = object : TypeToken<List<CoinList>>() {}.type
        return gson.toJson(value, type)
    }



}