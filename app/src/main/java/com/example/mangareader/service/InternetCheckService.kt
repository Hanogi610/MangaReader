package com.example.mangareader.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class InternetCheckService : Service() {

    private val networkChangeReceiver = NetworkChangeReceiver()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        // Register network change receiver
        registerReceiver(networkChangeReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))

        // Register local receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // Internet is available
                // Do something
            }
        }, IntentFilter("INTERNET_AVAILABLE"))
    }

    override fun onDestroy() {
        super.onDestroy()

        // Unregister network change receiver
        unregisterReceiver(networkChangeReceiver)
    }
}