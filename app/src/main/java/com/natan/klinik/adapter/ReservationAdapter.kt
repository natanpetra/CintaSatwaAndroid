package com.natan.klinik.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.natan.klinik.R
import com.natan.klinik.model.Reservation
import java.text.SimpleDateFormat
import java.util.Locale

class ReservationAdapter(private val reservations: List<Reservation>) :
    RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.bind(reservation)
    }

    override fun getItemCount(): Int = reservations.size

    inner class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvPetName: TextView = itemView.findViewById(R.id.tvPetName)
        private val tvPetType: TextView = itemView.findViewById(R.id.tvPetType)
        private val tvDateTime: TextView = itemView.findViewById(R.id.tvDateTime)
        private val tvSymptoms: TextView = itemView.findViewById(R.id.tvSymptoms)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)

        fun bind(reservation: Reservation) {
            tvPetName.text = "Nama Hewan: ${reservation.petName}"
            tvPetType.text = "Jenis: ${reservation.petType}"
            tvDateTime.text = "Jadwal: ${formatDate(reservation.reservationDate)} - ${reservation.reservationTime}"
            tvSymptoms.text = "Keluhan: ${reservation.symptoms}"
            if (reservation.doctorNotes != null){
                tvNotes.visibility = View.VISIBLE
                tvNotes.text = "Catatan Dokter: ${reservation.doctorNotes}"
                tvStatus.text = reservation.status ?: "Dijawab Dokter"
            }else{
                tvNotes.visibility = View.GONE
                tvStatus.text = reservation.status ?: "Menunggu"
            }

            // Set status dan warna

        }

        private fun formatDate(dateString: String?): String {
            if (dateString.isNullOrEmpty()) return "-"

            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                val date = inputFormat.parse(dateString)
                return outputFormat.format(date!!)
            } catch (e: Exception) {
                return dateString
            }
        }
    }
}