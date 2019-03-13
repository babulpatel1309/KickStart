package com.kickstart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kickstart.R
import com.kickstart.constants.SideMenuBean
import com.kickstart.main.DashboardActivity
import kotlinx.android.synthetic.main.row_sidemenu.view.*

class SideMenuAdapter(val context: Context) : ListAdapter<SideMenuBean, SideMenuAdapter.VH>(HashDiff()) {

    val baseActivity = context as DashboardActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(context).inflate(R.layout.row_sidemenu, null))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {

        holder.itemView.imgMenu.setImageResource(getItem(position).menuImg)
        holder.itemView.txtTitle.text = getItem(position).menuTxt

    }


    class HashDiff : DiffUtil.ItemCallback<SideMenuBean>() {
        override fun areItemsTheSame(p0: SideMenuBean, p1: SideMenuBean): Boolean {
            return p0 == p1
        }

        override fun areContentsTheSame(p0: SideMenuBean, p1: SideMenuBean): Boolean {
            return p0 == p1
        }
    }

    class VH(itemview: View) : RecyclerView.ViewHolder(itemview)
}