package com.example.retrofit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofit.model.Post
import com.example.retrofit.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(
    private val repository: Repository
): ViewModel() {
    val myResponse: MutableLiveData<Response<Post>> = MutableLiveData()
    val myResponse2: MutableLiveData<Response<Post>> = MutableLiveData()

    fun pushPost(post: Post) {
        viewModelScope.launch {
            val response: Response<Post> = repository.pushPost(post)
            myResponse.value = response
        }
    }

    fun pushPost2(post: Post){
        viewModelScope.launch {
            val response: Response<Post> = repository.pushPost2(1, 2, "Aadarsh", "Android Developer")
            myResponse.value = response
        }
    }
    fun getPost(auth: String) {
        viewModelScope.launch {
            val response: Response<Post> = repository.getPost()
            myResponse.value = response
        }
    }

    fun getPost2(number: Int) {
        viewModelScope.launch {
            val response: Response<Post> = repository.getPost2(number)
            myResponse2.value = response
        }
    }

    val myResponse3: MutableLiveData<Response<List<Post>>> = MutableLiveData()

    fun getPost3(userId: Int) {
        viewModelScope.launch {
            val response: Response<List<Post>> = repository.getPost3(userId)
            myResponse3.value = response
        }

    }
    val myResponse4: MutableLiveData<Response<List<Post>>> = MutableLiveData()

    fun getPost4(userId: Int, options: Map<String, String>) {
        viewModelScope.launch {
            val response: Response<List<Post>> = repository.getPost4(userId, options)
            myResponse4.value = response
        }

    }
}
