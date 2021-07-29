package com.android.farmmate.common

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.farmmate.App
import com.android.farmmate.R
import com.android.farmmate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyService : Service() {
    private lateinit var notificationManager: NotificationManagerCompat
    private var flag = 1
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private var email: String? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser?.uid!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            email = user.email!!
                        } else {
                            Toast.makeText(this@MyService, "User is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        notificationManager = NotificationManagerCompat.from(this)
        FirebaseDatabase.getInstance().getReference("Approval")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (flag == 1) flag = 0 else if (flag == 0) {
                            if (email == snapshot.child("email").value.toString().trim { it <= ' ' }) {
                                notification()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        return START_STICKY
    }

    private fun notification() {
        val notification = NotificationCompat.Builder(this@MyService, App.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_baseline_message_24)
                .setContentTitle("Booking approved")
                .setContentText("Your famr booking has been approved.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build()
        notificationManager.notify(1, notification)
    }

}