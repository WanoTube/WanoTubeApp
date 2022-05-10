package com.wanotube.wanotubeapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.wanotube.wanotubeapp.WanotubeApp.Companion.context
import com.wanotube.wanotubeapp.database.AppDatabase
import com.wanotube.wanotubeapp.database.asDomainModel
import com.wanotube.wanotubeapp.domain.Comment
import com.wanotube.wanotubeapp.network.services.ICommentService
import com.wanotube.wanotubeapp.network.objects.NetworkComment
import com.wanotube.wanotubeapp.network.ServiceGenerator
import com.wanotube.wanotubeapp.network.asDatabaseModel
import com.wanotube.wanotubeapp.network.authentication.AuthPreferences
import com.wanotube.wanotubeapp.ui.watch.WatchAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CommentRepository(private val database: AppDatabase) {
    val comments: LiveData<List<Comment>> = Transformations.map(database.commentDao.getComments()) {
        it.asDomainModel()
    }

    fun sendComment(content: String, videoId: String, adapter: WatchAdapter){
        val mAuthPreferences = context?.let { AuthPreferences(it) }
        mAuthPreferences?.authToken?.let {
            val commentService: ICommentService? =
                ServiceGenerator.createService(ICommentService::class.java, it)

            val contentBody = MultipartBody.Part.createFormData("content", content)
            val videoIdBody = MultipartBody.Part.createFormData("video_id", videoId)

            val response = commentService?.sendComment(
                contentBody,
                videoIdBody
            )
            response?.enqueue(object : Callback<NetworkComment> {
                override fun onResponse(
                    call: Call<NetworkComment>?,
                    response: Response<NetworkComment?>?
                ) {
                    val commentModel = response?.body()?.asDatabaseModel()
                    CoroutineScope(Dispatchers.IO).launch {
                        if (commentModel != null) {
                            database.commentDao.insert(commentModel)
                            withContext(Dispatchers.Main) {
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<NetworkComment>?, t: Throwable?) {
                    Timber.e("Failed: error: %s", t.toString())
                }
            })
        }
    }
}