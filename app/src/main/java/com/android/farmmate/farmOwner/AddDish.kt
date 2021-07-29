package com.android.farmmate.farmOwner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.farmmate.R
import com.android.farmmate.farmOwner.AddDish
import com.android.farmmate.model.ImageUriModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddDish : AppCompatActivity() {
    lateinit var addDishImage: ImageView
    lateinit var addDishName: EditText
    lateinit var addDishDescription: EditText
    lateinit var addDishPrice: EditText
    lateinit var addDish: Button
    lateinit var close: Button
    private lateinit var progress: ProgressBar

    //vars
    private val root = FirebaseDatabase.getInstance().getReference("Image")
    private val reference = FirebaseStorage.getInstance().reference
    private var imageUri: Uri? = null
    private var imageURL = ""
    private var name: String? = null
    private var description: String? = null
    private var price: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_dish)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "ADD DISH"
        addDishName = findViewById(R.id.addDishName)
        addDishDescription = findViewById(R.id.addDishDescription)
        addDishPrice = findViewById(R.id.addDishPrice)
        addDish = findViewById(R.id.addDish)
        close = findViewById(R.id.close)
        addDishImage = findViewById(R.id.addDishImage)
        progress = findViewById(R.id.progress)
        progress.setVisibility(View.GONE)
        addDishImage.setOnClickListener(View.OnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent, 2)
        })
        addDish.setOnClickListener(View.OnClickListener {
            if (imageUri != null) {
                uploadToFirebase(imageUri!!)
            } else {
                Toast.makeText(this@AddDish, "Please Select Image", Toast.LENGTH_SHORT).show()
            }
            //                addFarm();
            //Toast.makeText(AddFarm.this, imageURL, Toast.LENGTH_SHORT).show();
        })
        close.setOnClickListener(View.OnClickListener { finish() })
    }

    private fun addDish() {
        val map = HashMap<String, Any?>()
        map["name"] = name
        map["description"] = description
        map["price"] = price
        map["imageUrl"] = imageURL
        FirebaseDatabase.getInstance().reference.child("Dishes")
                .push()
                .setValue(map)
                .addOnCompleteListener {
                    progress.visibility = View.GONE
                    Toast.makeText(this@AddDish, "New Dish created....", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { Toast.makeText(this@AddDish, "Dish creation failed....", Toast.LENGTH_SHORT).show() }
    }

    private fun uploadToFirebase(uri: Uri) {
        name = addDishName.text.toString().trim { it <= ' ' }
        description = addDishDescription.text.toString().trim { it <= ' ' }
        price = addDishPrice.text.toString().trim { it <= ' ' }
        if (name!!.isEmpty()) {
            addDishName.error = "Dish name is required!"
            addDishName.requestFocus()
            return
        }
        if (description!!.isEmpty()) {
            addDishDescription.error = "Description is required!"
            addDishDescription.requestFocus()
            return
        }
        if (price!!.isEmpty()) {
            addDishPrice.error = "Price is required!"
            addDishPrice.requestFocus()
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
                //Toast.makeText(this@AddDish, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                addDish()
                //imageView.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24);
            }
        }.addOnProgressListener {
            //progressBar.setVisibility(View.VISIBLE);
        }.addOnFailureListener { //progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this@AddDish, "Uploading Failed ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileExtension(mUri: Uri): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(mUri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            addDishImage.setImageURI(imageUri)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}