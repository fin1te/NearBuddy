package com.finite.nearbuddy.ui

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finite.nearbuddy.R
import com.finite.nearbuddy.databinding.FragmentProfileBinding
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip

class ProfileFragment : Fragment() {


    private var binding: FragmentProfileBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.discoverBtn)

        val fragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences =
            requireActivity().getSharedPreferences("profileDataPreference", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")
        val gender = sharedPreferences.getString("gender", "")
        val dob = sharedPreferences.getString("dob", "")
        val about = sharedPreferences.getString("about", "")

        val interestFood = sharedPreferences.getString("interestFood", "")
        val interestReading = sharedPreferences.getString("interestReading", "")
        val interestSwimming = sharedPreferences.getString("interestSwimming", "")
        val interestProgramming = sharedPreferences.getString("interestProgramming", "")
        val interestMovies = sharedPreferences.getString("interestMovies", "")

        // set the profile image
        val profileImage = sharedPreferences.getString("profileImage", "")
        if (profileImage != "") {
            val decodedString: ByteArray = Base64.decode(profileImage, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            binding?.profileImage?.setImageBitmap(decodedByte)
        }

        binding?.textViewName?.text = name
        binding?.textViewGender?.text = gender
        binding?.textViewDOB?.text = dob
        binding?.textViewAbout?.text = about

        val chipsList = listOf(
            interestFood,
            interestReading,
            interestSwimming,
            interestMovies,
            interestProgramming
        )

        for (chipText in chipsList) {
            if (chipText == "") continue
            val chip = Chip(context)
            chip.text = chipText
            chip.setTextColor(Color.WHITE)
            chip.setChipBackgroundColorResource(R.color.discoverBtn)

            chip.textSize = 14f
            chip.typeface = Typeface.DEFAULT_BOLD
            val chipLayoutParams = FlexboxLayout.LayoutParams(
                FlexboxLayout.LayoutParams.WRAP_CONTENT,
                FlexboxLayout.LayoutParams.WRAP_CONTENT
            )
            chipLayoutParams.setMargins(10, 0, 10, 0)
            chip.layoutParams = chipLayoutParams
            binding!!.chipFlexboxLayout.addView(chip)
        }


        binding?.clickEditBtn?.setOnClickListener {
            // use this to navigate to edit profile fragment using nav graph
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.discoverBtn)
    }
}