
package com.example.artclass

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class LoginActivity : AppCompatActivity(){

    var selectedPhoto: Uri? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?){

        // the bar at the top is not needed
        supportActionBar?.hide()

        // lets make the activity screen the login page
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // preventing anything bad from happening with login
        verifyUserIsLoggedIn()

        // make the instances of the buttons from the xml
        val log_out_button : Button = findViewById(R.id.log_out_button)
        val choose_image_button : Button = findViewById(R.id.choose_image_button)
        val upload_image_button : Button = findViewById(R.id.upload_image_button)

        // this is what happens when the log out button is pressed
        log_out_button.setOnClickListener {

            // sign out
            getInstance().signOut()

            //just go to the next activity screen to set up log in information
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // when the choose image button is clicked we want to choose an image
        choose_image_button.setOnClickListener {
            openGalleryForImage()
        }

        // upload the image chosen for this button click
        upload_image_button.setOnClickListener {
            uploadImageToFirebaseStorage()
        }
    }

    // open the gallery
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    // setting the image on the current device screen to the image path chosen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 0){

            // specify the image from the xml we want to operate on
            val image_selected : ImageView = findViewById(R.id.image_selected)

            // the data of the photo we need to use later
            selectedPhoto = data?.data

            // make a bitmap image from the selected photo data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhoto)

            // make the bitmap drawable to the screen
            val bitmapDrawable = BitmapDrawable(bitmap)

            // draw the bitmap to the image we want on screen
            image_selected.setBackgroundDrawable(bitmapDrawable)

        }
    }

    // here we upload an image chosen in the device to firebase
    private fun uploadImageToFirebaseStorage(){
        // something bad might happen if we don't do this check
        if(selectedPhoto == null){
            return
        }

        // the filename will be same long random string when stored in firebase
        val filename = UUID.randomUUID().toString()

        // the reference is the place we want to put our file into firebase
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        // using the reference object we can put a selected photo into that reference
        ref.putFile(selectedPhoto!!).addOnSuccessListener {
            Log.d("LoginActivity", "WE UPLOADED AN IMAGE!!!")
        }
    }

    // lets make sure the user is actually signed in to be looking at the login page
    private fun verifyUserIsLoggedIn() {
        val uid = getInstance().uid
        if (uid == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

}