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

By following this tutorial, you can easily integrate Retrofit with MVVM in your Android app and handle real-world API scenarios efficiently.

