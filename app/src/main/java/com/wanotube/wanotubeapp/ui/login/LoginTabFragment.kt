package com.wanotube.wanotubeapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.wanotube.wanotubeapp.MainActivity
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.network.LoginResult
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class LoginTabFragment : Fragment() {
    private lateinit var btnLogin: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private var mAuthPreferences: AuthPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mAuthPreferences = context?.let { AuthPreferences(it) }
        return inflater.inflate(R.layout.fragment_login_tab, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnLogin = view.findViewById(R.id.btn_login) as Button
        txtEmail = view.findViewById(R.id.email) as EditText
        txtPassword = view.findViewById(R.id.password) as EditText

        btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                login(
                    txtEmail.text.toString(),
                    txtPassword.text.toString()
                )
            }
        }
    }
    
    private suspend fun login(email: String, password: String) {
        val responseBodyCall = AccountUtils.mServerAuthenticator.login(email, password)

        responseBodyCall?.enqueue(object : Callback<LoginResult> {
            override fun onResponse(
                call: Call<LoginResult>?,
                response: Response<LoginResult?>?,
            ) {
                val body = response?.body()
                if (body == null) {
                    Timber.e("Failed: error: %s", "Email or password is incorrect")
                } else {
                    val user = body.user
                    val token = "Bearer " + body.token

                    // Save session username & auth token
                    mAuthPreferences?.authToken = token
                    mAuthPreferences?.username = user?.username
                    mAuthPreferences?.email = email
                    mAuthPreferences?.isAdmin = user?.isAdmin

                    // Add account to AccountManager
                    AccountUtils.addAccount(context, email, token)

                    Toast.makeText(context, "Hi " + (user?.username ?: ""), Toast.LENGTH_SHORT).show()

                    openMainActivity()
                }
            }
            override fun onFailure(call: Call<LoginResult>?, t: Throwable?) {
                Timber.e("Failed: error: %s", t.toString())
            }
        })
        
    }
    
    private fun openMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }
}