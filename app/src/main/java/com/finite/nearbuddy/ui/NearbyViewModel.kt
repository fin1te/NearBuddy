package com.finite.nearbuddy.ui

import androidx.lifecycle.ViewModel
import com.google.android.gms.nearby.Nearby


class NearbyViewModel () : ViewModel() {



    var endptId = ""
    var connectionClient : com.google.android.gms.nearby.connection.ConnectionsClient? = null

}