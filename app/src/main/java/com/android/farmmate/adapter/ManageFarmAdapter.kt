package com.android.farmmate.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.model.Farm
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import com.squareup.picasso.Picasso
import java.util.*

class ManageFarmAdapter constructor(options: FirebaseRecyclerOptions<Farm?>,
                                    /**
                                     * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See
                                     * [FirebaseRecyclerOptions] for configuration options.
                                     *
                                     * @param options
                                     */
                                    private val context: Context) : FirebaseRecyclerAdapter<Farm, ManageFarmAdapter.FarmViewHolder>(options) {
    override fun onBindViewHolder(holder: FarmViewHolder, position: Int, model: Farm) {
        holder.farmName.setText(model.name)
        holder.farmDescription.setText("Description: " + model.description)
        holder.farmPrice.setText("Regular - ₹"+ model.price)
        holder.farmPriceOvernight.setText("Overnight - ₹"+ model.priceOvernight)
        holder.farmMobile.setText("Mobile: "+model.mobile)
        Picasso.get().load(model.imageUrl).fit().into(holder.farmImage)
        holder.deleteFarm.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Farms")
                        .child((getRef(holder.getAdapterPosition()).getKey())!!)
                        .removeValue()
                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                            public override fun onComplete(task: Task<Void?>) {}
                        })
                        .addOnSuccessListener(object : OnSuccessListener<Void?> {
                            public override fun onSuccess(aVoid: Void?) {
                                Toast.makeText(context, "Item successfully deleted", Toast.LENGTH_SHORT).show()
                            }
                        })
                        .addOnFailureListener(object : OnFailureListener {
                            public override fun onFailure(e: Exception) {}
                        })
            }
        })

        holder.locateFarm.setOnClickListener ( object : View.OnClickListener{
            override fun onClick(v: View) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(""+model.locationLink))
                val chooser = Intent.createChooser(intent, "")
                context.startActivity(chooser)
            }

        })

        holder.editFarm.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val dialog: DialogPlus = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setCancelable(false)
                        .setContentHolder(ViewHolder(R.layout.update_farm_dialog))
                        .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
                        .create()
                val holderView: View = dialog.getHolderView() as LinearLayout
                val name: EditText = holderView.findViewById(R.id.updateFarmName)
                val description: EditText = holderView.findViewById(R.id.updateFarmDescription)
                val price: EditText = holderView.findViewById(R.id.updateFarmPrice)
                val priceOvernight: EditText = holderView.findViewById(R.id.updateFarmPriceOvernight)
                val mobile: EditText = holderView.findViewById(R.id.addFarmMobile)
                val update: Button = holderView.findViewById(R.id.updateFarm)
                val close: Button = holderView.findViewById(R.id.close)
                name.setText(model.name)
                description.setText(model.description)
                price.setText(model.price)
                priceOvernight.setText(model.priceOvernight)
                mobile.setText(model.mobile)
                update.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        if (name.getText().toString().trim({ it <= ' ' }).isEmpty()) {
                            name.setError("Name is required!")
                            name.requestFocus()
                            return
                        }
                        if (description.getText().toString().trim({ it <= ' ' }).isEmpty()) {
                            description.setError("Description is required!")
                            description.requestFocus()
                            return
                        }
                        if (price.getText().toString().trim({ it <= ' ' }).isEmpty()) {
                            price.setError("Price is required!")
                            price.requestFocus()
                            return
                        }
                        if (priceOvernight.getText().toString().trim({ it <= ' ' }).isEmpty()) {
                            priceOvernight.setError("Overnight price is required!")
                            priceOvernight.requestFocus()
                            return
                        }
                        if (mobile.getText().toString().trim({ it <= ' ' }).isEmpty()) {
                            mobile.setError("Mobile number is required!")
                            mobile.requestFocus()
                            return
                        }
                        val map: HashMap<String, Any> = HashMap()
                        map.put("name", name.text.toString().trim { it <= ' ' })
                        map.put("description", description.text.toString().trim { it <= ' ' })
                        map.put("price", price.text.toString().trim { it <= ' ' })
                        map.put("mobile", mobile.text.toString().trim { it <= ' ' })
                        map.put("priceOvernight", priceOvernight.text.toString().trim { it <= ' ' })
                        FirebaseDatabase.getInstance().getReference()
                                .child("Farms")
                                .child((getRef(position).getKey())!!)
                                .updateChildren(map)
                                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                    public override fun onComplete(task: Task<Void?>) {
                                        dialog.dismiss()
                                        hideKeyboad(v)
                                    }
                                })
                    }
                })
                close.setOnClickListener(object : View.OnClickListener {
                    public override fun onClick(v: View) {
                        dialog.dismiss()
                    }
                })
                dialog.show()
            }
        })
    }

    private fun hideKeyboad(v: View) {
        val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0)
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_farm_template, parent, false)
        return FarmViewHolder(view)
    }

    inner class FarmViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var farmName: TextView
        var farmDescription: TextView
        var farmPrice: TextView
        var farmPriceOvernight: TextView
        var farmMobile: TextView
        var editFarm: ImageView
        var deleteFarm: ImageView
        var locateFarm: ImageView
        var farmImage: ImageView

        init {
            farmName = itemView.findViewById(R.id.farmName)
            farmDescription = itemView.findViewById(R.id.farmDescription)
            farmPrice = itemView.findViewById(R.id.farmPrice)
            farmPriceOvernight = itemView.findViewById(R.id.farmPriceOvernight)
            farmMobile = itemView.findViewById(R.id.farmMobile)
            editFarm = itemView.findViewById(R.id.editFarm)
            deleteFarm = itemView.findViewById(R.id.deleteFarm)
            locateFarm = itemView.findViewById(R.id.locateFarm)
            farmImage = itemView.findViewById(R.id.farmImage)
        }
    }
}