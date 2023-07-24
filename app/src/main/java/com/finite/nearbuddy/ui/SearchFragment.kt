package com.finite.nearbuddy.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.finite.nearbuddy.R
import com.finite.nearbuddy.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {

    private var binding: FragmentSearchBinding? = null
    private lateinit var ncvm: ConnectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.light_blue)

        val activity = requireActivity()
        ncvm = ViewModelProvider(activity)[ConnectionViewModel::class.java]

        val fragmentBinding = FragmentSearchBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!ncvm.isAdvertising) {
            ncvm.startAdvertising()
            binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))
            binding?.discoverBtn?.radius = 45.0F
            binding?.discoverText?.text = "Discover"
        }

        ncvm.user2.observe(viewLifecycleOwner) { user2 ->
            if (user2.name.isNotBlank()) {
                Toast.makeText(context, "Connected with ${user2.name}", Toast.LENGTH_SHORT).show()
                binding?.searchLottie?.pauseAnimation()
                binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))
                binding?.discoverBtn?.radius = 45.0F
                binding?.discoverText?.text = "Discover"
                ncvm.stopDiscovery()
                ncvm.stopAdvertising()
            }
        }

        binding?.discoverBtn?.setOnClickListener {

            if (binding?.discoverText?.text == "Stop") {
                binding?.searchLottie?.pauseAnimation()
                binding?.discoverText?.text = "Discover"
                binding?.discoverBtn?.radius = 45.0F
                binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))

                ncvm.stopDiscovery()
                ncvm.startAdvertising()

            } else if (binding?.discoverText?.text == "Discover") {
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

        if (!ncvm.isAdvertising && !ncvm.isDiscovering) {
            ncvm.startAdvertising()
        }

        if (ncvm.isAdvertising) {
            binding?.searchLottie?.pauseAnimation()
            binding?.discoverBtn?.background?.setTint(resources.getColor(R.color.discoverBtn))
            binding?.discoverBtn?.radius = 45.0F
            binding?.discoverText?.text = "Discover"
        }

        if (ncvm.isDiscovering) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }


}