package app.kowe.kowe.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import app.kowe.kowe.R
import app.kowe.kowe.bindView
import app.kowe.kowe.data.models.Record


class RecordsListAdapter(private val records: List<Record>): RecyclerView.Adapter<RecordsListAdapter.RecordViewHolder>() {

    var clickLister: (Record) -> Unit = {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_record, parent, false))

    override fun getItemCount() = records.size

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {

        val next = records[position]
        holder.nameOrPhoneNumber.text = next.phoneNumber
        holder.recordDateTime.text = next.readableTime
        holder.durationTextView.text = next.callDuration

        holder.root.setOnClickListener {
            clickLister(next)
        }
    }


    class RecordViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val durationTextView by bindView<TextView>(R.id.tv_duration_layout_record)
        val nameOrPhoneNumber by bindView<TextView>(R.id.tv_contact_name_or_phone)
        val recordDateTime by bindView<TextView>(R.id.tv_call_record_date_layout_record)
        val root by bindView<RelativeLayout>(R.id.btn_call_record_root)
    }
}