package com.example.loginapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    TextView displayName;                 //Profile page variable declaration
    TextView displayEmail;
    ImageView imageView;
    Button button;
    private  FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);                                  // Match the variable it related ids
        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.sign_out);
        displayName = findViewById(R.id.textViewName);
        displayEmail = findViewById(R.id.textViewEmail);
        imageView = findViewById(R.id.profile_imageView);
//        currentUserId = mAuth.getCurrentUser().getUid();
//        reference = FirebaseDatabase.getInstance().getReference();


//        RetriveUserInfo();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this,Login.class);      //User click on sigout button then move to the login page
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            Intent intent = new Intent(MainActivity.this,Login.class);      // If current user is expire then move to the Login page
            startActivity(intent);
            finish();
        }
    }

//    private void RetriveUserInfo() {
//
//        reference.child("User").child(currentUserId)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                     if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("Email"))) {
//                            String FName = dataSnapshot.child("First Name").getValue().toString();
//                            String LName = dataSnapshot.child("Last Name").getValue().toString();
//                            String Email = dataSnapshot.child("Email").getValue().toString();
//
//                            String name = FName + LName;
//
//                            displayName.setText(name);
//                            displayEmail.setText(Email);
//
//                     }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//    }
}