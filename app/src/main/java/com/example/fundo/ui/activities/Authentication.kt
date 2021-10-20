package com.example.fundo.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.fundo.R
import com.example.fundo.databinding.AuthBinding
import com.example.fundo.ui.fragments.LoginFragment
import com.example.fundo.ui.fragments.SignUpFragment
import com.example.fundo.utils.Utilities

class Authentication : AppCompatActivity() {
    private lateinit var binding: AuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,LoginFragment())

        binding.regLogChangeRText.setOnClickListener{
            if(binding.regLogChangeRText.text == "Register") {
                binding.regLogChangeLText.text = "Already have an account?"
                binding.regLogChangeRText.text = "Login"
                Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,SignUpFragment())
            }
            else{
                binding.regLogChangeLText.text = "Don't have an account?"
                binding.regLogChangeRText.text = "Register"
                Utilities.fragmentSwitcher(supportFragmentManager,R.id.authFragmentContainer,LoginFragment())
            }
        }
    }
}