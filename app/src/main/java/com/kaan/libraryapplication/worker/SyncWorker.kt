package com.kaan.libraryapplication.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kaan.libraryapplication.di.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // In a real app, this would get the repository from Dependency Injection
            // For now, we assume AppContainer is accessible or we initiate sync here
            AppContainer.bookRepository.refreshBooks()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
