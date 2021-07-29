package com.android.farmmate.farmOwner

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
import com.android.farmmate.common.ViewOrderedDish
import com.android.farmmate.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import java.util.*

class FarmerDashboard() : AppCompatActivity(), View.OnClickListener {
    lateinit var manageFarm: Button
    lateinit var farmBooking: Button
    lateinit var manageDish: Button
    lateinit var orderedDish: Button
    lateinit var editProfile: Button
    lateinit var logout: Button
    private lateinit var farmOwnerName: TextView
    private lateinit var farmOwnerMobileNumber: TextView
    private lateinit var farmOwnerAddress: TextView
    lateinit var toggleButton: ToggleButton
    lateinit var statusMap: HashMap<String, Any>
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseDatabase.getInstance().getReference("Ads")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.childrenCount == 0L) setAdsStatus("ON")
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer_dashboard)
        supportActionBar!!.title = "DASHBOARD"
        init()
        manageFarm.setOnClickListener(this)
        farmBooking.setOnClickListener(this)
        manageDish.setOnClickListener(this)
        orderedDish.setOnClickListener(this)
        editProfile.setOnClickListener(this)
        logout.setOnClickListener(this)
        toggleButton.setOnClickListener(this)
        setProfile()
        statusMap = HashMap()
        checkAdsStatus()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.manageFarm -> startActivity(Intent(this@FarmerDashboard, ManageFarm::class.java))
            R.id.farmBooking -> startActivity(Intent(this@FarmerDashboard, ManageFarmBooking::class.java))
            R.id.manageDish -> startActivity(Intent(this@FarmerDashboard, ManageDish::class.java))
            R.id.orderedDish -> startActivity(Intent(this@FarmerDashboard, ViewOrderedDish::class.java))
            R.id.editProfile -> updateProfile()
            R.id.toggle -> manageAds()
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@FarmerDashboard, Login::class.java))
                finish()
            }
        }
    }

    private fun init() {
        manageFarm = findViewById(R.id.manageFarm)
        farmBooking = findViewById(R.id.farmBooking)
        manageDish = findViewById(R.id.manageDish)
        orderedDish = findViewById(R.id.orderedDish)
        editProfile = findViewById(R.id.editProfile)
        logout = findViewById(R.id.logout)
        farmOwnerName = findViewById(R.id.farmOwnerName)
        farmOwnerMobileNumber = findViewById(R.id.farmOwnerMobileNumber)
        farmOwnerAddress = findViewById(R.id.farmOwnerAddress)
        toggleButton = findViewById(R.id.toggle)
    }

    private fun checkAdsStatus() {
        FirebaseDatabase.getInstance().getReference("Ads")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (((snapshot.child("status").value.toString().trim { it <= ' ' }) == "ON")) {
                            setAdsStatus("ON")
                            toggleButton.isChecked = true
                        } else if (((snapshot.child("status").value.toString().trim { it <= ' ' }) == "ON")) {
                            setAdsStatus("OFF")
                            toggleButton.isChecked = false
                        } else {
                            setAdsStatus("ON")
                            toggleButton.isChecked = true
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
    }

    private fun updateProfile() {
        val dialog = DialogPlus.newDialog(this@FarmerDashboard)
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
        name.text = farmOwnerName.text.toString()
        mobile.text = farmOwnerMobileNumber.text.toString()
        address.text = farmOwnerAddress.text.toString()
        close.setOnClickListener(View.OnClickListener { dialog.dismiss() })
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
        dialog.show()
    }

    private fun manageAds() {
        if (toggleButton.isChecked) setAdsStatus("ON") else setAdsStatus("OFF")
        //                Toast.makeText(this, "Disabled", Toast.LENGTH_SHORT).show();
    }

    private fun setAdsStatus(status: String) {
        statusMap["status"] = status
        FirebaseDatabase.getInstance().reference.child("Ads")
                .setValue(statusMap)
                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                    override fun onComplete(task: Task<Void?>) {}
                })
                .addOnFailureListener(object : OnFailureListener {
                    override fun onFailure(e: Exception) {}
                })
    }

    private fun setProfile() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            farmOwnerName.text = user.name.toString().trim { it <= ' ' }
                            farmOwnerMobileNumber.text = user.phoneNumber.toString().trim { it <= ' ' }
                            farmOwnerAddress.text = user.address.toString().trim { it <= ' ' }
                        } else {
                            Toast.makeText(this@FarmerDashboard, "User is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
    }

    private fun hideKeyboad(v: View) {
        val inputMethodManager = this@FarmerDashboard.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
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

    override fun onPostResume() {
        super.onPostResume()
        setProfile()
    }
}