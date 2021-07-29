package com.android.farmmate.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.android.farmmate.R
import com.android.farmmate.consumer.ConsumerDashboard
import com.android.farmmate.farmOwner.FarmerDashboard
import com.android.farmmate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Home : AppCompatActivity() {
    //private var intent: Intent? = null
    private lateinit var progressBar: ProgressBar
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar!!.hide()
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        progressBar = findViewById(R.id.progressBar)
        progressBar.setVisibility(View.VISIBLE)


        if (firebaseUser != null) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().currentUser.uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            progressBar.setVisibility(View.VISIBLE)
                            val user = snapshot.getValue(User::class.java)
                            if (user != null) {
                                val role = user.role
                                if (role == "Farm Owner") {
                                    val intent = Intent(this@Home, FarmerDashboard::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    progressBar.setVisibility(View.GONE)
                                    startActivity(intent)
                                    finish()
                                }
                                else if(role == "Consumer") {
                                    val intent = Intent(this@Home, ConsumerDashboard::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    progressBar.setVisibility(View.GONE)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
        } else {
            startActivity(Intent(this@Home, Login::class.java))
            finish()
        }
    }
}