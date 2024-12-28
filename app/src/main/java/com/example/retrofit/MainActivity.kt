package com.example.retrofit

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit.adapter.PostAdapter
import com.example.retrofit.databinding.ActivityMainBinding
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

        val options: MutableMap<String, String> = mutableMapOf()
        options["_sort"] = "id"
        options["_order"] = "desc"

        viewModel.getPost4(2, options)
        viewModel.myResponse4.observe(this,  {
            response->
            if(response.isSuccessful) {
                response.body()?.let { myAdapter.setData(it) }

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





//        binding.button.setOnClickListener {
//            val myNumber = Integer.parseInt(binding.edtText.text.toString())
//            viewModel.getPost4(myNumber,options)
//
//            viewModel.myResponse4.observe(
//                this, Observer {
//                        response->
//                    if(response.isSuccessful) {
//
//                        binding.textView.text = response.body().toString()
//                        response.body()?.forEach {
//                            Log.d("Response", it.id.toString())
//                            Log.d("Response", it.title.toString())
//                            Log.d("Response", it.userId.toString())
//                            Log.d("Response", it.body.toString())
//                            Log.d("Response", "----------------------------------------------------------------")
//                        }
//
//
//                    }
//
//                }
//            )
//        }