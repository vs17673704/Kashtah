package com.android.farmmate.farmOwner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.adapter.ManageDishAdapter
import com.android.farmmate.farmOwner.ManageDish
import com.android.farmmate.model.Dish
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class ManageDish : AppCompatActivity(), View.OnClickListener {
    lateinit var addDish: Button
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ManageDishAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_dish)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "MANAGE DISH"
        addDish = findViewById(R.id.addDish)
        addDish.setOnClickListener(this)
        recyclerView = findViewById(R.id.recycler)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val options = FirebaseRecyclerOptions.Builder<Dish>()
                .setQuery(FirebaseDatabase.getInstance().reference.child("Dishes"), Dish::class.java)
                .build()
        adapter = ManageDishAdapter(options, this)
        recyclerView.setAdapter(adapter)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addDish -> startActivity(Intent(this@ManageDish, AddDish::class.java))
        }
    }

    fun hideKeybaord(v: View) {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
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