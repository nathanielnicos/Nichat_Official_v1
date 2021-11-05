package id.nns.nichat.ui.user

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import id.nns.nichat.R
import id.nns.nichat.ui.chat.ChatActivity
import id.nns.nichat.databinding.ActivityUserBinding
import id.nns.nichat.domain.model.User
import id.nns.nichat.ui.image.ImageActivity
import id.nns.nichat.utils.OnSomethingClickListener

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var adapter: UserAdapter
    private val userViewModel: UserViewModel by viewModels()

    private var users = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAdapter()
        observeValue()
        onClickUser()
        onClickImage()
    }

    private fun setAdapter() {
        adapter = UserAdapter()
        binding.rvUsers.adapter = adapter
        binding.rvUsers.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun observeValue() {
        userViewModel.allUsers.observe(this) { arrayList ->
            arrayList.forEach { user ->
                users.add(user)
            }

            adapter.users = arrayList
            binding.animationEmpty.visibility = View.GONE
        }

        userViewModel.query.observe(this) { text ->
            val searchedUsers = ArrayList<User>()

            users.forEach { user ->
                if (user.name.toString().lowercase().contains(text.lowercase(), true)) {
                    searchedUsers.add(user)
                }
            }

            adapter.users = searchedUsers
            binding.animationEmpty.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.search_item)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                userViewModel.showSearchedUsers(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    private fun onClickUser() {
        adapter.setOnUserClickListener(object: OnSomethingClickListener {
            override fun onSomethingClick(view: View, position: Int) {
                Intent(this@UserActivity, ChatActivity::class.java).apply {
                    putExtra(ChatActivity.KEY_USER, adapter.users[position])
                    startActivity(this)
                }
            }
        })
    }

    private fun onClickImage() {
        adapter.setOnImageClickListener(object: OnSomethingClickListener {
            override fun onSomethingClick(view: View, position: Int) {
                Intent(this@UserActivity, ImageActivity::class.java).apply {
                    putExtra(ImageActivity.OPEN_IMAGE, adapter.users[position].imgUrl)
                    startActivity(this)
                }
            }
        })
    }

}