package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilguessr.databinding.ActivityHotPursuitResultBinding;
import com.example.bilguessr.databinding.ActivityHotpursuitBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

public class HotPursuitResult extends AppCompatActivity implements OnMapReadyCallback {

    private int questionNumber;
    private int distance;
    private String ppUrl;
    private double photoLong;
    private double photoLat;
    private double lon;
    private double lat;
    private String name;
    private GoogleMap mMap;
    private int tota;
    private ActivityHotPursuitResultBinding binding;
    private TextView nameTV;
    private ImageView ppTV;
    private TextView distanceTV;
    private TextView totalpointTV;
    private TextView pointGainedTV;
    private int pointsgained;
    private int prevpoints;


    private Handler h = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotPursuitResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        nameTV = binding.name;
        ppTV = binding.pp;
        distanceTV = binding.distance;
        totalpointTV = binding.totalPoints;
        pointGainedTV = binding.pointsGained;

        distance = getIntent().getIntExtra("d",-1);
        ppUrl = getIntent().getStringExtra("pp");
        lat = getIntent().getDoubleExtra("latitude",0);
        lon = getIntent().getDoubleExtra("longitude",0);
        photoLat = getIntent().getDoubleExtra("photolat",0);
        photoLong = getIntent().getDoubleExtra("photolong",0);
        name = getIntent().getStringExtra("name");
        tota = getIntent().getIntExtra("totalpoints",0);
        prevpoints = getIntent().getIntExtra("prevp",0);
        pointsgained = tota-prevpoints;
        prevpoints = tota;
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);


        pointGainedTV.setText(pointsgained+"");

        distanceTV.setText(distance+"");
        if(ppUrl.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(ppTV);
        else
            Picasso.get().load(ppUrl).into(ppTV);
        nameTV.setText(name);
        totalpointTV.setText(tota+"");

        questionNumber = getIntent().getIntExtra("q",1);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HotPursuitResult.this, HotPursuit.class);
                intent.putExtra("q",(questionNumber+1));
                intent.putExtra("point",tota);
                intent.putExtra("pp",ppUrl);
                intent.putExtra("name",name);
                intent.putExtra("pgained",prevpoints);
                startActivity(intent);
                finish();
            }
        },3000);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng photo = new LatLng(photoLat,photoLong );
        mMap.addMarker(new MarkerOptions().position(photo));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(photo,15));
    }
}