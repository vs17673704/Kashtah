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
import com.android.farmmate.adapter.ManageFarmAdapter
import com.android.farmmate.farmOwner.ManageFarm
import com.android.farmmate.model.Farm
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase

class ManageFarm : AppCompatActivity(), View.OnClickListener {
    lateinit var addFarm: Button
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ManageFarmAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_farm)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "MANAGE FARMS"
        addFarm = findViewById(R.id.addFarm)
        addFarm.setOnClickListener(this)
        recyclerView = findViewById(R.id.recycler)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        val options = FirebaseRecyclerOptions.Builder<Farm>()
                .setQuery(FirebaseDatabase.getInstance().reference.child("Farms"), Farm::class.java)
                .build()
        adapter = ManageFarmAdapter(options, this)
        recyclerView.setAdapter(adapter)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.addFarm -> {
                startActivity(Intent(this@ManageFarm, AddFarm::class.java))
            }
        }
    }

    /*private void addFarm() {
        final DialogPlus dialog = DialogPlus.newDialog(this)
                .setGravity(Gravity.CENTER)
                .setMargin(50,0,50,0)
                .setContentHolder(new ViewHolder(R.layout.update_farm_dialog))
                .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                .create();
        dialog.show();
        View holderView = (LinearLayout)dialog.getHolderView();
        EditText farmName           = holderView.findViewById(R.id.updateFarmName);
        EditText farmDescription    = holderView.findViewById(R.id.updateFarmDescription);
        EditText farmPrice          = holderView.findViewById(R.id.updateFarmPrice);
        Button   updateFarm            = holderView.findViewById(R.id.updateFarm);
        Button   close              = holderView.findViewById(R.id.close);

        updateFarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = farmName.getText().toString().trim();
                String description = farmDescription.getText().toString().trim();
                String price = farmPrice.getText().toString().trim();

                if(name.isEmpty()){
                    farmName.setError("Farm name is required!");
                    farmName.requestFocus();
                    return;
                }

                if(description.isEmpty()){
                    farmDescription.setError("Description is required!");
                    farmDescription.requestFocus();
                    return;
                }

                if(price.isEmpty()){
                    farmPrice.setError("Price sname is required!");
                    farmPrice.requestFocus();
                    return;
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("description", description);
                map.put("price", price);

                FirebaseDatabase.getInstance().getReference().child("Farms")
                        .push()
                        .setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ManageFarm.this, "New farm created....", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ManageFarm.this, "Farm creatio failed....", Toast.LENGTH_SHORT).show();
                            }
                        });
                hideKeybaord(v);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }*/
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