package com.finite.nearbuddy.ui

import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.finite.nearbuddy.model.ChatMessage
import com.finite.nearbuddy.model.CustomPayload
import com.finite.nearbuddy.model.UserProfile
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
import com.google.gson.Gson
import java.text.SimpleDateFormat
import kotlin.math.abs
import kotlin.math.absoluteValue

class ConnectionViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    private var currentEndPoint: String = ""
    var isAdvertising = false
    var isDiscovering = false

    var user1 = UserProfile("", "", "", mapOf(), "".toByteArray(Charsets.UTF_8)) // current user
    var user2 = MutableLiveData(
        UserProfile(
            "",
            "",
            "",
            mapOf(),
            "".toByteArray(Charsets.UTF_8)
        )
    ) // user to connect to

    private val _chatMessages = MutableLiveData<MutableList<ChatMessage>>()
    val chatMessages: LiveData<MutableList<ChatMessage>> = _chatMessages

    val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected


    // Function to initialize the chatMessages list with an empty list
    init {
        _chatMessages.value = mutableListOf()
    }

    fun startAdvertising() {
        val SERVICE_ID = "com.finite.nearbuddy"
        val advertisingOptions =
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(context).startAdvertising(
            "Rishabh123", SERVICE_ID, connectionLifecycleCallback, advertisingOptions
        ).addOnSuccessListener {
            isAdvertising = true
            //Toast.makeText(context, "Advertising Started.", Toast.LENGTH_SHORT).show()
            Log.d("DebugData", "Advertising Started.")
        }.addOnFailureListener { e ->
            Log.w("DebugData", "Advertising Fail: $e")
            //Toast.makeText(context, "Error Adv: $e", Toast.LENGTH_SHORT).show()
        }
    }

    fun startDiscovery() {
        val SERVICE_ID = "com.finite.nearbuddy"
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        Nearby.getConnectionsClient(context)
            .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
            .addOnSuccessListener {
                isDiscovering = true
                Log.d("DebugData", "Discovery Started.")
            }.addOnFailureListener { e ->
                Log.w("DebugData", "Discovery Fail: $e")
            }
    }

    fun stopAdvertising() {
        Nearby.getConnectionsClient(context).stopAdvertising()
        isAdvertising = false

        Log.d("DebugData", "Advertising Stopped.")
    }

    fun stopDiscovery() {
        Nearby.getConnectionsClient(context).stopDiscovery()
        isDiscovering = false

        Log.d("DebugData", "Discovery Stopped.")
    }


    fun sendChatMessage(chatMessage: ChatMessage) {
        val gson = Gson()
        val userProfileJson = gson.toJson(CustomPayload(chatMessage = chatMessage))
        val data = Payload.fromBytes(userProfileJson.toByteArray(Charsets.UTF_8))
        Nearby.getConnectionsClient(context).sendPayload(currentEndPoint, data)
    }

    private val endpointDiscoveryCallback: EndpointDiscoveryCallback =
        object : EndpointDiscoveryCallback() {

            override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {

                Log.d("DebugData", "onEndpointFound: endpoint found, connecting to $info")
                // An endpoint was found. We request a connection to it.
                Nearby.getConnectionsClient(context).requestConnection(
                    "Rishabh123",
                    endpointId,
                    connectionLifecycleCallback
                ) //change to faulty name- first param
                    .addOnSuccessListener {
                        // Connection request successfully sent.
                        Log.d("DebugData", "Connection request successfully sent.")
                    }.addOnFailureListener { e: java.lang.Exception? ->
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
            //log payload received
            Log.d(
                "DebugData",
                "onPayloadReceived: payload received ${
                    payload.asBytes()?.let { String(it, Charsets.UTF_8) }
                }"
            )

            val receivedData = payload.asBytes()?.let { String(it, Charsets.UTF_8) }
            val userProfile = Gson().fromJson(receivedData, CustomPayload::class.java).userProfile
            val chatMessage = Gson().fromJson(receivedData, CustomPayload::class.java).chatMessage

            if (userProfile.name.isNotBlank() && userProfile.name.isNotEmpty()) {
                user2.value = userProfile
                Log.d("user2received", "onPayloadReceived: ${user2.value.toString()}")
            }

            if (chatMessage.content.isNotBlank() || chatMessage.content.isNotEmpty()) {
                addChatMessage(chatMessage)
            }


        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Payload transfer update.
            Log.d("DebugData", "onPayloadTransferUpdate: ${update.status}")
        }
    }

    private val connectionLifecycleCallback: ConnectionLifecycleCallback =
        object : ConnectionLifecycleCallback() {
            override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
                // Automatically accept the connection on both sides.
                Nearby.getConnectionsClient(context).acceptConnection(endpointId, payloadCallback)
                Log.d("DebugData", "onConnectionInitiated: ")
            }

            override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
                Log.d("DebugData", "onConnectionResult real: ${result.status.statusCode}}")
                when (result.status.statusCode) {
                    ConnectionsStatusCodes.STATUS_OK -> {
                        // Once you have successfully connected to your friends' devices, you can leave
                        // discovery mode so you can stop discovering other devices
                        Log.d("DebugData", "Reaching OK")
                        // endpoint id of the connected device
                        currentEndPoint = endpointId
                        //_isConnected.value = true

                        val data = Payload.fromBytes(getUserProfileByteArray())
                        Nearby.getConnectionsClient(context).sendPayload(endpointId, data).addOnSuccessListener {
                            Log.d("DebugData", "onConnectionResult: success")
                        }.addOnFailureListener { e ->
                            Log.d("DebugData", "onConnectionResult: fail ${e.toString()}")
                        }
                        Nearby.getConnectionsClient(context).stopDiscovery()
                        isDiscovering = false
                    }

                    else -> {
                        Log.d(
                            "DebugData",
                            "onConnectionResult: fail ${result.status.statusMessage}}"
                        )
                        // Connection failed.
                    }
                }
            }

            override fun onDisconnected(endpointId: String) {
                // We've been disconnected from this endpoint. No more data can be
                // sent or received.
                Log.d("DebugData", "onDisconnected: Endpoint disconnected")
                currentEndPoint = ""
                _isConnected.value = false
            }
        }


    // Function to add a new ChatMessage to the list
    fun addChatMessage(newMessage: ChatMessage) {
        val currentMessages = _chatMessages.value ?: mutableListOf()
        currentMessages.add(newMessage)
        _chatMessages.value = currentMessages
    }

    private fun calculateCompatibility(user1: UserProfile, user2: UserProfile): Int {
        // Ensure both users have the same interests
        if (user1.interests.keys != user2.interests.keys) {
            throw IllegalArgumentException("Both users must have the same interests")
        }

        // Calculate the Jaccard similarity coefficient for interests
        var totalSimilarity = 0.0
        for ((interest, rating1) in user1.interests) {
            val rating2 = user2.interests[interest] ?: 0
            val similarity = 1 - (rating1 - rating2).absoluteValue / 5.0
            totalSimilarity += similarity
        }

        val similarity = totalSimilarity / user1.interests.size

        // Convert the similarity score to a compatibility percentage between 50 and 100
        var compatibilityPercentage = ((similarity * 40 + 30)).toInt()

        // Add age and gender factors if they are not at maximum compatibility
        if (compatibilityPercentage < 100) {
            // Calculate age difference in years
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val date1 = dateFormat.parse(user1.dateOfBirth)
            val date2 = dateFormat.parse(user2.dateOfBirth)
            val ageDifference = abs(date1.year - date2.year)

            // Introduce age factor to adjust the compatibility score
            val ageFactor =
                25 - ageDifference.coerceAtMost(10) // Maximum age difference adjustment is 10

            // Introduce gender factor to adjust the compatibility score
            val genderFactor = if (user1.gender != user2.gender) 10 else -1

            // Adjust the compatibility score with age and gender factors
            compatibilityPercentage += ageFactor + genderFactor
        }

        return compatibilityPercentage.coerceIn(50, 100)
    }

    private fun getUserProfileByteArray(): ByteArray {
        // get the user's profile data from the shared preferences and add in userprofile object and return it
        val sharedPreferences =
            context.getSharedPreferences("profileDataPreference", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")!!
        val gender = sharedPreferences.getString("gender", "")!!
        val dob = sharedPreferences.getString("dob", "")!!
        val about = sharedPreferences.getString("about", "")

        val interestFood =
            sharedPreferences.getString("interestFood", "")!!.split(" | ").getOrNull(1)
                ?.toIntOrNull() ?: 0
        val interestReading =
            sharedPreferences.getString("interestReading", "")!!.split(" | ").getOrNull(1)
                ?.toIntOrNull() ?: 0
        val interestSwimming =
            sharedPreferences.getString("interestSwimming", "")!!.split(" | ").getOrNull(1)
                ?.toIntOrNull() ?: 0
        val interestProgramming =
            sharedPreferences.getString("interestProgramming", "")!!.split(" | ").getOrNull(1)
                ?.toIntOrNull() ?: 0
        val interestMovies =
            sharedPreferences.getString("interestMovies", "")!!.split(" | ").getOrNull(1)
                ?.toIntOrNull() ?: 0

        val interests = mapOf(
            "Food" to interestFood,
            "Reading" to interestReading,
            "Swimming" to interestSwimming,
            "Programming" to interestProgramming,
            "Movies" to interestMovies
        )

        val profilePic = sharedPreferences.getString("profileImage", "")
        val profilePicByteArray = Base64.decode(profilePic, Base64.DEFAULT)

        val gson = Gson()
        val userProfileJson = gson.toJson(
            CustomPayload(
                UserProfile(
                    name,
                    dob,
                    gender,
                    interests,
                    profilePicByteArray
                )
            )
        )

        // Convert JSON string to bytes and returning it
        return userProfileJson.toByteArray(Charsets.UTF_8)
    }

    // destroy the connection when the app is closed and the context is destroyed
    override fun onCleared() {
        super.onCleared()
        Nearby.getConnectionsClient(context).stopAllEndpoints()
    }

}