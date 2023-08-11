package com.example.bilguessr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilguessr.Models.Photo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class DuelDeneme extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().
            getReferenceFromUrl("https://bilguessr-default-rtdb.firebaseio.com/");
    ValueEventListener wonEventListener;
    private TextView player1TV, player2TV;
    private String playerUniqueId;

    private String opponentUniqueId;
    private Photo[] photos;

    private String connectionId ;
    private String connectionUniqueId;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore fstore;
    private GoogleMap mMap;
    Timer timer;
    private ProgressBar pb;
    private int counter;
    private boolean isTimeFinished;
    private boolean isAnswerLocked;
    private double longitude;
    private double latitude;
    private ImageView imageView;
    private List<Photo> photoList;

    private Photo currentPhoto;
    private boolean matched;
    private  String playerName;
    private String playerpp;
    private int hp;
    private String pp;
    private  ImageView p1pp;
    private ImageView p2pp;
    private  boolean opponentLock ;

    private int turnNo;
    private TextView p1can;
    private TextView p2can;
    private long p1hp;
    private long p2hp;
    private boolean entered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
        turnNo=getIntent().getIntExtra("turn",0);

        player1TV = findViewById(R.id.player1TV);
        player2TV = findViewById(R.id.player2TV);
        p1pp = findViewById(R.id.player1pp);
        p2pp = findViewById(R.id.player2pp);
        p1can = findViewById(R.id.pcan1);
        p2can = findViewById(R.id.pcan2);

        photos = new Photo[50];
        pb=findViewById(R.id.pb);

        fstore = FirebaseFirestore.getInstance();
        isTimeFinished=false;

        imageView = findViewById(R.id.imageView);
        photoList = new ArrayList<>();
        matched = getIntent().getBooleanExtra("matched",false);
        connectionId= getIntent().getStringExtra("conID");
        playerUniqueId = getIntent().getStringExtra("player");



        counter = 100;
        Thread t = new Thread()
        {
            @Override
            public void run()
            {
                while ((isAnswerLocked ==false || opponentLock==false) && isTimeFinished==false)
                {
                    try {

                        Thread.sleep(10);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });


                    }catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                if((isAnswerLocked==true&&opponentLock == true) || isTimeFinished == true) {

                    if(turnNo==1)
                    {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(connectionId);
                        ref.child(playerUniqueId).child("distance").setValue(calculateDistance(latitude,longitude))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Data updated successfully
                                        // Do something on success if needed
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to update data
                                        // Handle the error
                                    }
                                });
                    }
                    else{

                        String conId= getIntent().getStringExtra("conID");
                        String player = getIntent().getStringExtra("player");
                        String opponent = getIntent().getStringExtra("opponent");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(conId);
                        ref.child(player).child("distance").setValue(calculateDistance(latitude,longitude))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Data updated successfully
                                        // Do something on success if needed
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Failed to update data
                                        // Handle the error
                                    }
                                });

                    }
                    Intent intent = new Intent(DuelDeneme.this, DuelResultScreen.class);
                    intent.putExtra("conID",connectionId);
                    intent.putExtra("matched",matched);
                    intent.putExtra("distance",calculateDistance(latitude,longitude));
                    intent.putExtra("player",playerUniqueId);
                    intent.putExtra("playerpp",playerpp);
                    intent.putExtra("p1name",playerName);
                    intent.putExtra("turn",turnNo);
                    intent.putExtra("p1hp",p1hp);
                    intent.putExtra("p2hp",p2hp);
                    intent.putExtra("photolat",currentPhoto.getLatitude());
                    intent.putExtra("photolong",currentPhoto.getLongitude());
                    startActivity(intent);
                    finish();
                }}

        };
        t.start();






        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        MyConstents info = new MyConstents();

        info.getPlayerName(player1TV);
        playerName = getIntent().getStringExtra("name");
        playerpp = getIntent().getStringExtra("pp")+"";
        if(playerpp.equals("default"))
            Picasso.get().load(R.drawable.wavy).into(p1pp);
        else
            Picasso.get().load(playerpp).into(p1pp);
        isAnswerLocked =info.isLockClicked();
        hp = info.getHp();


        player1TV.setText(playerName);
        if(!matched){
            p1hp = 100;
            p2hp = 100;
            p1can.setText(p1hp+"");
            if (MemoryData.getData("player_id", "", this).isEmpty()) {
                playerUniqueId = String.valueOf(System.currentTimeMillis());
                MemoryData.saveData("player_id", playerUniqueId, this);
            } else {
                playerUniqueId = MemoryData.getData("player_id", "", this);
            }
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Waiting for Opponent");
            progressDialog.show();
            databaseReference.child("connections").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.hasChildren())
                    {
                        for(DataSnapshot snapshots : snapshot.getChildren())
                        {
                            if(snapshots.child("isAvailable").getValue(Boolean.class))
                            {
                                connectionId =snapshots.getKey();
                                databaseReference.child("connections").child(connectionId).child(playerUniqueId);
                                databaseReference.child("connections").child(connectionId).child(playerUniqueId).child("name").setValue(playerName);
                                databaseReference.child("connections").child(connectionId).child(playerUniqueId).child("pp").setValue(playerpp);
                                databaseReference.child("connections").child(connectionId).child(playerUniqueId).child("hp").setValue(p1hp);
                                entered = true;
                                break;

                            }
                            if(!entered)
                            {
                                connectionUniqueId  = String.valueOf(System.currentTimeMillis());
                                connectionId =connectionUniqueId;
                                ImageList();

                                databaseReference.child("connections").child(connectionUniqueId).child("isAvailable").setValue(true);
                                databaseReference.child("connections").child(connectionId).child(playerUniqueId).child("hp").setValue(p1hp);
                                databaseReference.child("connections").child(connectionId).child(playerUniqueId).child("name").setValue(playerName);
                                databaseReference.child("connections").child(connectionUniqueId).child(playerUniqueId).child("pp").setValue(playerpp);
                                entered = true;
                            }
                        }
                    }else{
                        connectionUniqueId  = String.valueOf(System.currentTimeMillis());
                        connectionId =connectionUniqueId;
                        ImageList();

                        databaseReference.child("connections").child(connectionUniqueId).child("isAvailable").setValue(true);
                        databaseReference.child("connections").child(connectionId).child(playerUniqueId).child("hp").setValue(p1hp);
                        databaseReference.child("connections").child(connectionId).child(playerUniqueId).child("name").setValue(playerName);
                        databaseReference.child("connections").child(connectionUniqueId).child(playerUniqueId).child("pp").setValue(playerpp);

                        entered = true;
                    }
                    if(entered)
                    {

                        if(snapshot.child(connectionId).getChildrenCount()==4)
                        {

                            matched=true;

                            databaseReference.child("connections").child(connectionId).child("isAvailable").setValue(false);
                            if (progressDialog.isShowing()) {

                                progressDialog.dismiss();
                                pb();
                                getImage();
                                turnNo++;


                            }

                        }
                        if(matched){

                            for(DataSnapshot players : snapshot.child(connectionId).getChildren())
                            {
                                if(!players.getKey().equals(playerUniqueId) && !players.getKey().equals("isAvailable") && !players.getKey().equals("photos")){
                                    String pp = String.valueOf(players.child("pp").getValue());
                                    String name = String.valueOf(players.child("name").getValue());
                                    player2TV.setText(name);

                                        if(pp.equals("default"))
                                            Picasso.get().load(R.drawable.wavy).into(p2pp);
                                        else
                                            Picasso.get().load(pp).into(p2pp);
                                        p2pp.setVisibility(View.VISIBLE);
                                }}

                        }


                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }else{

            turnNo++;
            pb();
            getImage();
            p1hp = getIntent().getLongExtra("p1hp",100);

            p1can.setText(p1hp+"");


            String conId= getIntent().getStringExtra("conID");
            String player = getIntent().getStringExtra("player");


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(conId);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists())
                    {
                        for (DataSnapshot players : snapshot.getChildren())
                        {
                            if(!players.getKey().equals("isAvailable") && !players.getKey().equals("photos")){

                                if(!players.getKey().equals(player))
                                {
                                    String oppoName = String.valueOf(players.child("name").getValue(String.class));
                                    String oppoPp = String.valueOf(players.child("pp").getValue(String.class));

                                    Long hp = players.child("hp").getValue(Long.class);

                                    if(oppoPp.equals("default"))
                                        Picasso.get().load(R.drawable.wavy).into(p2pp);
                                    else
                                        Picasso.get().load(oppoPp).into(p2pp);
                                    player2TV.setText(oppoName);


                                    if(hp!=null){
                                        p2hp=hp;
                                        p2can.setText(p2hp+"");

                                    }
                                }
                                }

                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        }


    }
    private void ImageList()
    {
        fstore.collection("Photos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot d: queryDocumentSnapshots) {
                    Photo photo = d.toObject(Photo.class);
                    photoList.add(photo);
                }
                Random random = new Random();

                for (int j = 0; j < 50 ; j++) {
                    int i = random.nextInt(photoList.size());
                    photos[j]=photoList.get(i);
                }
                for (int i = 0; i < photos.length; i++) {
                    databaseReference.child("connections").child(connectionId).child("photos").child("photo"+i).setValue(photos[i]);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DuelDeneme.this, "Failed to fetch data.", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void getImage(){

        databaseReference.child("connections").child(connectionId).child("photos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentPhoto = snapshot.child("photo"+turnNo).getValue(Photo.class);
                if(currentPhoto.getDownloadUrl()!=null)
                    Picasso.get().load(currentPhoto.getDownloadUrl()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void pb()
    {
        int period;


        if(turnNo==1)
            period =10000;
        else
            period = 100;

        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                counter--;
                pb.setProgress(counter);
                if(counter==0) {
                    timer.cancel();
                    isTimeFinished=true;
                }
            }
        };
        timer.schedule(timerTask,0,period);

    }

    public void lock(View view)
    {
        /*System.out.println(turnNo);
        if(turnNo!=1)
        {

            String conId= getIntent().getStringExtra("conID");
            String player = getIntent().getStringExtra("player");
            String opponent = getIntent().getStringExtra("opponent");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(conId);

            isAnswerLocked=true;
            ref.child(player).child("lock").setValue("true")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data updated successfully
                            // Do something on success if needed
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update data
                            // Handle the error
                        }
                    });}
        else{
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("connections").child(connectionId);

            isAnswerLocked=true;
            ref.child(playerUniqueId).child("lock").setValue("true")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data updated successfully
                            // Do something on success if needed
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to update data
                            // Handle the error
                        }
                    });
        }*/

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        LatLng bilkent = new LatLng(39.87462344844274, 32.747621680995884);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bilkent,15));
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        mMap.clear();
        latitude = latLng.latitude;
        longitude = latLng.longitude;

        mMap.addMarker(new MarkerOptions().position(latLng));

    }
    public int calculateDistance(double latitude,double longitude) {
        final double EARTH_RADIUS_KM = 6371.0;
        double dLat = Math.toRadians(currentPhoto.getLatitude() - latitude);
        double dLon = Math.toRadians(currentPhoto.getLongitude() - longitude);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(currentPhoto.getLatitude())) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distance in kilometers
        double distance = EARTH_RADIUS_KM * c;

        return (int)((distance)*1000);
    }
}