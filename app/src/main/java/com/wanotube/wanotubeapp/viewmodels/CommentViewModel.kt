package com.wanotube.wanotubeapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.wanotube.wanotubeapp.database.getDatabase
import com.wanotube.wanotubeapp.repository.CommentRepository

class CommentViewModel(application: Application) : AndroidViewModel(application) {
    private val commentRepository = CommentRepository(getDatabase(application))
    var comments = commentRepository.comments

    class CommentViewModelFactory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CommentViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}