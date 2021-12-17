package com.example.fundo.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fundo.R
import com.example.fundo.data.networking.UserService
import com.example.fundo.databinding.ActivityTestBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getuserButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                UserService.getUsers().let {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.userNameTextView.text = it.toString()
                    }
                }
            }
        }
    }
}