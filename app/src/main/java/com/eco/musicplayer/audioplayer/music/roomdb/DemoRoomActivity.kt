package com.eco.musicplayer.audioplayer.music.roomdb

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.eco.musicplayer.audioplayer.music.databinding.ActivityDemoRoomBinding
import com.eco.musicplayer.audioplayer.music.roomdb.model.Address
import com.eco.musicplayer.audioplayer.music.roomdb.model.User
import com.eco.musicplayer.audioplayer.music.roomdb.repository.UserRepository
import com.eco.musicplayer.audioplayer.music.roomdb.viewmodel.UserViewModel
import com.eco.musicplayer.audioplayer.music.roomdb.viewmodel.UserViewModelFactory

class DemoRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDemoRoomBinding
    private lateinit var vm: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getInstance(applicationContext)
        val repo = UserRepository(db.userDao(), db.postDao())
        val factory = UserViewModelFactory(repo)
        vm = ViewModelProvider(this, factory)[UserViewModel::class.java]

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            val name = binding.edtName.text?.toString()?.trim().orEmpty()
            val email = binding.edtEmail.text?.toString()?.trim().orEmpty()
            val street = binding.edtStreet.text?.toString()?.trim().orEmpty()
            val city = binding.edtCity.text?.toString()?.trim().orEmpty()
            val title = binding.edtPostTitle.text?.toString()?.trim().orEmpty()
            val content = binding.edtPostContent.text?.toString()?.trim().orEmpty()

            if (name.isEmpty() || email.isEmpty() || title.isEmpty()) {
                binding.txtResult.text = "Vui lòng nhập tối thiểu: tên, email và tiêu đề bài viết"
                return@setOnClickListener
            }

            val user = User(
                name = name,
                email = email,
                address = Address(street, city)
            )

            vm.addUserAndPost(user, title, content)

            clearInputs()
        }

        binding.btnClear.setOnClickListener {
            vm.clearAll()
        }
    }

    private fun clearInputs() {
        binding.edtName.text?.clear()
        binding.edtEmail.text?.clear()
        binding.edtStreet.text?.clear()
        binding.edtCity.text?.clear()
        binding.edtPostTitle.text?.clear()
        binding.edtPostContent.text?.clear()
    }

    private fun setupObservers() {
        vm.users.observe(this) { list ->
            if (list.isNullOrEmpty()) {
                binding.txtResult.text = "Chưa có dữ liệu..."
            } else {
                val sb = StringBuilder()
                sb.append("Tổng users: ${list.size}\n\n")

                list.forEach { up ->
                    sb.append("${up.user.name} (${up.user.email})\n")
                    sb.append("${up.user.address.street}, ${up.user.address.city}\n")
                    sb.append("Bài viết:\n")

                    if (up.posts.isEmpty()) {
                        sb.append("   - (Không có bài viết)\n")
                    } else {
                        up.posts.forEach { p ->
                            sb.append("   • ${p.title}\n")
                            sb.append("       ${p.content}\n")
                        }
                    }

                    sb.append("\n")
                }

                binding.txtResult.text = sb.toString()
            }
        }

        vm.joinData.observe(this) { joinList ->
        }

        vm.count.observe(this) { total ->
        }
    }
}