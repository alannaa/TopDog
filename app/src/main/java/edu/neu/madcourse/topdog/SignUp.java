package edu.neu.madcourse.topdog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.topdog.DatabaseObjects.User;

public class SignUp extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String username;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Gather global information we need for this activity
        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        username = getIntent().getStringExtra(MainActivity.USERKEY);
        token = getIntent().getStringExtra(MainActivity.TOKEN);

        //Update the usernameInput with whatever the user entered in the main Activity,
        // for ease of use (so they dont have to re-type their preferred username if they typed it
        // into the main activity page already)
        EditText usernameInput = findViewById(R.id.username_input);
        usernameInput.setText(username);

        //Finally, handle whent he signup button is pressed
        Button signUpButton = findViewById(R.id.signUp_btn_on_signUp_page);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserExists_andSignUp();
            }
        });
    }

    public void checkUserExists_andSignUp(){
        EditText usernameInput = findViewById(R.id.username_input);
        username = usernameInput.getText().toString();

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.hasChild(username)) {
                    //if the username is not taken:
                    saveUserToDatabase();//also launches homepage
                } else {
                    Toast.makeText(SignUp.this, "Oops! Username is already taken.",
                            Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    public void saveUserToDatabase(){
        //Grab all data from input boxes
        EditText emailInput = findViewById(R.id.email_input);
        EditText dogNameInput = findViewById(R.id.dog_name_input);
        String email = emailInput.getText().toString();
        String dogName = dogNameInput.getText().toString();

        if (username.isEmpty() || email.isEmpty() || dogName.isEmpty()){
            Toast.makeText(SignUp.this, "Please enter all of the above information",
                    Toast.LENGTH_SHORT).show();
        } else if (!email.contains("@") || !email.contains(".")) {
            Toast.makeText(SignUp.this, "Please enter a valid email",
                    Toast.LENGTH_SHORT).show();
        } else {
            //Save user info to the database:
            User currentUser = new User(username, token, email, dogName);
            mDatabase.child(username).setValue(currentUser);
            openHomepage();
        }
    }

    public void openHomepage() {
        //Finally, launch homepage:
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }
}