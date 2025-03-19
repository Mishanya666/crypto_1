package com.example.crypto_1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import android.util.Log

class CryptoViewModel(application: Application) : AndroidViewModel(application) {

    private val _cryptoQuote = MutableLiveData<CryptoQuote?>()
    val cryptoQuote: MutableLiveData<CryptoQuote?> get() = _cryptoQuote

    private val apiClient = ApiClient()
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var previousQuote: CryptoQuote? = null

    fun fetchCryptoQuote(threshold: Double = 1.0) {
        uiScope.launch {
            try {
                val newQuote = apiClient.getCryptoQuote()

                if (newQuote != null) {
                    Log.d("CryptoViewModel", "Новая котировка: USD=${newQuote.USD}, JPY=${newQuote.JPY}, RUB=${newQuote.RUB}")

                    if (previousQuote != null) {
                        val changeUSD = ((newQuote.USD - previousQuote!!.USD) / previousQuote!!.USD) * 100
                        Log.d("CryptoViewModel", "Изменение курса USD: $changeUSD%")

                        if (kotlin.math.abs(changeUSD) >= threshold) {
                            val direction = if (changeUSD > 0) "⬆️" else "⬇️"
                            val message = "Курс изменился на ${"%.2f".format(changeUSD)}% $direction"
                            Log.d("CryptoViewModel", message)
                            NotificationHelper.sendNotification(getApplication(), "Криптовалюта", message)
                        }
                    }

                    previousQuote = newQuote
                    _cryptoQuote.postValue(newQuote)

                } else {
                    Log.e("CryptoViewModel", "Не удалось получить курс")
                }
            } catch (e: Exception) {
                Log.e("CryptoViewModel", "Ошибка при получении данных", e)
            }
        }
    }



    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
