package com.finite.nearbuddy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.finite.nearbuddy.databinding.ActivityMainBinding
import com.google.android.gms.nearby.Nearby

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding : ActivityMainBinding


    companion object {
        private const val RC_PERMISSIONS = 254
    }



    override fun onStart() {
        super.onStart()
        setupPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val window = this.window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        window.statusBarColor = this.resources.getColor(R.color.purple_500)


//        val nearbyViewModel = NearbyViewModel(application)

//        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application))
//            .get(nearbyViewModel::class.java)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList

        val wifi: WifiManager = getApplicationContext().getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifi.isWifiEnabled = true
        wifi.isWifiEnabled = false


    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    // Runtime Permissions

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if(permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.NEARBY_WIFI_DEVICES), RC_PERMISSIONS)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_PERMISSIONS -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need to give permissions!", Toast.LENGTH_SHORT).show()
                } else {
                    // working
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Nearby.getConnectionsClient(applicationContext).stopDiscovery()
        Log.d("Sender","Discovery stopped")
    }


}