package com.kickstart.adapter

import android.content.Context
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kickstart.R

class DemoAdapter(val context: Context) : ListAdapter<String, DemoAdapter.VH>(diffItems()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.grid_images, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
    }


    class diffItems() : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    class VH(itemview: View) : RecyclerView.ViewHolder(itemview)

}