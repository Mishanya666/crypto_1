package com.example.crypto_1

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log
import java.io.IOException
class ApiClient {

    private val client: OkHttpClient
    private val gson: Gson = Gson()

    init {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    suspend fun getCryptoQuote(): CryptoQuote? {
        val url = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=USD,JPY,RUB"

        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    Log.d("ApiClient", "API Response: $jsonResponse")
                    if (jsonResponse != null) {

                        val quote = gson.fromJson(jsonResponse, CryptoQuote::class.java)
                        Log.d("ApiClient", "Parsed CryptoQuote: $quote")
                        return@withContext quote
                    } else {
                        Log.e("ApiClient", "Response body is empty")
                        return@withContext null
                    }
                } else {
                    Log.e("ApiClient", "API Response Error: ${response.code}")
                    return@withContext null
                }
            } catch (e: Exception) {
                Log.e("ApiClient", "Error fetching data", e)
                return@withContext null
            }
        }
    }
}
