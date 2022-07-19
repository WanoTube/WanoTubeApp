package com.wanotube.wanotubeapp.ui.profile

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.databinding.FragmentProfileBinding
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.ui.login.LoginActivity

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<Button>(R.id.btn_logout)?.setOnClickListener {
            logOut()
        }
    }
    
    private fun logOut() {
        val mAccountManager = AccountManager.get(requireContext())
        val mAuthPreferences = AuthPreferences(requireContext())
        val authToken = mAuthPreferences.authToken
        // Clear session and ask for new auth token
        mAccountManager!!.invalidateAuthToken(AccountUtils.ACCOUNT_TYPE, authToken)
        mAuthPreferences.authToken = null
        mAuthPreferences.email = null
        mAuthPreferences.username = null

        // Open LoginActivity
        openLoginActivity()
    }

    private fun openLoginActivity() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
    }
}