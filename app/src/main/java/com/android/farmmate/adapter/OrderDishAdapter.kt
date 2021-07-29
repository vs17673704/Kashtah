package com.android.farmmate.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.model.Dish
import com.android.farmmate.model.OrderedDish
import com.android.farmmate.model.User
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class OrderDishAdapter
/**
 * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See
 * [FirebaseRecyclerOptions] for configuration options.
 *
 * @param options
 */ constructor(options: FirebaseRecyclerOptions<Dish?>, private val context: Context) : FirebaseRecyclerAdapter<Dish, OrderDishAdapter.DishViewHolder>(options) {
    private lateinit var radioRoleGroup: RadioGroup
    private val radioRoleButton: RadioButton? = null
    private var name: String = ""
    private var phoneNumber: String = ""
    private var email: String = ""
    private var imageUrl: String = ""
    private val extarMap: HashMap<String, String> = HashMap()
    var od: OrderedDish = OrderedDish()
    override fun onBindViewHolder(holder: DishViewHolder, position: Int, model: Dish) {
        holder.dishName.setText(model.name)
        holder.dishDescription.setText(model.description)
        holder.dishPrice.setText("₹"+ model.price)
        imageUrl = model.imageUrl.toString()
        Picasso.get().load(imageUrl).fit().into(holder.dishImage)
        od.price = holder.dishPrice.getText().toString().trim({ it <= ' ' })
        od.imageUrl = imageUrl
        od.dishName = holder.dishName.getText().toString().trim({ it <= ' ' })
        holder.orderDish.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                initValues(od)
            }
        })
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DishViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_dish_template, parent, false)
        return DishViewHolder(view)
    }

    inner class DishViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dishName: TextView
        var dishDescription: TextView
        var dishPrice: TextView
        var orderDish: ImageButton
        var dishImage: ImageView

        init {
            dishName = itemView.findViewById(R.id.dishName)
            dishDescription = itemView.findViewById(R.id.dishDescription)
            dishPrice = itemView.findViewById(R.id.dishPrice)
            orderDish = itemView.findViewById(R.id.orderDish)
            dishImage = itemView.findViewById(R.id.dishImage)
        }
    }

    private fun initValues(orderedDish: OrderedDish) {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    public override fun onDataChange(snapshot: DataSnapshot) {
                        val user: User? = snapshot.getValue(User::class.java)
                        if (user != null) {
                            name = user.name.toString()
                            phoneNumber = user.phoneNumber.toString()
                            email = user.email.toString()
                            val date: Date = Date()
                            val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
                            val strDate: String = formatter.format(date)
                            orderedDish.customeName = name
                            orderedDish.phoneNumber = phoneNumber
                            orderedDish.email = email
                            orderedDish.date = strDate
                            orderedDish.status = "Order placed"
                            addOrder(orderedDish)
                        } else {
                            Toast.makeText(context, "User is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    public override fun onCancelled(error: DatabaseError) {}
                })
    }

    private fun addOrder(orderedDish: OrderedDish) {
        FirebaseDatabase.getInstance().getReference().child("OrderedDish")
                .push()
                .setValue(orderedDish)
                .addOnCompleteListener(object : OnCompleteListener<Void?> {
                    public override fun onComplete(task: Task<Void?>) {
                        val dialog: DialogPlus = DialogPlus.newDialog(context)
                                .setGravity(Gravity.CENTER)
                                .setMargin(50, 0, 50, 0)
                                .setCancelable(false)
                                .setContentHolder(ViewHolder(R.layout.order_confirmation_dialog))
                                .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
                                .create()
                        val holderView: View = dialog.getHolderView() as LinearLayout
                        val close: Button = holderView.findViewById(R.id.close)
                        //radioRoleGroup = (holderView.findViewById<View>(R.id.radioGroup) as RadioGroup?)!!
                        close.setOnClickListener(object : View.OnClickListener {
                            public override fun onClick(v: View) {
                                dialog.dismiss()
                            }
                        })
                        dialog.show()
                    }
                })
                .addOnFailureListener(object : OnFailureListener {
                    public override fun onFailure(e: Exception) {}
                })
    }
}