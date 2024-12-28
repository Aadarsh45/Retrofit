package com.example.retrofit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit.adapter.PostAdapter
import com.example.retrofit.databinding.ActivityMainBinding
import com.example.retrofit.model.Post
import com.example.retrofit.repository.Repository

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val myAdapter by lazy { PostAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerview()


        val repository = Repository()
        val viewModeFactory = MainViewModeFactory(repository)
        val viewModel = ViewModelProvider(this, viewModeFactory).get(MainViewModel::class.java)

        val myPost : Post = Post(1, 1, "Steve", "Body...............")

        viewModel.pushPost2(myPost)
        viewModel.myResponse.observe(this,  {
            response->
            if(response.isSuccessful) {
                Log.d("Main", response.body()?.id.toString())
                Log.d("Main", response.body()?.title.toString())
                Log.d("Main", response.body()?.body.toString())

            }
            else{
                Toast.makeText(this, response.code(), Toast.LENGTH_SHORT).show()
            }

        })



    }
    private fun setupRecyclerview() {
        binding.recyclerView.adapter = myAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}





