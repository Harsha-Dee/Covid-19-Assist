package com.example.covid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CenterRVAdapter(private val centerList: List<CenterRvModal>) :
    RecyclerView.Adapter<CenterRVAdapter.CenterRVViewHolder>() {

    class CenterRVViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing all our text views along with its ids.
        val centerNameTV: TextView = itemView.findViewById(R.id.idTVCenterName)
        val centerAddressTV: TextView = itemView.findViewById(R.id.idTVCenterAddress)
        val centerTimings: TextView = itemView.findViewById(R.id.idTVCenterTimings)
        val vaccineNameTV: TextView = itemView.findViewById(R.id.idTVVaccineName)
        val centerAgeLimitTV: TextView = itemView.findViewById(R.id.idTVAgeLimit)
        val centerFeeTypeTV: TextView = itemView.findViewById(R.id.idTVFeeType)
        val avalabilityTV: TextView = itemView.findViewById(R.id.idTVAvaliablity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CenterRVViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.center_rv_item,
            parent, false
        )

        return CenterRVViewHolder(itemView)
    }

    override fun getItemCount(): Int {

        return centerList.size
    }

    // below method is to set the data to each view of our recycler view item.
    override fun onBindViewHolder(holder: CenterRVViewHolder, position: Int) {

        val currentItem = centerList[position]

        holder.centerNameTV.text = currentItem.centerName
        holder.centerAddressTV.text = currentItem.centerAddress
        holder.centerTimings.text = ("From : " + currentItem.centerFromTime + " To : " + currentItem.centerToTime)
        holder.vaccineNameTV.text = currentItem.vaccineName
        holder.centerAgeLimitTV.text = "Age Limit : " + currentItem.ageLimit.toString()
        holder.centerFeeTypeTV.text = currentItem.fee_type
        holder.avalabilityTV.text = "Availability : " + currentItem.availableCapacity.toString()
    }
}
