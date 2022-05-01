package com.wanotube.wanotubeapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.wanotube.wanotubeapp.MainActivity
import com.wanotube.wanotubeapp.R

class LoginTabFragment : Fragment() {
    private lateinit var btnLogin: Button
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_login_tab, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnLogin = view.findViewById<View>(R.id.btn_login) as Button
        btnLogin.setOnClickListener {
            openMainActivity()
        }
    }
    
    private fun openMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }
}