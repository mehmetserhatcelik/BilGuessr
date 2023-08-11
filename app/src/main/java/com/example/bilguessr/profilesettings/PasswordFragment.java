package com.example.bilguessr.profilesettings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bilguessr.MainScreen;
import com.example.bilguessr.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class PasswordFragment extends Fragment {
    FirebaseAuth firebaseAuth;


    EditText oldPassword;
    EditText newPassword;
    EditText againPassword;
    boolean check=false;//Validate password method

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button changeButton = view.findViewById(R.id.changePasswordBotton);
        oldPassword = view.findViewById(R.id.oldPasswordText);
        newPassword = view.findViewById(R.id.newPasswordText);
        againPassword = view.findViewById(R.id.againPasswordText);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword(view);
            }
        });
    }

    public void changePassword(View view){
        String oldPasswordString =  oldPassword.getText().toString();
        String newPasswordString =  newPassword.getText().toString();
        String againPasswordString = againPassword.getText().toString();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(oldPasswordString.equals("")||newPasswordString.equals("")||againPasswordString.equals("")){
            Toast.makeText(getContext(), "Please Enter Passwords", Toast.LENGTH_LONG).show();
        }

        else if(!validatePassword()){
            Toast.makeText(getContext(), "Your old password does not match", Toast.LENGTH_LONG).show();
        }

        else if(!newPasswordString.equals(againPasswordString)){
            Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_LONG).show();
        }
        else{
            user.updatePassword(newPasswordString);
            Toast.makeText(getContext(), "Successfully changed", Toast.LENGTH_LONG).show();
            Intent intent=new Intent(getContext(), MainScreen.class);
            startActivity(intent);
        }


    }

    private boolean validatePassword(){
        String oldPasswordString =  oldPassword.getText().toString();
        FirebaseUser user = firebaseAuth.getCurrentUser();

        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail().toString(),oldPasswordString);
        user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                check=true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                check=false;
            }
        });
        return  check;
    }


}