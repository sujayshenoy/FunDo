package com.example.fundo.ui.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fundo.R
import com.example.fundo.databinding.AuthBinding
import com.example.fundo.utils.Utilities

class Authentication : AppCompatActivity() {
    private lateinit var binding: AuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,
            LoginFragment()
        )
    }
}