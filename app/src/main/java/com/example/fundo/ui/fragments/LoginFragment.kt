package com.example.fundo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.fundo.R
import com.example.fundo.databinding.LoginFragmentBinding

class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var binding:LoginFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = LoginFragmentBinding.bind(view)
    }
}