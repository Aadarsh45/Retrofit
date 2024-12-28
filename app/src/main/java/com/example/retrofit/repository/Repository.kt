package com.example.retrofit.repository

import com.example.retrofit.api.RetrofitInstance
import com.example.retrofit.model.Post
import retrofit2.Response

class Repository {

    suspend fun getPost() : Response<Post> {
        return RetrofitInstance.api.getPost()
    }

    suspend fun getPost2(number: Int) : Response<Post> {
        return RetrofitInstance.api.getPost2(number)

    }

    suspend fun getPost3(userId: Int) : Response<List<Post>> {
        return RetrofitInstance.api.getPost3(userId)
    }

    suspend fun getPost4(userId: Int, options: Map<String, String>) : Response<List<Post>> {
        return RetrofitInstance.api.getPost4(userId, options)
    }


}