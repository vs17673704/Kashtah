package com.android.farmmate.common

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.farmmate.R
import com.android.farmmate.common.Login
import com.android.farmmate.consumer.ConsumerDashboard
import com.android.farmmate.farmOwner.FarmerDashboard
import com.android.farmmate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login : AppCompatActivity(), View.OnClickListener {
    private lateinit var signupButton: Button
    private lateinit var loginButton: Button
    private lateinit var email: EditText
    private lateinit var password: EditText
    private val forgotPasswordButton: TextView? = null
    private var firebaseAuth: FirebaseAuth? = null
    private val firebaseUser: FirebaseUser? = null
    private lateinit var loginProgress: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()
        signupButton = findViewById(R.id.signupButton)
        signupButton.setOnClickListener(this)
        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener(this)
        loginProgress = findViewById(R.id.loginProgress)
        loginProgress.setVisibility(View.GONE)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        firebaseAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.signupButton -> startActivity(Intent(this, Signup::class.java))
            R.id.loginButton -> userLogin()
        }
    }

    private fun userLogin() {
        val e = email.text.toString().trim { it <= ' ' }
        val p = password.text.toString().trim { it <= ' ' }
        if (e.isEmpty()) {
            email.error = "Email is required!"
            email.requestFocus()
            return
        }
        if (p.isEmpty()) {
            password.error = "Email is required!"
            password.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            email.error = "Please enter a valid email!"
            email.requestFocus()
            return
        }
        if (p.length < 6) {
            password.error = "Min password length should be 6 characters!"
            password.requestFocus()
            return
        }
        firebaseAuth!!.signInWithEmailAndPassword(e, p)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginProgress.visibility = View.VISIBLE
                        validateSession(FirebaseAuth.getInstance().currentUser)
                    } else {
                        Toast.makeText(this@Login, "Failed to login. Please check your credentials", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validateSession(firebaseUser: FirebaseUser) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            val role = user.role
                            if (role == "Farm Owner") {
                                loginProgress.visibility = View.GONE
                                startActivity(Intent(this@Login, FarmerDashboard::class.java))
                                finish()
                            } else {
                                startActivity(Intent(this@Login, ConsumerDashboard::class.java))
                                finish()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        startActivity(Intent(this@Login, Login::class.java))
                    }
                })
    }

    override fun onBackPressed() {
        val alertbox = AlertDialog.Builder(this)
                .setMessage("Do you want to exit application?")
                .setPositiveButton("Yes") { arg0, arg1 -> finish() }
                .setNegativeButton("No") { arg0, arg1 -> }
                .show()
    }
}