package com.android.farmmate.common

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.farmmate.R
import com.android.farmmate.common.Signup
import com.android.farmmate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity(), View.OnClickListener {
    private lateinit var registerButton: Button
    private lateinit var loginButton: TextView
    private lateinit var getName: EditText
    private lateinit var getAddress: EditText
    private lateinit var getPhoneNumber: EditText
    private lateinit var getEmail: EditText
    private lateinit var getPassword: EditText
    private lateinit var radioRoleGroup: RadioGroup
    private lateinit var radioRoleButton: RadioButton
    private lateinit var progress_circular: ProgressBar
    private var firebaseAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar!!.hide()
        firebaseAuth = FirebaseAuth.getInstance()
        registerButton = findViewById(R.id.registerButton)
        registerButton.setOnClickListener(this)
        getName = findViewById(R.id.getName)
        getAddress = findViewById(R.id.getAddress)
        getPhoneNumber = findViewById(R.id.getPhoneNumber)
        getEmail = findViewById(R.id.getEmail)
        getPassword = findViewById(R.id.getPassword)
        radioRoleGroup = findViewById<View>(R.id.radioGroup) as RadioGroup
        progress_circular = findViewById(R.id.progress_circular)
        progress_circular.setVisibility(View.GONE)
        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.registerButton -> registerUser()
            R.id.loginButton -> finish()
        }
    }

    private fun registerUser() {
        val name = getName.text.toString().trim { it <= ' ' }
        val address = getAddress.text.toString().trim { it <= ' ' }
        val phoneNumber = getPhoneNumber.text.toString().trim { it <= ' ' }
        val email = getEmail.text.toString().trim { it <= ' ' }
        val password = getPassword.text.toString().trim { it <= ' ' }
        val selectedId = radioRoleGroup.checkedRadioButtonId
        radioRoleButton = findViewById<View>(selectedId) as RadioButton
        //        Toast.makeText(this, radioRoleButton.getText(), Toast.LENGTH_SHORT).show();
        val role = radioRoleButton.text.toString()
        if (name.isEmpty()) {
            getName.error = "Full name is required!"
            getName.requestFocus()
            return
        }
        if (address.isEmpty()) {
            getAddress.error = "Address is required!"
            getAddress.requestFocus()
            return
        }
        if (phoneNumber.isEmpty()) {
            getPhoneNumber.error = "Phone number is required!"
            getPhoneNumber.requestFocus()
            return
        }
        if (email.isEmpty()) {
            getEmail.error = "Email is required!"
            getEmail.requestFocus()
            return
        }
//        if (!Patterns.WEB_URL.matcher(email).matches()) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            getEmail.error = "Please enter valid email!"
            getEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            getPassword.error = "Password is required!"
            getPassword.requestFocus()
            return
        }
        if (password.length < 6) {
            getPassword.error = "Min password length should be 6 characters!"
            getPassword.requestFocus()
            return
        }
        
        progress_circular.visibility = View.VISIBLE
        firebaseAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = User(name, address, phoneNumber, email, password, role)
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().currentUser.uid)
                                .setValue(user).addOnCompleteListener { task ->
                                    if (task.isComplete) {
                                        progress_circular.visibility = View.GONE
                                        Toast.makeText(this@Signup, "User had been successfully registered", Toast.LENGTH_SHORT).show()
                                        finish()
                                    } else {
                                        progress_circular.visibility = View.GONE
                                        Toast.makeText(this@Signup, "Failed to register! Try again!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                    } else {
                        progress_circular.visibility = View.GONE
                        Toast.makeText(this@Signup, "Failed to register! Try again!", Toast.LENGTH_SHORT).show()
                    }
                }
    }
}