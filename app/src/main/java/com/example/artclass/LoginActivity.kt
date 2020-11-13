
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

class LoginActivity : AppCompatActivity() {
    private val TAG = "MyActivity"

    // everything to create and do in this activity page
    override fun onCreate(savedInstanceState: Bundle?) {

        // the bar at the top is not needed
        supportActionBar?.hide()

        // any state it was in before if needed, get it back
        super.onCreate(savedInstanceState)

        // lets see the xml layout that was made
        setContentView(R.layout.activity_login)

        // make the instances of the buttons from the xml
        val sign_in_button : Button = findViewById(R.id.sign_in_button)
        val create_account_button : Button = findViewById(R.id.create_account_button)

        // these variables store the data entered in at the login page
        val email: EditText = findViewById(R.id.email_text_login)
        val password: EditText = findViewById(R.id.password_text_login)

        // this is what happens when the create account button is pressed
        create_account_button.setOnClickListener {
            //just go to the next activity screen to set up log in information
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        // this is what happens when the sign in button is pressed
        sign_in_button.setOnClickListener {
            // lets show a debug message when we click the button just in case
            Log.d(TAG, "create account activity")

            // check to see if we can log in then go to next activity
            loginUser(email.text, password.text)
        }

    }

    // the user may log in when this is called
    private fun loginUser(email: Editable, password: Editable){

        // lets make sure the user actually enters in stuff first
        if(email.isEmpty() || password.isEmpty()){
            // if we got here the user messed up and did not enter something
            Toast.makeText(this, "Please enter an email and password", Toast.LENGTH_SHORT).show()
            return  // go back and try again
        }

        // these show in the log what the text is that was entered
        //Log.d(TAG, "Email: " + email)
        //Log.d(TAG, "Password: $password")

        // put in the email and password into the firebase authentication
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.toString(), password.toString())

            //if successful
            .addOnCompleteListener{
                // did we have success in making a new account?
                if(!it.isSuccessful) {
                    return@addOnCompleteListener
                }

                // user is always welcome once logged in
                Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()

                // go to the next activity screen
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

                //show the user what went wrong
            .addOnFailureListener{
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}