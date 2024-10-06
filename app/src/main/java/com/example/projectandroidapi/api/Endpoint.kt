package com.example.projectandroidapi.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface Endpoint {
    @GET("/npm/@fawazahmed0/currency-api@latest/v1/currencies.json")
    fun getMoedas() : Call<JsonObject>

    @GET("/npm/@fawazahmed0/currency-api@latest/v1/currencies/{from}.json")
    fun getConversao(@Path(value = "from", encoded = true) from : String) : Call<JsonObject>
}