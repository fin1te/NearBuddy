package com.finite.nearbuddy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.finite.nearbuddy.databinding.ActivityMainBinding
import com.finite.nearbuddy.model.UserProfile
import com.finite.nearbuddy.ui.ConnectionViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ConnectionViewModel

    companion object {
        private const val RC_PERMISSIONS = 254
    }


    override fun onStart() {
        super.onStart()
        setupPermissions()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ConnectionViewModel::class.java]


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.itemIconTintList

        val wifi: WifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifi.isWifiEnabled = true
        wifi.isWifiEnabled = false

        // check if user is there in shared preferences, if yes, then set the user profile in viewmodel
        val sharedPreferences = getSharedPreferences("profileDataPreference", Context.MODE_PRIVATE)

        if (sharedPreferences.contains("name") && sharedPreferences.contains("gender") && sharedPreferences.contains(
                "dob"
            )
        ) {

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
//            val profilePicBitmap =
//                BitmapFactory.decodeByteArray(profilePicByteArray, 0, profilePicByteArray.size)
//
//            Log.d("CheckBitmap", "${profilePicBitmap == null}")
//            Log.d("CheckByteArray", "${profilePicByteArray == null}")

            viewModel.user1 = UserProfile(name, dob, gender, interests, profilePicByteArray)

            Log.d("profileUpdated to vm from main", viewModel.user1.profilePic.size.toString())
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    // Runtime Permissions

    private fun setupPermissions() {
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ), RC_PERMISSIONS
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            RC_PERMISSIONS -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need to give permissions!", Toast.LENGTH_SHORT).show()
                } else {
                    // working
                }
            }
        }
    }
}