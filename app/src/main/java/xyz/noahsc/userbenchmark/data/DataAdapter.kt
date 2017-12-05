package xyz.noahsc.userbenchmark.data

import android.content.Context
import android.os.Build
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.parts_list_row.view.*
import xyz.noahsc.userbenchmark.R

class DataAdapter(private val partsList: ArrayList<Hardware>) : RecyclerView.Adapter<DataAdapter.MyViewHolder>() {

    class MyViewHolder(private val ctx: Context, view: View): RecyclerView.ViewHolder(view) {

        fun bindItems(data: Hardware) {
            val card:     CardView = itemView.cv
            val rank:     TextView = itemView.rank
            val hardware: TextView = itemView.hardware
            val sample:   TextView = itemView.samples
            val relPerf:  TextView = itemView.relativePerf

            var end = "th"

            //card.radius = 10.0f

            with(data) {
                when (data.rank % 10) {
                    1 -> end = "st"
                    2 -> end = "nd"
                    3 -> end = "rd"
                }

                rank.text = "${data.rank}$end"
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    hardware.text = Html.fromHtml("<b>$brand</b> $model", Html.FROM_HTML_MODE_LEGACY)
                } else {
                    hardware.text = Html.fromHtml("<b>$brand</b> $model")
                }
                sample.text  = "Samples: $samples"
                relPerf.text = "Relative Performance: $benchmark%"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(parent.context, LayoutInflater.from(parent.context).inflate(R.layout.parts_list_row, parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = holder.bindItems(partsList[position])

    override fun getItemCount(): Int = partsList.size
}