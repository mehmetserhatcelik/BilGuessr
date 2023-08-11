package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.bilguessr.Adapters.LastAdapter;
import com.example.bilguessr.Adapters.LobbyAdapter;
import com.example.bilguessr.Models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LastManResultScreen extends AppCompatActivity implements OnMapReadyCallback {
    private RecyclerView recyclerView;
    private LastAdapter myAdapter;
    private DatabaseReference database;
    private String name;
    private String pp;
    private String conID;
    private String player;
    private ArrayList<User> list;
    private GoogleMap mMap;
    private double photoLat;
    private double photoLong;
    int turnNo;
    private String[] eliminated = new String[1];

    private Long min =Long.MIN_VALUE;
    private int playercount = 5;

    private Handler h = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_man_result_screen);
        eliminated[0] = null;
        turnNo = getIntent().getIntExtra("turn",1);
        name = getIntent().getStringExtra("name");
        pp = getIntent().getStringExtra("pp");
        player = getIntent().getStringExtra("player");
        conID = getIntent().getStringExtra("conID");
        photoLat = getIntent().getDoubleExtra("photolat",0);
        photoLong = getIntent().getDoubleExtra("photolong",0);


        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView2);
        myAdapter = new LastAdapter( list,LastManResultScreen.this );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map3);
        mapFragment.getMapAsync(this);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        EventChangeListener();

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                database = FirebaseDatabase.getInstance().getReference().child("lastconnections").child(conID);
                database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        String  e = task.getResult().child("removed").getValue(String.class);
                        DatabaseReference f = FirebaseDatabase.getInstance().getReference().child("lastconnections").child(conID);
                        f.child(e).removeValue();
                        if(e.equals(player))
                        {
                            Intent intent = new Intent(LastManResultScreen.this,LastManEnd.class);
                            intent.putExtra("d",(playercount-turnNo+1));
                            intent.putExtra("pp",pp);
                            intent.putExtra("name",name);
                            startActivity(intent);
                            finish();
                        }else{


                            if(1==playercount-turnNo)
                            {
                                Intent intent = new Intent(LastManResultScreen.this,LastManEnd.class);
                                intent.putExtra("d",1);
                                intent.putExtra("pp",pp);
                                intent.putExtra("name",name);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Intent intent = new Intent(LastManResultScreen.this, LastManStanding.class);
                                intent.putExtra("name",name);
                                intent.putExtra("pp",pp);
                                intent.putExtra("player",player);
                                intent.putExtra("conID",conID);
                                intent.putExtra("matched",true);
                                intent.putExtra("turn",turnNo);
                                startActivity(intent);
                                finish();}}
                    }
                });

            }
        },3000);
    }
    public void EventChangeListener()
    {

        database = FirebaseDatabase.getInstance().getReference().child("lastconnections");
        database.child(conID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot players : snapshot.getChildren()) {


                    if(!players.getKey().equals("isAvailable")&& !players.getKey().equals("removed")&& !players.getKey().equals("photos")) {


                        String nme = String.valueOf(players.child("name").getValue(String.class));
                        String pp = String.valueOf(players.child("pp").getValue(String.class));
                        Long distance = players.child("distance").getValue(Long.class);



                        User user = new User(nme, pp);
                        if(distance!=null) {
                            user.setDistance(distance);
                            if(distance>min)
                            {
                                min= distance;
                                eliminated[0] = players.getKey();
                            }
                        }
                        user.setID(players.getKey());
                        list.add(user);


                    }
                }
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < list.size() - i - 1; j++) {
                        if(list.get(j).getDistance()!=null&&list.get(j + 1).getDistance()!=null){
                        if (list.get(j).getDistance() > list.get(j + 1).getDistance()) {
                            User temp = list.get(j);
                            list.set(j, list.get(j + 1));
                            list.set(j + 1, temp);
                        }}
                    }
                }

                DatabaseReference data = FirebaseDatabase.getInstance().getReference().child("lastconnections");
                data.child(conID).child("removed").setValue(eliminated[0]);

                myAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng photo = new LatLng(photoLat,photoLong );
        mMap.addMarker(new MarkerOptions().position(photo));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(photo,15));
    }
}