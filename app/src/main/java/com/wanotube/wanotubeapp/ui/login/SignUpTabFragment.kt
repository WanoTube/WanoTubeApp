package com.wanotube.wanotubeapp.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.network.objects.LoginResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SignUpTabFragment : Fragment() {
    private var mAuthPreferences: AuthPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mAuthPreferences = context?.let { AuthPreferences(it) }
        return inflater.inflate(R.layout.fragment_signup_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val btnSignup = view.findViewById(R.id.btn_signup) as Button
        val txtEmail = view.findViewById(R.id.email) as EditText
        val txtUsername = view.findViewById(R.id.username) as EditText
        val txtFirstName = view.findViewById(R.id.first_name) as EditText
        val txtLastName = view.findViewById(R.id.last_name) as EditText
        val txtPhoneNumber = view.findViewById(R.id.phone_number) as EditText
        val txtPassword = view.findViewById(R.id.password) as EditText

        btnSignup.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                signUp(
                    txtEmail.text.toString(),
                    txtUsername.text.toString(),
                    txtFirstName.text.toString(),
                    txtLastName.text.toString(),
                    txtPhoneNumber.text.toString(),
                    txtPassword.text.toString(),
                    ""
                )
            }
        }
    }


    private fun signUp(email: String, username: String, password: String, firstName: String, lastName: String, phoneNumber: String, birthDate: String) {
        val responseBodyCall = AccountUtils.mServerAuthenticator.signUp(
            email, username, password, firstName, lastName, phoneNumber)

        responseBodyCall?.enqueue(object : Callback<LoginResult> {
            override fun onResponse(
                call: Call<LoginResult>?,
                response: Response<LoginResult?>?,
            ) {
                val body = response?.body()
                if (body == null) {
                    Toast.makeText(context, response?.message()?:"Email or password is incorrect", Toast.LENGTH_SHORT).show()
                } else {
                    val user = body.user
                    val token = "Bearer " + body.token

                    // Save session username & auth token
                    mAuthPreferences?.apply {
                        authToken = token
                        this.username = username
                        this.email = email
                        this.isAdmin = user?.isAdmin
                        this.avatar = user?.avatar
                        this.isBlocked = user?.isBlocked
                    }

                    // Add account to AccountManager
                    AccountUtils.addAccount(context, email, token)

                    Toast.makeText(context, "Hi " + (user?.username ?: ""), Toast.LENGTH_SHORT).show()

                    finish()
                }
            }
            override fun onFailure(call: Call<LoginResult>?, t: Throwable?) {
                Timber.e("Failed: error: %s", t.toString())
            }
        })

    }

    private fun finish() {
        requireActivity().onBackPressed()
    }
}