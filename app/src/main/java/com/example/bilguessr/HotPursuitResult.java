package com.example.bilguessr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class HotPursuitResult extends AppCompatActivity {

    private int questionNumber;
    private double distance;
    Handler h = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_pursuit_result);


        distance = getIntent().getDoubleExtra("d",-1);
        Toast.makeText(this, distance+"", Toast.LENGTH_SHORT).show();

        questionNumber = getIntent().getIntExtra("q",1);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HotPursuitResult.this, HotPursuit.class);
                intent.putExtra("q",questionNumber);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}