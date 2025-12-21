package com.kaan.libraryapplication.connectivity

import android.bluetooth.BluetoothAdapter
import android.content.Context

class ConnectivityManager(private val context: Context) {
    
    fun isBluetoothEnabled(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return bluetoothAdapter?.isEnabled == true
    }
    
    // Additional methods for WiFi etc. can be added here
}
