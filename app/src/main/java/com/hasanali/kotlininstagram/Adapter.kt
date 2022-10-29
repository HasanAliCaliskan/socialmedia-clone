package com.hasanali.kotlininstagram

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hasanali.kotlininstagram.databinding.RecyclerRowBinding
import com.squareup.picasso.Picasso

class Adapter(val arrayList: ArrayList<Post>): RecyclerView.Adapter<Adapter.ViewHolder>() {

    class ViewHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.textViewComment.text = arrayList[position].comment
        holder.binding.textViewEmail.text = arrayList[position].email
        Picasso.get().load(arrayList[position].downloadUrl).into(holder.binding.imageView)
    }

    override fun getItemCount(): Int { return arrayList.size }
}