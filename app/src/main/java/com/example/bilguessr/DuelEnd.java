package com.example.bilguessr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.bilguessr.databinding.ActivityDuelEndBinding;
import com.example.bilguessr.databinding.ActivityHotPursuitEndBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DuelEnd extends AppCompatActivity {
    private ActivityDuelEndBinding binding;
    private String photUrl;
    private String name;
    private  String who;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDuelEndBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        photUrl = getIntent().getStringExtra("pp");
        name = getIntent().getStringExtra("name");
        who = getIntent().getStringExtra("who");

        if(photUrl.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(binding.pp);
        else
            Picasso.get().load(photUrl).into(binding.pp);

        binding.name.setText(name);

        if(who.equals("lost"))
            binding.who.setText("You Lost!");
        else
            binding.who.setText("You Survived!");

    }
    public void main(View v)
    {
        Intent intent = new Intent(DuelEnd.this, MainScreen.class);
        startActivity(intent);
        finish();
    }

}