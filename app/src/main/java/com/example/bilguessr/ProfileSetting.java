package com.example.bilguessr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilguessr.profilesettings.ProfileSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ProfileSetting extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    String userName;
    String playerName;
    String playerpp;
    ImageView profileIcon;
    TextView usernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        profileIcon = findViewById(R.id.imageProfile);
        usernameText = findViewById(R.id.userName);


        playerName = getIntent().getStringExtra("name");
        playerpp = getIntent().getStringExtra("pp");
        System.out.println(playerName);
        usernameText.setText(playerName);
        if(playerpp.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(profileIcon);
        else
            Picasso.get().load(playerpp).into(profileIcon);

        findViewById(R.id.logOutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        findViewById(R.id.textViewChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        findViewById(R.id.textViewChangeIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeIcon();
            }
        });

        findViewById(R.id.textViewChangeEmail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeEmail();
            }
        });
    }

    public void logOut() {
        Intent intent = new Intent(this, MainScreen.class);
        startActivity(intent);
        finish();
    }

    public void changePassword() {
        Intent intent = new Intent(this, ProfileSettings.class);
        intent.putExtra("setting", "password");
        startActivity(intent);
    }

    public void changeIcon() {
        Intent intent = new Intent(this, ProfileSettings.class);
        intent.putExtra("setting", "icon");
        startActivity(intent);
    }

    private void changeEmail() {
        Intent intent = new Intent(this, ProfileSettings.class);
        intent.putExtra("setting", "email");
        startActivity(intent);
    }

}
