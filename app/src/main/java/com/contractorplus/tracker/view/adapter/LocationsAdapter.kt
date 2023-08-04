package com.contractorplus.tracker.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.contractorplus.tracker.databinding.ItemLocationBinding
import com.contractorplus.tracker.model.LocationInfo
import java.util.ArrayList

class LocationsAdapter: RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {
    private var locations: ArrayList<LocationInfo>? = ArrayList<LocationInfo>()
    class ViewHolder(val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLocationBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return locations!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.location = locations!![position]
    }

    fun updateLocations(it: ArrayList<LocationInfo>?) {
        locations!!.clear()
        locations!!.addAll(it!!)
        notifyDataSetChanged()
    }
}