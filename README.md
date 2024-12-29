# Retrofit with MVVM in Android

This document explains how to integrate Retrofit with MVVM architecture in Android. It includes code snippets, a theoretical overview, and real-life examples to help you understand the implementation and usage.

---
# Simple Get Request

## **Overview**

Retrofit is a type-safe HTTP client for Android and Java. It's widely used to simplify network operations, such as making API calls to fetch or send data. Pairing it with the MVVM architecture improves separation of concerns and makes the codebase easier to maintain.

### **Key Components**
1. **Retrofit**: Responsible for network calls.
2. **Repository**: Acts as a single source of truth for fetching data.
3. **ViewModel**: Handles the business logic and exposes data to the UI.
4. **LiveData**: Observes and responds to data changes.

---

## **Implementation Steps**

### 1. **Set Up Retrofit**

Create a `RetrofitInstance` object to configure Retrofit with a base URL and a converter factory.

```kotlin
package com.example.retrofit.api

import com.example.retrofit.utils.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SimpleApi by lazy {
        retrofit.create(SimpleApi::class.java)
    }
}
```

### 2. **Define API Endpoints**

Create an interface that specifies the API endpoints using Retrofit annotations.

```kotlin
package com.example.retrofit.api

import com.example.retrofit.model.Post
import retrofit2.Response
import retrofit2.http.GET

interface SimpleApi {

    @GET("posts/1")
    suspend fun getPost(): Response<Post>
}
```

### 3. **Create a Repository**

The repository interacts with the Retrofit API and provides data to the ViewModel.

```kotlin
package com.example.retrofit.repository

import com.example.retrofit.api.RetrofitInstance
import com.example.retrofit.model.Post
import retrofit2.Response

class Repository {

    suspend fun getPost(): Response<Post> {
        return RetrofitInstance.api.getPost()
    }
}
```

### 4. **Design the ViewModel**

The ViewModel fetches data from the repository and exposes it to the UI via LiveData.

```kotlin
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
) : ViewModel() {

    val myResponse: MutableLiveData<Response<Post>> = MutableLiveData()

    fun getPost() {
        viewModelScope.launch {
            val response: Response<Post> = repository.getPost()
            myResponse.value = response
        }
    }
}
```

### 5. **Create a ViewModel Factory**

This factory is required to instantiate a ViewModel with parameters.

```kotlin
package com.example.retrofit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.retrofit.repository.Repository

class MainViewModeFactory(
    private val repository: Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}
```

### 6. **Set Up the Activity**

Bind the ViewModel to the activity and observe LiveData for changes.

```kotlin
package com.example.retrofit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.retrofit.repository.Repository

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = Repository()
        val viewModelFactory = MainViewModeFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        viewModel.getPost()

        viewModel.myResponse.observe(this, Observer { response ->
            if (response.isSuccessful) {
                Log.d("Response", response.body()?.id.toString())
                Log.d("Response", response.body()?.userId.toString())
            }
        })
    }
}
```

### 7. **Define the Data Model**

Define a data class representing the JSON response.

```kotlin
package com.example.retrofit.model

data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
```

### 8. **Constants for Base URL**

Store the base URL as a constant.

```kotlin
package com.example.retrofit.utils

class Constants {
    companion object {
        const val BASE_URL = "https://jsonplaceholder.typicode.com"
    }
}
```

---

## **Directory Structure**

Below is the recommended directory structure for this project:

```
app/
├── java/
│   └── com/example/retrofit/
│       ├── api/
│       │   ├── RetrofitInstance.kt
│       │   └── SimpleApi.kt
│       ├── model/
│       │   └── Post.kt
│       ├── repository/
│       │   └── Repository.kt
│       ├── utils/
│       │   └── Constants.kt
│       ├── MainActivity.kt
│       ├── MainViewModel.kt
│       └── MainViewModeFactory.kt
└── res/
    └── layout/
        └── activity_main.xml
```

### Explanation of Directories:
1. **api/**: Contains Retrofit setup and API interface definitions.
2. **model/**: Includes data classes representing API responses.
3. **repository/**: Contains classes responsible for data fetching and business logic.
4. **utils/**: Holds utility classes or constants, such as the base URL.
5. **root directory**: Contains main application files such as `MainActivity`, `ViewModel`, and `ViewModelFactory`.

---

## **How It Works**

1. **Retrofit Setup**: The `RetrofitInstance` object sets up Retrofit with the base URL and Gson converter.
2. **API Calls**: The `SimpleApi` interface defines the endpoints. In this example, `getPost()` fetches a post.
3. **Repository**: The repository encapsulates the API calls, abstracting them from the ViewModel.
4. **ViewModel**: The ViewModel fetches data asynchronously using `viewModelScope` and exposes it through LiveData.
5. **Activity**: The `MainActivity` observes the LiveData to update the UI whenever the data changes.

---

## **Real-Life Example**

Imagine you’re building a news app. Instead of fetching a single post, you can extend this setup to retrieve a list of articles.

### API Interface for Articles:

```kotlin
@GET("articles")
suspend fun getArticles(): Response<List<Article>>
```

### Data Model for Article:

```kotlin
data class Article(
    val id: Int,
    val title: String,
    val description: String
)
```

### UI Integration:
When the `LiveData` changes, update a `RecyclerView` to display the list of articles.

---

# Advanced Retrofit Usage with MVVM in Android

This document extends the previous Retrofit setup with explanations on `@Path`, `@Query`, and `@QueryMap` annotations. It also demonstrates how to display data using a `RecyclerView`.

---

## **Retrofit Annotations**

### 1. **`@Path` Annotation**

The `@Path` annotation is used to replace parts of the URL with dynamic values. This is useful for endpoints that require variable segments.

#### Example:

```kotlin
@GET("posts/{postNumber}")
suspend fun getPost2(
    @Path("postNumber") number: Int
): Response<Post>
```

- **Explanation**: `{postNumber}` in the URL is replaced with the value passed to the `number` parameter.
- **Usage**:

```kotlin
viewModel.getPost2(5)
viewModel.myResponse2.observe(this, Observer { response ->
    if (response.isSuccessful) {
        Log.d("Response", response.body().toString())
    }
})
```

---

### 2. **`@Query` Annotation**

The `@Query` annotation appends query parameters to the URL. It is commonly used for filtering or pagination.

#### Example:

```kotlin
@GET("posts")
suspend fun getPost3(
    @Query("userId") userId: Int
): Response<List<Post>>
```

- **Explanation**: Adds `?userId=value` to the endpoint.
- **Usage**:

```kotlin
viewModel.getPost3(3)
viewModel.myResponse3.observe(this, Observer { response ->
    if (response.isSuccessful) {
        response.body()?.forEach {
            Log.d("Response", it.toString())
        }
    }
})
```

---

### 3. **`@QueryMap` Annotation**

The `@QueryMap` annotation allows passing a map of key-value pairs as query parameters.

#### Example:

```kotlin
@GET("posts")
suspend fun getPost4(
    @Query("userId") userId: Int,
    @QueryMap options: Map<String, String>
): Response<List<Post>>
```

- **Explanation**: Combines `userId` and additional parameters from the `options` map into the request.
- **Usage**:

```kotlin
val options: MutableMap<String, String> = mutableMapOf()
options["_sort"] = "id"
options["_order"] = "desc"

viewModel.getPost4(2, options)
viewModel.myResponse4.observe(this, Observer { response ->
    if (response.isSuccessful) {
        response.body()?.forEach {
            Log.d("Response", it.toString())
        }
    }
})
```

---

## **Displaying Data in a RecyclerView**

### 1. **Set Up RecyclerView in XML**

```xml
<androidx.recyclerview.widget.RecyclerView
    android:id="@+id/recyclerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    tools:listitem="@layout/item_post" />
```

### 2. **Create a Layout for Each Item**

`res/layout/item_post.xml`

```xml
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="8dp">

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Title" />

    <TextView
        android:id="@+id/tvBody"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Body" />

</LinearLayout>
```

### 3. **Create a RecyclerView Adapter**

`PostAdapter.kt`

```kotlin
package com.example.retrofit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofit.R
import com.example.retrofit.model.Post
import kotlinx.android.synthetic.main.item_post.view.*

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postList = emptyList<Post>()

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = postList[position]
        holder.itemView.tvTitle.text = currentPost.title
        holder.itemView.tvBody.text = currentPost.body
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    fun setData(newList: List<Post>) {
        postList = newList
        notifyDataSetChanged()
    }
}
```

### 4. **Bind RecyclerView to Adapter**

In `MainActivity`:

```kotlin
private fun setupRecyclerview() {
    binding.recyclerView.adapter = myAdapter
    binding.recyclerView.layoutManager = LinearLayoutManager(this)
}

viewModel.myResponse4.observe(this, { response ->
    if (response.isSuccessful) {
        response.body()?.let { myAdapter.setData(it) }
    } else {
        Toast.makeText(this, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
    }
})
```

---

## **Real-Life Example**

Imagine you're building a blogging app that shows posts filtered by user. Using `@Query` and `@QueryMap`, you can implement features such as:

- Sorting posts by date or title.
- Filtering posts by user ID.
- Customizing the order of posts.

By following this guide, you can efficiently fetch, filter, and display data using Retrofit and MVVM in Android.

# Retrofit Implementation with Detailed Explanation

## Overview
Retrofit is a powerful HTTP client library for Android that allows you to define your REST API interactions in a type-safe and declarative way. This document expands on previously covered topics and introduces POST requests using both `@Body` (simple POST) and `@FormUrlEncoded` (form URL encoding).

### Directory Structure
```
com.example.retrofit
├── adapter
├── api
├── model
├── repository
├── utils
└── MainActivity.kt
```

### Additional Features Covered
1. Simple POST requests using `@Body`.
2. Form URL encoding using `@FormUrlEncoded`.
3. Real-world examples to understand usage.

---

## Simple POST Requests with `@Body`
### Code Implementation
```kotlin
// In SimpleApi
@POST("posts")
suspend fun pushPost(
    @Body post: Post
): Response<Post>

// In Repository
suspend fun pushPost(post: Post): Response<Post> {
    return RetrofitInstance.api.pushPost(post)
}

// In MainViewModel
fun pushPost(post: Post) {
    viewModelScope.launch {
        val response: Response<Post> = repository.pushPost(post)
        myResponse.value = response
    }
}

// In MainActivity
val myPost = Post(1, 1, "Sample Title", "Sample Body")
viewModel.pushPost(myPost)
viewModel.myResponse.observe(this, {
    response ->
    if (response.isSuccessful) {
        Log.d("Main", "Post ID: ${response.body()?.id}")
        Log.d("Main", "Post Title: ${response.body()?.title}")
    } else {
        Toast.makeText(this, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
    }
})
```

### How It Works
- The `@Body` annotation is used to send a complete object (e.g., a Kotlin data class) as JSON in the request body.
- Retrofit automatically serializes the object into JSON format using the converter specified in the `Retrofit.Builder`.

### Real-World Example
Imagine submitting a new blog post. You can create a `Post` object with properties like `userId`, `title`, and `body`, then send it using the `pushPost` method. The server processes the data as JSON.

---

## Form URL Encoding with `@FormUrlEncoded`
### Code Implementation
```kotlin
// In SimpleApi
@FormUrlEncoded
@POST("posts")
suspend fun pushPost2(
    @Field("userId") userId: Int,
    @Field("id") id: Int,
    @Field("title") title: String,
    @Field("body") body: String
): Response<Post>

// In Repository
suspend fun pushPost2(userId: Int, id: Int, title: String, body: String): Response<Post> {
    return RetrofitInstance.api.pushPost2(userId, id, title, body)
}

// In MainViewModel
fun pushPost2(userId: Int, id: Int, title: String, body: String) {
    viewModelScope.launch {
        val response: Response<Post> = repository.pushPost2(userId, id, title, body)
        myResponse.value = response
    }
}

// In MainActivity
viewModel.pushPost2(1, 2, "Sample Title", "Sample Body")
viewModel.myResponse.observe(this, {
    response ->
    if (response.isSuccessful) {
        Log.d("Main", "Post ID: ${response.body()?.id}")
        Log.d("Main", "Post Title: ${response.body()?.title}")
    } else {
        Toast.makeText(this, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
    }
})
```

### How It Works
- The `@FormUrlEncoded` annotation indicates that the request body will use `application/x-www-form-urlencoded` format.
- Each field is sent as a key-value pair defined with the `@Field` annotation.

### Real-World Example
Consider filling out a login form. The `pushPost2` method can send form data like `username` and `password` to the server, which processes it as URL-encoded key-value pairs.

---

## RecyclerView Integration
### Code for RecyclerView
```kotlin
// In MainActivity
private fun setupRecyclerView() {
    binding.recyclerView.adapter = myAdapter
    binding.recyclerView.layoutManager = LinearLayoutManager(this)
}

viewModel.getPost4(2, options)
viewModel.myResponse4.observe(this, {
    response ->
    if (response.isSuccessful) {
        response.body()?.let { myAdapter.setData(it) }
    } else {
        Toast.makeText(this, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
    }
})
```

---

## Comparison: Simple POST vs. Form URL Encoding
| Feature                 | Simple POST                      | Form URL Encoding               |
|-------------------------|-----------------------------------|----------------------------------|
| Annotation             | `@Body`                          | `@FormUrlEncoded`, `@Field`     |
| Request Format         | JSON                             | Key-Value Pairs                 |
| Use Case               | Complex or Nested Data           | Simple Form Submission          |
| Example                | Submitting a blog post           | Submitting login credentials     |

---


### Detailed Report on Headers in Retrofit for Android Development

In this report, we will dive into the concept of handling HTTP headers using Retrofit in Android development. We'll cover how to pass headers in your API requests, real-world use cases, and how to implement custom interceptors to modify or add headers. This tutorial will incorporate example code snippets from the provided code.

---

### 1. **What are HTTP Headers?**

HTTP headers are metadata sent alongside HTTP requests and responses. They contain information about the request or response, such as content type, authorization credentials, or any other data necessary for processing the request correctly.

In Android, using Retrofit, you can easily set headers for each request by configuring them either globally (using interceptors) or individually for specific endpoints.

---

### 2. **Why Use Headers in Retrofit?**

Headers are useful for several reasons:

- **Authentication**: Many APIs require authorization, and headers such as `Authorization` or `X-Auth-Token` are used to provide tokens for secured access.
- **Content Type**: Setting headers like `Content-Type` informs the server about the format of the data being sent (e.g., `application/json`).
- **Custom Headers**: You can use custom headers for passing additional information, such as platform type (`X-Platform`) or versioning (`X-Version`).

---

### 3. **How to Add Headers in Retrofit**

#### 3.1 **Using @Header Annotations**

You can add headers dynamically for a specific API endpoint using Retrofit's `@Header` annotation. This is useful when the header's value is dynamic, such as an authentication token that varies per user.

```kotlin
interface SimpleApi {

    @GET("posts/1")
    suspend fun getPost(@Header("Auth") auth: String): Response<Post>
}
```

In this example, the `getPost` function accepts a `String auth` parameter that will be sent as a header in the request. When calling this function, the `auth` string can be passed to customize the header dynamically.

#### Example Usage:

```kotlin
val authToken = "Bearer myAccessToken"
val response = api.getPost(authToken)
```

This will send the `Authorization` header with the specified `authToken`.

---

#### 3.2 **Using @HeaderMap for Multiple Headers**

If you want to pass multiple headers dynamically, you can use the `@HeaderMap` annotation. It allows you to pass a `Map<String, String>` where each key-value pair represents a header.

```kotlin
interface SimpleApi {

    @GET("posts/1")
    suspend fun getPost(@HeaderMap headers: Map<String, String>): Response<Post>
}
```

#### Example Usage:

```kotlin
val headers = mapOf(
    "Authorization" to "Bearer myAccessToken",
    "X-Platform" to "Android",
    "Content-Type" to "application/json"
)

val response = api.getPost(headers)
```

This will send all the headers defined in the `headers` map in the API request.

---

#### 3.3 **Global Headers with Interceptors**

Sometimes, it is necessary to add headers to every request globally (such as an API token for all requests). This can be done using an **Interceptor** in Retrofit.

##### Interceptor Implementation:

```kotlin
class MyInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
            .newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Platform", "Android")
            .addHeader("X-Auth-Token", "1212121212")  // Example static token
            .build()
        return chain.proceed(request)
    }
}
```

This custom `MyInterceptor` adds a set of headers to every outgoing request. We then add this interceptor to the `OkHttpClient`:

```kotlin
private val client = OkHttpClient.Builder().apply {
    addInterceptor(MyInterceptor())
}.build()
```

By using this method, all requests sent via Retrofit will include these headers by default, eliminating the need to manually add them to each individual request.

##### Retrofit Instance with Interceptor:

```kotlin
object RetrofitInstance {

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(MyInterceptor())
    }.build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SimpleApi by lazy {
        retrofit.create(SimpleApi::class.java)
    }
}
```

---

### 4. **Real-World Use Cases of Headers**

#### 4.1 **Authentication (Bearer Token)**

A common use case for headers is passing authentication tokens to authorize API requests. This is typically done with the `Authorization` header.

**Example:**

```kotlin
@GET("user/profile")
suspend fun getUserProfile(@Header("Authorization") token: String): Response<UserProfile>
```

When making the request, you pass the Bearer token as an argument:

```kotlin
val token = "Bearer someJWTToken"
val response = api.getUserProfile(token)
```

This ensures the server can identify and authorize the user.

---

#### 4.2 **Custom Platform Headers**

Some APIs expect to know the platform or client version. For example, you may send a custom header to identify that the request is coming from an Android app:

```kotlin
@GET("posts")
suspend fun getPosts(@Header("X-Platform") platform: String): Response<List<Post>>
```

Example usage:

```kotlin
val platform = "Android"
val response = api.getPosts(platform)
```

---

#### 4.3 **Content-Type and Accept Headers**

Another essential use of headers is setting the content type and the type of data you expect in response. For example, when sending JSON data in a POST request, you need to include the `Content-Type` header.

```kotlin
@POST("posts")
suspend fun createPost(
    @Body post: Post,
    @Header("Content-Type") contentType: String
): Response<Post>
```

Usage:

```kotlin
val contentType = "application/json"
val response = api.createPost(post, contentType)
```

---

### 5. **Error Handling with Response Object**

When using Retrofit, the response is wrapped in a `Response<T>` object. This allows you to handle errors such as 404 (Not Found) or 500 (Server Error) and retrieve both the response body and headers.

```kotlin
val response = api.getPost("authToken")

if (response.isSuccessful) {
    Log.d("Success", response.body()?.userId.toString())
} else {
    Log.e("Error", "Error: ${response.code()}")
}
```

In the above example:
- `response.isSuccessful` checks if the HTTP status code is between 200 and 299.
- `response.code()` gives the HTTP status code, which can be used for further error handling.

---

### 6. **Conclusion**

In Retrofit, headers play a crucial role in API communication. They are used for authentication, specifying content types, and passing custom data like platform information. Whether you add headers individually using annotations or globally using interceptors, understanding how to manage headers is essential for working with most modern APIs.

In the provided example, we covered several methods to handle headers:

- Using `@Header` for dynamic headers.
- Using `@HeaderMap` for passing multiple headers.
- Using custom interceptors for adding global headers.

By applying these strategies, you can manage HTTP headers in a clean and maintainable way in your Android application using Retrofit.






