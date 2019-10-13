package xyz.noahsc.userbenchmark.data

import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.parts_list_row.view.*
import xyz.noahsc.userbenchmark.R
import xyz.noahsc.userbenchmark.R.layout.parts_list_row

class DataAdapter(var partsList: List<Hardware>) : RecyclerView.Adapter<DataAdapter.MyViewHolder>() {

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view) {

        fun bindItems(data: Hardware, position: Int) {
            val rankView:     TextView = itemView.rank
            val hardwareView: TextView = itemView.hardware
            val sampleView:   TextView = itemView.samples
            val relPerfView:  TextView = itemView.relativePerf

            var end = "th"

            if ((ComparisonData.getCompareFirst() != null && position == ComparisonData.position) || ComparisonData.getCompareFirst()?.model == data.model) {
                itemView.cv.setCardBackgroundColor(ContextCompat.getColor(itemView.cv.context, R.color.selected))
                itemView.invalidate()
            }else{
                itemView.cv.setCardBackgroundColor(ContextCompat.getColor(itemView.cv.context, R.color.cardview_light_background))
                itemView.invalidate()
            }

            with(data) {
                when (rank % 10) {
                    1 -> end = "st"
                    2 -> end = "nd"
                    3 -> end = "rd"
                }

                rankView.text = "${rank}$end"
                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    hardwareView.text = Html.fromHtml("<b>$brand</b> $model", Html.FROM_HTML_MODE_LEGACY)
                } else {
                    hardwareView.text = Html.fromHtml("<b>$brand</b> $model")
                }
                sampleView.text  = "Samples: $samples"
                relPerfView.text = "Rel. Perf: $benchmark%"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(LayoutInflater.from(parent.context).inflate(parts_list_row, parent, false))

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = holder.bindItems(partsList[position], position)

    override fun getItemCount(): Int = partsList.size
}