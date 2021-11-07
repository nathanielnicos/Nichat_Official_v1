package id.nns.nichat.ui.chat

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import id.nns.nichat.R
import id.nns.nichat.databinding.ActivityChatBinding
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.domain.model.Message
import id.nns.nichat.domain.model.User
import id.nns.nichat.utils.converters.ChatConverters
import id.nns.nichat.ui.other_profile.OtherProfileActivity
import id.nns.nichat.utils.CropActivityResultContract
import id.nns.nichat.utils.converters.uriToByteArray
import id.nns.nichat.viewmodel.ViewModelFactory

class ChatActivity : AppCompatActivity() {

    companion object {
        const val KEY_USER = "key_user"
    }

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var adapter: ChatAdapter
    private lateinit var preference: UserPreference
    private var selectedImageBytes: ByteArray? = null
    private var toUser: User? = null

    private lateinit var cropActivityResultContract: ActivityResultContract<Any?, Uri?>
    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>

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
        chatViewModel.setPartnerId(toUser?.uid.toString())

        showToolbar()
        observeValue()

        cropActivityResultContract = CropActivityResultContract(this, null, null)
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract) { uri ->
            if (uri != null) {
                selectedImageBytes = uriToByteArray(
                    baseContext = baseContext,
                    uri = uri
                )

                showSendImageDialog()
            }
        }

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

                chatViewModel.sendMessage(toUser?.uid.toString(), message).also {
                    chatViewModel.sendNotification(
                        toId = toUser?.uid.toString(),
                        sender = preference.getUser().name.toString(),
                        text = message
                    )
                }
            }
        }

        binding.btnImg.setOnClickListener {
            cropActivityResultLauncher.launch(null)
        }
    }

    private fun showSendImageDialog() {
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

    private fun observeValue() {
        chatViewModel.messages.observe(this) {
            if (it != null) {
                adapter.chats = ChatConverters.fromJsonToList(it) as ArrayList<Message>
                binding.rvChat.scrollToPosition(adapter.itemCount - 1)
            }
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