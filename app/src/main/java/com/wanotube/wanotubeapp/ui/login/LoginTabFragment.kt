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
import com.wanotube.wanotubeapp.network.IUserService
import com.wanotube.wanotubeapp.network.NetworkUser
import com.wanotube.wanotubeapp.network.ServiceGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

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
        login(email, password)
//        openMainActivity()
    }
    
    private fun login(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val userService: IUserService =
                ServiceGenerator.createService(IUserService::class.java)
            
            val emailBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("email", email)
            val passwordBodyPart: MultipartBody.Part = MultipartBody.Part.createFormData("password", password)

            val responseBodyCall: Call<NetworkUser> = userService.login(emailBodyPart, passwordBodyPart)
            responseBodyCall.enqueue(object : Callback<NetworkUser> {
                override fun onResponse(
                    call: Call<NetworkUser>?,
                    response: Response<NetworkUser?>?
                ) {

                    val videoModel = response?.body()
                    Timber.e("Ngan %s", "User: $videoModel")

                }
                override fun onFailure(call: Call<NetworkUser>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }
    private fun openMainActivity() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
    }
}