package com.finite.nearbuddy.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.finite.nearbuddy.MainActivity
import com.finite.nearbuddy.R


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        this.window.statusBarColor = this.resources.getColor(R.color.purple_200)



        Handler().postDelayed({

            val sharedPreferences = getSharedPreferences("profileDataPreference", MODE_PRIVATE)
            if (sharedPreferences.contains("name") && sharedPreferences.contains("dob") && sharedPreferences.contains(
                    "gender"
                )
            ) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {

                // hide the splash layout and visible the new user layout
                val splashLayout =
                    findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.splashLayout)
                splashLayout.visibility = android.view.View.GONE
                val newUserLayout =
                    findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.newUserLayout)
                newUserLayout.visibility = android.view.View.VISIBLE

                // inflate the newuserfragmentcontainer with edit profile fragment
                val editProfileFragment = EditProfileFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.newUserFragmentContainerView, editProfileFragment)
                transaction.commit()
            }

        }, 4000)
    }

    fun navigateToMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}