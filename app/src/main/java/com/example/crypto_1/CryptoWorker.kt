package com.example.crypto_1

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class CryptoWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val viewModel = CryptoViewModel(applicationContext as android.app.Application)
        viewModel.fetchCryptoQuote(threshold = 0.0005)
        return Result.success()
    }
}
