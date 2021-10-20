package com.example.fundo.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.fundo.R
import com.example.fundo.databinding.SignUpFragmentBinding

class SignUpFragment:Fragment(R.layout.sign_up_fragment) {
    private lateinit var binding : SignUpFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SignUpFragmentBinding.bind(view)
    }
}