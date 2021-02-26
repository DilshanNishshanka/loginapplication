package com.example.loginapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.util.UUID;

public class Register extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference ref;
    int maxid = 0;
    LoginDetails loginDetails;

    private EditText register_email_field;                     //Profile page variable declaration
    private EditText register_pass_field;
    private EditText register_first_name_field;
    private EditText register_last_name_field;
    private Button reg_btn;
    private Button reg_login_btn;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private CheckBox checkBox;
    private ImageView profilepic;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        checkBox = findViewById(R.id.register_checkbox);
        register_email_field = findViewById(R.id.register_email);
        register_pass_field = findViewById(R.id.register_password);                           // Match the variable it related ids
        register_first_name_field = findViewById(R.id.register_first_name);
        register_last_name_field = findViewById(R.id.register_last_name);
        reg_btn = findViewById(R.id.register_button);
        reg_login_btn = findViewById(R.id.register_login_button);
        progressBar = findViewById(R.id.register_progressBar);
        profilepic = findViewById(R.id.register_imageView);
        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();                  //storage reference

        loginDetails = new LoginDetails();
        ref = database.getInstance().getReference().child("User");             //Path in database

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    maxid = (int) snapshot.getChildrenCount();
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    register_pass_field.setTransformationMethod(HideReturnsTransformationMethod.getInstance());    // If user tick on the check box

                }else {
                    register_pass_field.setTransformationMethod(PasswordTransformationMethod.getInstance());     // If use tick on the check box

                }
            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loginDetails.setFname(register_first_name_field.getText().toString());            //set the value
                loginDetails.setLname(register_last_name_field.getText().toString());
                loginDetails.setEmail(register_email_field.getText().toString());

                ref.child(String.valueOf(maxid+1)).setValue(loginDetails);
                String email = register_email_field.getText().toString();                     //move value inside the firebase
                String pass = register_pass_field.getText().toString();
                String first_name = register_first_name_field.getText().toString();
                String last_name = register_last_name_field.getText().toString();

                if(!TextUtils.isEmpty(email)|| !TextUtils.isEmpty(pass) || !TextUtils.isEmpty(first_name) ||!TextUtils.isEmpty(last_name)){
                        progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    sendtoMain();
                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(getApplicationContext(),"Error :"+error,Toast.LENGTH_LONG).show();
                                }

                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                }
            }
        });

        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this,Login.class);     //If user clock on Login button the move to the login page
                startActivity(intent);
            }
        });


        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }       //Profile image chosen
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
                sendtoMain();                                                    //If user seession is not expired directly move to the profile page

        }
    }

    private void sendtoMain(){
        Intent intent = new Intent(Register.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void choosePicture(){                             //Select the profile picture using glarry
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profilepic.setImageURI(imageUri);
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);               //Upload image in to storage
        pd.setTitle("Uploading Image.....");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images"+randomKey);

        riversRef.putFile(imageUri)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"Image Uploaded",Snackbar.LENGTH_LONG).show();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed to upload",Toast.LENGTH_LONG).show();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        pd.setMessage("Percentage: "+(int) progressPercent+ "%");
                    }
                });
    }
}