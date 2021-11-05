package id.nns.nichat.ui.chat

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import id.nns.nichat.R
import id.nns.nichat.databinding.ActivityChatBinding
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.model.User
import id.nns.nichat.ui.other_profile.OtherProfileActivity
import id.nns.nichat.viewmodel.ViewModelFactory
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity() {

    companion object {
        const val KEY_USER = "key_user"
        private const val SELECT_IMAGE = 100
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var preference: UserPreference
    private var selectedImageBytes: ByteArray? = null
    private var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = ViewModelFactory.getInstance(this)
        chatViewModel = ViewModelProvider(this, factory)[ChatViewModel::class.java]

        preference = UserPreference(this)
        adapter = ChatAdapter(preference.getUser().uid)
        binding.rvChat.adapter = adapter

        toUser = intent.getParcelableExtra(KEY_USER)
        chatViewModel.getAllMessages(toUser?.uid.toString())

        showToolbar()
        observeValue()

        binding.toolbarChat.setOnClickListener {
            Intent(this, OtherProfileActivity::class.java).apply {
                putExtra(OtherProfileActivity.KEY_OTHER_USER, toUser)
                startActivity(this)
            }
        }

        binding.btnSend.setOnClickListener {
            val message = binding.etChat.text.toString().trim()
            if (message.isNotBlank()) {
                binding.btnSend.visibility = View.INVISIBLE
                binding.pbSend.visibility = View.VISIBLE

                chatViewModel.sendMessage(toUser?.uid.toString(), message)
                chatViewModel.sendNotification(
                    toId = toUser?.uid.toString(),
                    sender = preference.getUser().name.toString(),
                    text = message
                )
            }
        }

        binding.btnImg.setOnClickListener {
            openStorage()
        }
    }

    private fun showToolbar() {
        binding.toolbarChat.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_scroll_down -> {
                    binding.rvChat.scrollToPosition(adapter.itemCount - 1)
                }
                R.id.menu_scroll_up -> {
                    binding.rvChat.scrollToPosition(0)
                }
                R.id.menu_clear_chat -> {
                    chatViewModel.clearChat(toUser?.uid.toString())
                    adapter.clear()
                }
            }
            true
        }
        binding.tvTitleChat.text = toUser?.name
        Glide.with(this)
            .load(toUser?.imgUrl)
            .placeholder(R.drawable.profile)
            .into(binding.civToolbarChat)
    }

    private fun openStorage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        @Suppress("DEPRECATION")
        startActivityForResult(
            intent,
            SELECT_IMAGE
        )
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {
            val selectedImagePath = data.data
            @Suppress("DEPRECATION")
            val selectedImageBitmap = MediaStore.Images.Media
                .getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Send this image?")
                .setPositiveButton("Yes") { _, _ ->
                    binding.etChat.text.clear()
                    binding.btnSend.visibility = View.INVISIBLE
                    binding.pbSend.visibility = View.VISIBLE
                    chatViewModel.getImageUrl(selectedImageBytes)
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        }
    }

    private fun observeValue() {
        chatViewModel.messages.observe(this) {
            adapter.chats = it as ArrayList<Message>
            binding.rvChat.scrollToPosition(adapter.itemCount - 1)
        }

        chatViewModel.isSend.observe(this) {
            if (it) {
                binding.etChat.text.clear()
                binding.btnSend.visibility = View.VISIBLE
                binding.pbSend.visibility = View.GONE
                binding.rvChat.scrollToPosition(adapter.itemCount - 1)
            }
        }

        chatViewModel.imgUrl.observe(this) {
            chatViewModel.sendMessage(toUser?.uid.toString(), it.toString())
        }

        chatViewModel.error.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

}