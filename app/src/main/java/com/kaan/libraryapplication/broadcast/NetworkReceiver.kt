package com.kaan.libraryapplication.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            
            if (isConnected) {
                Toast.makeText(context, "Network Connected", Toast.LENGTH_SHORT).show()
                // Could trigger sync here
            } else {
                Toast.makeText(context, "Network Disconnected", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
