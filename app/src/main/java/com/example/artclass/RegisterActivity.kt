
package com.example.artclass

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity(){

    private var user = User()

    override fun onCreate(savedInstanceState: Bundle?){

        // the bar at the top is not needed
        supportActionBar?.hide()

        // any state it was in before if needed, get it back
        super.onCreate(savedInstanceState)

        // lets see the xml layout that was made
        setContentView(R.layout.register_activity)

        // make the instances of the buttons from the xml
        var sign_in_button : Button = findViewById(R.id.sign_in_button)

        // these variables store the data entered in at the login page
        val email: EditText = findViewById(R.id.email_text_login)
        val password: EditText = findViewById(R.id.password_text_login)
        val username: EditText = findViewById(R.id.username_text_login)

        // this is what happens when the create account button is clicked
        sign_in_button.setOnClickListener {
            // register with firebase if user input is good
            registerUser(email.text, password.text, username.text)
        }
    }

    // organizing user data to a firebase database
    private fun saveUserToFirebaseDatabase() {
        // get the user id from the firebase authentication
        val uid = FirebaseAuth.getInstance().uid ?: return

        // a node for a user database
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        // update uid
        user.uid = uid

        ref.setValue(user).addOnSuccessListener {
            Log.d("RegisterActivity", "User saved to database")
            Log.d("RegisterActivity", "UID:" + user.uid)
            Log.d("RegisterActivity", "Username:" + user.username)
        }
    }

    private fun registerUser(emailTab: Editable, passwordTab: Editable, usernameTab: Editable){

        // lets make sure the user actually enters in stuff first
        if(emailTab.isEmpty() || passwordTab.isEmpty() || usernameTab.isEmpty()){
            // if we got here the user messed up and did not enter something
            Toast.makeText(this, "Please enter an email, password, and username", Toast.LENGTH_SHORT).show()
            return  // go back and try again
        }

        //Firebase authorization sign up
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailTab.toString(),
            passwordTab.toString()
        )

            .addOnCompleteListener{
                // did we have success in making a new account?
                if(!it.isSuccessful){
                    return@addOnCompleteListener
                }

                // update username
                user.username = usernameTab.toString()

                // save the user to the database
                saveUserToFirebaseDatabase()

                // show it was successful
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()

                // lets move on the the next page because we are logged in now
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
