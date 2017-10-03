package xyz.noahsc.userbenchmark.data

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xyz.noahsc.userbenchmark.R

class DataAdapter(val partsList: ArrayList<HardwareData>) : RecyclerView.Adapter<DataAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindItems(data: HardwareData, position: Int) {
            val rank: TextView = this.itemView.findViewById(R.id.rank)
            val brand: TextView = this.itemView.findViewById(R.id.brand)
            val model: TextView = this.itemView.findViewById(R.id.model)
            val benchmark: TextView = this.itemView.findViewById(R.id.benchmark)
            val samples: TextView = this.itemView.findViewById(R.id.samples)
            var end = "th"
            when (position % 10) {
                1 -> end = "st"
                2 -> end = "nd"
                3 -> end = "rd"
            }
            rank.text = position.toString()+end
            brand.text = data.brand
            model.text = data.model
            benchmark.text = data.benchmark.toString()
            samples.text = "Samples: "+ data.samples.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.parts_list_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(partsList[position], position+1)
    }

    override fun getItemCount(): Int = partsList.size
}