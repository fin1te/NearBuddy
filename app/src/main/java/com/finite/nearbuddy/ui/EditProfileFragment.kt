package com.finite.nearbuddy.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.loader.content.CursorLoader
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

        val paddingInDp = 80
        val scale: Float = resources.displayMetrics.density
        val paddingInPixels = (paddingInDp * scale + 0.5f).toInt()

        if (requireActivity() is MainActivity) {
            binding!!.editProfileTitle.text = "Edit Profile"
            binding!!.detailsLayout.setPadding(0, 0, 0, paddingInPixels)
        } else if (requireActivity() is SplashActivity) {
            binding!!.editProfileTitle.text = "Complete Profile"
            binding!!.detailsLayout.setPadding(0, 0, 0, (paddingInPixels)/16)
        }

        val sharedPreferences = requireActivity().getSharedPreferences("profileDataPreference", Context.MODE_PRIVATE)

        // Updating the profile data with the data stored in shared preferences
        if (sharedPreferences.contains("name") && sharedPreferences.contains("gender") && sharedPreferences.contains("dob")) {
            binding?.editTextName?.setText(sharedPreferences.getString("name", ""))
            binding?.textViewDOB?.text = sharedPreferences.getString("dob", "")
            binding?.aboutEditText?.editText?.setText(sharedPreferences.getString("about", ""))

            // set the profile image
            val profileImage = sharedPreferences.getString("profileImage", "")
            if (profileImage != "") {
                val decodedString: ByteArray = Base64.decode(profileImage, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                binding?.profileImage?.setImageBitmap(decodedByte)
            }

            (binding?.genderMenu?.editText as? MaterialAutoCompleteTextView)?.setText(sharedPreferences.getString("gender", ""))
            (binding?.foodDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(sharedPreferences.getString("interestFood", ""))
            (binding?.readingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(sharedPreferences.getString("interestReading", ""))
            (binding?.swimmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(sharedPreferences.getString("interestSwimming", ""))
            (binding?.programmingDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(sharedPreferences.getString("interestProgramming", ""))
            (binding?.moviesDropDown?.editText as? MaterialAutoCompleteTextView)?.setText(sharedPreferences.getString("interestMovies", ""))
        }

        // set the drop down items
        val genderList = arrayOf("Male", "Female")
        val foodList = arrayOf("", "Food | 1", "Food | 2", "Food | 3", "Food | 4", "Food | 5")
        val readingList = arrayOf("", "Reading | 1", "Reading | 2", "Reading | 3", "Reading | 4", "Reading | 5")
        val swimmingList = arrayOf("", "Swimming | 1", "Swimming | 2", "Swimming | 3", "Swimming | 4", "Swimming | 5")
        val moviesList = arrayOf("", "Movies | 1", "Movies | 2", "Movies | 3", "Movies | 4", "Movies | 5")
        val programmingList = arrayOf("", "Programming | 1", "Programming | 2", "Programming | 3", "Programming | 4", "Programming | 5")

        (binding!!.genderMenu.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(genderList)
        (binding!!.foodDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(foodList)
        (binding!!.readingDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(readingList)
        (binding!!.swimmingDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(swimmingList)
        (binding!!.programmingDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(programmingList)
        (binding!!.moviesDropDown.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(moviesList)

        binding?.profileImage?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        binding!!.genderMenuAutoComplete.setOnClickListener { closeKeyboard() }
        binding!!.foodDropDownAutoComplete.setOnClickListener { closeKeyboard() }
        binding!!.readingDropDownAutoComplete.setOnClickListener { closeKeyboard() }
        binding!!.swimmingDropDownAutoComplete.setOnClickListener { closeKeyboard() }
        binding!!.programmingDropDownAutoComplete.setOnClickListener { closeKeyboard() }
        binding!!.moviesDropDownAutoComplete.setOnClickListener { closeKeyboard() }

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

            val byteArray = compressImageTo300KB(bitmap)

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

    private fun compressImageTo300KB(bitmap: Bitmap, quality: Int = 100, attempt: Int = 1): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()

        if (byteArray.size <= 300 * 1024) {
            // Image is under 300KB, return the compressed byteArray
            return byteArray
        } else {
            // Image size is still larger than 300KB
            // Reduce the quality by 10% and try again
            val newQuality = (quality - 10)
            if (newQuality > 0 && attempt < 10) {
                return compressImageTo300KB(bitmap, newQuality, attempt + 1)
            } else {
                // Image cannot be compressed further or max attempts reached
                // Return null or take other actions based on your requirements
                return byteArray
            }
        }
    }

     //a function that closes the keyboard when the user clicks outside the edit text
    private fun closeKeyboard() {
         Log.d("DebugData", "closeKeyboard: called")
        if (requireActivity().currentFocus != null) {
            Log.d("DebugData", "closeKeyboard: if block called")
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken, 0
            )
        }
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
//            val selectedImage = data.data
//            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImage)
//            binding?.profileImage?.setImageBitmap(bitmap)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data!!
            val filePath = getPathFromUri(requireContext(),selectedImage)!! // Utility method to get the file path from the Uri
            val rotatedBitmap = rotateImageIfNeeded(filePath)
            binding?.profileImage?.setImageBitmap(rotatedBitmap)
        }
    }

    private fun rotateImageIfNeeded(filePath: String): Bitmap? {
        val bitmap = BitmapFactory.decodeFile(filePath)
        val exif = ExifInterface(filePath)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.preScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postScale(-1f, 1f)
                matrix.postRotate(-90f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postScale(-1f, 1f)
                matrix.postRotate(90f)
            }
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun getPathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context, uri, projection, null, null, null)
        val cursor = loader.loadInBackground()

        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = cursor?.getString(columnIndex ?: 0)
        cursor?.close()

        return path
    }


}