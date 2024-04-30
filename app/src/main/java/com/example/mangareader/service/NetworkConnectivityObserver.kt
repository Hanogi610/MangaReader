package com.example.mangareader.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class NetworkConnectivityObserver(
    private val context: Context
): ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _statusFlow = MutableSharedFlow<ConnectivityObserver.Status>()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            _statusFlow.tryEmit(ConnectivityObserver.Status.Available)
        }

        override fun onLosing(network: Network, maxMsToLive: Int) {
            super.onLosing(network, maxMsToLive)
            _statusFlow.tryEmit(ConnectivityObserver.Status.Losing)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            _statusFlow.tryEmit(ConnectivityObserver.Status.Lost)
        }

        override fun onUnavailable() {
            super.onUnavailable()
            _statusFlow.tryEmit(ConnectivityObserver.Status.Unavailable)
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    override fun observe(): Flow<ConnectivityObserver.Status> {
        return _statusFlow.shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(),
            replay = 1
        )
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}