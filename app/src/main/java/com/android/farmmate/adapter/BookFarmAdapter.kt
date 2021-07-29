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
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.farmmate.R
import com.android.farmmate.consumer.CardPayment
import com.android.farmmate.consumer.OrderConfirmaton
import com.android.farmmate.model.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.dialogplus.DialogPlus
import com.orhanobut.dialogplus.ViewHolder
import com.squareup.picasso.Picasso
import java.util.*


class BookFarmAdapter(options: FirebaseRecyclerOptions<Farm?>, private val context: Context) : FirebaseRecyclerAdapter<Farm, BookFarmAdapter.FarmViewHolder>(options) {
    /**
     * Initialize a [RecyclerView.Adapter] that listens to a Firebase query. See
     * [FirebaseRecyclerOptions] for configuration options.
     *
     * @param options
     */
    private lateinit var radioRoleGroup: RadioGroup
    private lateinit var radioGroupPrice: RadioGroup
    private var name = ""
    private var phoneNumber = ""
    private var stayPrice = ""
    private var email = ""
    private var imageurl = ""
    private var stay = ""
    private val extarMap = HashMap<String, String>()
    override fun onBindViewHolder(holder: FarmViewHolder, position: Int, model: Farm) {
        holder.farmName.text = model.name
        holder.farmDescription.text = "Description: " + model.description
        holder.farmPrice.text = "Regular - ₹"+ model.price
        holder.farmPriceOvernight.text = "Overnight - ₹"+ model.priceOvernight
        holder.farmMobile.text = "Mobile: "+model.mobile
        imageurl = model.imageUrl!!
        Picasso.get().load(imageurl).fit().into(holder.farmImage)
        holder.bookFarm.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                initValues()
                val dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(ViewHolder(R.layout.book_farm_dialog))
                        .setCancelable(false)
                        .setExpanded(false) // This will enable the expand feature, (similar to android L share dialog)
                        .create()
                val holderView: View = dialog.holderView as LinearLayout
                val bookNow = holderView.findViewById<Button>(R.id.bookNow)
                val close = holderView.findViewById<Button>(R.id.close)
                radioRoleGroup = holderView.findViewById<View>(R.id.radioGroup) as RadioGroup
                radioGroupPrice = holderView.findViewById<View>(R.id.radioGroupPrice) as RadioGroup
                close.setOnClickListener(View.OnClickListener {
                    dialog.dismiss()
                    /*Intent intent = new Intent(context, OrderConfirmaton.class);
                        intent.putExtra("farm_title",model.getFarmName());
                        context.startActivity(intent);*/
                })
                bookNow.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View) {
                        val selectedId = radioRoleGroup.checkedRadioButtonId
                        val selectedIdPrice = radioGroupPrice.checkedRadioButtonId
                        var payment_mode = "NONE"
                        if (selectedId == R.id.byCard) payment_mode = "CARD" else if (selectedId == R.id.byCash) payment_mode = "CASH" else Toast.makeText(context, "Please select an option!", Toast.LENGTH_SHORT).show()
                        if (selectedIdPrice == R.id.regularPrice){
                            stayPrice = model.price.toString()
                            stay    =   "Day-time"
                        }
                        else if (selectedIdPrice == R.id.overnightPrice){
                            stayPrice = model.priceOvernight.toString()
                            stay    =   "Overnight"
                        }
                        else if (selectedIdPrice == R.id.fullDayPrice){
                            stayPrice = (Integer.parseInt(model.price.toString()) + Integer.parseInt(model.priceOvernight.toString())).toString()
                            stay    =   "Full day"
                        }

                        extarMap["payment_mode"] = payment_mode
                        extarMap["name"] = name
                        extarMap["price"] = stayPrice
                        extarMap["farmName"] = model.name.toString()
                        extarMap["email"] = email
                        extarMap["phone_number"] = model.mobile.toString()
                        extarMap["image_url"] = imageurl
                        extarMap["stay"] = stay

                        if ((payment_mode == "CASH")) {
                            val intent = Intent(context, OrderConfirmaton::class.java)
                            intent.putExtra("extra_map", extarMap)
                            dialog.dismiss()
                            context.startActivity(intent)
                        }
                        if ((payment_mode == "CARD")) {
                            val intent = Intent(context, CardPayment::class.java)
                            intent.putExtra("extra_map", extarMap)
                            dialog.dismiss()
                            context.startActivity(intent)
                        }
                    }
                })
                dialog.show()
            }
        })
        holder.locateFarm.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setData(Uri.parse(""+model.locationLink))
                val chooser = Intent.createChooser(intent, "Launch Maps")
                context.startActivity(chooser)
                /*
                val intent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"))
                context.startActivity(intent)*/
                /*val uri = java.lang.String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                context.startActivity(intent)*/
            }
        })

        holder.callFarm.setOnClickListener ( object : View.OnClickListener{
            override fun onClick(v: View) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(model.mobile)))
                context.startActivity(intent)
            }

        })
    }

    private fun initValues() {
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            name = user.name.toString()
                            phoneNumber = user.phoneNumber.toString()
                            email = user.email.toString()
                        } else {
                            Toast.makeText(context, "User is null", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
    }

    private fun hideKeyboad(v: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.book_farm_template, parent, false)
        return FarmViewHolder(view)
    }

    inner class FarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var farmName: TextView
        var farmDescription: TextView
        var farmPrice: TextView
        var farmPriceOvernight: TextView
        var farmMobile: TextView
        var bookFarm: ImageButton
        var locateFarm: ImageButton
        var callFarm: ImageButton
        var farmImage: ImageView

        init {
            farmName = itemView.findViewById(R.id.farmName)
            farmDescription = itemView.findViewById(R.id.farmDescription)
            farmPrice = itemView.findViewById(R.id.farmPrice)
            bookFarm = itemView.findViewById(R.id.bookFarm)
            farmImage = itemView.findViewById(R.id.farmImage)
            farmPriceOvernight = itemView.findViewById(R.id.farmPriceOvernight)
            farmMobile = itemView.findViewById(R.id.farmMobile)
            locateFarm = itemView.findViewById(R.id.locateFarm)
            callFarm = itemView.findViewById(R.id.callFarm)
        }
    }
}