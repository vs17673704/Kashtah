package com.android.farmmate.consumer

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.farmmate.R
import com.android.farmmate.consumer.OrderConfirmaton
import com.android.farmmate.model.BookedFarm
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import java.util.*

class OrderConfirmaton : AppCompatActivity(), View.OnClickListener {
    private lateinit var scheduledDate: String
    private lateinit var select_date: Button
    private lateinit var confirm_button: Button
    private lateinit var date: TextView
    private lateinit var farm_name: TextView
    private lateinit var stay: TextView
    private lateinit var farm_price: TextView
    private lateinit var name: TextView
    private lateinit var email: TextView
    private lateinit var mode_of_payment: TextView
    private lateinit var farmImage: ImageView
    private lateinit var hashMap: HashMap<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmaton)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "CONFIRM ORDER"
        initConf()
        scheduledDate = date.text.toString()
        val intent = intent
        hashMap = ((intent.getSerializableExtra("extra_map") as HashMap<String, String>?)!!)
        farm_name.text = hashMap["farmName"]
        farm_price.text = hashMap["price"]
        name.text = hashMap["name"]
        email.text = hashMap["email"]
        stay.text = hashMap["stay"]
        mode_of_payment.text = hashMap["payment_mode"]
        Picasso.get().load(hashMap["image_url"]).fit().into(farmImage)
        select_date.setOnClickListener(this)
        confirm_button.setOnClickListener(this)
    }

    private fun initConf() {
        farm_name = findViewById(R.id.farm_name)
        farm_price = findViewById(R.id.farm_price)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        stay = findViewById(R.id.stay)
        mode_of_payment = findViewById(R.id.mode_of_payment)
        farmImage = findViewById(R.id.farmImage)
        date = findViewById(R.id.date)
        select_date = findViewById(R.id.select_date)
        confirm_button = findViewById(R.id.confirm_button)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.select_date -> setDate()
            R.id.confirm_button -> addFarmBooking()
        }
    }

    private fun addFarmBooking() {
        if (scheduledDate.isEmpty()) {
            date.error = "Please select a date!"
            date.requestFocus()
            return
        }
        val bookedFarm = BookedFarm(
                hashMap["farmName"],
                hashMap["price"],
                scheduledDate,
                hashMap["name"],
                hashMap["email"],
                hashMap["phone_number"],
                hashMap["payment_mode"],
                hashMap["image_url"],
                "Booking pending",
                hashMap["stay"]
        )
        FirebaseDatabase.getInstance().reference.child("FarmBooking")
                .push()
                .setValue(bookedFarm)
                .addOnCompleteListener {
                    Toast.makeText(this@OrderConfirmaton, "Farm successfully booked....", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@OrderConfirmaton, "Farm booking failed....", Toast.LENGTH_SHORT).show()
                    finish()
                }
    }

    private fun setDate() {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this@OrderConfirmaton, { view, year, month, dayOfMonth ->
            val sDate = "$dayOfMonth/$month/$year"
            date.text = sDate
            scheduledDate = sDate
            date.setTextColor(Color.parseColor("#FF3700B3"))
            date.error = null
        }, year, month, day)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.setCanceledOnTouchOutside(false)
        datePickerDialog.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}