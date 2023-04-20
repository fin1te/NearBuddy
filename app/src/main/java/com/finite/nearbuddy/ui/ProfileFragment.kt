package com.finite.nearbuddy.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.finite.nearbuddy.R
import com.finite.nearbuddy.databinding.FragmentProfileBinding
import com.finite.nearbuddy.databinding.FragmentSearchBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.Payload

class ProfileFragment : Fragment() {


    private var binding: FragmentProfileBinding? = null
    private val nearbyViewModel by activityViewModels<NearbyViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.discoverBtn)

        val fragmentBinding = FragmentProfileBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding?.clickEditBtn?.setOnClickListener{
            Toast.makeText(activity, "Edit your Profile.", Toast.LENGTH_SHORT).show()
        }
//        var currentClient = null
//        if(nearbyViewModel.connectionClient != null) {
//            var currentClient = nearbyViewModel.connectionClient
//        }

            Nearby.getConnectionsClient(requireActivity().applicationContext).sendPayload(nearbyViewModel.endptId, Payload.fromBytes("Hello Profile".toByteArray())).addOnSuccessListener {
                Toast.makeText(activity, "Payload sent.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(activity, "$it", Toast.LENGTH_SHORT).show()
                Log.d("Sender", "Payload not sent: $it : ${nearbyViewModel.endptId}")
            }


    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = resources.getColor(R.color.discoverBtn)
    }
}