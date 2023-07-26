package com.finite.nearbuddy.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
//                if(!ncvm.isConnectionActive) {
//                    Toast.makeText(context, "Connected with ${user2.name}", Toast.LENGTH_SHORT).show()
//                    ncvm.isConnectionActive = true
//                }
                if (!ncvm._isConnected.value!!) {
                    showCustomDialog()
                    ncvm._isConnected.value = true
                }
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

    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_user2_connected, null)
        val dialogBuilder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val customDialog = dialogBuilder.create()
        customDialog.setCancelable(false)

        dialogView.findViewById<TextView>(R.id.dialogMessageTextView).text = "Connected with ${ncvm.user2.value?.name}"
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // set the dialogwidth to wrap content
        customDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val paddingInDp = 50
        val scale: Float = resources.displayMetrics.density
        val paddingInPixels = (paddingInDp * scale + 0.5f).toInt()
        // add horizontal padding to the dialog of 50dp not pixels (left and right)
        customDialog.window?.decorView?.setPadding(paddingInPixels, 0, paddingInPixels, 0)

        dialogView.findViewById<View>(R.id.buttonOK).setOnClickListener {
            customDialog.dismiss()
            findNavController().navigate(R.id.nav_chat)
        }

        customDialog.show()
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