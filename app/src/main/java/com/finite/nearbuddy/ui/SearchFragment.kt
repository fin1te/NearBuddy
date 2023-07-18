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
import com.google.android.gms.tasks.Tasks

class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private lateinit var ncvm: ConnectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.light_blue)

        ncvm = ViewModelProvider(this)[ConnectionViewModel::class.java]

        val fragmentBinding = FragmentSearchBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!ncvm.isAdvertising) {
            ncvm.startAdvertising()
            binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))
            binding?.discoverBtn?.radius = 45.0F
            binding?.discoverText?.text = "Discover"
        }

        binding?.discoverBtn?.setOnClickListener {

            if(binding?.discoverText?.text == "Stop") {
                binding?.searchLottie?.pauseAnimation()
                binding?.discoverText?.text = "Discover"
                binding?.discoverBtn?.radius = 45.0F
                binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))

                ncvm.stopDiscovery()
                ncvm.startAdvertising()

            } else if(binding?.discoverText?.text == "Discover") {
                binding?.searchLottie?.resumeAnimation()
                binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.red))
                binding?.discoverBtn?.radius = 45.0F
                binding?.discoverText?.text = "Stop"

                ncvm.stopAdvertising()
                ncvm.startDiscovery()
            }
        }
    }


    override fun onResume() {
        super.onResume()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.light_blue)

        if(!ncvm.isAdvertising && !ncvm.isDiscovering) {
            ncvm.startAdvertising()
        }

        if(ncvm.isAdvertising) {
            binding?.searchLottie?.pauseAnimation()
            binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))
            binding?.discoverBtn?.radius = 45.0F
            binding?.discoverText?.text = "Discover"
        }

        if(ncvm.isDiscovering) {
            binding?.searchLottie?.resumeAnimation()
            binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.red))
            binding?.discoverBtn?.radius = 45.0F
            binding?.discoverText?.text = "Stop"
        }
    }

    override fun onPause() {
        super.onPause()
        binding?.searchLottie?.pauseAnimation()
    }
}