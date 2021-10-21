package com.example.fundo.ui.authentication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.fundo.R
import com.example.fundo.databinding.LoginFragmentBinding
import com.example.fundo.utils.Utilities

class LoginFragment : Fragment(R.layout.login_fragment) {
    private lateinit var binding:LoginFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = LoginFragmentBinding.bind(view)

        binding.regLogChangeRText.setOnClickListener{
            Utilities.fragmentSwitcher(requireActivity().supportFragmentManager,R.id.authFragmentContainer,
                SignUpFragment()
            )
        }
    }
}