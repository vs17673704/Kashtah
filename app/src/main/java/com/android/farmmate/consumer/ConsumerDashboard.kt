package com.android.farmmate.consumer

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.farmmate.R
import com.android.farmmate.common.Login
import com.android.farmmate.common.MyService
import com.android.farmmate.common.ViewOrderedDish
import com.android.farmmate.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import java.util.*

class ConsumerDashboard() : AppCompatActivity() {
    private lateinit var orderDish: Button
    private lateinit var viewFarm: Button
    private lateinit var farmBooking: Button
    private lateinit var orderedDish: Button
    private lateinit var editProfile: Button
    private lateinit var logout: Button
    private lateinit var consumerName: TextView
    private lateinit var consumerMobileNumber: TextView
    private lateinit var consumerAddress: TextView

    //private String name, phoneNumber, address;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consumer_dashboard)
        supportActionBar!!.title = "DASHBOARD"
        startService(Intent(this, MyService::class.java))
        init()
        setProfile()
        viewFarm.setOnClickListener(View.OnClickListener { startActivity(Intent(this@ConsumerDashboard, BookFarm::class.java)) })
        orderDish.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(Intent(this@ConsumerDashboard, OrderDish::class.java))
            }
        })
        farmBooking.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(Intent(this@ConsumerDashboard, OrganizeFarmBooking::class.java))
            }
        })
        orderedDish.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                startActivity(Intent(this@ConsumerDashboard, ViewOrderedDish::class.java))
            }
        })
        logout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@ConsumerDashboard, Login::class.java))
                finish()
                stopService(Intent(this@ConsumerDashboard, MyService::class.java))
            }
        })
        editProfile.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val dialog = DialogPlus.newDialog(this@ConsumerDashboard)
                        .setGravity(Gravity.TOP)
                        .setMargin(50, 100, 50, 0)
                        .setContentHolder(ViewHolder(R.layout.edit_consumer_profile))
                        .setCancelable(false)
                        .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
                        .create()
                val holderView: View = dialog.holderView as LinearLayout
                val update = holderView.findViewById<Button>(R.id.update)
                val close = holderView.findViewById<Button>(R.id.close)
                val name = holderView.findViewById<TextView>(R.id.name)
                val mobile = holderView.findViewById<TextView>(R.id.mobile)
                val address = holderView.findViewById<TextView>(R.id.address)
                name.text = consumerName.text.toString()
                mobile.text = consumerMobileNumber.text.toString()
                address.text = consumerAddress.text.toString()
                update.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        if (name.text.toString().trim { it <= ' ' }.isEmpty()) {
                            name.error = "Name is required!"
                            name.requestFocus()
                            return
                        }
                        if (mobile.text.toString().trim { it <= ' ' }.isEmpty()) {
                            mobile.error = "Mobile number is required!"
                            mobile.requestFocus()
                            return
                        }
                        if (address.text.toString().trim { it <= ' ' }.isEmpty()) {
                            address.error = "Address is required!"
                            address.requestFocus()
                            return
                        }
                        val map = HashMap<String, Any>()
                        map["name"] = name.text.toString()
                        map["address"] = address.text.toString()
                        map["phoneNumber"] = mobile.text.toString()
                        FirebaseDatabase.getInstance().reference
                                .child("Users")
                                .child(FirebaseAuth.getInstance().currentUser.uid)
                                .updateChildren(map)
                                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                    override fun onComplete(task: Task<Void?>) {
                                        setProfile()
                                        dialog.dismiss()
                                        hideKeyboad(v)
                                    }
                                })
                    }
                })
                close.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        dialog.dismiss()
                    }
                })
                dialog.show()
            }
        })
    }

    private fun hideKeyboad(v: View) {
        val inputMethodManager = this@ConsumerDashboard.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }

    private fun init() {
        orderDish = findViewById(R.id.orderDish)
        viewFarm = findViewById(R.id.viewFarm)
        farmBooking = findViewById(R.id.farmBooking)
        orderedDish = findViewById(R.id.orderedDish)
        consumerName = findViewById(R.id.consumerName)
        consumerMobileNumber = findViewById(R.id.consumerMobileNumber)
        consumerAddress = findViewById(R.id.consumerAddress)
        logout = findViewById(R.id.logout)
        editProfile = findViewById(R.id.editProfile)
    }

    private fun setProfile() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            consumerName.text = user.name.toString().trim { it <= ' ' }
                            consumerMobileNumber.text = user.phoneNumber.toString().trim { it <= ' ' }
                            consumerAddress.text = user.address.toString().trim { it <= ' ' }
                        } else {
                            Toast.makeText(this@ConsumerDashboard, "User is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
    }

    override fun onPostResume() {
        super.onPostResume()
        setProfile()
    }

    override fun onBackPressed() {
        val alertbox = AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(arg0: DialogInterface, arg1: Int) {
                        finish()
                    }
                })
                .setNegativeButton("No", object : DialogInterface.OnClickListener {
                    override fun onClick(arg0: DialogInterface, arg1: Int) {}
                })
                .show()
    }
}