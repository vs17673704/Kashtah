package com.android.farmmate.farmOwner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.farmmate.R
import com.android.farmmate.consumer.ConsumerDashboard
import com.android.farmmate.farmOwner.AddFarm
import com.android.farmmate.model.ImageUriModel
import com.android.farmmate.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddFarm : AppCompatActivity() {
    private lateinit var farmName: EditText
    private lateinit var farmDescription: EditText
    private lateinit var farmPrice: EditText
    private lateinit var farmPriceOvernight: EditText
    private lateinit var farmLocationLink: EditText
    private lateinit var farmMobile: EditText
    private lateinit var addFarm: Button
    private lateinit var close: Button
    private lateinit var name: String
    private lateinit var description: String
    private lateinit var price: String
    private lateinit var priceOvernight: String
    private lateinit var locationLink: String
    private lateinit var mobile: String
    private lateinit var progress: ProgressBar
    private lateinit var imageView: ImageView

    //vars
    private val root = FirebaseDatabase.getInstance().getReference("Image")
    private val reference = FirebaseStorage.getInstance().reference
    private var imageUri: Uri? = null
    private var imageURL = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_farm)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "ADD FARM"
        farmName = findViewById(R.id.addFarmName)
        farmDescription = findViewById(R.id.addFarmDescription)
        farmPrice = findViewById(R.id.addFarmPrice)
        farmPriceOvernight = findViewById(R.id.addFarmPriceOvernight)
        farmLocationLink = findViewById(R.id.addFarmLocationLink)
        farmMobile = findViewById(R.id.addFarmMobile)
        addFarm = findViewById(R.id.addFarm)
        close = findViewById(R.id.close)
        imageView = findViewById(R.id.addFarmImage)
        progress = findViewById(R.id.progress)
        progress.setVisibility(View.GONE)

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null) {
                            farmMobile.setText(user.phoneNumber)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

        imageView.setOnClickListener(View.OnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        })
        addFarm.setOnClickListener(View.OnClickListener {
            if (imageUri != null) {
                uploadToFirebase(imageUri!!)
            } else {
                Toast.makeText(this@AddFarm, "Please Select Image", Toast.LENGTH_SHORT).show()
            }
        })
        close.setOnClickListener(View.OnClickListener { v: View? -> finish() })
    }

    private fun addFarm() {
        val map = HashMap<String, Any?>()
        map["name"] = name
        map["description"] = description
        map["price"] = price
        map["priceOvernight"] = priceOvernight
        map["locationLink"] = locationLink
        map["mobile"] = mobile
        map["imageUrl"] = imageURL
        FirebaseDatabase.getInstance().reference.child("Farms")
                .push()
                .setValue(map)
                .addOnCompleteListener {
                    progress.visibility = View.GONE
                    Toast.makeText(this@AddFarm, "New farm created....", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { Toast.makeText(this@AddFarm, "Farm creation failed....", Toast.LENGTH_SHORT).show() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadToFirebase(uri: Uri) {
        name = farmName.text.toString().trim { it <= ' ' }
        description = farmDescription.text.toString().trim { it <= ' ' }
        price = farmPrice.text.toString().trim { it <= ' ' }
        priceOvernight = farmPriceOvernight.text.toString().trim { it <= ' ' }
        mobile = farmMobile.text.toString().trim { it <= ' ' }
        locationLink = farmLocationLink.text.toString().trim { it <= ' ' }
        if (name.isEmpty()) {
            farmName.error = "Farm name is required!"
            farmName.requestFocus()
            return
        }
        if (description.isEmpty()) {
            farmDescription.error = "Description is required!"
            farmDescription.requestFocus()
            return
        }
        if (price.isEmpty()) {
            farmPrice.error = "Price is required!"
            farmPrice.requestFocus()
            return
        }
        if (priceOvernight.isEmpty()) {
            farmPriceOvernight.error = "Overnight price is required!"
            farmPriceOvernight.requestFocus()
            return
        }
        if (mobile.isEmpty()) {
            farmMobile.error = "Mobile is required!"
            farmMobile.requestFocus()
            return
        }
        if (locationLink.isEmpty()) {
            farmLocationLink.error = "Location link is required!"
            farmLocationLink.requestFocus()
            return
        }
        progress.visibility = View.VISIBLE
        val fileRef = reference.child(System.currentTimeMillis().toString() + "." + getFileExtension(uri))
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val model = ImageUriModel(uri.toString())
                val modelId = root.push().key
                root.child(modelId!!).setValue(model)
                imageURL = model.imageUrl.toString()
                //progressBar.setVisibility(View.INVISIBLE);
                //Toast.makeText(this@AddFarm, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                addFarm()
                //imageView.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
            }
        }.addOnProgressListener {
            //progressBar.setVisibility(View.VISIBLE);
        }.addOnFailureListener { //progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this@AddFarm, "Uploading Failed ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(mUri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}