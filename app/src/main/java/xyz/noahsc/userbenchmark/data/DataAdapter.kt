package xyz.noahsc.userbenchmark.data

import android.os.Build
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import xyz.noahsc.userbenchmark.R

class DataAdapter(private val partsList: ArrayList<HardwareData>) : RecyclerView.Adapter<DataAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bindItems(data: HardwareData, position: Int) {
            val card: CardView     = this.itemView.findViewById<CardView>(R.id.cv)
            val rank: TextView     = this.itemView.findViewById(R.id.rank)
            val hardware: TextView = this.itemView.findViewById(R.id.hardware)
            val sample: TextView   = this.itemView.findViewById(R.id.samples)
            val relPerf: TextView  = this.itemView.findViewById(R.id.relative_perf)
            var end = "th"
            when (position % 10) {
                1 -> end = "st"
                2 -> end = "nd"
                3 -> end = "rd"
            }

            card.radius = 10.0f

            with(data) {
                rank.text = "$position$end"
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    hardware.text = Html.fromHtml("<b>$brand</b> $model", Html.FROM_HTML_MODE_LEGACY)
                }else {
                    hardware.text = Html.fromHtml("<b>$brand</b> $model")
                }
                sample.text = "Samples: $samples"
                relPerf.text = "Relative Performance: $benchmark%"
            }
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