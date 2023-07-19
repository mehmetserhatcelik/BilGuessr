package com.example.bilguessr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

    }
    public void HotPursuit(View view)
    {
        Intent intent = new Intent(MainScreen.this, HotPursuit.class);
        startActivity(intent);
        finish();
    }

    public void singlePlayer(View view)
    {
        Intent intent = new Intent(MainScreen.this, HotPursuit.class);
        startActivity(intent);
        finish();
    }
}