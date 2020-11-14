
package com.example.artclass.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.artclass.LoginActivity
import com.example.artclass.R
import com.example.artclass.SafeClickListener
import com.example.artclass.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// The home screen
class HomeFragment : Fragment() {

    // photo URI to use from image gallery
    var selectedPhoto: Uri? = null

    // total number of photos for a given uesr
    var totalNumberOfPhotos: Long = 0

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
            .setPositiveButton("Yes"){ _, _ ->
                Toast.makeText(context, "Logging out", Toast.LENGTH_SHORT)

                // sign out
                FirebaseAuth.getInstance().signOut()

                //just go to the next activity screen to set up log in information
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setNegativeButton("No"){ _, _ ->
                Toast.makeText(context, "Logged in", Toast.LENGTH_SHORT)
            }

        // this is what happens when the log out button is pressed
        binding.logOutButton.setSafeOnClickListener {
            // showing the dialog gives the option to log out
            dialog.show()
        }

        // when the choose image button is clicked we want to choose an image
        binding.chooseImageButton.setSafeOnClickListener {
            openGalleryForImage()
        }

        // upload the image chosen for this button click
        binding.uploadImageButton.setSafeOnClickListener {
            uploadImageToFirebaseStorage()
        }

        // return the important binding info
        return binding.root
    }

    // prevent user from clicking too many times in a row
    private fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
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

        // update number of photos user currently has
        updateNumberOfPhotos()

        //see if user is upgraded
        var isUpgraded = isUpgraded()

        // if user is not upgraded and there is 12 or more photos we cant do anything
        if(totalNumberOfPhotos >= 12 && !isUpgraded){
            val dialog = AlertDialog.Builder(context)
                    .setTitle("Sorry")
                    .setMessage("We can only allow up to 12 of your masterpieces." +
                            "\n Please subscribe in settings to publish unlimited art.")
            dialog.show()

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

    // keep track of image references in the data base
    private fun saveUserImageToDatabase(ref: StorageReference){
        // url from the reference
        val url = ref.downloadUrl.toString()

        // get the user id for the current user logged in
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val userRef = FirebaseDatabase.getInstance().getReference("/users/$uid/masterpieces/")

        userRef.push().setValue(url).addOnFailureListener{
                // show that something went wrong
                val dialog = AlertDialog.Builder(context)
                        .setTitle("Oops!")
                        .setMessage("There was a server error, please try again later.")
                dialog.show()
        }
    }

    // to know the amount of photos a user has stored
    private fun updateNumberOfPhotos(){
        // get the user id for the current user logged in
        val uid = FirebaseAuth.getInstance().uid ?: ""

        // get the firebase instance
        val db = FirebaseDatabase.getInstance().reference

        // find out how many images are in the users profile
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                totalNumberOfPhotos = dataSnapshot.child("/users/$uid/masterpieces/").childrenCount
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // show that something went wrong
                val dialog = AlertDialog.Builder(context)
                        .setTitle("Oops!")
                        .setMessage("There was a server error, please try again later.")
                dialog.show()
            }
        })
    }

    // TODO: make a subscription feature and decide if subscribed or not
    private fun isUpgraded(): Boolean{
        return false
    }
}