package com.android.farmmate.adapter

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.adapter.OrganizeFarmBookingAdapter.FarmOrderViewHolder
import com.android.farmmate.model.BookedFarm
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import com.squareup.picasso.Picasso
import java.util.*

class OrganizeFarmBookingAdapter constructor(options: FirebaseRecyclerOptions<BookedFarm?>,
                                             /**
                                              * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See
                                              * [FirebaseRecyclerOptions] for configuration options.
                                              *
                                              * @param options
                                              */
                                             private val context: Context) : FirebaseRecyclerAdapter<BookedFarm, FarmOrderViewHolder>(options) {
    private var rescheduledDate: String? = null
    override fun onBindViewHolder(holder: FarmOrderViewHolder, position: Int, model: BookedFarm) {
        holder.bookedFarm.setText(model.farmName)
        holder.bookingPrice.setText("₹"+model.price)
        holder.modeOfPayment.setText(model.paymentMode)
        holder.bookingStatus.setText(model.status)
        holder.bookingDate.setText(model.date)
        holder.stay.setText(model.stay)
        Picasso.get().load(model.imageUrl).fit().into(holder.farmImage)

        holder.deleteFarm.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                FirebaseDatabase.getInstance().getReference()
                        .child("FarmBooking")
                        .child((getRef(holder.getAdapterPosition()).getKey())!!)
                        .removeValue()
                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                            public override fun onComplete(task: Task<Void?>) {
                                Toast.makeText(context, "Item successfully deleted", Toast.LENGTH_SHORT).show()
                            }
                        })
                        .addOnSuccessListener(object : OnSuccessListener<Void?> {
                            public override fun onSuccess(aVoid: Void?) {}
                        })
                        .addOnFailureListener(object : OnFailureListener {
                            public override fun onFailure(e: Exception) {}
                        })
            }
        })
        holder.reschedule.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                //Toast.makeText(context, model.getFarmName()+"\n"+model.getFarmDescription()+"\n"+model.getFarmPrice(), Toast.LENGTH_SHORT).show();
                val dialog: DialogPlus = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setCancelable(false)
                        .setContentHolder(ViewHolder(R.layout.rechedule_dialog))
                        .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
                        .create()
                val holderView: View = dialog.getHolderView() as LinearLayout
                val reschedule_booking: Button = holderView.findViewById(R.id.reschedule_booking)
                val close: Button = holderView.findViewById(R.id.close)
                val selectDate: Button = holderView.findViewById(R.id.selectDate)
                val date: TextView = holderView.findViewById(R.id.date)
                rescheduledDate = date.getText().toString()
                close.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        dialog.dismiss()
                    }
                })
                selectDate.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        val calendar: Calendar = Calendar.getInstance()
                        val year: Int = calendar.get(Calendar.YEAR)
                        val month: Int = calendar.get(Calendar.MONTH)
                        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
                        val datePickerDialog: DatePickerDialog = DatePickerDialog(context, object : OnDateSetListener {
                            public override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
                                val sDate: String = dayOfMonth.toString() + "/" + month + "/" + year
                                date.setText(sDate)
                                rescheduledDate = sDate
                                date.setTextColor(Color.parseColor("#FF3700B3"))
                                date.setError(null)
                            }
                        }, year, month, day)
                        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000)
                        datePickerDialog.setCanceledOnTouchOutside(false)
                        datePickerDialog.show()
                    }
                })
                reschedule_booking.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        if (rescheduledDate!!.isEmpty()) {
                            date.setError("Please select a date!")
                            date.requestFocus()
                            return
                        }
                        val map: HashMap<String, Any> = HashMap()
                        map.put("date", rescheduledDate!!)
                        map.put("status", "Booking pending")
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
                dialog.show()
            }
        })
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmOrderViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.organize_farm_booking_template, parent, false)
        return FarmOrderViewHolder(view)
    }

    inner class FarmOrderViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bookedFarm: TextView
        var bookingPrice: TextView
        var modeOfPayment: TextView
        var bookingStatus: TextView
        var stay: TextView
        var bookingDate: TextView
        var reschedule: ImageView
        var deleteFarm: ImageView
        var farmImage: ImageView

        init {
            bookedFarm = itemView.findViewById(R.id.bookedFarm)
            bookingPrice = itemView.findViewById(R.id.bookingPrice)
            modeOfPayment = itemView.findViewById(R.id.modeOfPayment)
            bookingStatus = itemView.findViewById(R.id.bookingStatus)
            stay = itemView.findViewById(R.id.stay)
            bookingDate = itemView.findViewById(R.id.bookingDate)
            reschedule = itemView.findViewById(R.id.reschedule)
            deleteFarm = itemView.findViewById(R.id.deleteFarm)
            farmImage = itemView.findViewById(R.id.farmImage)
        }
    }
}