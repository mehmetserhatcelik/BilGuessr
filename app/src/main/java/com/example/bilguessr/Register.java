package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.bilguessr.Models.User;
import com.example.bilguessr.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();


        ImageView image = binding.imageView3;
        image.setImageResource(R.drawable.visibleeye);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.passwordText.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance()))
                {
                    binding.passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    image.setImageResource(R.drawable.visibleeye);

                }
                else
                {
                    binding.passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    image.setImageResource(R.drawable.indir);
                }
            }
        });

    }
    public void signUpClicked(View view)
    {
        String email = binding.emailText.getText().toString();
        String password = binding.passwordText.getText().toString();
        String name = binding.userName.getText().toString();

        if(email.equals("") || password.equals("")){
            Toast.makeText(this ,"Password or email cannot be empty !",Toast.LENGTH_LONG).show();
        }
        else {

            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {



                    FirebaseUser user = auth.getCurrentUser();
                    DocumentReference df = fstore.collection("Users").document(user.getUid());


                    Map<String,Object> userInfo = new HashMap<>();
                    userInfo.put("name",binding.userName.getText().toString());
                    userInfo.put("email",binding.emailText.getText().toString());
                    userInfo.put("hotPursuitRecord",0);
                    userInfo.put("timeRushRecord",0);
                    userInfo.put("isAdmin",0);
                    userInfo.put("userPhotoUrl","default");




                    df.set(userInfo);



                    Toast.makeText(Register.this,"You successfully signed up.",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Register.this , LogInPage.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Register.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public void goToLogin(View View )
    {
        Intent intent = new Intent(Register.this , LogInPage.class);
        startActivity(intent);
        finish();
    }

}