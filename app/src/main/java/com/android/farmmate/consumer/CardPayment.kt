package com.android.farmmate.consumer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.farmmate.R
import java.util.*

class CardPayment : AppCompatActivity(), View.OnClickListener {
    lateinit var cardNumber: EditText
    lateinit var month: EditText
    lateinit var year: EditText
    lateinit var pinCode: EditText
    lateinit var pay: Button
    lateinit var close: Button
    lateinit var hashMap: HashMap<String, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_payment)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "CARD PAYMENT"
        val intent = intent
        hashMap = ((intent.getSerializableExtra("extra_map") as HashMap<String, String>?)!!)
        init()
        cardNumber.setOnClickListener(this)
        month.setOnClickListener(this)
        year.setOnClickListener(this)
        pinCode.setOnClickListener(this)
        pay.setOnClickListener(this)
        close.setOnClickListener(this)
    }

    private fun init() {
        cardNumber = findViewById(R.id.cardNumber)
        month = findViewById(R.id.month)
        year = findViewById(R.id.year)
        pinCode = findViewById(R.id.pinCode)
        pay = findViewById(R.id.pay)
        close = findViewById(R.id.close)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.pay -> {
                if (cardNumber.text.toString().trim { it <= ' ' }.isEmpty()) {
                    cardNumber.error = "Card number is required!"
                    cardNumber.requestFocus()
                    return
                }
                if (month.text.toString().trim { it <= ' ' }.isEmpty()) {
                    Toast.makeText(this, "Month is required!", Toast.LENGTH_SHORT).show()
                    month.requestFocus()
                    return
                }
                if (year.text.toString().trim { it <= ' ' }.isEmpty()) {
                    Toast.makeText(this, "Year is required!", Toast.LENGTH_SHORT).show()
                    year.requestFocus()
                    return
                }
                if (cardNumber.text.toString().trim { it <= ' ' }.length > 16) {
                    cardNumber.error = "Please enter 16 digit number!"
                    cardNumber.requestFocus()
                    return
                }
                if (month.text.toString().trim { it <= ' ' }.toInt() == 0 || month.text.toString().trim { it <= ' ' }.toInt() >= 13) {
                    Toast.makeText(this, "Please enter valid month", Toast.LENGTH_SHORT).show()
                    month.requestFocus()
                    return
                }
                if (year.text.toString().trim { it <= ' ' }.toInt() == 0 || year.text.toString().trim { it <= ' ' }.toInt() < Calendar.getInstance()[Calendar.YEAR] % 2000) {
                    Toast.makeText(this, "Please enter valid year", Toast.LENGTH_SHORT).show()
                    year.requestFocus()
                    return
                }
                if (pinCode.text.toString().trim { it <= ' ' }.isEmpty()) {
                    pinCode.error = "Pin code is required!"
                    pinCode.requestFocus()
                    return
                }
                val intent = Intent(this, OrderConfirmaton::class.java)
                intent.putExtra("extra_map", hashMap)
                this.startActivity(intent)
                finish()
            }
            R.id.close -> finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}