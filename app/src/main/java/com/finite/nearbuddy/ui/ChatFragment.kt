package com.finite.nearbuddy.ui

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.finite.nearbuddy.R
import com.finite.nearbuddy.adapter.ChatAdapter
import com.finite.nearbuddy.databinding.FragmentChatBinding
import com.finite.nearbuddy.model.ChatMessage
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar


class ChatFragment : Fragment() {

    private var binding: FragmentChatBinding? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextMessage: TextInputLayout
    private lateinit var buttonSend: CircleImageView
    private lateinit var chatAdapter: ChatAdapter

    private lateinit var ncvm: ConnectionViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = Color.GRAY
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        var fragmentBinding = FragmentChatBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ncvm = ViewModelProvider(requireActivity())[ConnectionViewModel::class.java]

        recyclerView = view.findViewById(R.id.recyclerViewChat)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        chatAdapter = ChatAdapter(ncvm.chatMessages.value ?: emptyList(), ncvm)
        recyclerView.adapter = chatAdapter

        // observe the isconnected live data and change the ui accordingly
        ncvm.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                binding!!.noMessageLayout.visibility = View.GONE
                binding!!.chatLayout.visibility = View.VISIBLE
            } else {
                binding!!.chatLayout.visibility = View.GONE
                binding!!.noMessageLayout.visibility = View.VISIBLE
            }
        }

        ncvm.user2.observe(viewLifecycleOwner) { user2 ->
            if (user2 != null && user2.name.isNotEmpty()) {
                //binding?.profilePicture?.setImageBitmap(user2.profilePic)
                val newBitmap =
                    BitmapFactory.decodeByteArray(user2.profilePic, 0, user2.profilePic.size)
                binding?.profilePicture?.setImageBitmap(newBitmap)
                //logBitmap(user2.profilePic)
                Log.d("checkBitmapChat", (user2.profilePic).toString())
                binding?.user2Name?.text = user2.name
                binding?.user2AgeGender?.text = "${getAge(user2.dateOfBirth)} | ${user2.gender}"
            }
        }


        ncvm.chatMessages.observe(viewLifecycleOwner) { chatMessages ->
            // Update the RecyclerView with the new chatMessages list
            chatAdapter.submitList(chatMessages)
            chatAdapter.notifyItemInserted(ncvm.chatMessages.value!!.size - 1)
            // Scroll to the last item in the list when a new message is added
            recyclerView.scrollToPosition(chatMessages.size - 1)
        }



        buttonSend.setOnClickListener {
            val messageContent = editTextMessage.editText!!.text.toString()
            if (messageContent.isNotEmpty()) {


                val chatMessage = ChatMessage(

                    content = messageContent,
                    author = ncvm.user1.name,

                    )


                addMessageToRecyclerView(chatMessage)


                sendMessage(chatMessage)


                editTextMessage.editText!!.text.clear()
            }
        }
    }


    private fun getAge(dob: String): Int {
        val calendar = Calendar.getInstance()
        val dobArr = dob.split("/")
        val day = dobArr[0].toInt()
        val month = dobArr[1].toInt()
        val year = dobArr[2].toInt()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth =
            calendar.get(Calendar.MONTH) + 1 // January is represented by 0, so add 1 to get the correct month
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        var age = currentYear - year
        if (currentMonth < month) {
            age--
        } else if (currentMonth == month) {
            if (currentDay < day) {
                age--
            }
        }
        return age
    }


    private fun addMessageToRecyclerView(chatMessage: ChatMessage) {
        ncvm.addChatMessage(chatMessage)
        chatAdapter.notifyItemInserted(ncvm.chatMessages.value!!.size - 1)
        recyclerView.scrollToPosition(ncvm.chatMessages.value!!.size - 1)
    }

    private fun sendMessage(chatMessage: ChatMessage) {
        // TODO: Implement sending the message using Nearby Connections
        ncvm.sendChatMessage(chatMessage)
    }


    override fun onResume() {
        super.onResume()

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        requireActivity().window.statusBarColor = Color.GRAY
    }
}