package com.kaan.libraryapplication.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.kaan.libraryapplication.BuildConfig
import com.kaan.libraryapplication.data.local.db.LibraryDatabase
import com.kaan.libraryapplication.data.remote.api.AuthApiService
import com.kaan.libraryapplication.data.remote.api.LibraryApiService
import com.kaan.libraryapplication.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.PREFERENCES_NAME
)

/**
 * Simple dependency injection container
 * Holds all app-level dependencies as singletons
 */
object AppContainer {

    private lateinit var applicationContext: Context

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

    // DataStore
    val dataStore: DataStore<Preferences> by lazy {
        applicationContext.dataStore
    }

    // Database
    val database: LibraryDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            LibraryDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // DAOs
    val userDao by lazy { database.userDao() }
    val bookDao by lazy { database.bookDao() }
    val favoriteDao by lazy { database.favoriteDao() }
    val reviewDao by lazy { database.reviewDao() }

    // Network - Moshi
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // Network - OkHttp
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                com.kaan.libraryapplication.data.remote.AuthInterceptor(dataStore)
            )
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .connectTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(Constants.TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    // Network - Retrofit
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    // API Services
    val libraryApiService: LibraryApiService by lazy {
        retrofit.create(LibraryApiService::class.java)
    }

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    // Repositories
    val authRepository: com.kaan.libraryapplication.data.repository.AuthRepository by lazy {
        com.kaan.libraryapplication.data.repository.AuthRepository(userDao, authApiService, dataStore)
    }

    val bookRepository: com.kaan.libraryapplication.data.repository.BookRepository by lazy {
        com.kaan.libraryapplication.data.repository.BookRepository(bookDao, libraryApiService)
    }
}
