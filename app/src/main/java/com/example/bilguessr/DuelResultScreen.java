package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class DuelResultScreen extends AppCompatActivity implements OnMapReadyCallback {
    String connectionUniqueId;
    String player;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().
            getReferenceFromUrl("https://bilguessr-default-rtdb.firebaseio.com/");

    boolean matched ;
    String playerpp;
    int turnno;
    String name;
    long p1hp;
    long p2hp;
    GoogleMap mMap;

    double latitude;
    double longitude;
    TextView opponameTV;
    TextView nameTV;
    TextView distanceTV;
    TextView oppodistanceTV;
    ImageView pp;
    ImageView oppoPP;
    TextView damage1;
    TextView damage2;
    TextView hp1;
    TextView hp2;
    int pdistance;
    Handler h = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel_result_screen);

        opponameTV = findViewById(R.id.name2);
        nameTV = findViewById(R.id.name1);
        distanceTV = findViewById(R.id.distance1);
        oppodistanceTV = findViewById(R.id.distance2);
        damage1 = findViewById(R.id.damage1);
        damage2 = findViewById(R.id.damage2);
        hp1 = findViewById(R.id.pcan1);
        hp2 = findViewById(R.id.pcan2);
        pp = findViewById(R.id.pp1);
        oppoPP = findViewById(R.id.pp2);


        p1hp = getIntent().getLongExtra("p1hp",100);
        p2hp = getIntent().getLongExtra("p2hp",100);
        connectionUniqueId = getIntent().getStringExtra("conID");
        player = getIntent().getStringExtra("player");

        matched = getIntent().getBooleanExtra("matched",false);
        playerpp =  getIntent().getStringExtra("playerpp");
        turnno = getIntent().getIntExtra("turn",0);
        name = getIntent().getStringExtra("p1name");
        latitude = getIntent().getDoubleExtra("photolat",0);
        longitude = getIntent().getDoubleExtra("photolong",0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);


        pdistance = getIntent().getIntExtra("distance",0);
        distanceTV.setText(pdistance+"m");
            long damage = pdistance/20;
            damage1.setText("-"+damage+"");
            p1hp = p1hp-damage;
        databaseReference.child("connections").child(connectionUniqueId).child(player).child("hp").setValue(p1hp);

        if(playerpp.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(pp);
        else
            Picasso.get().load(playerpp).into(pp);
        nameTV.setText(name);
        hp1.setText(p1hp+"");
        hp2.setText(p2hp+"");

        getData();
        //deleteRoom();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println(p1hp+"  "+p2hp);
                if(p1hp<=0 || p2hp <=0)
                {

                    Intent intent = new Intent(DuelResultScreen.this,DuelEnd.class);
                    if(p1hp<=0)
                    {
                        intent.putExtra("who","lost");
                    }
                    else
                    {
                        intent.putExtra("who","win");
                    }
                    intent.putExtra("pp",playerpp);
                    intent.putExtra("name",name);
                    startActivity(intent);
                    finish();
                }else{
                    databaseReference.child("connections").child(connectionUniqueId).child(player).child("hp").setValue(p1hp);
                Intent intent = new Intent(DuelResultScreen.this, DuelDeneme.class);
                intent.putExtra("matched",matched);
                intent.putExtra("conID",connectionUniqueId);
                intent.putExtra("player",player);

                intent.putExtra("pp",playerpp);
                intent.putExtra("turn",turnno);
                intent.putExtra("name",name);
                    intent.putExtra("p1hp",p1hp);
                    intent.putExtra("p2hp",p2hp);
                startActivity(intent);
                finish();}
            }
        },3000);
    }
    private void getData()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(connectionUniqueId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists())
                    {
                        for (DataSnapshot players : snapshot.getChildren())
                        {
                            if(!players.getKey().equals("isAvailable") && !players.getKey().equals("photos")){
                                String oppoName = String.valueOf(players.child("name").getValue(String.class));
                                String oppoPp = String.valueOf(players.child("pp").getValue(String.class));
                                Long distance = players.child("distance").getValue(Long.class);
                                Long hp = players.child("hp").getValue(Long.class);
                                if(!oppoName.equals(name))
                                {
                                    if(oppoPp.equals("default"))
                                        Picasso.get().load(R.drawable.wavy).into(oppoPP);
                                    else
                                        Picasso.get().load(oppoPp).into(oppoPP);
                                    opponameTV.setText(name);
                                    oppodistanceTV.setText(distance+"m");
                                    if(distance!=null){
                                        long damage = distance/20;
                                    damage2.setText("-"+damage+"");

                                    }
                                    if(hp!=null){
                                        p2hp=hp;
                                    }
                                }
                                else{

                                }}

                        }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

                /*.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        for (DataSnapshot players : task.getResult().getChildren())
                        {
                            if(!players.getKey().equals("isAvailable")){
                            String oppoName = String.valueOf(players.child("name").getValue(String.class));
                            String oppoPp = String.valueOf(players.child("pp").getValue(String.class));
                            Long distance = players.child("distance").getValue(Long.class);
                            System.out.println(oppoName);
                            if(!oppoName.equals(name))
                            {
                                if(oppoPp.equals("default"))
                                    Picasso.get().load(R.drawable.wavy).into(oppoPP);
                                else
                                    Picasso.get().load(oppoPp).into(oppoPP);
                                opponameTV.setText(name);
                                oppodistanceTV.setText(distance+"m");
                                long damage = distance/10;
                                damage2.setText("-"+damage+"");
                                p2hp = p2hp-damage;
                            }
                            else{
                                distanceTV.setText(distance+"m");
                                long damage = distance/10;
                                damage1.setText("-"+damage+"");
                                p1hp = p1hp-damage;
                            }}

                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });*/
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng photo = new LatLng(latitude,longitude );
        mMap.addMarker(new MarkerOptions().position(photo));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(photo,15));
    }
}