package com.example.retrofit.api

import com.example.retrofit.model.Post
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SimpleApi {

    @GET("posts/1")
    suspend fun getPost(): Response<Post>             //for handelling exceptinon change post to Response<Post>

    @GET("posts/{postNumber}")  // customization in path                //posts/$postnumber
    suspend fun getPost2(
        @Path("postNumber") number: Int
    ): Response<Post>

    @GET("posts")                      //posts?userId=3 ->endpoints
    suspend fun getPost3(
        @Query("userId") userId: Int
    ): Response<List<Post>>


    //Quety Map
    @GET("posts")
    suspend fun getPost4(
        @Query("userId") userId: Int,
        @QueryMap options: Map<String, String>
    ): Response<List<Post>>

}