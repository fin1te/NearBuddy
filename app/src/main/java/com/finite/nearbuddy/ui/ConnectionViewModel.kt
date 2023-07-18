package com.finite.nearbuddy.ui

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy

class ConnectionViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    private var currentEndPoint: String = ""
    var isAdvertising = false
    var isDiscovering = false

    fun startAdvertising() {
        val SERVICE_ID = "com.finite.nearbuddy"
        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()
        Nearby.getConnectionsClient(context)
            .startAdvertising(
                "Rishabh123", SERVICE_ID, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener {
                isAdvertising = true
                //Toast.makeText(context, "Advertising Started.", Toast.LENGTH_SHORT).show()
                Log.d("DebugData", "Advertising Started.")
            }
            .addOnFailureListener { e ->
                Log.d("DebugData", "Advertising fail: $e")
                //Toast.makeText(context, "Error Adv: $e", Toast.LENGTH_SHORT).show()
            }
    }

    fun startDiscovery() {
        val SERVICE_ID = "com.finite.nearbuddy"
        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()
        Nearby.getConnectionsClient(context)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                isDiscovering = true
                //Toast.makeText(context, "Discovery Started.", Toast.LENGTH_SHORT).show()
                Log.d("DebugData", "Discovery Started.")
            }
            .addOnFailureListener { e ->
                //Toast.makeText(context, "Discovery Error: $e", Toast.LENGTH_SHORT).show()
                Log.d("DebugData", "Error Dis: $e")
            }
    }

    fun stopAdvertising() {
        Nearby.getConnectionsClient(context)
            .stopAdvertising()
        isAdvertising = false
        //Toast.makeText(context, "Advertising Stopped.", Toast.LENGTH_SHORT).show()
        Log.d("DebugData", "stopAdvertising")
    }

    fun stopDiscovery() {
        Nearby.getConnectionsClient(context)
            .stopDiscovery()
        isDiscovering = false
        //Toast.makeText(context, "Discovery Stopped.", Toast.LENGTH_SHORT).show()
        Log.d("DebugData", "stopDiscovery")
    }


    // send a message to the connected device using the payload
    fun sendMessage(message: String) {
        val data = Payload.fromBytes(message.toByteArray())
        Nearby.getConnectionsClient(context).sendPayload(currentEndPoint, data)
    }

    private val endpointDiscoveryCallback: EndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {

        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {

            Log.d("DebugData", "onEndpointFound: endpoint found, connecting to $info")
            // An endpoint was found. We request a connection to it.
            Nearby.getConnectionsClient(context)
                .requestConnection("Rishabh123", endpointId, connectionLifecycleCallback) //change to faulty name- first param
                .addOnSuccessListener {
                    // Connection request successfully sent.
                    Log.d("DebugData", "Connection request successfully sent.")
                }
                .addOnFailureListener { e: java.lang.Exception? ->
                    // Connection request failed.
                    Log.d("DebugData", "Connection request failed: ${e.toString()}")
                }
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
            Log.d("DebugData", "onEndpointLost: endpoint lost")
        }
    }

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            val data_recieved = payload.asBytes()?.let { String(it, Charsets.UTF_8) }
            Toast.makeText(context, "Data received: $data_recieved", Toast.LENGTH_SHORT).show()
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Payload transfer update.
        }
    }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            // Automatically accept the connection on both sides.
            Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    // Once you have successfully connected to your friends' devices, you can leave
                    // discovery mode so you can stop discovering other devices

                    // if you were advertising, you can stop as well
                    currentEndPoint = endpointId
                    Toast.makeText(context, "EndID of Receiver: $endpointId", Toast.LENGTH_SHORT).show()
                    val data = Payload.fromBytes("Hello Rishabh Mehta".toByteArray())
                    Nearby.getConnectionsClient(context).sendPayload(endpointId, data)
                    Nearby.getConnectionsClient(context).stopDiscovery()
                }
                else -> {
                    // Connection failed.
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            Log.d("DebugData", "onDisconnected: Endpoint disconnected")
            currentEndPoint = ""
        }
    }

    // destroy the connection when the app is closed and the context is destroyed
    override fun onCleared() {
        super.onCleared()
        Nearby.getConnectionsClient(context).stopAllEndpoints()
    }

}