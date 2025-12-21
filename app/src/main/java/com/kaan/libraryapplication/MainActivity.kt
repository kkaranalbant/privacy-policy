package com.kaan.libraryapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kaan.libraryapplication.presentation.ui.navigation.NavGraph
import com.kaan.libraryapplication.presentation.ui.theme.LibraryApplicationTheme

class MainActivity : ComponentActivity() {

    private lateinit var shakeDetector: com.kaan.libraryapplication.sensor.ShakeDetector
    private lateinit var networkReceiver: com.kaan.libraryapplication.broadcast.NetworkReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Platform Features
        shakeDetector = com.kaan.libraryapplication.sensor.ShakeDetector(this) {
             android.widget.Toast.makeText(this, "Shake Detected! Refreshing...", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        networkReceiver = com.kaan.libraryapplication.broadcast.NetworkReceiver()
        registerReceiver(networkReceiver, android.content.IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION))

        // Schedule Work
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.kaan.libraryapplication.worker.SyncWorker>(
            15, java.util.concurrent.TimeUnit.MINUTES
        ).build()
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SyncBooks", androidx.work.ExistingPeriodicWorkPolicy.KEEP, workRequest
        )

        enableEdgeToEdge()
        setContent {
            LibraryApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { paddingValues ->
                        NavGraph(
                            navController = navController,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.start() // Intentionally kept valid but usually should stop. 
        // Logic: Stop to save battery, but for demo keep it or stop it. 
        // Correct pattern:
        shakeDetector.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }
}
