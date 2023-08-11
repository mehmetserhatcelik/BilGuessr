package com.example.bilguessr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.bilguessr.databinding.ActivityDuelEndBinding;
import com.example.bilguessr.databinding.ActivityHotPursuitEndBinding;
import com.example.bilguessr.databinding.ActivityLastManEndBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class LastManEnd extends AppCompatActivity {
    private ActivityLastManEndBinding binding;
    private String photUrl;
    private String name;
    private int who;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLastManEndBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        photUrl = getIntent().getStringExtra("pp");
        name = getIntent().getStringExtra("name");
        who = getIntent().getIntExtra("d",0);

        if(photUrl.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(binding.pp);
        else
            Picasso.get().load(photUrl).into(binding.pp);

        binding.name.setText(name);

        binding.who.setText("You are the #"+who);
        if(who == 1)
        {

            Glide.with(this).load(R.drawable.trophy).into(binding.imageView5);
        }
        else{
            Picasso.get().load(R.drawable.images).into(binding.imageView5);
        }

    }
    public void main(View v)
    {
        Intent intent = new Intent(LastManEnd.this, MainScreen.class);
        startActivity(intent);
        finish();
    }

}