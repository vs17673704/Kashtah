package com.android.farmmate.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.model.Dish
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import com.squareup.picasso.Picasso
import java.util.*

class ManageDishAdapter
/**
 * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See
 * [FirebaseRecyclerOptions] for configuration options.
 *
 * @param options
 */ constructor(options: FirebaseRecyclerOptions<Dish?>, private val context: Context) : FirebaseRecyclerAdapter<Dish, ManageDishAdapter.DishViewHolder>(options) {
    private var imageUrl: String = ""
    override fun onBindViewHolder(holder: DishViewHolder, position: Int, model: Dish) {
        holder.dishName.setText(model.name)
        holder.dishDescription.setText(model.description)
        holder.dishPrice.setText("₹"+ model.price)
        imageUrl = model.imageUrl.toString()
        Picasso.get().load(imageUrl).fit().into(holder.dishImage)
        holder.deleteDish.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                FirebaseDatabase.getInstance().getReference()
                        .child("Dishes")
                        .child((getRef(holder.getAdapterPosition()).getKey())!!)
                        .removeValue()
                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                            public override fun onComplete(task: Task<Void?>) {}
                        })
            }
        })
        holder.editDish.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val dialog: DialogPlus = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(ViewHolder(R.layout.update_dish_dialog))
                        .setCancelable(false)
                        .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
                        .create()
                val holderView: View = dialog.getHolderView() as LinearLayout
                val name: EditText = holderView.findViewById(R.id.updateDishName)
                val description: EditText = holderView.findViewById(R.id.updateDishDescription)
                val price: EditText = holderView.findViewById(R.id.updateDishPrice)
                val update: Button = holderView.findViewById(R.id.updateDish)
                val close: Button = holderView.findViewById(R.id.close)
                name.setText(model.name)
                description.setText(model.description)
                price.setText(model.price)
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
                        val map: HashMap<String, Any> = HashMap()
                        map.put("name", name.getText().toString())
                        map.put("description", description.getText().toString())
                        map.put("price", price.getText().toString())
                        FirebaseDatabase.getInstance().getReference()
                                .child("Dishes")
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

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.manage_dish_template, parent, false)
        return DishViewHolder(view)
    }

    inner class DishViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dishName: TextView
        var dishDescription: TextView
        var dishPrice: TextView
        var editDish: ImageView
        var deleteDish: ImageView
        var dishImage: ImageView

        init {
            dishName = itemView.findViewById(R.id.dishName)
            dishDescription = itemView.findViewById(R.id.dishDescription)
            dishPrice = itemView.findViewById(R.id.dishPrice)
            editDish = itemView.findViewById(R.id.editDish)
            deleteDish = itemView.findViewById(R.id.deleteDish)
            dishImage = itemView.findViewById(R.id.dishImage)
        }
    }

    private fun hideKeyboad(v: View) {
        val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0)
    }
}