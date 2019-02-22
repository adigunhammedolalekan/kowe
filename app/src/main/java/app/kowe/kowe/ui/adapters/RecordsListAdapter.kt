package app.kowe.kowe.ui.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import app.kowe.kowe.R
import app.kowe.kowe.bindView
import app.kowe.kowe.data.models.Record

class RecordsListAdapter(private val records: List<Record>): RecyclerView.Adapter<RecordsListAdapter.RecordViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            RecordViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_record, parent, false))

    override fun getItemCount() = records.size

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {

        val next = records[position]
        holder.nameOrPhoneNumber.text = next.phoneNumber
        holder.recordDateTime.text = next.readableTime
    }


    class RecordViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val nameOrPhoneNumber by bindView<TextView>(R.id.tv_contact_name_or_phone)
        val recordDateTime by bindView<TextView>(R.id.tv_call_record_date_layout_record)
    }
}