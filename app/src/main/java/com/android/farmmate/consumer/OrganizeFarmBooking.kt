package com.android.farmmate.consumer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.adapter.OrganizeFarmBookingAdapter
import com.android.farmmate.model.BookedFarm
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrganizeFarmBooking constructor() : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: OrganizeFarmBookingAdapter
    private lateinit var mAdView: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_organize_farm_booking)
        getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
        getSupportActionBar()!!.setTitle("ORGANIZE BOOKING")
        showads()
        recyclerView = findViewById(R.id.recycler)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val options: FirebaseRecyclerOptions<BookedFarm?> = FirebaseRecyclerOptions.Builder<BookedFarm>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("FarmBooking").orderByChild("email").equalTo(FirebaseAuth.getInstance().currentUser.email), BookedFarm::class.java)
                .build()
        adapter = OrganizeFarmBookingAdapter(options, this)
        recyclerView.setAdapter(adapter)
    }

    private fun showads() {
        FirebaseDatabase.getInstance().getReference("Ads")
                .addValueEventListener(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        addBanner(snapshot.child("status").getValue().toString())
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
    }

    private fun addBanner(status: String) {
        MobileAds.initialize(this, object : OnInitializationCompleteListener {
            public override fun onInitializationComplete(initializationStatus: InitializationStatus) {}
        })
        mAdView = findViewById(R.id.adView)
        val adRequest: AdRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        if ((status == "ON")) mAdView.setVisibility(View.VISIBLE) else mAdView.setVisibility(View.GONE)
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