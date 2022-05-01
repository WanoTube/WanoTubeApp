package com.wanotube.wanotubeapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.wanotube.wanotubeapp.MainActivity
import com.wanotube.wanotubeapp.R

class LoginTabFragment : Fragment() {
    private lateinit var btnLogin: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_login_tab, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnLogin = view.findViewById(R.id.btn_login) as Button
        txtEmail = view.findViewById(R.id.email) as EditText
        txtPassword = view.findViewById(R.id.password) as EditText

        btnLogin.setOnClickListener {
            handleLogin(
                txtEmail.text.toString(), 
                txtPassword.text.toString()
            )
        }
    }
    
    private fun handleLogin(email: String, password: String) {
        openMainActivity()
    }
    
    private fun openMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }
}