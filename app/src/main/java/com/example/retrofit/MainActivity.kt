package com.example.retrofit

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProvider
import com.example.retrofit.repository.Repository

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val repository = Repository()
        val viewModeFactory = MainViewModeFactory(repository)
        val viewModel = ViewModelProvider(this, viewModeFactory).get(MainViewModel::class.java)
        viewModel.getPost()

        viewModel.myResponse.observe(
            this, Observer {
                response->
                if(response.isSuccessful) {
                    Log.d("Responce", response.body()?.id.toString())
                    Log.d("Responce", response.body()?.userId.toString())

                }

            }
        )




    }
}