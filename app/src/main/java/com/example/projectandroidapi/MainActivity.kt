package com.example.projectandroidapi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.projectandroidapi.api.Endpoint
import com.example.projectandroidapi.util.NetworkUtils
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    private lateinit var spFrom: Spinner
    private lateinit var spTo: Spinner
    private lateinit var btConvert: Button
    private lateinit var tvResult: TextView
    private lateinit var etValueFrom: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spFrom = findViewById(R.id.spFrom)
        spTo = findViewById(R.id.spTo)
        btConvert = findViewById(R.id.btConvert)
        tvResult = findViewById(R.id.tvResult)
        etValueFrom = findViewById(R.id.etValueFrom)

        // Carregar as moedas disponíveis
        getMoedas()

        // Ação de conversão quando o botão for clicado
        btConvert.setOnClickListener { getConversao() }
    }

    fun getConversao() {
        // Verifica se o campo de valor de entrada não está vazio
        if (etValueFrom.text.toString().isNotEmpty()) {
            val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
            val endpoint = retrofitClient.create(Endpoint::class.java)

            val baseMoeda = spFrom.selectedItem.toString()
            val alvoMoeda = spTo.selectedItem.toString()

            // Faz a requisição para obter a conversão a partir da moeda base
            endpoint.getConversao(baseMoeda).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    if (response.isSuccessful) {
                        val data = response.body()?.getAsJsonObject(baseMoeda)
                        if (data != null && data.has(alvoMoeda)) {
                            // Faz a conversão se o par de moedas for encontrado
                            val variacao: Double = data.get(alvoMoeda).asDouble
                            val valorConvertido = etValueFrom.text.toString().toDouble() * variacao
                            tvResult.text = String.format("%.2f", valorConvertido) // Exibe o resultado formatado com 2 casas decimais
                        } else {
                            tvResult.text = "Erro: Moeda de destino não encontrada."
                        }
                    } else {
                        tvResult.text = "Erro: Falha na resposta da API."
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // Exibe uma mensagem de erro no caso de falha na chamada
                    tvResult.text = "Erro: Não foi possível realizar a conversão."
                }
            })
        } else {
            tvResult.text = "Insira um valor para converter."
        }
    }

    fun getMoedas() {
        val retrofitClient = NetworkUtils.getRetrofitInstance("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(Endpoint::class.java)

        endpoint.getMoedas().enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("Erro: Não foi possível carregar as moedas.")
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val data = mutableListOf<String>()

                    response.body()?.keySet()?.iterator()?.forEach {
                        data.add(it)
                    }

                    val posBRL = data.indexOf("brl")
                    val posUSD = data.indexOf("usd")

                    val adapter = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, data)
                    spFrom.adapter = adapter
                    spTo.adapter = adapter

                    spFrom.setSelection(posBRL)
                    spTo.setSelection(posUSD)
                } else {
                    println("Erro: Falha ao carregar as moedas.")
                }
            }
        })
    }
}
