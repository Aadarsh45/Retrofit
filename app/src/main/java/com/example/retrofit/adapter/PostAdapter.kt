package com.example.retrofit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofit.databinding.ItemPostBinding
import com.example.retrofit.model.Post

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    private var myList = emptyList<Post>()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = myList[position]
        holder.binding.tvUserId.text = currentItem.userId.toString()
        holder.binding.tvId.text = currentItem.id.toString()
        holder.binding.tvTitle.text = currentItem.title
        holder.binding.tvBody.text = currentItem.body

    }

    override fun getItemCount(): Int = myList.size

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    fun setData(newList: List<Post>) {
        myList = newList
        notifyDataSetChanged()


    }
}
