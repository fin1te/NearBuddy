package com.finite.nearbuddy.ui

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.finite.nearbuddy.R
import com.finite.nearbuddy.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null


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

        binding?.discoverBtn?.setOnClickListener {

                if(binding?.discoverText?.text == "Stop") {
                    binding?.discoverText?.text = "Discover"
                    binding?.discoverBtn?.radius = 45.0F
                    binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))
                    binding?.searchLottie?.cancelAnimation()
                    Toast.makeText(activity, "Stopped Searching.", Toast.LENGTH_SHORT).show()
                } else {
                    binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.red))
                    binding?.discoverBtn?.radius = 45.0F
                    binding?.discoverText?.text = "Stop"
                    binding?.searchLottie?.playAnimation()
                    Toast.makeText(activity, "Searching for nearby users....", Toast.LENGTH_SHORT).show()
                }


        }
    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.light_blue)
    }
}