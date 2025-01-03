package com.example.retrofit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.retrofit.repository.Repository

class MainViewModeFactory(
    private val repository: Repository
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T

    }
}