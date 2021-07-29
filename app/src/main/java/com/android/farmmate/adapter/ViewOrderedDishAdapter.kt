package com.android.farmmate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.adapter.ViewOrderedDishAdapter.OrderedDishViewHolder
import com.android.farmmate.model.OrderedDish
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso

class ViewOrderedDishAdapter
/**
 * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See
 * [FirebaseRecyclerOptions] for configuration options.
 *
 * @param options
 */ constructor(options: FirebaseRecyclerOptions<OrderedDish?>, private val context: Context) : FirebaseRecyclerAdapter<OrderedDish, OrderedDishViewHolder>(options) {
    private var imageUrl: String = ""
    override fun onBindViewHolder(holder: OrderedDishViewHolder, position: Int, model: OrderedDish) {
        holder.orderedDish.setText(model.dishName)
        holder.dishPrice.setText(model.price)
        holder.orderDate.setText(model.date)
        holder.customerName.setText(model.customeName)
        holder.customerMobile.setText(model.phoneNumber)
        holder.orderStatus.setText(model.status)
        imageUrl = model.imageUrl.toString()
        Picasso.get().load(imageUrl).fit().into(holder.dishImage)
        Picasso.get().load(model.imageUrl).into(holder.dishImage)
    }

    public override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderedDishViewHolder {
        val view: View = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_ordered_dish_template, parent, false)
        return OrderedDishViewHolder(view)
    }

    inner class OrderedDishViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var orderedDish: TextView
        var dishPrice: TextView
        var orderDate: TextView
        var customerName: TextView
        var customerMobile: TextView
        var orderStatus: TextView
        var bookingDate: TextView? = null
        var dishImage: ImageView

        init {
            orderedDish = itemView.findViewById(R.id.orderedDish)
            dishPrice = itemView.findViewById(R.id.dishPrice)
            orderDate = itemView.findViewById(R.id.orderDate)
            customerName = itemView.findViewById(R.id.customerName)
            customerMobile = itemView.findViewById(R.id.customerMobile)
            orderStatus = itemView.findViewById(R.id.orderStatus)
            dishImage = itemView.findViewById(R.id.dishImage)
        }
    }
}