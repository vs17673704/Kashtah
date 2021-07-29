package com.android.farmmate.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.adapter.ManageFarmBookingAdapter.FarmBookingViewHolder
import com.android.farmmate.model.BookedFarm
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.util.*

class ManageFarmBookingAdapter constructor(options: FirebaseRecyclerOptions<BookedFarm?>,
                                           /**
                                            * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See
                                            * [FirebaseRecyclerOptions] for configuration options.
                                            *
                                            * @param options
                                            */
                                           private val context: Context) : FirebaseRecyclerAdapter<BookedFarm, FarmBookingViewHolder>(options) {
    override fun onBindViewHolder(holder: FarmBookingViewHolder, position: Int, model: BookedFarm) {
        holder.bookedFarm.setText(" "+model.farmName)
        holder.bookingPrice.setText(" ₹"+model.price)
        holder.customerName.setText(" "+model.customeName)
        holder.customerMobile.setText(" Mobile: "+model.phoneNumber)
        holder.modeOfPayment.setText(" "+model.paymentMode)
        holder.bookingStatus.setText(" "+model.status)
        holder.bookingDate.setText(" "+model.date)
        Picasso.get().load(model.imageUrl).fit().into(holder.farmImage)
        if (((model.status == "Booking confirmed")) || ((model.status == "Booking cancelled"))) {
            holder.approve.setVisibility(View.GONE)
            holder.reject.setVisibility(View.GONE)
        }
        if (((model.status == "Booking pending"))) {
            holder.approve.setVisibility(View.VISIBLE)
            holder.reject.setVisibility(View.VISIBLE)
        }
        holder.approve.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setMessage("Do you want to approve the booking?")
                        .setPositiveButton("Approve", object : DialogInterface.OnClickListener {
                            public override fun onClick(dialog: DialogInterface, which: Int) {
                                val map: HashMap<String, Any> = HashMap()
                                map.put("status", "Booking confirmed")
                                FirebaseDatabase.getInstance().getReference()
                                        .child("FarmBooking")
                                        .child((getRef(position).getKey())!!)
                                        .updateChildren(map)
                                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            public override fun onComplete(task: Task<Void?>) {
                                                dialog.dismiss()
                                            }
                                        })
                                val map1: HashMap<String, String?> = HashMap()
                                map1.put("email", model.email)
                                map1.put("bookingId", getRef(position).getKey())
                                FirebaseDatabase.getInstance().getReference().child("Approval")
                                        .setValue(map1)
                                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            public override fun onComplete(task: Task<Void?>) {}
                                        })
                                        .addOnFailureListener(object : OnFailureListener {
                                            public override fun onFailure(e: Exception) {}
                                        })
                            }
                        })
                        .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                            public override fun onClick(dialog: DialogInterface, which: Int) {
                                dialog.dismiss()
                            }
                        })
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        })
        holder.reject.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
                builder.setMessage("Do you want to reject the booking?")
                        .setPositiveButton("Reject", object : DialogInterface.OnClickListener {
                            public override fun onClick(dialog: DialogInterface, which: Int) {
                                val map: HashMap<String, Any> = HashMap()
                                map.put("status", "Booking cancelled")
                                FirebaseDatabase.getInstance().getReference()
                                        .child("FarmBooking")
                                        .child((getRef(position).getKey())!!)
                                        .updateChildren(map)
                                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            public override fun onComplete(task: Task<Void?>) {
                                                dialog.dismiss()
                                            }
                                        })
                            }
                        })
                        .setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
                            public override fun onClick(dialog: DialogInterface, which: Int) {
                                dialog.dismiss()
                            }
                        })
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        })
        holder.deleteFarm.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                FirebaseDatabase.getInstance().getReference()
                        .child("FarmBooking")
                        .child((getRef(holder.getAdapterPosition()).getKey())!!)
                        .removeValue()
                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                            public override fun onComplete(task: Task<Void?>) {}
                        })
                        .addOnSuccessListener(object : OnSuccessListener<Void?> {
                            public override fun onSuccess(aVoid: Void?) {
                                Toast.makeText(context, "Item successfully deleted", Toast.LENGTH_SHORT).show()
                            }
                        })
                        .addOnFailureListener(object : OnFailureListener {
                            public override fun onFailure(e: Exception) {}
                        })
            }
        })
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmBookingViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_farm_booking_template, parent, false)
        return FarmBookingViewHolder(view)
    }

    inner class FarmBookingViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bookedFarm: TextView
        var bookingPrice: TextView
        var customerName: TextView
        var customerMobile: TextView
        var modeOfPayment: TextView
        var bookingStatus: TextView
        var bookingDate: TextView
        var approve: ImageView
        var reject: ImageView
        var farmImage: ImageView
        var deleteFarm: ImageView

        init {
            bookedFarm = itemView.findViewById(R.id.bookedFarm)
            bookingPrice = itemView.findViewById(R.id.bookingPrice)
            customerName = itemView.findViewById(R.id.customerName)
            customerMobile = itemView.findViewById(R.id.customerMobile)
            modeOfPayment = itemView.findViewById(R.id.modeOfPayment)
            bookingStatus = itemView.findViewById(R.id.bookingStatus)
            bookingDate = itemView.findViewById(R.id.bookingDate)
            approve = itemView.findViewById(R.id.approve)
            reject = itemView.findViewById(R.id.reject)
            deleteFarm = itemView.findViewById(R.id.deleteFarm)
            farmImage = itemView.findViewById(R.id.farmImage)
        }
    }
}