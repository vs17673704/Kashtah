package com.android.farmmate.consumer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.adapter.OrderDishAdapter
import com.android.farmmate.model.Dish
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderDish : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: OrderDishAdapter
    lateinit var searchView: SearchView
    private lateinit var mAdView: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_dish)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "DISH"
        showads()
        recyclerView = findViewById(R.id.recycler)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        searchView = findViewById(R.id.searchView)
        val options = FirebaseRecyclerOptions.Builder<Dish>()
                .setQuery(FirebaseDatabase.getInstance().reference.child("Dishes"), Dish::class.java)
                .build()
        adapter = OrderDishAdapter(options, this)
        recyclerView.setAdapter(adapter)
        searchView.setQueryHint("Type dish name")
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                processSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                processSearch(newText)
                return false
            }
        })
    }

    private fun processSearch(query: String) {
        val options = FirebaseRecyclerOptions.Builder<Dish>()
                .setQuery(FirebaseDatabase.getInstance().reference.child("Dishes").orderByChild("name").startAt(query).endAt(query + "\uf8ff"), Dish::class.java)
                .build()
        adapter = OrderDishAdapter(options, this)
        adapter.startListening()
        recyclerView.adapter = adapter
    }

    private fun showads() {
        FirebaseDatabase.getInstance().getReference("Ads")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        addBanner(snapshot.child("status").value.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
    }

    private fun addBanner(status: String) {
        MobileAds.initialize(this) { }
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        if (status == "ON") mAdView.setVisibility(View.VISIBLE) else mAdView.setVisibility(View.GONE)
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

    override fun onPostResume() {
        showads()
        super.onPostResume()
    }
}