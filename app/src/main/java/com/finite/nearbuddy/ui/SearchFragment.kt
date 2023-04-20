package com.finite.nearbuddy.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.finite.nearbuddy.R
import com.finite.nearbuddy.databinding.FragmentSearchBinding
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

class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private val nearbyViewModel by activityViewModels<NearbyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.light_blue)

        val fragmentBinding = FragmentSearchBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        startAdvertising()


        binding?.discoverBtn?.setOnClickListener {

            if(binding?.discoverText?.text == "Stop") {
                binding?.discoverText?.text = "Discover"
                binding?.discoverBtn?.radius = 45.0F
                binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))
                binding?.searchLottie?.cancelAnimation()

                stopDiscovery()
                startAdvertising()

                Toast.makeText(activity, "Stopped Searching.", Toast.LENGTH_SHORT).show()
            } else {
                binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.red))
                binding?.discoverBtn?.radius = 45.0F
                binding?.discoverText?.text = "Stop"
                binding?.searchLottie?.playAnimation()

                stopAdvertising()
                startDiscovery()

                Toast.makeText(activity, "Searching for nearby users....", Toast.LENGTH_SHORT).show()
            }
        }


    }





    override fun onResume() {
        super.onResume()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.light_blue)
    }


    private fun startAdvertising() {
        val SERVICE_ID = "com.example.demonearbysend"
        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()
        Nearby.getConnectionsClient(requireActivity())
            .startAdvertising(
                "Rishabh123", SERVICE_ID, connectionLifecycleCallback, advertisingOptions
            )
            .addOnSuccessListener {
                Toast.makeText(requireActivity(), "Ready to Receive", Toast.LENGTH_SHORT).show()
                Log.d("Receiver", "Advertising success")
            }
            .addOnFailureListener { e ->
                Log.d("Receiver", "Advertising fail: ${e.toString()}")
                Toast.makeText(requireActivity(), "Error Initialising: ${e.toString()}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun startDiscovery() {
        val SERVICE_ID = "com.example.demonearbysend"
        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()
        Nearby.getConnectionsClient(requireActivity())
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                Toast.makeText(requireActivity(), "Connecting to peer", Toast.LENGTH_SHORT).show()
                Log.d("Sender", "Discovery success")
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireActivity(), "Discovery Error: ${e.toString()}", Toast.LENGTH_SHORT).show()
                Log.d("errorData", "startDiscovery: ${e.toString()}")
            }
    }

    private fun stopAdvertising() {
        Nearby.getConnectionsClient(requireActivity())
            .stopAdvertising()
    }

    private fun stopDiscovery() {
        Nearby.getConnectionsClient(requireActivity())
            .stopDiscovery()
    }

    private val endpointDiscoveryCallback: EndpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d("Sender", "onEndpointFound: endpoint found, connecting...")
            // An endpoint was found. We request a connection to it.
            Nearby.getConnectionsClient(requireActivity())
                .requestConnection("Rishabh123", endpointId, connectionLifecycleCallback) //change to faulty name- first param
                .addOnSuccessListener {
                    // Connection request successfully sent.
                    Log.d("Sender", "Connection request successfully sent.")
                }
                .addOnFailureListener { e: java.lang.Exception? ->
                    // Connection request failed.
                    Log.d("Sender", "Connection request failed: ${e.toString()}")
                }
        }

        override fun onEndpointLost(endpointId: String) {
            // A previously discovered endpoint has gone away.
            Log.d("Sender", "onEndpointLost: endpoint lost")
            var endptId = nearbyViewModel.endptId
            endptId = ""
        }
    }

    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            // Payload received.
            var endptId = nearbyViewModel.endptId
            if(endptId == endpointId) {
                val data_recieved = payload.asBytes()?.let { String(it, Charsets.UTF_8) }
                Toast.makeText(context, "Data received: $data_recieved", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Payload transfer update.
        }
    }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            // Automatically accept the connection on both sides.
            Nearby.getConnectionsClient(requireActivity()).acceptConnection(endpointId, payloadCallback)
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    // Once you have successfully connected to your friends' devices, you can leave
                    // discovery mode so you can stop discovering other devices

                    // if you were advertising, you can stop as well
                    Toast.makeText(requireActivity(), "EndID of Receiver: $endpointId", Toast.LENGTH_SHORT).show()
                    var endptId = nearbyViewModel.endptId
                    endptId = endpointId
                    nearbyViewModel.endptId = endpointId
                    nearbyViewModel.connectionClient = Nearby.getConnectionsClient(requireActivity())
                    val data = Payload.fromBytes("Hello Rishabh Mehta".toByteArray())
                    Nearby.getConnectionsClient(requireActivity()).sendPayload(endpointId, data)
                    Nearby.getConnectionsClient(requireActivity()).stopDiscovery()
                }
                else -> {
                    // Connection failed.
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            // We've been disconnected from this endpoint. No more data can be
            // sent or received.
            Log.d("Sender", "onDisconnected: Endpoint disconnected")
            var endptId = nearbyViewModel.endptId
            endptId = ""
        }
    }
}