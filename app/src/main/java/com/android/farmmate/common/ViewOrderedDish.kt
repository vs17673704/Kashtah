package com.android.farmmate.common

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.adapter.ViewOrderedDishAdapter
import com.google.firebase.auth.FirebaseUser
import android.os.Bundle
import com.android.farmmate.R
import com.google.firebase.auth.FirebaseAuth
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.android.farmmate.model.OrderedDish
import android.widget.Toast
import com.android.farmmate.model.User
import com.google.firebase.database.*

class ViewOrderedDish : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ViewOrderedDishAdapter
    lateinit var firebaseUser: FirebaseUser
    lateinit var query: Query
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_ordered_dish)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "ORDERED DISH HISTORY"
        firebaseUser = FirebaseAuth.getInstance().currentUser
        recyclerView = findViewById(R.id.recycler)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        checkUser(firebaseUser)
        val options = FirebaseRecyclerOptions.Builder<OrderedDish>()
                .setQuery(FirebaseDatabase.getInstance().reference.child("OrderedDish"), OrderedDish::class.java)
                .build()
        adapter = ViewOrderedDishAdapter(options, this)
        adapter.startListening()
        recyclerView.setAdapter(adapter)
    }

    private fun checkUser(userId: FirebaseUser?) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            if (user.role == "Consumer") {
                                query = FirebaseDatabase.getInstance().reference.child("OrderedDish").orderByChild("email").equalTo(FirebaseAuth.getInstance().currentUser.email)
                                val options = FirebaseRecyclerOptions.Builder<OrderedDish>()
                                        .setQuery(query, OrderedDish::class.java)
                                        .build()
                                adapter = ViewOrderedDishAdapter(options, this@ViewOrderedDish)
                                adapter.startListening()
                                recyclerView.adapter = adapter
                            } else {
                                query = FirebaseDatabase.getInstance().reference.child("OrderedDish")
                                val options = FirebaseRecyclerOptions.Builder<OrderedDish>()
                                        .setQuery(query, OrderedDish::class.java)
                                        .build()
                                adapter = ViewOrderedDishAdapter(options, this@ViewOrderedDish)
                                adapter.startListening()
                                recyclerView.adapter = adapter
                            }
                        } else {
                            Toast.makeText(this@ViewOrderedDish, "User is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        //        Toast.makeText(this@ViewOrderedDish, role, Toast.LENGTH
        //return  "Consumer";
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}