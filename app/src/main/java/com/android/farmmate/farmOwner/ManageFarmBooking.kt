package com.android.farmmate.farmOwner

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.adapter.ManageFarmBookingAdapter
import com.android.farmmate.model.BookedFarm
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class ManageFarmBooking constructor() : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ManageFarmBookingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_farm_booking)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()?.setTitle("MANAGE FARM BOOKINGS")
        recyclerView = findViewById(R.id.recycler)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val options: FirebaseRecyclerOptions<BookedFarm?> = FirebaseRecyclerOptions.Builder<BookedFarm>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("FarmBooking"), BookedFarm::class.java)
                .build()
        adapter = ManageFarmBookingAdapter(options, this)
        recyclerView.setAdapter(adapter)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    public override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}