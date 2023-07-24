package com.finite.nearbuddy.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.finite.nearbuddy.MainActivity
import com.finite.nearbuddy.R
import com.finite.nearbuddy.databinding.FragmentEditProfileBinding
import com.finite.nearbuddy.model.UserProfile
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone


class EditProfileFragment : Fragment() {

    // data binding
    private var binding: FragmentEditProfileBinding? = null
    private lateinit var ncvm: ConnectionViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.discoverBtn)

        ncvm = ViewModelProvider(requireActivity())[ConnectionViewModel::class.java]

        val fragmentBinding = FragmentEditProfileBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
            requireActivity().getSharedPreferences("profileDataPreference", Context.MODE_PRIVATE)

        // Updating the profile data with the data stored in shared preferences
        if (sharedPreferences.contains("name") && sharedPreferences.contains("gender") && sharedPreferences.contains(
                "dob"
            )
        ) {
            binding?.editTextName?.setText(sharedPreferences.getString("name", ""))
            binding?.textViewDOB?.text = sharedPreferences.getString("dob", "")
            binding?.aboutEditText?.editText?.setText(sharedPreferences.getString("about", ""))
            when (sharedPreferences.getString("gender", "")) {
                // when gender is male, select the male from drop down
                "Male" -> (binding?.genderMenu?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Male"
                )

                "Female" -> (binding?.genderMenu?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Female"
                )
            }

            // when food is selected, select the food from drop down
            when (sharedPreferences.getString("interestFood", "")) {
                "Food | 1" -> (binding?.foodDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Food | 1"
                )

                "Food | 2" -> (binding?.foodDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Food | 2"
                )

                "Food | 3" -> (binding?.foodDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Food | 3"
                )

                "Food | 4" -> (binding?.foodDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Food | 4"
                )

                "Food | 5" -> (binding?.foodDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Food | 5"
                )
            }

            // when reading is selected, select the reading from drop down
            when (sharedPreferences.getString("interestReading", "")) {
                "Reading | 1" -> (binding?.readingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Reading | 1"
                )

                "Reading | 2" -> (binding?.readingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Reading | 2"
                )

                "Reading | 3" -> (binding?.readingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Reading | 3"
                )

                "Reading | 4" -> (binding?.readingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Reading | 4"
                )

                "Reading | 5" -> (binding?.readingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Reading | 5"
                )
            }

            // when swimming is selected, select the swimming from drop down
            when (sharedPreferences.getString("interestSwimming", "")) {
                "Swimming | 1" -> (binding?.swimmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Swimming | 1"
                )

                "Swimming | 2" -> (binding?.swimmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Swimming | 2"
                )

                "Swimming | 3" -> (binding?.swimmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Swimming | 3"
                )

                "Swimming | 4" -> (binding?.swimmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Swimming | 4"
                )

                "Swimming | 5" -> (binding?.swimmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Swimming | 5"
                )
            }

            // when programming is selected, select the programming from drop down
            when (sharedPreferences.getString("interestProgramming", "")) {
                "Programming | 1" -> (binding?.programmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Programming | 1"
                )

                "Programming | 2" -> (binding?.programmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Programming | 2"
                )

                "Programming | 3" -> (binding?.programmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Programming | 3"
                )

                "Programming | 4" -> (binding?.programmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Programming | 4"
                )

                "Programming | 5" -> (binding?.programmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Programming | 5"
                )
            }

            // when movies is selected, select the movies from drop down
            when (sharedPreferences.getString("interestMovies", "")) {
                "Movies | 1" -> (binding?.moviesDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Movies | 1"
                )

                "Movies | 2" -> (binding?.moviesDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Movies | 2"
                )

                "Movies | 3" -> (binding?.moviesDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Movies | 3"
                )

                "Movies | 4" -> (binding?.moviesDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Movies | 4"
                )

                "Movies | 5" -> (binding?.moviesDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(
                    "Movies | 5"
                )
            }
        }

        if (requireActivity() is MainActivity) {
            binding!!.editProfileTitle.text = "Edit Profile"
        } else if (requireActivity() is SplashActivity) {
            binding!!.editProfileTitle.text = "Complete Profile"
        }

        val genderList = arrayOf("Male", "Female")
        (binding!!.genderMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(genderList)

        val foodList = arrayOf("", "Food | 1", "Food | 2", "Food | 3", "Food | 4", "Food | 5")
        (binding!!.foodDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(foodList)

        val readingList =
            arrayOf("", "Reading | 1", "Reading | 2", "Reading | 3", "Reading | 4", "Reading | 5")
        (binding!!.readingDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            readingList
        )

        val swimmingList = arrayOf(
            "", "Swimming | 1", "Swimming | 2", "Swimming | 3", "Swimming | 4", "Swimming | 5"
        )
        (binding!!.swimmingDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            swimmingList
        )

        val programmingList = arrayOf(
            "",
            "Programming | 1",
            "Programming | 2",
            "Programming | 3",
            "Programming | 4",
            "Programming | 5"
        )
        (binding!!.programmingDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            programmingList
        )

        val moviesList =
            arrayOf("", "Movies | 1", "Movies | 2", "Movies | 3", "Movies | 4", "Movies | 5")
        (binding!!.moviesDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            moviesList
        )


        binding?.saveChanges?.setOnClickListener {

            val sharedPreferences = requireActivity().getSharedPreferences(
                "profileDataPreference", Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putString("name", binding!!.editTextName.text.toString())
            editor.putString("gender", binding!!.genderMenu.editText?.text.toString())
            editor.putString("dob", binding!!.textViewDOB.text.toString())
            editor.putString("about", binding!!.aboutEditText.editText?.text.toString())
            editor.putString("interestFood", binding!!.foodDropDown.editText?.text.toString())
            editor.putString("interestReading", binding!!.readingDropDown.editText?.text.toString())
            editor.putString(
                "interestSwimming", binding!!.swimmingDropDown.editText?.text.toString()
            )
            editor.putString(
                "interestProgramming", binding!!.programmingDropDown.editText?.text.toString()
            )
            editor.putString("interestMovies", binding!!.moviesDropDown.editText?.text.toString())


            val drawable: Drawable? = binding!!.profileImage.drawable


// Step 2: Convert the Drawable to a Bitmap
            val bitmap: Bitmap = if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else {
                val width = drawable?.intrinsicWidth ?: 0
                val height = drawable?.intrinsicHeight ?: 0
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                    val canvas = Canvas(this)
                    drawable?.setBounds(0, 0, canvas.width, canvas.height)
                    drawable?.draw(canvas)
                }
            }

// Step 3: Compress the Bitmap to a byte array (Choose the format as needed)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val byteArray: ByteArray = outputStream.toByteArray()


            // Save the ByteArray to SharedPreferences
            editor.putString("profileImage", Base64.encodeToString(byteArray, Base64.DEFAULT))

            editor.apply()

            ncvm.user1 = UserProfile(
                binding!!.editTextName.text.toString(),
                binding!!.textViewDOB.text.toString(),
                binding!!.genderMenu.editText?.text.toString(),
                mapOf(
                    "Food" to binding!!.foodDropDown.editText?.text.toString().split("|").last()
                        .trim().toInt(),
                    "Reading" to binding!!.readingDropDown.editText?.text.toString().split("|")
                        .last().trim().toInt(),
                    "Swimming" to binding!!.swimmingDropDown.editText?.text.toString().split("|")
                        .last().trim().toInt(),
                    "Programming" to binding!!.programmingDropDown.editText?.text.toString()
                        .split("|").last().trim().toInt(),
                    "Movies" to binding!!.moviesDropDown.editText?.text.toString().split("|").last()
                        .trim().toInt()
                ),
                byteArray
            )

            // if the parent activity is splash activity, then navigate to main activity
            if (requireActivity() is SplashActivity) {
                val splashActivity = requireActivity() as SplashActivity
                splashActivity.navigateToMainActivity()
            } else if (requireActivity() is MainActivity) {
                Log.d("profileUpdated U1", ncvm.user1.toString())
                Log.d("profileUpdated U2", ncvm.user2.toString())
                requireActivity().onBackPressed()
            }
        }

        val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build()

        datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selectedDateInMillis

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val selectedDate = dateFormat.format(calendar.time)

            binding!!.textViewDOB.text = selectedDate
        }

        binding!!.textViewDOB.setOnClickListener {
            datePicker.show(requireActivity().supportFragmentManager, "date_picker")
        }

    }
}