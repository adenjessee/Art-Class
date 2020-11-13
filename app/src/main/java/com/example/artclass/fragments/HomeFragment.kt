
package com.example.artclass.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.artclass.LoginActivity
import com.example.artclass.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import com.example.artclass.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.FirebaseDatabaseKtxRegistrar
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import java.lang.StringBuilder
import java.lang.ref.Reference

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// The home screen
class HomeFragment : Fragment() {

    // photo URI to use from image gallery
    var selectedPhoto: Uri? = null

    // make a binding so we can access elements safely
    private lateinit var binding: FragmentHomeBinding

    // The pager widget handles animation and allows swiping horizontally
    private lateinit var viewPager: ViewPager2

    // lets make the fragment features
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // the binding of the home fragment
        val binding = FragmentHomeBinding.inflate(layoutInflater)

        // dialog box
        val dialog = AlertDialog.Builder(context)
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton ("Yes"){ _, _ ->
                Toast.makeText(context, "Logging out", Toast.LENGTH_SHORT)

                // sign out
                FirebaseAuth.getInstance().signOut()

                //just go to the next activity screen to set up log in information
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setNegativeButton ("No"){ _, _ ->
                Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT)
            }

        // this is what happens when the log out button is pressed
        binding.logOutButton.setOnClickListener {
            dialog.show()
        }

        // when the choose image button is clicked we want to choose an image
        binding.chooseImageButton.setOnClickListener {
            openGalleryForImage()
        }

        // upload the image chosen for this button click
        binding.uploadImageButton.setOnClickListener {
            uploadImageToFirebaseStorage()
        }

        // return the important binding info
        return binding.root
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

            // the data of the photo we need to use later
            selectedPhoto = data?.data

            // specify the image from the xml we want to operate on
            view?.findViewById<ImageView?>(R.id.image_to_upload)?.setImageURI(selectedPhoto)
        }
    }

    // here we upload an image chosen in the device to firebase
    private fun uploadImageToFirebaseStorage(){
        // something bad might happen if we don't do this check
        if(selectedPhoto == null){
            Toast.makeText(context, "Please add an image to publish", Toast.LENGTH_SHORT).show()
            return
        }

        // the filename will be a long random string when stored in firebase
        val filename = UUID.randomUUID().toString()

        // the reference is the place we want to put our file into firebase
        val imageRef = FirebaseStorage.getInstance().getReference("/images/$filename")

        // store the image ref in the user
        saveUserImageToDatabase(imageRef)

        // put the selected photo data into the database image reference
        imageRef.putFile(selectedPhoto!!).addOnSuccessListener {
            // show everything went well
            val dialog = AlertDialog.Builder(context)
                .setTitle("Congratulations!")
                .setMessage("Image was published successfully!")
            dialog.show()
        }.addOnFailureListener{
            // show that something went wrong
            val dialog = AlertDialog.Builder(context)
                    .setTitle("Oops!")
                    .setMessage("Something went wrong, please try and publish again later.")
            dialog.show()
        }
    }

    private fun saveUserImageToDatabase(ref: StorageReference){
        // url from the reference
        val url = ref.downloadUrl.toString()



        // TODO: Get your masterpiece numbers to work with the loop
        // Make is so when you add an image it will by #2 and the total can be known


        // get the user id for the current user logged in
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val userRef = FirebaseDatabase.getInstance().getReference("/users/$uid/masterpieces/$1" )

        val getData = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var builder = StringBuilder()
                for(i in dataSnapshot.child(uid).child("masterpieces").children){
                    var images = i.child("$i").getValue()
                    var key = i.key
                    builder.append("$key $images")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }

        userRef.setValue(url)
    }
}