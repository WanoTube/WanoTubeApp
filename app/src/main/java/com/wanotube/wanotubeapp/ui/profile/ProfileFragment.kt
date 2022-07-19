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
import com.bumptech.glide.Glide
import com.wanotube.wanotubeapp.R
import com.wanotube.wanotubeapp.database.entity.DatabaseUser
import com.wanotube.wanotubeapp.databinding.FragmentProfileBinding
import com.wanotube.wanotubeapp.network.ServiceGenerator
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.network.authentication.AccountUtils
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.network.objects.UserResult
import com.wanotube.wanotubeapp.network.services.IChannelService
import com.wanotube.wanotubeapp.ui.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )

        val mAuthPreferences = context?.let { AuthPreferences(it) }
        mAuthPreferences?.userId?.let { getUserInfo(it) }
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

    private fun getUserInfo(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val channelService: IChannelService? =
                ServiceGenerator.createService(IChannelService::class.java)

            val responseBodyCall: Call<UserResult>? = channelService?.getUserByUserId(userId)
            responseBodyCall?.enqueue(object : Callback<UserResult> {
                override fun onResponse(
                    call: Call<UserResult>?,
                    response: Response<UserResult?>?
                ) {
                    val userInfo = response?.body()?.user?.asDatabaseModel()
                    updateFields(userInfo)

                }
                override fun onFailure(call: Call<UserResult>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }

    fun updateFields(userInfo: DatabaseUser?) {
        if (userInfo != null) {
            binding.apply {
                firstName = userInfo.firstName
                lastName = userInfo.lastName
                gender = userInfo.gender
                country = userInfo.country
                description = userInfo.description
                phoneNumber = userInfo.phoneNumber
            }
            Glide.with(this)
                .load(userInfo.avatar)
                .placeholder(R.drawable.image_placeholder)
                .circleCrop()
                .into(binding.avatar)
        }
    }
}